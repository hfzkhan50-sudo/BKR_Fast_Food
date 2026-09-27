/*
 * Decompiled with CFR 0.152.
 */
package dao;

import api.ApiClient;
import api.SimpleJson;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import model.MenuItem;

public class MenuDAO {
    public List<MenuItem> getAllActiveMenuItems() {
        try {
            String string = ApiClient.get("/menu/active");
            return this.parseMenuList(string);
        }
        catch (Exception exception) {
            System.err.println("Warning: Failed to fetch active menu items: " + exception.getMessage());
            return new ArrayList<MenuItem>();
        }
    }

    public List<MenuItem> getAllMenuItems() {
        try {
            String string = ApiClient.get("/menu");
            return this.parseMenuList(string);
        }
        catch (Exception exception) {
            System.err.println("Warning: Failed to fetch menu items: " + exception.getMessage());
            return new ArrayList<MenuItem>();
        }
    }

    public int createMenuItem(String string, String string2, BigDecimal bigDecimal, String string3) {
        try {
            String string4 = string3 != null && !string3.isBlank() && !string3.equalsIgnoreCase("None") ? string3 : "";
            String string5 = String.format("{\"name\":\"%s\",\"category\":\"%s\",\"price\":%s,\"size\":\"%s\",\"active\":true}", SimpleJson.escape(string), SimpleJson.escape(string2), bigDecimal != null ? bigDecimal.toPlainString() : "0", SimpleJson.escape(string4));
            String string6 = ApiClient.post("/menu", string5);
            Map<String, Object> map = SimpleJson.parseObject(string6);
            return SimpleJson.getInt(map, "menuItemId", -1);
        }
        catch (Exception exception) {
            throw new RuntimeException("Failed to create menu item: " + exception.getMessage(), exception);
        }
    }

    public int createMenuItem(String string, String string2, BigDecimal bigDecimal) {
        return this.createMenuItem(string, string2, bigDecimal, null);
    }

    public void setActive(int n, boolean bl) {
        try {
            String string = "{\"active\":" + bl + "}";
            ApiClient.put("/menu/" + n + "/status", string);
        }
        catch (Exception exception) {
            throw new RuntimeException("Failed to update menu item status: " + exception.getMessage(), exception);
        }
    }

    public void updatePrice(int n, BigDecimal bigDecimal) {
        try {
            String string = String.format("{\"menuItemId\":%d,\"price\":%s}", n, bigDecimal != null ? bigDecimal.toPlainString() : "0");
            ApiClient.post("/menu", string);
        }
        catch (Exception exception) {
            throw new RuntimeException("Failed to update price: " + exception.getMessage(), exception);
        }
    }

    public void deleteMenuItem(int n) {
        try {
            ApiClient.delete("/menu/" + n);
        }
        catch (Exception exception) {
            throw new RuntimeException("Failed to delete menu item: " + exception.getMessage(), exception);
        }
    }

    public void clearAllMenuItems() {
        try {
            ApiClient.delete("/menu");
        }
        catch (Exception exception) {
            throw new RuntimeException("Failed to clear menu items: " + exception.getMessage(), exception);
        }
    }

    private List<MenuItem> parseMenuList(String string) {
        ArrayList<MenuItem> arrayList = new ArrayList<MenuItem>();
        List<Map<String, Object>> list = SimpleJson.parseArray(string);
        for (Map<String, Object> map : list) {
            int n = SimpleJson.getInt(map, "menuItemId", 0);
            String string2 = SimpleJson.getString(map, "name", "");
            String string3 = SimpleJson.getString(map, "category", "General");
            BigDecimal bigDecimal = SimpleJson.getBigDecimal(map, "price", BigDecimal.ZERO);
            boolean bl = SimpleJson.getBoolean(map, "active", true);
            String string4 = SimpleJson.getString(map, "size", "");
            arrayList.add(new MenuItem(n, string2, string3, bigDecimal, bl, string4));
        }
        return arrayList;
    }
}

