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
const dateRange = (req) => [req.query.from || req.query.startDate || req.query.date || '1900-01-01', req.query.to || req.query.endDate || req.query.date || '2999-12-31'];
const sendError = (res, error) => res.status(error.code === '23505' ? 409 : 500).json({ error: error.message });


const menuSelect = `SELECT menu_item_id AS "menuItemId", name, category, price, active FROM menu_items`;
const inventorySelect = `SELECT item_id AS "itemId", item_name AS "itemName", quantity, reorder_level AS "reorderLevel", last_unit_price AS "lastUnitPrice" FROM inventory_items`;


app.get('/health', async (_req, res) => {
  if (!pool) return res.status(503).json({ status: 'ok', database: 'not-configured' });
