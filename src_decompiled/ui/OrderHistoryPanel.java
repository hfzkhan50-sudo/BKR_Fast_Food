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
import java.time.LocalDate;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import model.Order;
import util.BillPrinter;
import util.UIHelper;

public class OrderHistoryPanel
extends JPanel {
    private final OrderDAO orderDAO = new OrderDAO();
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField fromField;
    private JTextField toField;

    public OrderHistoryPanel() {
        this.setLayout(new BorderLayout(12, 12));
        this.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        this.add((Component)this.buildFilterBar(), "North");
        this.add((Component)this.buildTable(), "Center");
        this.add((Component)this.buildActionBar(), "South");
        this.refresh();
    }

    private JComponent buildFilterBar() {
        JPanel jPanel = new JPanel(new FlowLayout(0, 12, 8));
        TitledBorder titledBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1), "Filter Orders", 1, 2, new Font("SansSerif", 1, 14), new Color(33, 33, 33));
        jPanel.setBorder(titledBorder);
        Font font = new Font("SansSerif", 1, 14);
        Font font2 = new Font("SansSerif", 0, 14);
        JButton jButton = UIHelper.createButton("Today's Orders", new Color(25, 118, 210), Color.WHITE, 14);
        jButton.setPreferredSize(new Dimension(160, 36));
        jButton.addActionListener(actionEvent -> {
            this.fromField.setText(LocalDate.now().toString());
            this.toField.setText(LocalDate.now().toString());
            this.loadOrders(LocalDate.now(), LocalDate.now());
        });
        JButton jButton2 = UIHelper.createButton("Last 30 Days", new Color(69, 90, 100), Color.WHITE, 14);
        jButton2.setPreferredSize(new Dimension(150, 36));
        jButton2.addActionListener(actionEvent -> {
            LocalDate localDate = LocalDate.now().minusDays(30L);
            this.fromField.setText(localDate.toString());
            this.toField.setText(LocalDate.now().toString());
            this.loadOrders(localDate, LocalDate.now());
        });
        JButton jButton3 = UIHelper.createButton("All Orders", new Color(120, 85, 137), Color.WHITE, 14);
        jButton3.setPreferredSize(new Dimension(130, 36));
        jButton3.addActionListener(actionEvent -> {
            LocalDate localDate = LocalDate.of(2020, 1, 1);
            this.fromField.setText(localDate.toString());
            this.toField.setText(LocalDate.now().toString());
            this.loadOrders(localDate, LocalDate.now());
        });
        this.fromField = new JTextField(LocalDate.now().toString(), 10);
        this.fromField.setFont(font2);
        this.fromField.setPreferredSize(new Dimension(110, 34));
        this.toField = new JTextField(LocalDate.now().toString(), 10);
        this.toField.setFont(font2);
        this.toField.setPreferredSize(new Dimension(110, 34));
        JButton jButton4 = UIHelper.createButton("Filter Date Range", new Color(0, 150, 136), Color.WHITE, 14);
        jButton4.setPreferredSize(new Dimension(170, 36));
        jButton4.addActionListener(actionEvent -> {
            try {
                LocalDate localDate = LocalDate.parse(this.fromField.getText().trim());
                LocalDate localDate2 = LocalDate.parse(this.toField.getText().trim());
                this.loadOrders(localDate, localDate2);
            }
            catch (Exception exception) {
                JOptionPane.showMessageDialog(this, "Enter dates in format YYYY-MM-DD.", "Invalid Date", 0);
            }
        });
        JLabel jLabel = new JLabel("From:");
        jLabel.setFont(font);
        JLabel jLabel2 = new JLabel("To:");
        jLabel2.setFont(font);
        jPanel.add(jButton);
        jPanel.add(jButton2);
        jPanel.add(jButton3);
        jPanel.add(jLabel);
        jPanel.add(this.fromField);
        jPanel.add(jLabel2);
        jPanel.add(this.toField);
        jPanel.add(jButton4);
        return jPanel;
    }

    private JComponent buildTable() {
        Object[] objectArray = new String[]{"Order ID", "Order No", "Date", "Time", "Type", "Payment", "Total (Rs.)"};
        this.tableModel = new DefaultTableModel(objectArray, 0){

            @Override
            public boolean isCellEditable(int n, int n2) {
                return false;
            }
        };
        this.table = new JTable(this.tableModel);
        this.table.setFont(new Font("SansSerif", 0, 14));
        this.table.getTableHeader().setFont(new Font("SansSerif", 1, 14));
        this.table.setRowHeight(30);
        DefaultTableCellRenderer defaultTableCellRenderer = new DefaultTableCellRenderer(){

            @Override
            public Component getTableCellRendererComponent(JTable jTable, Object object, boolean bl, boolean bl2, int n, int n2) {
                Component component = super.getTableCellRendererComponent(jTable, object, bl, bl2, n, n2);
                this.setHorizontalAlignment(4);
                this.setFont(this.getFont().deriveFont(1));
                if (!bl) {
                    this.setForeground(new Color(27, 94, 32));
                }
                return component;
            }
        };
        this.table.getColumnModel().getColumn(6).setCellRenderer(defaultTableCellRenderer);
        JPanel jPanel = new JPanel(new BorderLayout());
        TitledBorder titledBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1), "Orders List", 1, 2, new Font("SansSerif", 1, 14), new Color(33, 33, 33));
        jPanel.setBorder(titledBorder);
        jPanel.add((Component)new JScrollPane(this.table), "Center");
        return jPanel;
    }

    private JComponent buildActionBar() {
        JPanel jPanel = new JPanel(new FlowLayout(2, 12, 8));
        JButton jButton = UIHelper.createButton("View / Reprint Selected Order", new Color(46, 125, 50), Color.WHITE, 14);
        jButton.setPreferredSize(new Dimension(260, 38));
        jButton.addActionListener(actionEvent -> this.viewSelected());
        jPanel.add(jButton);
        return jPanel;
    }

    public void refresh() {
        this.loadOrders(LocalDate.now(), LocalDate.now());
    }

    private void loadOrders(LocalDate localDate, LocalDate localDate2) {
        this.tableModel.setRowCount(0);
        List<Order> list = this.orderDAO.getOrdersBetween(localDate, localDate2);
        for (Order order : list) {
            this.tableModel.addRow(new Object[]{order.getOrderId(), order.getOrderNo(), order.getOrderDate(), order.getOrderTime(), order.getOrderType(), order.getPaymentType(), order.getTotalAmount()});
        }
    }

    private void viewSelected() {
        int n = this.table.getSelectedRow();
        if (n < 0) {
            JOptionPane.showMessageDialog(this, "Select an order first.", "No Selection", 2);
            return;
        }
        int n2 = (Integer)this.tableModel.getValueAt(n, 0);
        Order order = this.orderDAO.getOrderWithItems(n2);
        BillPrinter.showOrderBill(this, order);
    }
}

