/*
 * Decompiled with CFR 0.152.
 */
package model;

import java.math.BigDecimal;

public class MenuItem {
    private int menuItemId;
    private String name;
    private String category;
    private BigDecimal price;
    private boolean active;
    private String size;

    public MenuItem() {
    }

    public MenuItem(int n, String string, String string2, BigDecimal bigDecimal, boolean bl) {
        this.menuItemId = n;
        this.name = string;
        this.category = string2;
        this.price = bigDecimal;
        this.active = bl;
        this.size = null;
    }

    public MenuItem(int n, String string, String string2, BigDecimal bigDecimal, boolean bl, String string3) {
        this.menuItemId = n;
        this.name = string;
        this.category = string2;
        this.price = bigDecimal;
        this.active = bl;
        this.size = string3;
    }

    public String getDisplayName() {
        if (this.size != null && !this.size.isBlank() && !this.size.equalsIgnoreCase("None")) {
            return this.name + " (" + this.size + ")";
        }
        return this.name;
    }

    public boolean hasSizeVariant() {
        return this.size != null && !this.size.isBlank() && !this.size.equalsIgnoreCase("None");
    }

    public String toString() {
        return this.name + " - Rs. " + this.price.toPlainString();
    }

    public int getMenuItemId() {
        return this.menuItemId;
    }

    public void setMenuItemId(int n) {
        this.menuItemId = n;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String string) {
        this.name = string;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String string) {
        this.category = string;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public void setPrice(BigDecimal bigDecimal) {
        this.price = bigDecimal;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean bl) {
        this.active = bl;
    }

    public String getSize() {
        return this.size;
    }

    public void setSize(String string) {
        this.size = string;
    }
}

