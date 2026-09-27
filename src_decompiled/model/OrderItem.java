/*
 * Decompiled with CFR 0.152.
 */
package model;

import java.math.BigDecimal;

public class OrderItem {
    private int orderItemId;
    private int menuItemId;
    private String menuItemName;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;

    public OrderItem() {
    }

    public OrderItem(int n, String string, int n2, BigDecimal bigDecimal) {
        this.menuItemId = n;
        this.menuItemName = string;
        this.quantity = n2;
        this.unitPrice = bigDecimal;
        this.lineTotal = bigDecimal.multiply(BigDecimal.valueOf(n2));
    }

    public int getOrderItemId() {
        return this.orderItemId;
    }

    public void setOrderItemId(int n) {
        this.orderItemId = n;
    }

    public int getMenuItemId() {
        return this.menuItemId;
    }

    public void setMenuItemId(int n) {
        this.menuItemId = n;
    }

    public String getMenuItemName() {
        return this.menuItemName;
    }

    public void setMenuItemName(String string) {
        this.menuItemName = string;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void setQuantity(int n) {
        this.quantity = n;
    }

    public BigDecimal getUnitPrice() {
        return this.unitPrice;
    }

    public void setUnitPrice(BigDecimal bigDecimal) {
        this.unitPrice = bigDecimal;
    }

    public BigDecimal getLineTotal() {
        return this.lineTotal;
    }

    public void setLineTotal(BigDecimal bigDecimal) {
        this.lineTotal = bigDecimal;
    }
}

