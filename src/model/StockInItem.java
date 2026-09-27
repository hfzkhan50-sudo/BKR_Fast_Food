/*
 * Decompiled with CFR 0.152.
 */
package model;

import java.math.BigDecimal;

public class StockInItem {
    private int stockInItemId;
    private int itemId;
    private String itemName;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;

    public StockInItem() {
    }

    public StockInItem(int n, String string, BigDecimal bigDecimal, BigDecimal bigDecimal2) {
        this.itemId = n;
        this.itemName = string;
        this.quantity = bigDecimal;
        this.unitPrice = bigDecimal2;
        this.lineTotal = bigDecimal.multiply(bigDecimal2);
    }

    public int getStockInItemId() {
        return this.stockInItemId;
    }

    public void setStockInItemId(int n) {
        this.stockInItemId = n;
    }

    public int getItemId() {
        return this.itemId;
    }

    public void setItemId(int n) {
        this.itemId = n;
    }

    public String getItemName() {
        return this.itemName;
    }

    public void setItemName(String string) {
        this.itemName = string;
    }

    public BigDecimal getQuantity() {
        return this.quantity;
    }

    public void setQuantity(BigDecimal bigDecimal) {
        this.quantity = bigDecimal;
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

