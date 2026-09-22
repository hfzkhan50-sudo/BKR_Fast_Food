import express from 'express';
import fs from 'node:fs/promises';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import pg from 'pg';

const { Pool } = pg;
const app = express();
const port = Number(process.env.PORT || 10000);
const pool = process.env.DATABASE_URL
  ? new Pool({ connectionString: process.env.DATABASE_URL, ssl: { rejectUnauthorized: false } })
  : null;

app.use(express.json({ limit: '1mb' }));
app.use((req, res, next) => {
  res.setHeader('Access-Control-Allow-Origin', process.env.ALLOWED_ORIGIN || '*');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type');
  res.setHeader('Access-Control-Allow-Methods', 'GET,POST,PUT,DELETE,OPTIONS');
  if (req.method === 'OPTIONS') return res.sendStatus(204);
  next();
});

const query = (text, values = []) => {
  if (!pool) throw new Error('DATABASE_URL is not configured');
  return pool.query(text, values);
};

const number = (value, fallback = 0) => Number.isFinite(Number(value)) ? Number(value) : fallback;
const normalizeDrinkName = (value) => String(value || '').toLowerCase().replace(/[^a-z0-9]/g, '');
const drinkInventoryKey = (value) => {
  const normalized = normalizeDrinkName(value);
  if (normalized === '1ltr' || normalized === '1liter' || normalized === '1litre') return '1ltr';
  if (normalized === '15ltr' || normalized === '15liter' || normalized === '15litre') return '15ltr';
  if (normalized === 'watersmall' || normalized === 'smallwater') return 'watersmall';
  if (normalized === 'waterlarge' || normalized === 'largewater') return 'waterlarge';
  return '';
};
const drinkInventorySql = `CASE regexp_replace(LOWER(BTRIM(item_name)), '[^a-z0-9]', '', 'g')
  WHEN '1ltr' THEN '1ltr'
  WHEN '1liter' THEN '1ltr'
  WHEN '1litre' THEN '1ltr'
  WHEN '15ltr' THEN '15ltr'
  WHEN '15liter' THEN '15ltr'
  WHEN '15litre' THEN '15ltr'
  WHEN 'watersmall' THEN 'watersmall'
  WHEN 'smallwater' THEN 'watersmall'
  WHEN 'waterlarge' THEN 'waterlarge'
  WHEN 'largewater' THEN 'waterlarge'
  ELSE ''
END`;
const dateRange = (req) => [req.query.from || req.query.startDate || req.query.date || '1900-01-01', req.query.to || req.query.endDate || req.query.date || '2999-12-31'];
const sendError = (res, error) => res.status(error.code === '23505' ? 409 : 500).json({ error: error.message });

const menuSelect = `SELECT DISTINCT ON (LOWER(BTRIM(category)), LOWER(BTRIM(name)), LOWER(BTRIM(size))) menu_item_id AS "menuItemId", name, category, price, size, active FROM menu_items`;
const inventorySelect = `SELECT item_id AS "itemId", item_name AS "itemName", quantity, reorder_level AS "reorderLevel", last_unit_price AS "lastUnitPrice" FROM inventory_items`;

app.get('/health', async (_req, res) => {
  if (!pool) return res.status(503).json({ status: 'ok', database: 'not-configured' });
  try { await query('SELECT 1'); res.json({ status: 'ok', database: 'connected' }); }
  catch (_error) { res.status(503).json({ status: 'ok', database: 'unavailable' }); }
});

const api = express.Router();
api.get('/status', (_req, res) => res.json({ status: 'ok' }));

