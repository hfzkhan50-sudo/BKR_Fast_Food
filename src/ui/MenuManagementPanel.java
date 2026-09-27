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
import util.WrapLayout;

public class MenuManagementPanel
extends JPanel {
    private static final String[] SIZES = new String[]{"None", "Small", "Medium", "Large", "XL", "Half", "Full"};
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
        this.setBackground(UIHelper.DARK_BG);
        this.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        this.add((Component)this.buildForm(), "North");
        this.add((Component)this.buildTable(), "Center");
        this.refresh();
    }

    private JComponent buildForm() {
        JPanel jPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 14, 10));
        jPanel.setBackground(UIHelper.PANEL_BG);
        jPanel.setBorder(UIHelper.createCustomTitledBorder("Add New Menu Item"));

        Font font = new Font("SansSerif", 1, 14);
        Font font2 = new Font("SansSerif", 0, 14);

        this.nameField = new JTextField(16);
        UIHelper.styleTextField(this.nameField);
        this.nameField.setPreferredSize(new Dimension(180, 34));

        this.categoryField = new JTextField(12);
        UIHelper.styleTextField(this.categoryField);
        this.categoryField.setPreferredSize(new Dimension(160, 34));

        this.priceField = new JTextField(8);
        UIHelper.styleTextField(this.priceField);
        this.priceField.setPreferredSize(new Dimension(100, 34));

        this.sizeCombo = new JComboBox<String>(SIZES);
        this.sizeCombo.setFont(font2);
        this.sizeCombo.setPreferredSize(new Dimension(120, 34));
        UIHelper.styleComboBox(this.sizeCombo);

        JButton jButton = UIHelper.createButton("+ Add Menu Item", UIHelper.BKR_RED, Color.WHITE, 14);
        jButton.setPreferredSize(new Dimension(170, 36));
        jButton.addActionListener(actionEvent -> this.addMenuItem());

        JButton saveFlyerBtn = UIHelper.createButton("Save as BKR Flyer Menu", UIHelper.BKR_GOLD, Color.BLACK, 14);
        saveFlyerBtn.setPreferredSize(new Dimension(210, 36));
        saveFlyerBtn.addActionListener(actionEvent -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Save the current menu as the BKR Flyer menu?\nThis replaces the previously saved flyer menu.", "Save Flyer Menu", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    int savedCount = this.menuDAO.saveAsFlyerMenu();
                    JOptionPane.showMessageDialog(this, savedCount + " menu items saved as the BKR Flyer menu.", "Flyer Menu Saved", JOptionPane.INFORMATION_MESSAGE);
                }
                catch (Exception exception) {
                    JOptionPane.showMessageDialog(this, "Failed to save flyer menu: " + exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton restoreFlyerBtn = UIHelper.createButton("Restore BKR Flyer Menu", new Color(60, 60, 80), Color.WHITE, 14);
        restoreFlyerBtn.setPreferredSize(new Dimension(210, 36));
        restoreFlyerBtn.addActionListener(actionEvent -> this.restoreFlyerMenu());

        JLabel jLabel = new JLabel("Name:");
        jLabel.setFont(font);
        jLabel.setForeground(UIHelper.TEXT_WHITE);

        JLabel jLabel2 = new JLabel("Category:");
        jLabel2.setFont(font);
        jLabel2.setForeground(UIHelper.TEXT_WHITE);

        JLabel jLabel3 = new JLabel("Size:");
        jLabel3.setFont(font);
        jLabel3.setForeground(UIHelper.TEXT_WHITE);

        JLabel jLabel4 = new JLabel("Price (Rs.):");
        jLabel4.setFont(font);
        jLabel4.setForeground(UIHelper.TEXT_WHITE);

        jPanel.add(jLabel);
        jPanel.add(this.nameField);
        jPanel.add(jLabel2);
        jPanel.add(this.categoryField);
        jPanel.add(jLabel3);
        jPanel.add(this.sizeCombo);
        jPanel.add(jLabel4);
        jPanel.add(this.priceField);
        jPanel.add(jButton);
        jPanel.add(saveFlyerBtn);
        jPanel.add(restoreFlyerBtn);
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
        UIHelper.styleTable(this.table);

        DefaultTableCellRenderer defaultTableCellRenderer = new DefaultTableCellRenderer();
        defaultTableCellRenderer.setHorizontalAlignment(0);
        this.table.getColumnModel().getColumn(3).setCellRenderer(defaultTableCellRenderer);

        DefaultTableCellRenderer defaultTableCellRenderer2 = new DefaultTableCellRenderer(){

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
        this.table.getColumnModel().getColumn(4).setCellRenderer(defaultTableCellRenderer2);

        JButton jButton = UIHelper.createButton("Toggle Active / Inactive", new Color(60, 60, 80), Color.WHITE, 14);
        jButton.setPreferredSize(new Dimension(210, 36));
        jButton.addActionListener(actionEvent -> this.toggleSelected());

        JButton jButton2 = UIHelper.createButton("Delete Selected Item", UIHelper.BKR_RED, Color.WHITE, 14);
        jButton2.setPreferredSize(new Dimension(190, 36));
        jButton2.addActionListener(actionEvent -> this.deleteSelected());

        JPanel jPanel = new JPanel(new BorderLayout(8, 8));
        jPanel.setBackground(UIHelper.PANEL_BG);
        jPanel.setBorder(UIHelper.createCustomTitledBorder("Menu Items List"));

        JScrollPane scrollPane = new JScrollPane(this.table);
        UIHelper.styleScrollPane(scrollPane);
        jPanel.add((Component)scrollPane, "Center");

        JPanel jPanel2 = new JPanel(new FlowLayout(2, 10, 4));
        jPanel2.setBackground(UIHelper.PANEL_BG);
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

    private void restoreFlyerMenu() {
        int confirm = JOptionPane.showConfirmDialog(this, "Restore the saved BKR Flyer menu?\nThis will replace the current menu.", "Restore Flyer Menu", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            int restoredCount = this.menuDAO.restoreFlyerMenu();
            this.refresh();
            if (this.onMenuChanged != null) {
                this.onMenuChanged.run();
            }
            JOptionPane.showMessageDialog(this, restoredCount + " menu items restored from the BKR Flyer menu.", "Flyer Menu Restored", JOptionPane.INFORMATION_MESSAGE);
        }
        catch (Exception exception) {
            JOptionPane.showMessageDialog(this, "Failed to restore flyer menu: " + exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
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

