/*
 * Decompiled with CFR 0.152.
 */
package ui;

import dao.InventoryDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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
import model.InventoryItem;
import util.UIHelper;

public class ViewInventoryPanel
extends JPanel {
    private final InventoryDAO inventoryDAO = new InventoryDAO();
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField searchField;
    private JLabel statusLabel;

    public ViewInventoryPanel() {
        this.setLayout(new BorderLayout(12, 12));
        this.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        this.add((Component)this.buildSearchBar(), "North");
        this.add((Component)this.buildTable(), "Center");
        this.add((Component)this.buildStatusBar(), "South");
        this.refresh();
    }

    private JComponent buildSearchBar() {
        JPanel jPanel = new JPanel(new FlowLayout(0, 12, 8));
        TitledBorder titledBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1), "Search & Manage Inventory", 1, 2, new Font("SansSerif", 1, 14), new Color(33, 33, 33));
        jPanel.setBorder(titledBorder);
        Font font = new Font("SansSerif", 1, 14);
        Font font2 = new Font("SansSerif", 0, 14);
        this.searchField = new JTextField(18);
        this.searchField.setFont(font2);
        this.searchField.setPreferredSize(new Dimension(200, 34));
        JButton jButton = UIHelper.createButton("Search", new Color(25, 118, 210), Color.WHITE, 14);
        jButton.setPreferredSize(new Dimension(100, 36));
        jButton.addActionListener(actionEvent -> this.loadItems(this.inventoryDAO.searchItems(this.searchField.getText().trim())));
        JButton jButton2 = UIHelper.createButton("Show All", new Color(69, 90, 100), Color.WHITE, 14);
        jButton2.setPreferredSize(new Dimension(110, 36));
        jButton2.addActionListener(actionEvent -> this.refresh());
        JButton jButton3 = UIHelper.createButton("Show Low Stock", new Color(230, 81, 0), Color.WHITE, 14);
        jButton3.setPreferredSize(new Dimension(150, 36));
        jButton3.addActionListener(actionEvent -> this.loadItems(this.inventoryDAO.getLowStockItems()));
        JButton jButton4 = UIHelper.createButton("Delete Selected", new Color(198, 40, 40), Color.WHITE, 14);
        jButton4.setPreferredSize(new Dimension(150, 36));
        jButton4.addActionListener(actionEvent -> this.deleteSelected());
        JButton jButton5 = UIHelper.createButton("Clear All", new Color(136, 14, 79), Color.WHITE, 14);
        jButton5.setPreferredSize(new Dimension(110, 36));
        jButton5.addActionListener(actionEvent -> this.clearAll());
        JLabel jLabel = new JLabel("Search:");
        jLabel.setFont(font);
        jPanel.add(jLabel);
        jPanel.add(this.searchField);
        jPanel.add(jButton);
        jPanel.add(jButton2);
        jPanel.add(jButton3);
        jPanel.add(jButton4);
        jPanel.add(jButton5);
        return jPanel;
    }

    private JComponent buildTable() {
        Object[] objectArray = new String[]{"ID", "Item Name", "Quantity", "Reorder Level"};
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
        this.table.setDefaultRenderer(Object.class, new LowStockRenderer());
        JPanel jPanel = new JPanel(new BorderLayout());
        TitledBorder titledBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1), "Current Stock List", 1, 2, new Font("SansSerif", 1, 14), new Color(33, 33, 33));
        jPanel.setBorder(titledBorder);
        jPanel.add((Component)new JScrollPane(this.table), "Center");
        return jPanel;
    }

    private JComponent buildStatusBar() {
        this.statusLabel = new JLabel(" ");
        this.statusLabel.setFont(new Font("SansSerif", 1, 14));
        this.statusLabel.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        return this.statusLabel;
    }

    public void refresh() {
        this.searchField.setText("");
        this.loadItems(this.inventoryDAO.getAllItems());
    }

    private void loadItems(List<InventoryItem> list) {
        this.tableModel.setRowCount(0);
        int n = 0;
        for (InventoryItem inventoryItem : list) {
            this.tableModel.addRow(new Object[]{inventoryItem.getItemId(), inventoryItem.getItemName(), inventoryItem.getQuantity(), inventoryItem.getReorderLevel()});
            if (!inventoryItem.isLowStock()) continue;
            ++n;
        }
        this.statusLabel.setText(String.format("  Total: %d item(s) | Low Stock: %d item(s) highlighted in red", list.size(), n));
    }

    private void deleteSelected() {
        int n = this.table.getSelectedRow();
        if (n < 0) {
            JOptionPane.showMessageDialog(this, "Select an item from the table first to delete.", "No Selection", 2);
            return;
        }
        int n2 = (Integer)this.tableModel.getValueAt(n, 0);
        String string = this.tableModel.getValueAt(n, 1).toString();
        int n3 = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete \"" + string + "\" from inventory?", "Confirm Delete", 0, 2);
        if (n3 == 0) {
            try {
                this.inventoryDAO.deleteItem(n2);
                this.refresh();
                JOptionPane.showMessageDialog(this, "\"" + string + "\" has been deleted.", "Deleted", 1);
            }
            catch (Exception exception) {
                JOptionPane.showMessageDialog(this, "Failed to delete item: " + exception.getMessage(), "Error", 0);
            }
        }
    }

    private void clearAll() {
        int n = JOptionPane.showConfirmDialog(this, "Are you sure you want to DELETE ALL inventory items?\nThis cannot be undone!", "Clear All Inventory", 0, 2);
        if (n == 0) {
            try {
                this.inventoryDAO.clearAllItems();
                this.refresh();
                JOptionPane.showMessageDialog(this, "All inventory items have been cleared.", "Inventory Cleared", 1);
            }
            catch (Exception exception) {
                JOptionPane.showMessageDialog(this, "Failed to clear inventory: " + exception.getMessage(), "Error", 0);
            }
        }
    }

    private class LowStockRenderer
    extends DefaultTableCellRenderer {
        private LowStockRenderer() {
        }

        @Override
        public Component getTableCellRendererComponent(JTable jTable, Object object, boolean bl, boolean bl2, int n, int n2) {
            Component component = super.getTableCellRendererComponent(jTable, object, bl, bl2, n, n2);
            if (n2 >= 2) {
                this.setHorizontalAlignment(4);
            } else {
                this.setHorizontalAlignment(2);
            }
            try {
                boolean bl3;
                double d = Double.parseDouble(ViewInventoryPanel.this.tableModel.getValueAt(n, 2).toString());
                double d2 = Double.parseDouble(ViewInventoryPanel.this.tableModel.getValueAt(n, 3).toString());
                boolean bl4 = bl3 = d <= d2;
                if (!bl) {
                    if (bl3) {
                        component.setBackground(new Color(255, 205, 210));
                        component.setForeground(new Color(183, 28, 28));
                    } else {
                        component.setBackground(n % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                        component.setForeground(Color.BLACK);
                    }
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
            return component;
        }
    }
}