api.get('/menu/active', async (_req, res) => { try { res.json((await query(`${menuSelect} WHERE active = TRUE ORDER BY LOWER(BTRIM(category)), LOWER(BTRIM(name)), LOWER(BTRIM(size)), menu_item_id`)).rows); } catch (e) { sendError(res, e); } });
api.get('/menu', async (_req, res) => { try { res.json((await query(`${menuSelect} ORDER BY LOWER(BTRIM(category)), LOWER(BTRIM(name)), LOWER(BTRIM(size)), menu_item_id`)).rows); } catch (e) { sendError(res, e); } });
api.post('/menu/flyer', async (_req, res) => {
  const client = await pool.connect();
  try {
    await client.query('BEGIN');
    const saved = await client.query('SELECT name, category, price, size, active FROM menu_items ORDER BY menu_item_id');
    if (!saved.rowCount) return res.status(400).json({ error: 'The current menu is empty.' });
    await client.query('DELETE FROM menu_flyer_items');
    await client.query('INSERT INTO menu_flyer_items (name, category, price, size, active) SELECT name, category, price, size, active FROM menu_items ORDER BY menu_item_id');
    await client.query('COMMIT');
    res.json({ savedCount: saved.rowCount });
  } catch (e) { await client.query('ROLLBACK'); sendError(res, e); } finally { client.release(); }
});
api.put('/menu/flyer/restore', async (_req, res) => {
  const client = await pool.connect();
  try {
    await client.query('BEGIN');
    const saved = await client.query('SELECT name, category, price, size, active FROM menu_flyer_items ORDER BY flyer_item_id');
    if (!saved.rowCount) return res.status(404).json({ error: 'No BKR Flyer menu has been saved yet.' });
    await client.query('DELETE FROM menu_items');
    await client.query('INSERT INTO menu_items (name, category, price, size, active) SELECT name, category, price, size, active FROM menu_flyer_items ORDER BY flyer_item_id');
    await client.query('COMMIT');
    res.json({ restoredCount: saved.rowCount });
  } catch (e) { await client.query('ROLLBACK'); sendError(res, e); } finally { client.release(); }
});
api.post('/menu', async (req, res) => {
  try {
    const name = req.body.name || '';
    const category = req.body.category || '';
    const size = req.body.size || '';
    const existing = await query('SELECT menu_item_id AS "menuItemId" FROM menu_items WHERE LOWER(BTRIM(name)) = LOWER(BTRIM($1)) AND LOWER(BTRIM(category)) = LOWER(BTRIM($2)) AND LOWER(BTRIM(size)) = LOWER(BTRIM($3)) LIMIT 1', [name, category, size]);
    if (existing.rowCount) return res.json(existing.rows[0]);
    const r = await query('INSERT INTO menu_items (name, category, price, size, active) VALUES ($1,$2,$3,$4,$5) RETURNING menu_item_id AS "menuItemId"', [name, category, number(req.body.price), size, req.body.active !== false]); res.status(201).json(r.rows[0]);
  } catch (e) { sendError(res, e); }
});
api.put('/menu', async (req, res) => {
  try { const r = await query('UPDATE menu_items SET name = COALESCE($1, name), category = COALESCE($2, category), price = COALESCE($3, price), size = COALESCE($4, size), active = COALESCE($5, active) WHERE menu_item_id = $6 RETURNING menu_item_id AS "menuItemId"', [req.body.name, req.body.category, req.body.price == null ? null : number(req.body.price), req.body.size, req.body.active == null ? null : Boolean(req.body.active), req.body.menuItemId || req.body.id]); if (!r.rowCount) return res.sendStatus(404); res.json(r.rows[0]); } catch (e) { sendError(res, e); }
});
api.delete('/menu/:id', async (req, res) => { try { await query('DELETE FROM menu_items WHERE menu_item_id = $1', [req.params.id]); res.sendStatus(204); } catch (e) { sendError(res, e); } });

