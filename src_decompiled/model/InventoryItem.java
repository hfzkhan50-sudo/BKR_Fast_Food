/*
 * Decompiled with CFR 0.152.
 */
package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InventoryItem {
    private int itemId;
    private String itemName;
    private BigDecimal quantity;
    private BigDecimal reorderLevel;
    private BigDecimal lastUnitPrice;
    private LocalDateTime updatedAt;

    public InventoryItem() {
    }

    public InventoryItem(int n, String string, BigDecimal bigDecimal, BigDecimal bigDecimal2, BigDecimal bigDecimal3) {
        this.itemId = n;
        this.itemName = string;
        this.quantity = bigDecimal;
        this.reorderLevel = bigDecimal2;
        this.lastUnitPrice = bigDecimal3;
    }

    public boolean isLowStock() {
        return this.quantity != null && this.reorderLevel != null && this.quantity.compareTo(this.reorderLevel) <= 0;
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

    public BigDecimal getReorderLevel() {
        return this.reorderLevel;
    }

    public void setReorderLevel(BigDecimal bigDecimal) {
        this.reorderLevel = bigDecimal;
    }

    public BigDecimal getLastUnitPrice() {
        return this.lastUnitPrice;
    }

    public void setLastUnitPrice(BigDecimal bigDecimal) {
        this.lastUnitPrice = bigDecimal;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(LocalDateTime localDateTime) {
        this.updatedAt = localDateTime;
    }
}

