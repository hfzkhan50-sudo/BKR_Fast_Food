/*
 * Decompiled with CFR 0.152.
 */
package dao;

import api.ApiClient;
import api.SimpleJson;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import model.InventoryItem;

public class InventoryDAO {
    public List<InventoryItem> getAllItems() {
        try {
            String string = ApiClient.get("/inventory");
            return this.parseItemList(string);
        }
        catch (Exception exception) {
            System.err.println("Warning: Failed to fetch inventory: " + exception.getMessage());
            return new ArrayList<InventoryItem>();
        }
    }

    public List<InventoryItem> searchItems(String string) {
        try {
            String string2 = URLEncoder.encode(string, StandardCharsets.UTF_8);
            String string3 = ApiClient.get("/inventory/search?q=" + string2);
            return this.parseItemList(string3);
        }
        catch (Exception exception) {
            throw new RuntimeException("Search failed: " + exception.getMessage(), exception);
        }
    }

    public InventoryItem findByName(String string) {
        if (string == null || string.isBlank()) {
            return null;
        }
        List<InventoryItem> list = this.getAllItems();
        for (InventoryItem inventoryItem : list) {
            if (inventoryItem.getItemName() == null || !inventoryItem.getItemName().equalsIgnoreCase(string.trim())) continue;
            return inventoryItem;
        }
        return null;
    }

    public int createItem(String string, BigDecimal bigDecimal) {
        try {
            String string2 = String.format("{\"itemName\":\"%s\",\"quantity\":0,\"reorderLevel\":%s,\"lastUnitPrice\":0}", SimpleJson.escape(string), bigDecimal != null ? bigDecimal.toPlainString() : "5");
            String string3 = ApiClient.post("/inventory", string2);
            Map<String, Object> map = SimpleJson.parseObject(string3);
            return SimpleJson.getInt(map, "itemId", -1);
        }
        catch (Exception exception) {
            throw new RuntimeException("Failed to create item: " + exception.getMessage(), exception);
        }
    }

    public List<InventoryItem> getLowStockItems() {
        try {
            String string = ApiClient.get("/inventory/low-stock");
            return this.parseItemList(string);
        }
        catch (Exception exception) {
            System.err.println("Warning: Failed to fetch low stock items: " + exception.getMessage());
            return new ArrayList<InventoryItem>();
        }
    }

    public void deleteItem(int n) {
        try {
            ApiClient.delete("/inventory/" + n);
        }
        catch (Exception exception) {
            throw new RuntimeException("Failed to delete inventory item: " + exception.getMessage(), exception);
        }
    }

    public void clearAllItems() {
        try {
            ApiClient.delete("/inventory");
        }
        catch (Exception exception) {
            throw new RuntimeException("Failed to clear inventory: " + exception.getMessage(), exception);
        }
    }

    private List<InventoryItem> parseItemList(String string) {
        ArrayList<InventoryItem> arrayList = new ArrayList<InventoryItem>();
        List<Map<String, Object>> list = SimpleJson.parseArray(string);
        for (Map<String, Object> map : list) {
            InventoryItem inventoryItem = new InventoryItem();
            inventoryItem.setItemId(SimpleJson.getInt(map, "itemId", 0));
            inventoryItem.setItemName(SimpleJson.getString(map, "itemName", ""));
            inventoryItem.setQuantity(SimpleJson.getBigDecimal(map, "quantity", BigDecimal.ZERO));
            inventoryItem.setReorderLevel(SimpleJson.getBigDecimal(map, "reorderLevel", BigDecimal.ZERO));
            inventoryItem.setLastUnitPrice(SimpleJson.getBigDecimal(map, "lastUnitPrice", BigDecimal.ZERO));
            arrayList.add(inventoryItem);
        }
        return arrayList;
    }
}

