/*
 * Decompiled with CFR 0.152.
 */
package ui;

import dao.MenuDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import model.MenuItem;
import util.UIHelper;

public class MenuManagementPanel
extends JPanel {
    private static final String[] SIZES = new String[]{"None", "Small", "Medium", "Large", "XLarge"};
    private final MenuDAO menuDAO = new MenuDAO();
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField nameField;
    private JTextField categoryField;
    private JTextField priceField;
    private JComboBox<String> sizeCombo;
    private final Runnable onMenuChanged;

    public MenuManagementPanel(Runnable runnable) {
        this.onMenuChanged = runnable;
        this.setLayout(new BorderLayout(12, 12));
        this.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        this.add((Component)this.buildForm(), "North");
        this.add((Component)this.buildTable(), "Center");
        this.refresh();
    }

    private JComponent buildForm() {
        JPanel jPanel = new JPanel(new FlowLayout(0, 14, 10));
        TitledBorder titledBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1), "Add New Menu Item", 1, 2, new Font("SansSerif", 1, 14), new Color(33, 33, 33));
        jPanel.setBorder(titledBorder);
        Font font = new Font("SansSerif", 1, 14);
        Font font2 = new Font("SansSerif", 0, 14);
        this.nameField = new JTextField(16);
        this.nameField.setFont(font2);
        this.nameField.setPreferredSize(new Dimension(180, 34));
        this.categoryField = new JTextField(12);
        this.categoryField.setFont(font2);
        this.categoryField.setPreferredSize(new Dimension(160, 34));
        this.priceField = new JTextField(8);
        this.priceField.setFont(font2);
        this.priceField.setPreferredSize(new Dimension(100, 34));
        this.sizeCombo = new JComboBox<String>(SIZES);
        this.sizeCombo.setFont(font2);
        this.sizeCombo.setPreferredSize(new Dimension(120, 34));
        this.sizeCombo.setToolTipText("Select size variant for this item (optional)");
        JButton jButton = UIHelper.createButton("+ Add Menu Item", new Color(46, 125, 50), Color.WHITE, 14);
        jButton.setPreferredSize(new Dimension(170, 36));
        jButton.addActionListener(actionEvent -> this.addMenuItem());
        JLabel jLabel = new JLabel("Name:");
        jLabel.setFont(font);
        JLabel jLabel2 = new JLabel("Category:");
        jLabel2.setFont(font);
        JLabel jLabel3 = new JLabel("Size:");
        jLabel3.setFont(font);
        JLabel jLabel4 = new JLabel("Price (Rs.):");
        jLabel4.setFont(font);
        jPanel.add(jLabel);
        jPanel.add(this.nameField);
        jPanel.add(jLabel2);
        jPanel.add(this.categoryField);
        jPanel.add(jLabel3);
        jPanel.add(this.sizeCombo);
        jPanel.add(jLabel4);
        jPanel.add(this.priceField);
        jPanel.add(jButton);
        return jPanel;
    }

    private JComponent buildTable() {
        Object[] objectArray = new String[]{"ID", "Name", "Category", "Size", "Price (Rs.)", "Status"};
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
        DefaultTableCellRenderer defaultTableCellRenderer = new DefaultTableCellRenderer();
        defaultTableCellRenderer.setHorizontalAlignment(0);
        this.table.getColumnModel().getColumn(3).setCellRenderer(defaultTableCellRenderer);
        DefaultTableCellRenderer defaultTableCellRenderer2 = new DefaultTableCellRenderer(){

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
        this.table.getColumnModel().getColumn(4).setCellRenderer(defaultTableCellRenderer2);
        JButton jButton = UIHelper.createButton("Toggle Active / Inactive", new Color(25, 118, 210), Color.WHITE, 14);
        jButton.setPreferredSize(new Dimension(210, 36));
        jButton.addActionListener(actionEvent -> this.toggleSelected());
        JButton jButton2 = UIHelper.createButton("Delete Selected Item", new Color(183, 28, 28), Color.WHITE, 14);
        jButton2.setPreferredSize(new Dimension(190, 36));
        jButton2.addActionListener(actionEvent -> this.deleteSelected());
        JPanel jPanel = new JPanel(new BorderLayout(8, 8));
        TitledBorder titledBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1), "Menu Items List", 1, 2, new Font("SansSerif", 1, 14), new Color(33, 33, 33));
        jPanel.setBorder(titledBorder);
        jPanel.add((Component)new JScrollPane(this.table), "Center");
        JPanel jPanel2 = new JPanel(new FlowLayout(2, 10, 4));
        jPanel2.add(jButton);
        jPanel2.add(jButton2);
        jPanel.add((Component)jPanel2, "South");
        return jPanel;
    }

    public void refresh() {
        this.tableModel.setRowCount(0);
        List<MenuItem> list = this.menuDAO.getAllMenuItems();
        for (MenuItem menuItem : list) {
            String string = menuItem.getSize() == null || menuItem.getSize().isBlank() ? "-" : menuItem.getSize();
            this.tableModel.addRow(new Object[]{menuItem.getMenuItemId(), menuItem.getName(), menuItem.getCategory(), string, menuItem.getPrice(), menuItem.isActive() ? "Active" : "Inactive"});
        }
    }

    private void addMenuItem() {
        String string = this.nameField.getText().trim();
        String string2 = this.categoryField.getText().trim();
        String string3 = this.priceField.getText().trim();
        String string4 = (String)this.sizeCombo.getSelectedItem();
        if (string.isEmpty() || string3.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and price are required.", "Missing Information", 2);
            return;
        }
        try {
            BigDecimal bigDecimal = new BigDecimal(string3);
            this.menuDAO.createMenuItem(string, string2.isEmpty() ? "General" : string2, bigDecimal, string4);
            this.nameField.setText("");
            this.categoryField.setText("");
            this.priceField.setText("");
            this.sizeCombo.setSelectedIndex(0);
            this.refresh();
            this.onMenuChanged.run();
        }
        catch (NumberFormatException numberFormatException) {
            JOptionPane.showMessageDialog(this, "Price must be a valid number.", "Invalid Input", 0);
        }
        catch (Exception exception) {
            JOptionPane.showMessageDialog(this, "Failed to add item: " + exception.getMessage(), "Database Error", 0);
        }
    }

    private void toggleSelected() {
        int n = this.table.getSelectedRow();
        if (n < 0) {
            JOptionPane.showMessageDialog(this, "Select a menu item first.", "No Selection", 2);
            return;
        }
        int n2 = (Integer)this.tableModel.getValueAt(n, 0);
        boolean bl = "Active".equals(this.tableModel.getValueAt(n, 5));
        this.menuDAO.setActive(n2, !bl);
        this.refresh();
        this.onMenuChanged.run();
    }

    private void deleteSelected() {
        int n = this.table.getSelectedRow();
        if (n < 0) {
            JOptionPane.showMessageDialog(this, "Select a menu item first to delete.", "No Selection", 2);
            return;
        }
        String string = this.tableModel.getValueAt(n, 1).toString();
        int n2 = (Integer)this.tableModel.getValueAt(n, 0);
        int n3 = JOptionPane.showConfirmDialog(this, "Are you sure you want to DELETE:\n\n\"" + string + "\"\n\nThis cannot be undone!", "Confirm Delete", 0, 2);
        if (n3 == 0) {
            try {
                this.menuDAO.deleteMenuItem(n2);
                this.refresh();
                this.onMenuChanged.run();
                JOptionPane.showMessageDialog(this, "\"" + string + "\" has been deleted successfully.", "Deleted", 1);
            }
            catch (Exception exception) {
                JOptionPane.showMessageDialog(this, "Failed to delete item: " + exception.getMessage(), "Error", 0);
            }
        }
    }
}

