/*
 * Decompiled with CFR 0.152.
 */
package dao;

import api.ApiClient;
import api.SimpleJson;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import model.StockIn;
import model.StockInItem;

public class StockInDAO {
    public String generateBillNo() {
        try {
            String string = ApiClient.get("/stock-in/generate-no");
            Map<String, Object> map = SimpleJson.parseObject(string);
            return SimpleJson.getString(map, "billNo", "STK-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-0001");
        }
        catch (Exception exception) {
            String string = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            return String.format("STK-%s-%04d", string, (int)(Math.random() * 9000.0) + 1000);
        }
    }

    public int saveStockIn(StockIn stockIn) {
        try {
            Object object;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("{");
            stringBuilder.append("\"billNo\":\"").append(SimpleJson.escape(stockIn.getBillNo())).append("\",");
            stringBuilder.append("\"remarks\":\"").append(SimpleJson.escape(stockIn.getRemarks())).append("\",");
            stringBuilder.append("\"totalAmount\":").append(stockIn.getTotalAmount() != null ? stockIn.getTotalAmount().toPlainString() : "0").append(",");
            stringBuilder.append("\"items\":[");
            List<StockInItem> list = stockIn.getItems();
            for (int i = 0; i < list.size(); ++i) {
                object = list.get(i);
                if (i > 0) {
                    stringBuilder.append(",");
                }
                stringBuilder.append("{");
                stringBuilder.append("\"itemId\":").append(((StockInItem)object).getItemId()).append(",");
                stringBuilder.append("\"itemName\":\"").append(SimpleJson.escape(((StockInItem)object).getItemName())).append("\",");
                stringBuilder.append("\"quantity\":").append(((StockInItem)object).getQuantity() != null ? ((StockInItem)object).getQuantity().toPlainString() : "0").append(",");
                stringBuilder.append("\"unitPrice\":").append(((StockInItem)object).getUnitPrice() != null ? ((StockInItem)object).getUnitPrice().toPlainString() : "0").append(",");
                stringBuilder.append("\"lineTotal\":").append(((StockInItem)object).getLineTotal() != null ? ((StockInItem)object).getLineTotal().toPlainString() : "0");
                stringBuilder.append("}");
            }
            stringBuilder.append("]}");
            String string = ApiClient.post("/stock-in", stringBuilder.toString());
            object = SimpleJson.parseObject(string);
            int n = SimpleJson.getInt((Map<String, Object>)object, "stockInId", 0);
            stockIn.setStockInId(n);
            return n;
        }
        catch (Exception exception) {
            throw new RuntimeException("Failed to save stock-in bill: " + exception.getMessage(), exception);
        }
    }

    public List<StockIn> getBillsBetween(LocalDate localDate, LocalDate localDate2) {
        try {
            String string = ApiClient.get("/stock-in/range?from=" + localDate.toString() + "&to=" + localDate2.toString());
            ArrayList<StockIn> arrayList = new ArrayList<StockIn>();
            List<Map<String, Object>> list = SimpleJson.parseArray(string);
            for (Map<String, Object> map : list) {
                StockIn stockIn = new StockIn();
                stockIn.setStockInId(SimpleJson.getInt(map, "stockInId", 0));
                stockIn.setBillNo(SimpleJson.getString(map, "billNo", ""));
                stockIn.setStockDate(SimpleJson.getLocalDate(map, "stockDate"));
                stockIn.setTotalAmount(SimpleJson.getBigDecimal(map, "totalAmount", BigDecimal.ZERO));
                stockIn.setRemarks(SimpleJson.getString(map, "remarks", ""));
                arrayList.add(stockIn);
            }
            return arrayList;
        }
        catch (Exception exception) {
            System.err.println("Warning: Failed to fetch bills: " + exception.getMessage());
            return new ArrayList<StockIn>();
        }
    }

    public StockIn getBillWithItems(int n) {
        try {
            String string = ApiClient.get("/stock-in/" + n);
            Map<String, Object> map = SimpleJson.parseObject(string);
            StockIn stockIn = new StockIn();
            stockIn.setStockInId(SimpleJson.getInt(map, "stockInId", 0));
            stockIn.setBillNo(SimpleJson.getString(map, "billNo", ""));
            stockIn.setStockDate(SimpleJson.getLocalDate(map, "stockDate"));
            stockIn.setTotalAmount(SimpleJson.getBigDecimal(map, "totalAmount", BigDecimal.ZERO));
            stockIn.setRemarks(SimpleJson.getString(map, "remarks", ""));
            Object object = map.get("items");
            if (object instanceof List) {
                for (Object e : (List)object) {
                    if (!(e instanceof Map)) continue;
                    Map map2 = (Map)e;
                    StockInItem stockInItem = new StockInItem();
                    stockInItem.setItemId(SimpleJson.getInt(map2, "itemId", 0));
                    stockInItem.setItemName(SimpleJson.getString(map2, "itemName", ""));
                    stockInItem.setQuantity(SimpleJson.getBigDecimal(map2, "quantity", BigDecimal.ZERO));
                    stockInItem.setUnitPrice(SimpleJson.getBigDecimal(map2, "unitPrice", BigDecimal.ZERO));
                    stockInItem.setLineTotal(SimpleJson.getBigDecimal(map2, "lineTotal", BigDecimal.ZERO));
                    stockIn.addItem(stockInItem);
                }
            }
            return stockIn;
        }
        catch (Exception exception) {
            System.err.println("Warning: Failed to load bill " + n + ": " + exception.getMessage());
            return null;
        }
    }
}

