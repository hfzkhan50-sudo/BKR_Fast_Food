package util;

import java.awt.Component;
import model.Order;
import model.StockIn;

public final class BillPrinter {
    private BillPrinter() {
    }

    public static void showOrderBill(Component parent, Order order) {
        BillPrinterPatch.showOrderBill(parent, order);
    }

    public static void showStockInBill(Component parent, StockIn stockIn) {
        BillPrinterPatch.showStockInBill(parent, stockIn);
    }

    public static void showReportText(Component parent, String title, String text) {
        BillPrinterPatch.showReportText(parent, title, text);
    }
}