api.get('/inventory', async (req, res) => { try { const search = String(req.query.search || '').trim(); const r = await query(`${inventorySelect} ${search ? 'WHERE item_name ILIKE $1' : ''} ORDER BY item_name`, search ? [`%${search}%`] : []); res.json(r.rows); } catch (e) { sendError(res, e); } });
api.get('/inventory/low-stock', async (_req, res) => { try { res.json((await query(`${inventorySelect} WHERE quantity <= reorder_level ORDER BY item_name`)).rows); } catch (e) { sendError(res, e); } });
api.post('/inventory', async (req, res) => { try { const r = await query('INSERT INTO inventory_items (item_name, quantity, reorder_level, last_unit_price) VALUES ($1,$2,$3,$4) RETURNING item_id AS "itemId"', [req.body.itemName || req.body.name || '', number(req.body.quantity), number(req.body.reorderLevel), number(req.body.lastUnitPrice)]); res.status(201).json(r.rows[0]); } catch (e) { sendError(res, e); } });
api.delete('/inventory/:id', async (req, res) => { try { await query('DELETE FROM inventory_items WHERE item_id = $1', [req.params.id]); res.sendStatus(204); } catch (e) { sendError(res, e); } });

const nextOrderNo = async (client = pool) => {
  const result = await client.query(`
    SELECT COALESCE(MAX(
      CASE
        WHEN order_no ~ '^[0-9]+$' THEN order_no::int
      END
    ), 0) + 1 AS next_no,
    (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Karachi')::date AS order_date
    FROM orders
    WHERE order_date = (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Karachi')::date
  `);
  return String(result.rows[0].next_no);
};

