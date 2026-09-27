/*
 * Decompiled with CFR 0.152.
 */
package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.StockInItem;

public class StockIn {
    private int stockInId;
    private String billNo;
    private Integer supplierId;
    private String supplierName;
    private LocalDate stockDate;
    private BigDecimal totalAmount;
    private String remarks;
    private List<StockInItem> items = new ArrayList<StockInItem>();

    public int getStockInId() {
        return this.stockInId;
    }

    public void setStockInId(int n) {
        this.stockInId = n;
    }

    public String getBillNo() {
        return this.billNo;
    }

    public void setBillNo(String string) {
        this.billNo = string;
    }

    public Integer getSupplierId() {
        return this.supplierId;
    }

    public void setSupplierId(Integer n) {
        this.supplierId = n;
    }

    public String getSupplierName() {
        return this.supplierName;
    }

    public void setSupplierName(String string) {
        this.supplierName = string;
    }

    public LocalDate getStockDate() {
        return this.stockDate;
    }

    public void setStockDate(LocalDate localDate) {
        this.stockDate = localDate;
    }

    public BigDecimal getTotalAmount() {
        return this.totalAmount;
    }

    public void setTotalAmount(BigDecimal bigDecimal) {
        this.totalAmount = bigDecimal;
    }

    public String getRemarks() {
        return this.remarks;
    }

    public void setRemarks(String string) {
        this.remarks = string;
    }

    public List<StockInItem> getItems() {
        return this.items;
    }

    public void setItems(List<StockInItem> list) {
        this.items = list;
    }

    public void addItem(StockInItem stockInItem) {
        this.items.add(stockInItem);
    }
}

