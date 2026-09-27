/*
 * Decompiled with CFR 0.152.
 */
package ui;

import dao.OrderDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import model.Order;
import model.OrderItem;
import util.BillPrinter;
import util.UIHelper;

public class SalesReportPanel
extends JPanel {
    private final OrderDAO orderDAO = new OrderDAO();
    private JTextField fromField;
    private JTextField toField;
    private JTextArea resultArea;
    private JLabel summaryCountLabel;
    private JLabel summaryTotalLabel;
    private JLabel summaryDeliveryLabel;
    private JLabel summaryServiceLabel;
    private JLabel summaryCashLabel;
    private JLabel summaryOnlineLabel;

    public SalesReportPanel() {
        this.setLayout(new BorderLayout(12, 12));
        this.setBackground(UIHelper.DARK_BG);
        this.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        this.add((Component)this.buildControls(), "North");
        this.add((Component)this.buildResultArea(), "Center");
        this.add((Component)this.buildBottomSummaryBar(), "South");
        this.generateReport(LocalDate.now(), LocalDate.now());
    }

    private JComponent buildControls() {
        JPanel jPanel = new JPanel(new FlowLayout(0, 12, 8));
        jPanel.setBackground(UIHelper.PANEL_BG);
        jPanel.setBorder(UIHelper.createCustomTitledBorder("Sales Report Filter"));

        Font font = new Font("SansSerif", 1, 14);
        Font font2 = new Font("SansSerif", 0, 14);

        JButton jButton = UIHelper.createButton("Today's Sales", UIHelper.BKR_RED, Color.WHITE, 14);
        jButton.setPreferredSize(new Dimension(140, 36));
        jButton.addActionListener(actionEvent -> this.generateReport(LocalDate.now(), LocalDate.now()));

        this.fromField = new JTextField(LocalDate.now().toString(), 10);
        UIHelper.styleTextField(this.fromField);
        this.fromField.setPreferredSize(new Dimension(110, 34));

        this.toField = new JTextField(LocalDate.now().toString(), 10);
        UIHelper.styleTextField(this.toField);
        this.toField.setPreferredSize(new Dimension(110, 34));

        JButton jButton2 = UIHelper.createButton("Filter Date Range", UIHelper.BKR_GOLD, Color.BLACK, 14);
        jButton2.setPreferredSize(new Dimension(160, 36));
        jButton2.addActionListener(actionEvent -> {
            try {
                LocalDate localDate = LocalDate.parse(this.fromField.getText().trim());
                LocalDate localDate2 = LocalDate.parse(this.toField.getText().trim());
                this.generateReport(localDate, localDate2);
            }
            catch (Exception exception) {
                JOptionPane.showMessageDialog(this, "Enter dates in format YYYY-MM-DD.", "Invalid Date", 0);
            }
        });

        JButton jButton3 = UIHelper.createButton("Print Report", UIHelper.BKR_RED_DARK, Color.WHITE, 14);
        jButton3.setPreferredSize(new Dimension(140, 36));
        jButton3.addActionListener(actionEvent -> BillPrinter.showReportText(this, "Sales Report", this.resultArea.getText()));

        JLabel jLabel = new JLabel("From (YYYY-MM-DD):");
        jLabel.setFont(font);
        jLabel.setForeground(UIHelper.TEXT_WHITE);

        JLabel jLabel2 = new JLabel("To:");
        jLabel2.setFont(font);
        jLabel2.setForeground(UIHelper.TEXT_WHITE);

        jPanel.add(jButton);
        jPanel.add(jLabel);
        jPanel.add(this.fromField);
        jPanel.add(jLabel2);
        jPanel.add(this.toField);
        jPanel.add(jButton2);
        jPanel.add(jButton3);
        return jPanel;
    }

    private JComponent buildResultArea() {
        this.resultArea = new JTextArea();
        this.resultArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        this.resultArea.setBackground(UIHelper.PANEL_BG);
        this.resultArea.setForeground(UIHelper.BKR_GOLD_BRIGHT);
        this.resultArea.setCaretColor(UIHelper.BKR_GOLD);
        this.resultArea.setEditable(false);
        this.resultArea.setMargin(new Insets(12, 16, 12, 16));

        JScrollPane pane = new JScrollPane(this.resultArea);
        UIHelper.styleScrollPane(pane);
        return pane;
    }

    private JComponent buildBottomSummaryBar() {
        JPanel jPanel = new JPanel(new GridLayout(2, 3, 12, 6));
        jPanel.setBackground(UIHelper.PANEL_BG);
        jPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, UIHelper.BORDER_DARK),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        this.summaryCountLabel = new JLabel("Total Orders: 0");
        this.summaryCountLabel.setFont(new Font("SansSerif", 1, 16));
        this.summaryCountLabel.setForeground(UIHelper.TEXT_WHITE);

        this.summaryTotalLabel = new JLabel("Final Total Sales: Rs. 0.00");
        this.summaryTotalLabel.setFont(new Font("SansSerif", 1, 20));
        this.summaryTotalLabel.setForeground(UIHelper.BKR_GOLD_BRIGHT);

        this.summaryDeliveryLabel = new JLabel("Delivery Charges: Rs. 0.00");
        this.summaryDeliveryLabel.setFont(new Font("SansSerif", 1, 16));
        this.summaryDeliveryLabel.setForeground(UIHelper.TEXT_MUTED);

        this.summaryServiceLabel = new JLabel("Service Charges: Rs. 0.00");
        this.summaryServiceLabel.setFont(new Font("SansSerif", 1, 16));
        this.summaryServiceLabel.setForeground(UIHelper.TEXT_MUTED);
        this.summaryCashLabel = new JLabel("Cash: Rs. 0.00");
        this.summaryCashLabel.setFont(new Font("SansSerif", 1, 16));
        this.summaryCashLabel.setForeground(UIHelper.TEXT_MUTED);
        this.summaryOnlineLabel = new JLabel("Online: Rs. 0.00");
        this.summaryOnlineLabel.setFont(new Font("SansSerif", 1, 16));
        this.summaryOnlineLabel.setForeground(UIHelper.TEXT_MUTED);

        UIHelper.styleSummaryCard(this.summaryCountLabel, UIHelper.BKR_RED);
        UIHelper.styleSummaryCard(this.summaryTotalLabel, UIHelper.BKR_GOLD);
        UIHelper.styleSummaryCard(this.summaryDeliveryLabel, UIHelper.BKR_GOLD);
        UIHelper.styleSummaryCard(this.summaryServiceLabel, UIHelper.BKR_GOLD);
        UIHelper.styleSummaryCard(this.summaryCashLabel, new Color(60, 130, 70));
        UIHelper.styleSummaryCard(this.summaryOnlineLabel, new Color(40, 100, 180));
        this.summaryCountLabel.setForeground(new Color(50, 50, 50));
        this.summaryTotalLabel = new JLabel("Final Total Sales: Rs. 0.00");
        this.summaryTotalLabel.setFont(new Font("SansSerif", 1, 20));
        this.summaryTotalLabel.setForeground(new Color(27, 94, 32));
        this.summaryDeliveryLabel = new JLabel("Delivery Charges: Rs. 0.00");
        this.summaryDeliveryLabel.setFont(new Font("SansSerif", 1, 16));
        this.summaryDeliveryLabel.setForeground(new Color(50, 50, 50));
        this.summaryServiceLabel = new JLabel("Service Charges: Rs. 0.00");
        this.summaryServiceLabel.setFont(new Font("SansSerif", 1, 16));
        this.summaryServiceLabel.setForeground(new Color(50, 50, 50));
        this.summaryCashLabel = new JLabel("Cash: Rs. 0.00");
        this.summaryCashLabel.setFont(new Font("SansSerif", 1, 16));
        this.summaryCashLabel.setForeground(new Color(50, 50, 50));
        this.summaryOnlineLabel = new JLabel("Online: Rs. 0.00");
        this.summaryOnlineLabel.setFont(new Font("SansSerif", 1, 16));
        this.summaryOnlineLabel.setForeground(new Color(50, 50, 50));
        jPanel.add(this.summaryCountLabel);
        jPanel.add(this.summaryDeliveryLabel);
        jPanel.add(this.summaryServiceLabel);
        jPanel.add(this.summaryCashLabel);
        jPanel.add(this.summaryOnlineLabel);
        jPanel.add(this.summaryTotalLabel);
        return jPanel;
    }

    private void generateReport(LocalDate localDate, LocalDate localDate2) {
        this.fromField.setText(localDate.toString());
        this.toField.setText(localDate2.toString());
        StringBuilder stringBuilder = new StringBuilder();
        String string = "======================================================================\n";
        String string2 = "----------------------------------------------------------------------\n";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter dateTimeFormatter2 = DateTimeFormatter.ofPattern("hh:mm a");
        stringBuilder.append(string);
        stringBuilder.append("                      BKR FAST FOOD - SALES REPORT\n");
        stringBuilder.append(string);
        if (localDate.equals(localDate2)) {
            stringBuilder.append("Report Date   : ").append(localDate.format(dateTimeFormatter)).append("\n");
        } else {
            stringBuilder.append("Report Period : ").append(localDate.format(dateTimeFormatter)).append(" to ").append(localDate2.format(dateTimeFormatter)).append("\n");
        }
        stringBuilder.append(string).append("\n");
        BigDecimal bigDecimal = BigDecimal.ZERO;
        BigDecimal bigDecimal2 = BigDecimal.ZERO;
        BigDecimal bigDecimal3 = BigDecimal.ZERO;
        BigDecimal cashTotal = BigDecimal.ZERO;
        BigDecimal onlineTotal = BigDecimal.ZERO;
        BigDecimal cardTotal = BigDecimal.ZERO;
        int n = 0;
        LocalDate localDate3 = localDate;
        while (!localDate3.isAfter(localDate2)) {
            List<Order> list = this.orderDAO.getOrdersBetweenWithItems(localDate3, localDate3);
            if (!list.isEmpty()) {
                for (Order order : list) {
                    if (order == null) continue;
                    ++n;
                    if ("Completed".equalsIgnoreCase(order.getStatus())) {
                        bigDecimal = bigDecimal.add(order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO);
                        bigDecimal2 = bigDecimal2.add(order.getDeliveryCharge() != null ? order.getDeliveryCharge() : BigDecimal.ZERO);
                        bigDecimal3 = bigDecimal3.add(order.getServiceCharge() != null ? order.getServiceCharge() : BigDecimal.ZERO);
                        BigDecimal orderTotal = order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO;
                        if ("Online".equalsIgnoreCase(order.getPaymentType())) {
                            onlineTotal = onlineTotal.add(orderTotal);
                        } else if ("Card".equalsIgnoreCase(order.getPaymentType())) {
                            cardTotal = cardTotal.add(orderTotal);
                        } else {
                            cashTotal = cashTotal.add(orderTotal);
                        }
                    }
                    stringBuilder.append(string2);
                    stringBuilder.append("ORDER # ").append(order.getOrderNo()).append("\n");
                    stringBuilder.append(string2);
                    stringBuilder.append("Date     : ").append(order.getOrderDate() != null ? order.getOrderDate().format(dateTimeFormatter) : "").append("\n");
                    stringBuilder.append("Time     : ").append(order.getOrderTime() != null ? order.getOrderTime().format(dateTimeFormatter2) : "").append("\n");
                    stringBuilder.append("Type     : ").append(order.getOrderType()).append("\n");
                    stringBuilder.append("Payment  : ").append(order.getPaymentType()).append("\n");
                    stringBuilder.append("Items Ordered :\n");
                    int n2 = 1;
                    if (order.getItems() != null) {
                        for (OrderItem orderItem : order.getItems()) {
                            stringBuilder.append(String.format("  %d. %-24s  Qty: %-3d  Total: Rs. %s%n", n2++, orderItem.getMenuItemName(), orderItem.getQuantity(), orderItem.getLineTotal() != null ? orderItem.getLineTotal().toPlainString() : "0"));
                        }
                    }
                    stringBuilder.append("Subtotal : Rs. ").append(order.getSubtotal() != null ? order.getSubtotal().toPlainString() : "0").append("\n");
                    if (order.getDiscount() != null && order.getDiscount().signum() > 0) {
                        stringBuilder.append("Discount : Rs. -").append(order.getDiscount().toPlainString()).append("\n");
                    }
                    if (order.getDeliveryCharge() != null && order.getDeliveryCharge().signum() > 0) {
                        stringBuilder.append("Delivery Charge : Rs. ").append(order.getDeliveryCharge().toPlainString()).append("\n");
                    }
                    if (order.getServiceCharge() != null && order.getServiceCharge().signum() > 0) {
                        stringBuilder.append("Service Charge").append((String)(order.getServiceChargePercent() != null && order.getServiceChargePercent().signum() > 0 ? " (" + order.getServiceChargePercent().toPlainString() + "%)" : "")).append(" : Rs. ").append(order.getServiceCharge().toPlainString()).append("\n");
                    }
                    stringBuilder.append("Order Total : Rs. ").append(order.getTotalAmount() != null ? order.getTotalAmount().toPlainString() : "0").append("\n");
                    stringBuilder.append(string2).append("\n");
                }
            }
            localDate3 = localDate3.plusDays(1L);
        }
        if (n == 0) {
            stringBuilder.append("No orders found for this selected date/period.\n\n");
        }
        stringBuilder.append(string);
        stringBuilder.append("                         FINAL SALES SUMMARY\n");
        stringBuilder.append(string);
        stringBuilder.append(String.format("Total Orders Placed : %d%n", n));
        stringBuilder.append(String.format("FINAL TOTAL SALES   : Rs. %s%n", bigDecimal.toPlainString()));
        stringBuilder.append(String.format("DELIVERY CHARGES   : Rs. %s%n", bigDecimal2.toPlainString()));
        stringBuilder.append(String.format("SERVICE CHARGES    : Rs. %s%n", bigDecimal3.toPlainString()));
        stringBuilder.append(String.format("CASH SALES         : Rs. %s%n", cashTotal.toPlainString()));
        stringBuilder.append(String.format("ONLINE SALES       : Rs. %s%n", onlineTotal.toPlainString()));
        stringBuilder.append(String.format("CARD SALES         : Rs. %s%n", cardTotal.toPlainString()));
        stringBuilder.append(string);
        this.resultArea.setText(stringBuilder.toString());
        this.resultArea.setCaretPosition(0);
        this.summaryCountLabel.setText("Total Orders: " + n);
        this.summaryDeliveryLabel.setText("Delivery Charges: Rs. " + bigDecimal2.toPlainString());
        this.summaryServiceLabel.setText("Service Charges: Rs. " + bigDecimal3.toPlainString());
        this.summaryCashLabel.setText("Cash: Rs. " + cashTotal.toPlainString());
        this.summaryOnlineLabel.setText("Online: Rs. " + onlineTotal.toPlainString());
        this.summaryTotalLabel.setText("Final Total Sales: Rs. " + bigDecimal.toPlainString());
    }

    public void refresh() {
        this.fromField.setText(LocalDate.now().toString());
        this.toField.setText(LocalDate.now().toString());
        this.generateReport(LocalDate.now(), LocalDate.now());
    }
}

