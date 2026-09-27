/*
 * Decompiled with CFR 0.152.
 */
package ui;

import dao.InventoryDAO;
import dao.StockInDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
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
import model.InventoryItem;
import model.StockIn;
import model.StockInItem;
import util.BillPrinter;
import util.UIHelper;

public class StockInPanel
extends JPanel {
    private final InventoryDAO inventoryDAO = new InventoryDAO();
    private final StockInDAO stockInDAO = new StockInDAO();
    private final Runnable onSaved;
    private JTextField itemNameField;
    private JTextField qtyField;
    private JTextField priceField;
    private DefaultTableModel lineTableModel;
    private JTable lineTable;
    private JLabel totalLabel;

    public StockInPanel(Runnable runnable) {
        this.onSaved = runnable;
        this.setLayout(new BorderLayout(12, 12));
        this.setBackground(UIHelper.DARK_BG);
        this.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        this.add((Component)this.buildEntryForm(), "North");
        this.add((Component)this.buildLineItemsTable(), "Center");
        this.add((Component)this.buildBottomBar(), "South");
    }

    private JComponent buildEntryForm() {
        JPanel jPanel = new JPanel(new FlowLayout(0, 14, 10));
        jPanel.setBackground(UIHelper.PANEL_BG);
        jPanel.setBorder(UIHelper.createCustomTitledBorder("Add Item to This Delivery"));

        Font font = new Font("SansSerif", 1, 14);
        Font font2 = new Font("SansSerif", 0, 14);

        this.itemNameField = new JTextField(16);
        UIHelper.styleTextField(this.itemNameField);
        this.itemNameField.setPreferredSize(new Dimension(180, 34));

        this.qtyField = new JTextField(8);
        UIHelper.styleTextField(this.qtyField);
        this.qtyField.setPreferredSize(new Dimension(100, 34));

        this.priceField = new JTextField(8);
        UIHelper.styleTextField(this.priceField);
        this.priceField.setPreferredSize(new Dimension(100, 34));

        JButton jButton = UIHelper.createButton("+ Add Line", UIHelper.BKR_RED, Color.WHITE, 14);
        jButton.setPreferredSize(new Dimension(130, 36));
        jButton.addActionListener(actionEvent -> this.addLineItem());

        JLabel jLabel = new JLabel("Item Name:");
        jLabel.setFont(font);
        jLabel.setForeground(UIHelper.TEXT_WHITE);

        JLabel jLabel2 = new JLabel("Quantity:");
        jLabel2.setFont(font);
        jLabel2.setForeground(UIHelper.TEXT_WHITE);

        JLabel jLabel3 = new JLabel("Price (Rs.):");
        jLabel3.setFont(font);
        jLabel3.setForeground(UIHelper.TEXT_WHITE);

        jPanel.add(jLabel);
        jPanel.add(this.itemNameField);
        jPanel.add(jLabel2);
        jPanel.add(this.qtyField);
        jPanel.add(jLabel3);
        jPanel.add(this.priceField);
        jPanel.add(jButton);

        JLabel jLabel4 = new JLabel(" Tip: if the item already exists in inventory, type its exact name to add to current stock.");
        jLabel4.setFont(new Font("SansSerif", Font.ITALIC, 12));
        jLabel4.setForeground(UIHelper.TEXT_MUTED);

        JPanel jPanel2 = new JPanel(new BorderLayout(4, 4));
        jPanel2.setBackground(UIHelper.DARK_BG);
        jPanel2.add((Component)jPanel, "Center");
        jPanel2.add((Component)jLabel4, "South");
        return jPanel2;
    }

    private JComponent buildLineItemsTable() {
        Object[] objectArray = new String[]{"Item Name", "Quantity", "Total (Rs.)"};
        this.lineTableModel = new DefaultTableModel(objectArray, 0){

            @Override
            public boolean isCellEditable(int n, int n2) {
                return false;
            }
        };
        this.lineTable = new JTable(this.lineTableModel);
        UIHelper.styleTable(this.lineTable);

        DefaultTableCellRenderer defaultTableCellRenderer = new DefaultTableCellRenderer();
        defaultTableCellRenderer.setHorizontalAlignment(4);
        this.lineTable.getColumnModel().getColumn(1).setCellRenderer(defaultTableCellRenderer);
        this.lineTable.getColumnModel().getColumn(2).setCellRenderer(defaultTableCellRenderer);

        JButton jButton = UIHelper.createButton("Remove Selected Line", UIHelper.BKR_RED, Color.WHITE, 13);
        jButton.setPreferredSize(new Dimension(200, 34));
        jButton.addActionListener(actionEvent -> {
            int n = this.lineTable.getSelectedRow();
            if (n >= 0) {
                this.lineTableModel.removeRow(n);
                this.recalcTotal();
            }
        });
        JPanel jPanel = new JPanel(new BorderLayout(8, 8));
        jPanel.setBackground(UIHelper.PANEL_BG);
        jPanel.setBorder(UIHelper.createCustomTitledBorder("Delivery Line Items"));

        JScrollPane scrollPane = new JScrollPane(this.lineTable);
        UIHelper.styleScrollPane(scrollPane);
        jPanel.add((Component)scrollPane, "Center");

        JPanel jPanel2 = new JPanel(new FlowLayout(2));
        jPanel2.setBackground(UIHelper.PANEL_BG);
        jPanel2.add(jButton);
        jPanel.add((Component)jPanel2, "South");
        return jPanel;
    }

    private JComponent buildBottomBar() {
        JPanel jPanel = new JPanel(new BorderLayout(15, 15));
        jPanel.setBackground(UIHelper.PANEL_BG);
        jPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, UIHelper.BORDER_DARK),
            BorderFactory.createEmptyBorder(10, 10, 5, 10)
        ));

        this.totalLabel = new JLabel("Total: Rs. 0.00");
        this.totalLabel.setFont(new Font("SansSerif", 1, 22));
        this.totalLabel.setForeground(UIHelper.BKR_GOLD_BRIGHT);

        JButton jButton = UIHelper.createButton("Save & Generate Bill", UIHelper.BKR_RED, Color.WHITE, 16);
        jButton.setPreferredSize(new Dimension(240, 48));
        jButton.addActionListener(actionEvent -> this.saveStockIn());

        jPanel.add((Component)this.totalLabel, "West");
        jPanel.add((Component)jButton, "East");
        return jPanel;
    }

    private void addLineItem() {
        String string = this.itemNameField.getText().trim();
        String string2 = this.qtyField.getText().trim();
        String string3 = this.priceField.getText().trim();
        if (string.isEmpty() || string2.isEmpty() || string3.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in Item Name, Quantity, and Price.", "Missing Information", 2);
            return;
        }
        try {
            BigDecimal bigDecimal = new BigDecimal(string2);
            BigDecimal bigDecimal2 = new BigDecimal(string3);
            BigDecimal bigDecimal3 = bigDecimal.multiply(bigDecimal2);
            this.lineTableModel.addRow(new Object[]{string, bigDecimal, bigDecimal3});
            this.recalcTotal();
            this.itemNameField.setText("");
            this.qtyField.setText("");
            this.priceField.setText("");
            this.itemNameField.requestFocus();
        }
        catch (NumberFormatException numberFormatException) {
            JOptionPane.showMessageDialog(this, "Quantity and Price must be valid numbers.", "Invalid Input", 0);
        }
    }

    private void recalcTotal() {
        BigDecimal bigDecimal = BigDecimal.ZERO;
        for (int i = 0; i < this.lineTableModel.getRowCount(); ++i) {
            bigDecimal = bigDecimal.add((BigDecimal)this.lineTableModel.getValueAt(i, 2));
        }
        this.totalLabel.setText("Total: Rs. " + bigDecimal.toPlainString());
    }

    private void saveStockIn() {
        if (this.lineTableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Add at least one item before saving.", "Nothing to Save", 2);
            return;
        }
        try {
            StockIn stockIn = new StockIn();
            stockIn.setBillNo(this.stockInDAO.generateBillNo());
            stockIn.setStockDate(LocalDate.now());
            stockIn.setRemarks("");
            BigDecimal bigDecimal = BigDecimal.ZERO;
            for (int i = 0; i < this.lineTableModel.getRowCount(); ++i) {
                String string = this.lineTableModel.getValueAt(i, 0).toString();
                BigDecimal bigDecimal2 = (BigDecimal)this.lineTableModel.getValueAt(i, 1);
                BigDecimal bigDecimal3 = (BigDecimal)this.lineTableModel.getValueAt(i, 2);
                BigDecimal bigDecimal4 = bigDecimal2.signum() > 0 ? bigDecimal3.divide(bigDecimal2, 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
                InventoryItem inventoryItem = this.inventoryDAO.findByName(string);
                int n = inventoryItem != null ? inventoryItem.getItemId() : this.inventoryDAO.createItem(string, new BigDecimal("5"));
                StockInItem stockInItem = new StockInItem(n, string, bigDecimal2, bigDecimal4);
                stockInItem.setLineTotal(bigDecimal3);
                stockIn.addItem(stockInItem);
                bigDecimal = bigDecimal.add(bigDecimal3);
            }
            stockIn.setTotalAmount(bigDecimal);
            this.stockInDAO.saveStockIn(stockIn);
            BillPrinter.showStockInBill(this, stockIn);
            this.lineTableModel.setRowCount(0);
            this.recalcTotal();
            this.onSaved.run();
        }
        catch (Exception exception) {
            JOptionPane.showMessageDialog(this, "Failed to save: " + exception.getMessage(), "Database Error", 0);
        }
    }
}

