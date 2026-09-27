/*
 * Decompiled with CFR 0.152.
 */
package util;

import java.awt.Component;
import model.Order;
import model.StockIn;
import util.BillPrinterPatch;

public final class BillPrinter {
    private BillPrinter() {
    }

    public static void showOrderBill(Component component, Order order) {
        BillPrinterPatch.showOrderBill(component, order);
    }

    public static void showStockInBill(Component component, StockIn stockIn) {
        BillPrinterPatch.showStockInBill(component, stockIn);
    }

    public static void showReportText(Component component, String string, String string2) {
        BillPrinterPatch.showReportText(component, string, string2);
    }
}