api.get('/orders/generate-no', async (_req, res) => {
  try { res.json({ orderNo: await nextOrderNo() }); } catch (e) { sendError(res, e); }
});
api.get('/orders', async (req, res) => { try { const [from, to] = dateRange(req); res.json((await query('SELECT order_id AS "orderId", order_no AS "orderNo", order_date::text AS "orderDate", order_time::text AS "orderTime", customer_name AS "customerName", order_type AS "orderType", table_number AS "tableNumber", payment_type AS "paymentType", subtotal, discount, delivery_charge AS "deliveryCharge", service_charge_percent AS "serviceChargePercent", service_charge AS "serviceCharge", total_amount AS "totalAmount", status FROM orders WHERE order_date BETWEEN $1 AND $2 AND status <> \'Cancelled\' ORDER BY order_date DESC, order_time DESC', [from, to])).rows); } catch (e) { sendError(res, e); } });
api.get('/orders/range', async (req, res) => { try { const [from, to] = dateRange(req); res.json((await query('SELECT order_id AS "orderId", order_no AS "orderNo", order_date::text AS "orderDate", order_time::text AS "orderTime", customer_name AS "customerName", order_type AS "orderType", table_number AS "tableNumber", payment_type AS "paymentType", subtotal, discount, delivery_charge AS "deliveryCharge", service_charge_percent AS "serviceChargePercent", service_charge AS "serviceCharge", total_amount AS "totalAmount", status FROM orders WHERE order_date BETWEEN $1 AND $2 AND status <> \'Cancelled\' ORDER BY order_date DESC, order_time DESC', [from, to])).rows); } catch (e) { sendError(res, e); } });
api.get('/orders/cancelled/range', async (req, res) => { try { const [from, to] = dateRange(req); res.json((await query('SELECT order_id AS "orderId", order_no AS "orderNo", order_date::text AS "orderDate", order_time::text AS "orderTime", customer_name AS "customerName", order_type AS "orderType", table_number AS "tableNumber", payment_type AS "paymentType", subtotal, discount, delivery_charge AS "deliveryCharge", service_charge_percent AS "serviceChargePercent", service_charge AS "serviceCharge", total_amount AS "totalAmount", status FROM orders WHERE order_date BETWEEN $1 AND $2 AND status = \'Cancelled\' ORDER BY order_date DESC, order_time DESC', [from, to])).rows); } catch (e) { sendError(res, e); } });
api.get('/orders/sales', async (req, res) => { try { const [from, to] = dateRange(req); const r = await query('SELECT COALESCE(SUM(total_amount),0) AS "totalSales" FROM orders WHERE order_date BETWEEN $1 AND $2 AND status <> \'Cancelled\'', [from, to]); res.json(r.rows[0]); } catch (e) { sendError(res, e); } });
api.get('/orders/cancelled/sales', async (req, res) => { try { const [from, to] = dateRange(req); const r = await query('SELECT COALESCE(SUM(total_amount),0) AS "totalSales" FROM orders WHERE order_date BETWEEN $1 AND $2 AND status = \'Cancelled\'', [from, to]); res.json(r.rows[0]); } catch (e) { sendError(res, e); } });
api.get('/sales/date/:date', async (req, res) => { try { const r = await query('SELECT COALESCE(SUM(total_amount),0) AS "totalSales", COUNT(*)::int AS "orderCount" FROM orders WHERE order_date = $1 AND status <> \'Cancelled\'', [req.params.date]); res.json(r.rows[0]); } catch (e) { sendError(res, e); } });
api.get('/orders/count', async (req, res) => { try { const [from, to] = dateRange(req); const r = await query('SELECT COUNT(*)::int AS "orderCount" FROM orders WHERE order_date BETWEEN $1 AND $2 AND status <> \'Cancelled\'', [from, to]); res.json(r.rows[0]); } catch (e) { sendError(res, e); } });
api.get('/orders/item-sales', async (req, res) => { try { const [from, to] = dateRange(req); res.json((await query('SELECT menu_item_name AS "itemName", SUM(quantity)::int AS quantity, SUM(line_total) AS "totalSales" FROM order_items oi JOIN orders o USING (order_id) WHERE o.order_date BETWEEN $1 AND $2 AND o.status <> \'Cancelled\' GROUP BY menu_item_name ORDER BY menu_item_name', [from, to])).rows); } catch (e) { sendError(res, e); } });
api.get('/orders/:id', async (req, res) => { try { const order = (await query('SELECT order_id AS "orderId", order_no AS "orderNo", order_date::text AS "orderDate", order_time::text AS "orderTime", customer_name AS "customerName", order_type AS "orderType", table_number AS "tableNumber", payment_type AS "paymentType", subtotal, discount, delivery_charge AS "deliveryCharge", service_charge_percent AS "serviceChargePercent", service_charge AS "serviceCharge", total_amount AS "totalAmount", status FROM orders WHERE order_id = $1', [req.params.id])).rows[0]; if (!order) return res.sendStatus(404); order.items = (await query('SELECT menu_item_id AS "menuItemId", menu_item_name AS "menuItemName", quantity, unit_price AS "unitPrice", line_total AS "lineTotal" FROM order_items WHERE order_id = $1 ORDER BY order_item_id', [req.params.id])).rows; res.json(order); } catch (e) { sendError(res, e); } });
api.post('/orders/:id/cancel', async (req, res) => {
  const client = await pool.connect();
  try {
    await client.query('BEGIN');
    const order = (await client.query('SELECT order_id, status FROM orders WHERE order_id = $1 FOR UPDATE', [req.params.id])).rows[0];
    if (!order) { await client.query('ROLLBACK'); return res.sendStatus(404); }
    if (order.status === 'Cancelled') { await client.query('ROLLBACK'); return res.status(409).json({ error: 'This order is already cancelled.' }); }
    const items = (await client.query('SELECT menu_item_name, quantity FROM order_items WHERE order_id = $1', [req.params.id])).rows;
    for (const item of items) {
      const drinkKey = drinkInventoryKey(item.menu_item_name);
      if (drinkKey && number(item.quantity) > 0) {
        await client.query(`UPDATE inventory_items SET quantity = quantity + $1 WHERE ${drinkInventorySql} = $2`, [number(item.quantity), drinkKey]);
      }
    }
    const result = (await client.query("UPDATE orders SET status = 'Cancelled' WHERE order_id = $1 RETURNING order_id AS \"orderId\", status", [req.params.id])).rows[0];
    await client.query('COMMIT');
    res.json(result);
  } catch (e) { await client.query('ROLLBACK'); sendError(res, e); } finally { client.release(); }
});
api.post('/orders/:id/add-items', async (req, res) => {
  const client = await pool.connect();
  try {
    await client.query('BEGIN');
    const orderId = Number(req.params.id);
    const items = Array.isArray(req.body.items) ? req.body.items : [];
    if (!items.length) return res.status(400).json({ error: 'No items were provided to add to this order.' });

    const order = (await client.query('SELECT order_id, subtotal, discount, delivery_charge, service_charge_percent, service_charge, total_amount FROM orders WHERE order_id = $1 FOR UPDATE', [orderId])).rows[0];
    if (!order) { await client.query('ROLLBACK'); return res.sendStatus(404); }

    for (const item of items) {
      const qty = number(item.quantity, 1);
      if (qty <= 0) continue;
      const menuItemName = String(item.menuItemName || item.name || '').trim();
      const unitPrice = number(item.unitPrice, 0);
      const lineTotal = number(item.lineTotal, unitPrice * qty);
      await client.query('INSERT INTO order_items (order_id, menu_item_id, menu_item_name, quantity, unit_price, line_total) VALUES ($1,$2,$3,$4,$5,$6)', [orderId, Number(item.menuItemId || 0), menuItemName, qty, unitPrice, lineTotal]);
      const drinkKey = drinkInventoryKey(menuItemName);
      if (drinkKey) {
        await client.query(`UPDATE inventory_items SET quantity = GREATEST(quantity - $1, 0) WHERE ${drinkInventorySql} = $2`, [qty, drinkKey]);
      }
    }

    const totals = (await client.query('SELECT COALESCE(SUM(line_total), 0)::numeric AS subtotal FROM order_items WHERE order_id = $1', [orderId])).rows[0];
    const subtotal = number(totals.subtotal, 0);
    const discount = number(order.discount, 0);
    const deliveryCharge = number(order.delivery_charge, 0);
    const servicePercent = number(order.service_charge_percent, 0);
    const discountedSubtotal = Math.max(subtotal - discount, 0);
    const serviceCharge = discountedSubtotal * (servicePercent / 100);
    const totalAmount = discountedSubtotal + deliveryCharge + serviceCharge;

    await client.query('UPDATE orders SET subtotal = $1, service_charge = $2, total_amount = $3 WHERE order_id = $4', [subtotal, serviceCharge, totalAmount, orderId]);
    await client.query('COMMIT');
    res.json({ orderId, subtotal, totalAmount, addedCount: items.length });
  } catch (e) { await client.query('ROLLBACK'); sendError(res, e); } finally { client.release(); }
});
api.post('/orders', async (req, res) => {
  const client = await pool.connect();
  try {
    await client.query('BEGIN');
    await client.query("SELECT pg_advisory_xact_lock(hashtext('orders-' || (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Karachi')::date::text))");
    const b = req.body;
    const orderNo = await nextOrderNo(client);
    const order = (await client.query("INSERT INTO orders (order_no, order_date, order_time, customer_name, order_type, table_number, payment_type, subtotal, discount, delivery_charge, service_charge_percent, service_charge, total_amount, status) VALUES ($1, (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Karachi')::date, (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Karachi')::time, $2,$3,$4,$5,$6,$7,$8,$9,$10,$11,$12) RETURNING order_id AS \"orderId\", order_no AS \"orderNo\"", [orderNo, b.customerName || '', b.orderType || 'Dine In', b.tableNumber || '', b.paymentType || 'Cash', number(b.subtotal), number(b.discount), number(b.deliveryCharge), number(b.serviceChargePercent), number(b.serviceCharge), number(b.totalAmount), b.status || 'Completed'])).rows[0];
    for (const item of b.items || []) {
      const qty = number(item.quantity, 1);
      await client.query('INSERT INTO order_items (order_id, menu_item_id, menu_item_name, quantity, unit_price, line_total) VALUES ($1,$2,$3,$4,$5,$6)', [order.orderId, item.menuItemId || 0, item.menuItemName || '', qty, number(item.unitPrice), number(item.lineTotal)]);
      // Only bottled drinks and water reduce inventory; other menu items are not stock-tracked here.
      const drinkKey = drinkInventoryKey(item.menuItemName);
      if (qty > 0 && drinkKey) {
        await client.query(`UPDATE inventory_items SET quantity = GREATEST(quantity - $1, 0) WHERE ${drinkInventorySql} = $2`, [qty, drinkKey]);
      }
    }
    await client.query('COMMIT');
    res.status(201).json(order);
  } catch (e) { await client.query('ROLLBACK'); sendError(res, e); } finally { client.release(); }
});

