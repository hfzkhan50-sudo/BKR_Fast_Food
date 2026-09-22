/*
 * Decompiled with CFR 0.152.
 */
package ui;

import dao.MenuDAO;
import dao.OrderDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import model.MenuItem;
import model.Order;
import model.OrderItem;
import util.BillPrinter;
import util.UIHelper;

public class OrderHistoryPanel
extends JPanel {
    private final OrderDAO orderDAO = new OrderDAO();
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField fromField;
    private JTextField toField;
    private JLabel totalLabel;
    private boolean cancelledView;

    public OrderHistoryPanel() {
        this.setLayout(new BorderLayout(12, 12));
        this.setBackground(UIHelper.DARK_BG);
        this.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        this.add((Component)this.buildFilterBar(), "North");
        this.add((Component)this.buildTable(), "Center");
        this.add((Component)this.buildActionBar(), "South");
        this.refresh();
    }

    private JComponent buildFilterBar() {
        JPanel jPanel = new JPanel(new FlowLayout(0, 12, 8));
        jPanel.setBackground(UIHelper.PANEL_BG);
        jPanel.setBorder(UIHelper.createCustomTitledBorder("Filter Orders"));

        Font font = new Font("SansSerif", 1, 14);
        Font font2 = new Font("SansSerif", 0, 14);

        JButton jButton = UIHelper.createButton("Today's Orders", UIHelper.BKR_RED, Color.WHITE, 14);
        jButton.setPreferredSize(new Dimension(160, 36));
        jButton.addActionListener(actionEvent -> {
            this.cancelledView = false;
            this.fromField.setText(LocalDate.now().toString());
            this.toField.setText(LocalDate.now().toString());
            this.loadOrders(LocalDate.now(), LocalDate.now());
        });

        JButton jButton2 = UIHelper.createButton("Last 30 Days", new Color(60, 60, 75), Color.WHITE, 14);
        jButton2.setPreferredSize(new Dimension(150, 36));
        jButton2.addActionListener(actionEvent -> {
            this.cancelledView = false;
            LocalDate localDate = LocalDate.now().minusDays(30L);
            this.fromField.setText(localDate.toString());
            this.toField.setText(LocalDate.now().toString());
            this.loadOrders(localDate, LocalDate.now());
        });

        JButton jButton3 = UIHelper.createButton("All Orders", new Color(80, 80, 100), Color.WHITE, 14);
        jButton3.setPreferredSize(new Dimension(130, 36));
        jButton3.addActionListener(actionEvent -> {
            this.cancelledView = false;
            LocalDate localDate = LocalDate.of(2020, 1, 1);
            this.fromField.setText(localDate.toString());
            this.toField.setText(LocalDate.now().toString());
            this.loadOrders(localDate, LocalDate.now());
        });

        JButton cancelledButton = UIHelper.createButton("Cancelled Orders", new Color(120, 55, 55), Color.WHITE, 14);
        cancelledButton.setPreferredSize(new Dimension(170, 36));
        cancelledButton.addActionListener(actionEvent -> {
            this.cancelledView = true;
            this.loadOrders(this.currentFromDate(), this.currentToDate());
        });

        this.fromField = new JTextField(LocalDate.now().toString(), 10);
        UIHelper.styleTextField(this.fromField);
        this.fromField.setPreferredSize(new Dimension(110, 34));

        this.toField = new JTextField(LocalDate.now().toString(), 10);
        UIHelper.styleTextField(this.toField);
        this.toField.setPreferredSize(new Dimension(110, 34));

        JButton jButton4 = UIHelper.createButton("Filter Date Range", UIHelper.BKR_GOLD, Color.BLACK, 14);
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
        jLabel.setForeground(UIHelper.TEXT_WHITE);

        JLabel jLabel2 = new JLabel("To:");
        jLabel2.setFont(font);
        jLabel2.setForeground(UIHelper.TEXT_WHITE);

        jPanel.add(jButton);
        jPanel.add(jButton2);
        jPanel.add(jButton3);
        jPanel.add(cancelledButton);
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
        UIHelper.styleTable(this.table);

        DefaultTableCellRenderer defaultTableCellRenderer = new DefaultTableCellRenderer(){

            @Override
            public Component getTableCellRendererComponent(JTable jTable, Object object, boolean bl, boolean bl2, int n, int n2) {
                Component component = super.getTableCellRendererComponent(jTable, object, bl, bl2, n, n2);
                this.setHorizontalAlignment(4);
                this.setFont(this.getFont().deriveFont(Font.BOLD));
                if (!bl) {
                    this.setForeground(UIHelper.BKR_GOLD_BRIGHT);
                }
                return component;
            }
        };
        this.table.getColumnModel().getColumn(6).setCellRenderer(defaultTableCellRenderer);

        JPanel jPanel = new JPanel(new BorderLayout());
        jPanel.setBackground(UIHelper.PANEL_BG);
        jPanel.setBorder(UIHelper.createCustomTitledBorder("Orders List"));

        JScrollPane scrollPane = new JScrollPane(this.table);
        UIHelper.styleScrollPane(scrollPane);
        jPanel.add((Component)scrollPane, "Center");
        return jPanel;
    }

    private JComponent buildActionBar() {
        JPanel jPanel = new JPanel(new FlowLayout(2, 12, 8));
        jPanel.setBackground(UIHelper.DARK_BG);

        JButton jButton = UIHelper.createButton("View / Reprint Selected Order", UIHelper.BKR_RED, Color.WHITE, 14);
        jButton.setPreferredSize(new Dimension(270, 38));
        jButton.addActionListener(actionEvent -> this.viewSelected());
        jPanel.add(jButton);

        JButton addButton = UIHelper.createButton("Add to Selected Order", new Color(42, 104, 167), Color.WHITE, 14);
        addButton.setPreferredSize(new Dimension(190, 38));
        addButton.addActionListener(actionEvent -> this.addToSelectedOrder());
        jPanel.add(addButton);

        JButton cancelButton = UIHelper.createButton("Cancel Selected Order", new Color(120, 55, 55), Color.WHITE, 14);
        cancelButton.setPreferredSize(new Dimension(190, 38));
        cancelButton.addActionListener(actionEvent -> this.cancelSelected());
        jPanel.add(cancelButton);
        this.totalLabel = new JLabel("Sales Total: Rs. 0.00");
        this.totalLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        this.totalLabel.setForeground(UIHelper.BKR_GOLD_BRIGHT);
        jPanel.add(this.totalLabel);
        return jPanel;
    }

    public void refresh() {
        this.cancelledView = false;
        this.loadOrders(LocalDate.now(), LocalDate.now());
    }

    private void loadOrders(LocalDate localDate, LocalDate localDate2) {
        this.tableModel.setRowCount(0);
        List<Order> list = this.cancelledView ? this.orderDAO.getCancelledOrdersBetween(localDate, localDate2) : this.orderDAO.getOrdersBetween(localDate, localDate2);
        for (Order order : list) {
            this.tableModel.addRow(new Object[]{order.getOrderId(), order.getOrderNo(), order.getOrderDate(), order.getOrderTime(), order.getOrderType(), order.getPaymentType(), order.getTotalAmount()});
        }
        BigDecimal total = this.cancelledView ? this.orderDAO.getCancelledTotalBetween(localDate, localDate2) : list.stream().map(Order::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        this.totalLabel.setText((this.cancelledView ? "Cancelled Total: Rs. " : "Sales Total: Rs. ") + total.toPlainString());
    }

    private LocalDate currentFromDate() {
        try { return LocalDate.parse(this.fromField.getText().trim()); } catch (Exception exception) { return LocalDate.now(); }
    }

    private LocalDate currentToDate() {
        try { return LocalDate.parse(this.toField.getText().trim()); } catch (Exception exception) { return LocalDate.now(); }
    }

    private void cancelSelected() {
        if (this.cancelledView) {
            JOptionPane.showMessageDialog(this, "Cancelled orders cannot be cancelled again.", "Already Cancelled", 2);
            return;
        }
        int row = this.table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an order first.", "No Selection", 2);
            return;
        }
        int orderId = (Integer)this.tableModel.getValueAt(row, 0);
        int choice = JOptionPane.showConfirmDialog(this, "Move this order to Cancelled Orders? Its sales will be removed and tracked separately.", "Cancel Order", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) return;
        try {
            this.orderDAO.cancelOrder(orderId);
            this.loadOrders(this.currentFromDate(), this.currentToDate());
        }
        catch (Exception exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Cancellation Failed", 0);
        }
    }

    private void addToSelectedOrder() {
        int row = this.table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an order first.", "No Selection", 2);
            return;
        }

        int orderId = (Integer)this.tableModel.getValueAt(row, 0);
        List<MenuItem> menuItems = new MenuDAO().getAllActiveMenuItems();
        if (menuItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "There are no active menu items to add.", "No Menu Items", 2);
            return;
        }

        JPanel panel = new JPanel(new BorderLayout(10, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JComboBox<MenuItem> itemCombo = new JComboBox<>();
        for (MenuItem item : menuItems) {
            itemCombo.addItem(item);
        }
        itemCombo.setPreferredSize(new Dimension(260, 32));

        JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 50, 1));
        qtySpinner.setPreferredSize(new Dimension(80, 32));

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        form.add(new JLabel("Item:"));
        form.add(itemCombo);
        form.add(new JLabel("Qty:"));
        form.add(qtySpinner);

        panel.add(form, BorderLayout.CENTER);

        int choice = JOptionPane.showConfirmDialog(this, panel, "Add More Items to Order #" + orderId, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        MenuItem selectedItem = (MenuItem)itemCombo.getSelectedItem();
        if (selectedItem == null) {
            return;
        }
        int quantity = (Integer)qtySpinner.getValue();
        if (quantity <= 0) {
            JOptionPane.showMessageDialog(this, "Quantity must be greater than zero.", "Invalid Quantity", 2);
            return;
        }

        List<OrderItem> items = new ArrayList<OrderItem>();
        OrderItem item = new OrderItem(selectedItem.getMenuItemId(), selectedItem.getDisplayName(), quantity, selectedItem.getPrice());
        item.setLineTotal(selectedItem.getPrice().multiply(BigDecimal.valueOf(quantity)));
        items.add(item);

        try {
            this.orderDAO.addItemsToOrder(orderId, items);
            this.loadOrders(this.currentFromDate(), this.currentToDate());
            JOptionPane.showMessageDialog(this, quantity + " x " + selectedItem.getDisplayName() + " added to order #" + orderId, "Item Added", JOptionPane.INFORMATION_MESSAGE);
        }
        catch (Exception exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Add Failed", 0);
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

