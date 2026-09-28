package util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.print.PrinterException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.OrientationRequested;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import model.Order;
import model.StockIn;

final class BillPrinterPatch {
    private BillPrinterPatch() {
    }

    public static void showOrderBill(Component parent, Order order) {
        printHtml(parent, formatOrderHtml(order));
    }

    public static void showStockInBill(Component parent, StockIn stockIn) {
        printHtml(parent, formatStockHtml(stockIn));
    }

    public static void showReportText(Component parent, String title, String text) {
        JEditorPane editor = new JEditorPane("text/plain", text);
        editor.setEditable(false);
        editor.setCaretPosition(0);
        editor.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
        try {
            HashPrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
            attributes.add(new MediaPrintableArea(1, 1, 78, 270, MediaPrintableArea.MM));
            attributes.add(OrientationRequested.PORTRAIT);
            editor.print(null, null, false, null, attributes, false);
        } catch (PrinterException error) {
            JOptionPane.showMessageDialog(parent, "Print error: " + error.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void printHtml(Component parent, String html) {
        JEditorPane editor = new JEditorPane("text/html", html);
        editor.setEditable(false);
        editor.setCaretPosition(0);
        try {
            HashPrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
            attributes.add(new MediaPrintableArea(1, 1, 78, 270, MediaPrintableArea.MM));
            attributes.add(OrientationRequested.PORTRAIT);
            editor.print(null, null, false, null, attributes, false);
        } catch (PrinterException error) {
            JOptionPane.showMessageDialog(parent, "Print error: " + error.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String formatOrderHtml(Order order) {
        StringBuilder html = new StringBuilder("<html><body style='font-family:Arial;font-size:10pt;width:100%;'>");
        html.append("<div style='text-align:center;font-size:13pt;font-weight:bold;'>BKR BACHA KHAN RESTAURANT</div>");
        html.append("<div style='text-align:center;font-size:11pt;font-weight:bold;'>SALES RECEIPT</div><hr>");
        html.append("<b>Order #:</b> ").append(order.getOrderNo()).append("<br>");
        html.append("<b>Date:</b> ").append(order.getOrderDate()).append("<br>");
        html.append("<b>Time:</b> ").append(order.getOrderTime() == null ? "" : order.getOrderTime().format(DateTimeFormatter.ofPattern("hh:mm a"))).append("<br>");
        html.append("<b>Type:</b> ").append(order.getOrderType()).append("<br>");
        html.append("<b>Payment:</b> ").append(order.getPaymentType()).append("<hr>");
        html.append("<table width='100%'><tr><th align='left'>Item</th><th>Qty</th><th align='right'>Total</th></tr>");
        if (order.getItems() != null) {
            order.getItems().forEach(item -> html.append("<tr><td>").append(item.getMenuItemName()).append("</td><td align='center'>")
                .append(item.getQuantity()).append("</td><td align='right'>Rs.").append(money(item.getLineTotal())).append("</td></tr>"));
        }
        html.append("</table><hr><table width='100%'>");
        html.append("<tr><td>Subtotal:</td><td align='right'>Rs.").append(money(order.getSubtotal())).append("</td></tr>");
        if (positive(order.getDiscount())) html.append("<tr><td>Discount:</td><td align='right'>- Rs.").append(money(order.getDiscount())).append("</td></tr>");
        if (positive(order.getDeliveryCharge())) html.append("<tr><td>Delivery Charges:</td><td align='right'>Rs.").append(money(order.getDeliveryCharge())).append("</td></tr>");
        if (positive(order.getServiceCharge())) html.append("<tr><td>Service Charges (").append(money(order.getServiceChargePercent())).append("%):</td><td align='right'>Rs.").append(money(order.getServiceCharge())).append("</td></tr>");
        html.append("<tr style='font-size:11pt;font-weight:bold;'><td>TOTAL:</td><td align='right'>Rs.").append(money(order.getTotalAmount())).append("</td></tr></table><hr>");
        html.append("<div style='text-align:center;'>Thank you for visiting BKR Fast Food!<br>ADDRESS: MAIN GT ROAD, ESSORI STOP, NEAR ARMY PUBLIC SCHOOL<br><b>CONTACT NUMBER: 0333-6250944</b></div></body></html>");
        return html.toString();
    }

    private static String formatStockHtml(StockIn stockIn) {
        return "<html><body><b>STOCK PURCHASE RECEIPT</b><br>Bill No: " + stockIn.getBillNo() + "<br>Total: Rs." + money(stockIn.getTotalAmount()) + "</body></html>";
    }

    private static boolean positive(BigDecimal value) {
        return value != null && value.signum() > 0;
    }

    private static String money(BigDecimal value) {
        return value == null ? "0" : value.toPlainString();
    }
}