api.get('/stock-in/generate-no', (_req, res) => res.json({ billNo: `STK-${new Date().toISOString().slice(0, 10).replaceAll('-', '')}-${String(Date.now()).slice(-4)}` }));
api.get('/stock-in', async (req, res) => { try { const [from, to] = dateRange(req); res.json((await query('SELECT stock_in_id AS "stockInId", bill_no AS "billNo", stock_date::text AS "stockDate", total_amount AS "totalAmount", remarks FROM stock_ins WHERE stock_date BETWEEN $1 AND $2 ORDER BY stock_date DESC, stock_in_id DESC', [from, to])).rows); } catch (e) { sendError(res, e); } });
api.get('/stock-in/:id', async (req, res) => { try { const r = (await query('SELECT stock_in_id AS "stockInId", bill_no AS "billNo", stock_date::text AS "stockDate", total_amount AS "totalAmount", remarks FROM stock_ins WHERE stock_in_id = $1', [req.params.id])).rows[0]; if (!r) return res.sendStatus(404); r.items = (await query('SELECT item_id AS "itemId", item_name AS "itemName", quantity, unit_price AS "unitPrice", line_total AS "lineTotal" FROM stock_in_items WHERE stock_in_id = $1', [req.params.id])).rows; res.json(r); } catch (e) { sendError(res, e); } });
api.post('/stock-in', async (req, res) => { const client = await pool.connect(); try { await client.query('BEGIN'); const b = req.body; const stock = (await client.query('INSERT INTO stock_ins (bill_no, remarks, total_amount) VALUES ($1,$2,$3) RETURNING stock_in_id AS "stockInId"', [b.billNo, b.remarks || '', number(b.totalAmount)])).rows[0]; for (const item of b.items || []) { const itemId = number(item.itemId); const values = [stock.stockInId, itemId, item.itemName || '', number(item.quantity), number(item.unitPrice), number(item.lineTotal)]; await client.query('INSERT INTO stock_in_items (stock_in_id, item_id, item_name, quantity, unit_price, line_total) VALUES ($1,$2,$3,$4,$5,$6)', values); if (itemId > 0) await client.query('INSERT INTO inventory_items (item_id, item_name, quantity, last_unit_price) VALUES ($1,$2,$3,$4) ON CONFLICT (item_name) DO UPDATE SET quantity = inventory_items.quantity + EXCLUDED.quantity, last_unit_price = EXCLUDED.last_unit_price', [itemId, item.itemName || '', number(item.quantity), number(item.unitPrice)]); else await client.query('INSERT INTO inventory_items (item_name, quantity, last_unit_price) VALUES ($1,$2,$3) ON CONFLICT (item_name) DO UPDATE SET quantity = inventory_items.quantity + EXCLUDED.quantity, last_unit_price = EXCLUDED.last_unit_price', [item.itemName || '', number(item.quantity), number(item.unitPrice)]); } await client.query('COMMIT'); res.status(201).json(stock); } catch (e) { await client.query('ROLLBACK'); sendError(res, e); } finally { client.release(); } });

app.use('/api', api);
app.use((err, _req, res, _next) => sendError(res, err));

async function start() {
  if (pool) { const schema = await fs.readFile(path.join(path.dirname(fileURLToPath(import.meta.url)), '..', 'schema.sql'), 'utf8'); await pool.query(schema); }
  app.listen(port, '0.0.0.0', () => console.log(`BKR backend listening on ${port}`));
}

start().catch((error) => { console.error(error); process.exit(1); });