/*
 * Decompiled with CFR 0.152.
 */
package util;

import java.awt.Component;
import java.awt.print.PrinterException;
import java.math.BigDecimal;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.OrientationRequested;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import model.Order;
import model.StockIn;

final class BillPrinterPatch {
    private BillPrinterPatch() {
    }

    public static void showOrderBill(Component component, Order order) {
        BillPrinterPatch.printHtml(component, BillPrinterPatch.formatOrderHtml(order));
    }

    public static void showStockInBill(Component component, StockIn stockIn) {
        BillPrinterPatch.printHtml(component, BillPrinterPatch.formatStockHtml(stockIn));
    }

    public static void showReportText(Component component, String string, String string2) {
        JOptionPane.showMessageDialog(component, new JScrollPane(new JTextArea(string2)), string, 1);
    }

    private static void printHtml(Component component, String string) {
        JEditorPane jEditorPane = new JEditorPane("text/html", string);
        jEditorPane.setEditable(false);
        jEditorPane.setCaretPosition(0);
        try {
            HashPrintRequestAttributeSet hashPrintRequestAttributeSet = new HashPrintRequestAttributeSet();
            hashPrintRequestAttributeSet.add(new MediaPrintableArea(1, 1, 78, 270, 1000));
            hashPrintRequestAttributeSet.add(OrientationRequested.PORTRAIT);
            jEditorPane.print(null, null, false, null, hashPrintRequestAttributeSet, false);
        }
        catch (PrinterException printerException) {
            JOptionPane.showMessageDialog(component, "Print error: " + printerException.getMessage(), "Print Error", 0);
        }
    }

    private static String formatOrderHtml(Order order) {
        StringBuilder stringBuilder = new StringBuilder("<html><body style='font-family:Arial;font-size:10pt;width:100%;'>");
        stringBuilder.append("<div style='text-align:center;font-size:13pt;font-weight:bold;'>BKR BACHA KHAN RESTAURANT</div>");
        stringBuilder.append("<div style='text-align:center;font-size:11pt;font-weight:bold;'>SALES RECEIPT</div><hr>");
        stringBuilder.append("<b>Order #:</b> ").append(order.getOrderNo()).append("<br>");
        stringBuilder.append("<b>Date:</b> ").append(order.getOrderDate()).append("<br>");
        stringBuilder.append("<b>Type:</b> ").append(order.getOrderType()).append("<br>");
        stringBuilder.append("<b>Payment:</b> ").append(order.getPaymentType()).append("<hr>");
        stringBuilder.append("<table width='100%'><tr><th align='left'>Item</th><th>Qty</th><th align='right'>Total</th></tr>");
        if (order.getItems() != null) {
            order.getItems().forEach(orderItem -> stringBuilder.append("<tr><td>").append(orderItem.getMenuItemName()).append("</td><td align='center'>").append(orderItem.getQuantity()).append("</td><td align='right'>Rs.").append(BillPrinterPatch.money(orderItem.getLineTotal())).append("</td></tr>"));
        }
        stringBuilder.append("</table><hr><table width='100%'>");
        stringBuilder.append("<tr><td>Subtotal:</td><td align='right'>Rs.").append(BillPrinterPatch.money(order.getSubtotal())).append("</td></tr>");
        if (BillPrinterPatch.positive(order.getDiscount())) {
            stringBuilder.append("<tr><td>Discount:</td><td align='right'>- Rs.").append(BillPrinterPatch.money(order.getDiscount())).append("</td></tr>");
        }
        if (BillPrinterPatch.positive(order.getDeliveryCharge())) {
            stringBuilder.append("<tr><td>Delivery Charges:</td><td align='right'>Rs.").append(BillPrinterPatch.money(order.getDeliveryCharge())).append("</td></tr>");
        }
        if (BillPrinterPatch.positive(order.getServiceCharge())) {
            stringBuilder.append("<tr><td>Service Charges (").append(BillPrinterPatch.money(order.getServiceChargePercent())).append("%):</td><td align='right'>Rs.").append(BillPrinterPatch.money(order.getServiceCharge())).append("</td></tr>");
        }
        stringBuilder.append("<tr style='font-size:11pt;font-weight:bold;'><td>TOTAL:</td><td align='right'>Rs.").append(BillPrinterPatch.money(order.getTotalAmount())).append("</td></tr></table><hr>");
        stringBuilder.append("<div style='text-align:center;'>Thank you for visiting BKR Fast Food!<br>ADDRESS: MAIN GT ROAD, ESSORI STOP, NEAR ARMY PUBLIC SCHOOL<br><b>CONTACT NUMBER: 0333-6250944</b></div></body></html>");
        return stringBuilder.toString();
    }

    private static String formatStockHtml(StockIn stockIn) {
        return "<html><body><b>STOCK PURCHASE RECEIPT</b><br>Bill No: " + stockIn.getBillNo() + "<br>Total: Rs." + BillPrinterPatch.money(stockIn.getTotalAmount()) + "</body></html>";
    }

    private static boolean positive(BigDecimal bigDecimal) {
        return bigDecimal != null && bigDecimal.signum() > 0;
    }

    private static String money(BigDecimal bigDecimal) {
        return bigDecimal == null ? "0" : bigDecimal.toPlainString();
    }
}

