/*
 * Decompiled with CFR 0.152.
 */
package ui;

import dao.MenuDAO;
import dao.OrderDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import model.MenuItem;
import model.Order;
import model.OrderItem;
import util.BillPrinter;
import util.UIHelper;
import util.WrapLayout;

public class NewOrderPanel
extends JPanel {
    private static final List<String> CATEGORY_ORDER = Arrays.asList("Pizza", "Burger", "Wings", "Nuggets", "Chicken Broast", "Fries", "Drinks", "Juices", "Deals", "Pasta", "Paratha Roll", "Shawarma", "Wrap", "BKR Special Karahi", "BKR Handi (Boneless)", "BKR Soup", "Naan");
    private final MenuDAO menuDAO = new MenuDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private final Runnable onSaved;
    private final LinkedHashMap<String, LinkedHashMap<String, List<MenuItem>>> categorizedItems = new LinkedHashMap();
    private JPanel categoryListPanel;
    private JButton activeCategoryBtn = null;
    private JPanel itemGridPanel;
    private JScrollPane itemScrollPane;
    private JSpinner qtySpinner;
    private JComboBox<String> orderTypeCombo;
    private JComboBox<String> paymentTypeCombo;
    private JTextField discountField;
    private JCheckBox deliveryChargeCheck;
    private JTextField deliveryChargeField;
    private JCheckBox serviceChargeCheck;
    private JTextField serviceChargeField;
    private DefaultTableModel lineTableModel;
    private JTable lineTable;
    private JLabel subtotalLabel;
    private JLabel totalLabel;
    private List<MenuItem> rawMenuItems = new ArrayList<MenuItem>();

    public NewOrderPanel(Runnable runnable) {
        this.onSaved = runnable;
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.add((Component)this.buildMenuBrowserSection(), "Center");
        this.add((Component)this.buildOrderSummarySection(), "South");
        this.refreshMenuItems();
    }

    private JComponent buildMenuBrowserSection() {
        JPanel jPanel = new JPanel(new BorderLayout(8, 8));
        JPanel jPanel2 = new JPanel(new FlowLayout(0, 12, 6));
        jPanel2.setBackground(new Color(245, 247, 250));
        jPanel2.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(215, 220, 230)));
        JLabel jLabel = new JLabel("Quantity per click:");
        jLabel.setFont(new Font("SansSerif", 1, 14));
        this.qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        this.qtySpinner.setFont(new Font("SansSerif", 1, 14));
        this.qtySpinner.setPreferredSize(new Dimension(75, 32));
        JLabel jLabel2 = new JLabel("\ud83d\udca1 Tip: Select category on left, then click any size button to add to bill.");
        jLabel2.setFont(new Font("SansSerif", 0, 13));
        jLabel2.setForeground(new Color(90, 100, 120));
        jPanel2.add(jLabel);
        jPanel2.add(this.qtySpinner);
        jPanel2.add(Box.createHorizontalStrut(10));
        jPanel2.add(jLabel2);
        this.categoryListPanel = new JPanel();
        this.categoryListPanel.setLayout(new BoxLayout(this.categoryListPanel, 1));
        this.categoryListPanel.setBackground(new Color(26, 35, 126));
        this.categoryListPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        JScrollPane jScrollPane = new JScrollPane(this.categoryListPanel);
        jScrollPane.setPreferredSize(new Dimension(195, 260));
        jScrollPane.setHorizontalScrollBarPolicy(31);
        jScrollPane.setVerticalScrollBarPolicy(20);
        jScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        jScrollPane.setBorder(null);
        this.itemGridPanel = new JPanel(new WrapLayout(0, 10, 10));
        this.itemGridPanel.setBackground(new Color(250, 252, 255));
        this.itemScrollPane = new JScrollPane(this.itemGridPanel);
        this.itemScrollPane.setVerticalScrollBarPolicy(20);
        this.itemScrollPane.setHorizontalScrollBarPolicy(31);
        this.itemScrollPane.getVerticalScrollBar().setUnitIncrement(18);
        this.itemScrollPane.setBorder(BorderFactory.createLineBorder(new Color(210, 215, 230), 1));
        JPanel jPanel3 = new JPanel(new BorderLayout(6, 0));
        jPanel3.add((Component)jScrollPane, "West");
        jPanel3.add((Component)this.itemScrollPane, "Center");
        jPanel.add((Component)jPanel2, "North");
        jPanel.add((Component)jPanel3, "Center");
        return jPanel;
    }

    private JComponent buildOrderSummarySection() {
        JPanel jPanel = new JPanel(new BorderLayout(8, 8));
        jPanel.setPreferredSize(new Dimension(0, 250));
        Object[] objectArray = new String[]{"Item", "Qty", "Price", "Total (Rs.)"};
        this.lineTableModel = new DefaultTableModel(objectArray, 0){

            @Override
            public boolean isCellEditable(int n, int n2) {
                return false;
            }
        };
        this.lineTable = new JTable(this.lineTableModel);
        this.lineTable.setFont(new Font("SansSerif", 0, 14));
        this.lineTable.getTableHeader().setFont(new Font("SansSerif", 1, 14));
        this.lineTable.setRowHeight(28);
        DefaultTableCellRenderer defaultTableCellRenderer = new DefaultTableCellRenderer();
        defaultTableCellRenderer.setHorizontalAlignment(4);
        this.lineTable.getColumnModel().getColumn(1).setCellRenderer(defaultTableCellRenderer);
        this.lineTable.getColumnModel().getColumn(2).setCellRenderer(defaultTableCellRenderer);
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
        this.lineTable.getColumnModel().getColumn(3).setCellRenderer(defaultTableCellRenderer2);
        this.lineTable.getColumnModel().getColumn(0).setPreferredWidth(320);
        this.lineTable.getColumnModel().getColumn(1).setPreferredWidth(60);
        this.lineTable.getColumnModel().getColumn(2).setPreferredWidth(90);
        this.lineTable.getColumnModel().getColumn(3).setPreferredWidth(110);
        JScrollPane jScrollPane = new JScrollPane(this.lineTable);
        jScrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        JPanel jPanel2 = new JPanel(new BorderLayout(10, 6));
        jPanel2.setBorder(BorderFactory.createEmptyBorder(6, 4, 4, 4));
        JPanel jPanel3 = new JPanel(new WrapLayout(0, 10, 2));
        Font font = new Font("SansSerif", 1, 13);
        Font font2 = new Font("SansSerif", 0, 13);
        this.orderTypeCombo = new JComboBox<String>(new String[]{"Takeaway", "Dine-in", "Delivery"});
        this.orderTypeCombo.setFont(font2);
        this.orderTypeCombo.setPreferredSize(new Dimension(110, 32));
        this.paymentTypeCombo = new JComboBox<String>(new String[]{"Cash", "Card", "Online"});
        this.paymentTypeCombo.setFont(font2);
        this.paymentTypeCombo.setPreferredSize(new Dimension(100, 32));
        this.discountField = new JTextField("0", 6);
        this.discountField.setFont(font2);
        this.discountField.setPreferredSize(new Dimension(80, 32));
        this.discountField.addActionListener(actionEvent -> this.recalcTotals());
        this.discountField.addFocusListener(new FocusAdapter(){

            @Override
            public void focusLost(FocusEvent focusEvent) {
                NewOrderPanel.this.recalcTotals();
            }
        });
        this.deliveryChargeCheck = new JCheckBox("Delivery (Rs.)");
        this.deliveryChargeCheck.setFont(font);
        this.deliveryChargeField = new JTextField("0", 5);
        this.deliveryChargeField.setFont(font2);
        this.deliveryChargeField.setPreferredSize(new Dimension(65, 32));
        this.serviceChargeCheck = new JCheckBox("Service (%)");
        this.serviceChargeCheck.setFont(font);
        this.serviceChargeField = new JTextField("0", 4);
        this.serviceChargeField.setFont(font2);
        this.serviceChargeField.setPreferredSize(new Dimension(55, 32));
        this.deliveryChargeCheck.addActionListener(actionEvent -> this.recalcTotals());
        this.serviceChargeCheck.addActionListener(actionEvent -> this.recalcTotals());
        this.deliveryChargeField.addActionListener(actionEvent -> this.recalcTotals());
        this.serviceChargeField.addActionListener(actionEvent -> this.recalcTotals());
        this.deliveryChargeField.addFocusListener(new FocusAdapter(){

            @Override
            public void focusLost(FocusEvent focusEvent) {
                NewOrderPanel.this.recalcTotals();
            }
        });
        this.serviceChargeField.addFocusListener(new FocusAdapter(){

            @Override
            public void focusLost(FocusEvent focusEvent) {
                NewOrderPanel.this.recalcTotals();
            }
        });
        JButton jButton = UIHelper.createButton("+ Qty", new Color(25, 118, 210), Color.WHITE, 12);
        jButton.setPreferredSize(new Dimension(75, 32));
        jButton.addActionListener(actionEvent -> this.modifySelectedQty(1));
        JButton jButton2 = UIHelper.createButton("- Qty", new Color(69, 90, 100), Color.WHITE, 12);
        jButton2.setPreferredSize(new Dimension(75, 32));
        jButton2.addActionListener(actionEvent -> this.modifySelectedQty(-1));
        JButton jButton3 = UIHelper.createButton("Remove", new Color(211, 47, 47), Color.WHITE, 12);
        jButton3.setPreferredSize(new Dimension(85, 32));
        jButton3.addActionListener(actionEvent -> {
            int n = this.lineTable.getSelectedRow();
            if (n >= 0) {
                this.lineTableModel.removeRow(n);
                this.recalcTotals();
            } else {
                JOptionPane.showMessageDialog(this, "Select an item from table to remove.", "No Selection", 2);
            }
        });
        JLabel jLabel = new JLabel("Type:");
        jLabel.setFont(font);
        JLabel jLabel2 = new JLabel("Pay:");
        jLabel2.setFont(font);
        JLabel jLabel3 = new JLabel("Disc (Rs.):");
        jLabel3.setFont(font);
        jPanel3.add(jLabel);
        jPanel3.add(this.orderTypeCombo);
        jPanel3.add(jLabel2);
        jPanel3.add(this.paymentTypeCombo);
        jPanel3.add(jLabel3);
        jPanel3.add(this.discountField);
        jPanel3.add(this.deliveryChargeCheck);
        jPanel3.add(this.deliveryChargeField);
        jPanel3.add(this.serviceChargeCheck);
        jPanel3.add(this.serviceChargeField);
        jPanel3.add(Box.createHorizontalStrut(8));
        jPanel3.add(jButton);
        jPanel3.add(jButton2);
        jPanel3.add(jButton3);
        JPanel jPanel4 = new JPanel(new FlowLayout(2, 15, 0));
        this.subtotalLabel = new JLabel("Subtotal: Rs. 0.00");
        this.subtotalLabel.setFont(new Font("SansSerif", 1, 14));
        this.subtotalLabel.setForeground(new Color(60, 60, 60));
        this.totalLabel = new JLabel("TOTAL: Rs. 0.00");
        this.totalLabel.setFont(new Font("SansSerif", 1, 20));
        this.totalLabel.setForeground(new Color(27, 94, 32));
        JButton jButton4 = UIHelper.createButton("Save & Print Bill", new Color(46, 125, 50), Color.WHITE, 15);
        jButton4.setPreferredSize(new Dimension(200, 42));
        jButton4.addActionListener(actionEvent -> this.saveOrder());
        jPanel4.add(this.subtotalLabel);
        jPanel4.add(this.totalLabel);
        jPanel4.add(jButton4);
        jPanel2.add((Component)jPanel3, "Center");
        jPanel2.add((Component)jPanel4, "South");
        jPanel.add((Component)jScrollPane, "Center");
        jPanel.add((Component)jPanel2, "South");
        return jPanel;
    }

    public void refreshMenuItems() {
        if (this.categoryListPanel == null) {
            return;
        }
        this.rawMenuItems = this.menuDAO.getAllActiveMenuItems();
        this.categorizedItems.clear();
        for (String cat : CATEGORY_ORDER) {
            this.categorizedItems.put(cat, new LinkedHashMap<String, List<MenuItem>>());
        }
        for (MenuItem menuItem : this.rawMenuItems) {
            String cat = menuItem.getCategory() != null && !menuItem.getCategory().isBlank() ? menuItem.getCategory().trim() : "General";
            String name = menuItem.getName().trim();
            this.categorizedItems.computeIfAbsent(cat, k -> new LinkedHashMap<String, List<MenuItem>>()).computeIfAbsent(name, k -> new ArrayList<MenuItem>()).add(menuItem);
        }
        this.categorizedItems.entrySet().removeIf(entry -> entry.getValue().isEmpty());
        this.categoryListPanel.removeAll();
        this.activeCategoryBtn = null;
        if (this.categorizedItems.isEmpty()) {
            JLabel noItemsLabel = new JLabel("<html><center>No menu items.<br>Add from Menu<br>Management tab.</center></html>");
            noItemsLabel.setForeground(Color.WHITE);
            noItemsLabel.setFont(new Font("SansSerif", 2, 13));
            noItemsLabel.setAlignmentX(0.5f);
            this.categoryListPanel.add(noItemsLabel);
            this.itemGridPanel.removeAll();
        } else {
            for (String catName : this.categorizedItems.keySet()) {
                JButton btn = this.createCategoryTabButton(catName);
                this.categoryListPanel.add(btn);
                this.categoryListPanel.add(Box.createVerticalStrut(4));
            }
            String firstCat = this.categorizedItems.keySet().iterator().next();
            JButton jButton = (JButton)this.categoryListPanel.getComponent(0);
            this.selectCategory(firstCat, jButton);
        }
        this.categoryListPanel.revalidate();
        this.categoryListPanel.repaint();
    }

    private JButton createCategoryTabButton(String string) {
        String string2 = this.getCategoryIcon(string);
        JButton jButton = new JButton(string2 + " " + string);
        jButton.setFont(new Font("SansSerif", 1, 13));
        jButton.setForeground(Color.WHITE);
        jButton.setBackground(new Color(40, 53, 147));
        jButton.setFocusPainted(false);
        jButton.setBorderPainted(false);
        jButton.setOpaque(true);
        jButton.setCursor(new Cursor(12));
        jButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        jButton.setPreferredSize(new Dimension(180, 40));
        jButton.setAlignmentX(0.0f);
        jButton.setHorizontalAlignment(2);
        jButton.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 6));
        jButton.addActionListener(actionEvent -> this.selectCategory(string, jButton));
        return jButton;
    }

    private String getCategoryIcon(String string) {
        String string2 = string.toLowerCase();
        if (string2.contains("pizza")) {
            return "\ud83c\udf55";
        }
        if (string2.contains("burger")) {
            return "\ud83c\udf54";
        }
        if (string2.contains("nugget")) {
            return "\ud83c\udf57";
        }
        if (string2.contains("wing")) {
            return "\ud83c\udf57";
        }
        if (string2.contains("broast")) {
            return "\ud83c\udf57";
        }
        if (string2.contains("fries")) {
            return "\ud83c\udf5f";
        }
        if (string2.contains("drink")) {
            return "\ud83e\udd64";
        }
        if (string2.contains("juice")) {
            return "\ud83c\udf79";
        }
        if (string2.contains("deal")) {
            return "\ud83c\udf81";
        }
        if (string2.contains("pasta")) {
            return "\ud83c\udf5d";
        }
        if (string2.contains("roll")) {
            return "\ud83c\udf2f";
        }
        if (string2.contains("shawarma")) {
            return "\ud83e\udd59";
        }
        if (string2.contains("wrap")) {
            return "\ud83c\udf2f";
        }
        if (string2.contains("karahi")) {
            return "\ud83c\udf72";
        }
        if (string2.contains("handi")) {
            return "\ud83c\udf72";
        }
        if (string2.contains("soup")) {
            return "\ud83e\udd63";
        }
        if (string2.contains("naan") || string2.contains("roti")) {
            return "\ud83e\uded3";
        }
        return "\ud83c\udf74";
    }

    private void selectCategory(String string, JButton jButton) {
        if (this.activeCategoryBtn != null) {
            this.activeCategoryBtn.setBackground(new Color(40, 53, 147));
            this.activeCategoryBtn.setForeground(Color.WHITE);
        }
        jButton.setBackground(new Color(255, 179, 0));
        jButton.setForeground(new Color(20, 20, 20));
        this.activeCategoryBtn = jButton;
        this.renderCategoryProducts(string);
    }

    private void renderCategoryProducts(String string) {
        this.itemGridPanel.removeAll();
        LinkedHashMap<String, List<MenuItem>> linkedHashMap = this.categorizedItems.getOrDefault(string, new LinkedHashMap<String, List<MenuItem>>());
        if (linkedHashMap.isEmpty()) {
            JLabel jLabel = new JLabel("No items found in this category.");
            jLabel.setFont(new Font("SansSerif", 2, 14));
            jLabel.setForeground(Color.GRAY);
            this.itemGridPanel.add(jLabel);
        } else {
            for (Map.Entry<String, List<MenuItem>> entry : linkedHashMap.entrySet()) {
                String string2 = entry.getKey();
                List<MenuItem> list = entry.getValue();
                this.itemGridPanel.add(this.createProductCard(string2, list));
            }
        }
        this.itemGridPanel.revalidate();
        this.itemGridPanel.repaint();
        SwingUtilities.invokeLater(() -> this.itemScrollPane.getVerticalScrollBar().setValue(0));
    }

    private JPanel createProductCard(String string, List<MenuItem> list) {
        boolean bl;
        JPanel jPanel = new JPanel(new BorderLayout(4, 4));
        jPanel.setBackground(Color.WHITE);
        jPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(218, 224, 233), 1), BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        JLabel jLabel = new JLabel("<html><b>" + string + "</b></html>", 2);
        jLabel.setFont(new Font("SansSerif", 1, 13));
        jLabel.setForeground(new Color(33, 33, 33));
        jPanel.add((Component)jLabel, "North");
        JPanel jPanel2 = new JPanel();
        boolean bl2 = bl = list.size() > 1 || list.size() == 1 && list.get(0).hasSizeVariant();
        if (bl) {
            jPanel2.setLayout(new GridLayout(list.size(), 1, 4, 4));
            jPanel2.setOpaque(false);
            for (MenuItem menuItem : list) {
                String string2 = menuItem.getSize();
                if (string2 == null || string2.isBlank() || string2.equalsIgnoreCase("None")) {
                    string2 = "Regular";
                }
                String string3 = "<html><b>" + string2 + "</b> \u2014 <font color='#2e7d32'>Rs. " + menuItem.getPrice().toPlainString() + "</font></html>";
                final JButton jButton = new JButton(string3);
                jButton.setFont(new Font("SansSerif", 0, 12));
                jButton.setBackground(new Color(240, 244, 248));
                jButton.setForeground(new Color(20, 20, 20));
                jButton.setFocusPainted(false);
                jButton.setCursor(new Cursor(12));
                jButton.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(180, 200, 220), 1), BorderFactory.createEmptyBorder(4, 6, 4, 6)));
                jButton.addMouseListener(new MouseAdapter(){

                    @Override
                    public void mouseEntered(MouseEvent mouseEvent) {
                        jButton.setBackground(new Color(227, 242, 253));
                    }

                    @Override
                    public void mouseExited(MouseEvent mouseEvent) {
                        jButton.setBackground(new Color(240, 244, 248));
                    }
                });
                jButton.addActionListener(actionEvent -> this.addItemToOrder(menuItem));
                jPanel2.add(jButton);
            }
            int n = Math.max(165, string.length() * 8 + 30);
            int n2 = 35 + list.size() * 32;
            jPanel.setPreferredSize(new Dimension(n, n2));
        } else {
            MenuItem menuItem = list.get(0);
            jPanel2.setLayout(new BorderLayout());
            jPanel2.setOpaque(false);
            String string4 = "<html><font color='#ffffff'><b>Rs. " + menuItem.getPrice().toPlainString() + "</b></font></html>";
            final JButton jButton = new JButton(string4);
            jButton.setFont(new Font("SansSerif", 1, 13));
            jButton.setBackground(new Color(25, 118, 210));
            jButton.setForeground(Color.WHITE);
            jButton.setFocusPainted(false);
            jButton.setOpaque(true);
            jButton.setBorderPainted(false);
            jButton.setCursor(new Cursor(12));
            jButton.setPreferredSize(new Dimension(140, 36));
            jButton.addMouseListener(new MouseAdapter(){

                @Override
                public void mouseEntered(MouseEvent mouseEvent) {
                    jButton.setBackground(new Color(21, 101, 192));
                }

                @Override
                public void mouseExited(MouseEvent mouseEvent) {
                    jButton.setBackground(new Color(25, 118, 210));
                }
            });
            jButton.addActionListener(actionEvent -> this.addItemToOrder(menuItem));
            jPanel2.add((Component)jButton, "Center");
            int n = Math.max(150, string.length() * 8 + 30);
            jPanel.setPreferredSize(new Dimension(n, 75));
        }
        jPanel.add((Component)jPanel2, "Center");
        return jPanel;
    }

    private void addItemToOrder(MenuItem menuItem) {
        int n = (Integer)this.qtySpinner.getValue();
        if (n <= 0) {
            n = 1;
        }
        String string = menuItem.getDisplayName();
        BigDecimal bigDecimal = menuItem.getPrice();
        BigDecimal bigDecimal2 = bigDecimal.multiply(BigDecimal.valueOf(n));
        for (int i = 0; i < this.lineTableModel.getRowCount(); ++i) {
            if (!this.lineTableModel.getValueAt(i, 0).equals(string)) continue;
            int n2 = (Integer)this.lineTableModel.getValueAt(i, 1);
            int n3 = n2 + n;
            BigDecimal bigDecimal3 = bigDecimal.multiply(BigDecimal.valueOf(n3));
            this.lineTableModel.setValueAt(n3, i, 1);
            this.lineTableModel.setValueAt(bigDecimal3, i, 3);
            this.recalcTotals();
            this.qtySpinner.setValue(1);
            return;
        }
        this.lineTableModel.addRow(new Object[]{string, n, bigDecimal, bigDecimal2});
        this.recalcTotals();
        this.qtySpinner.setValue(1);
    }

    private void modifySelectedQty(int n) {
        int n2 = this.lineTable.getSelectedRow();
        if (n2 < 0) {
            JOptionPane.showMessageDialog(this, "Please select an item from the table first.", "No Selection", 2);
            return;
        }
        int n3 = (Integer)this.lineTableModel.getValueAt(n2, 1);
        int n4 = n3 + n;
        if (n4 <= 0) {
            this.lineTableModel.removeRow(n2);
        } else {
            BigDecimal bigDecimal = (BigDecimal)this.lineTableModel.getValueAt(n2, 2);
            BigDecimal bigDecimal2 = bigDecimal.multiply(BigDecimal.valueOf(n4));
            this.lineTableModel.setValueAt(n4, n2, 1);
            this.lineTableModel.setValueAt(bigDecimal2, n2, 3);
        }
        this.recalcTotals();
    }

    public void refreshMenuComboPublic() {
        this.refreshMenuItems();
    }

    private void recalcTotals() {
        BigDecimal bigDecimal = BigDecimal.ZERO;
        for (int i = 0; i < this.lineTableModel.getRowCount(); ++i) {
            bigDecimal = bigDecimal.add((BigDecimal)this.lineTableModel.getValueAt(i, 3));
        }
        BigDecimal bigDecimal2 = this.parseDiscount();
        BigDecimal bigDecimal3 = bigDecimal.subtract(bigDecimal2).max(BigDecimal.ZERO);
        BigDecimal bigDecimal4 = this.parseDeliveryCharge();
        BigDecimal bigDecimal5 = this.parseServiceChargePercent();
        BigDecimal bigDecimal6 = bigDecimal3.multiply(bigDecimal5).divide(BigDecimal.valueOf(100L));
        BigDecimal bigDecimal7 = bigDecimal3.add(this.deliveryChargeCheck.isSelected() ? bigDecimal4 : BigDecimal.ZERO).add(this.serviceChargeCheck.isSelected() ? bigDecimal6 : BigDecimal.ZERO);
        this.subtotalLabel.setText("Subtotal: Rs. " + bigDecimal.toPlainString());
        this.totalLabel.setText("TOTAL: Rs. " + bigDecimal7.toPlainString());
    }

    private BigDecimal parseDiscount() {
        try {
            return new BigDecimal(this.discountField.getText().trim());
        }
        catch (Exception exception) {
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal parseDeliveryCharge() {
        try {
            return new BigDecimal(this.deliveryChargeField.getText().trim()).max(BigDecimal.ZERO);
        }
        catch (Exception exception) {
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal parseServiceChargePercent() {
        try {
            return new BigDecimal(this.serviceChargeField.getText().trim()).max(BigDecimal.ZERO);
        }
        catch (Exception exception) {
            return BigDecimal.ZERO;
        }
    }

    private void saveOrder() {
        if (this.lineTableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Add at least one item before saving.", "Nothing to Save", 2);
            return;
        }
        try {
            BigDecimal bigDecimal;
            BigDecimal bigDecimal2;
            Object object;
            List<MenuItem> list = this.menuDAO.getAllActiveMenuItems();
            Order order = new Order();
            order.setOrderNo(this.orderDAO.generateOrderNo());
            order.setOrderDate(LocalDate.now());
            order.setOrderTime(LocalTime.now());
            order.setCustomerName("");
            order.setOrderType((String)this.orderTypeCombo.getSelectedItem());
            order.setPaymentType((String)this.paymentTypeCombo.getSelectedItem());
            BigDecimal bigDecimal3 = BigDecimal.ZERO;
            for (int i = 0; i < this.lineTableModel.getRowCount(); ++i) {
                String itemName = this.lineTableModel.getValueAt(i, 0).toString();
                int n = (Integer)this.lineTableModel.getValueAt(i, 1);
                bigDecimal2 = (BigDecimal)this.lineTableModel.getValueAt(i, 2);
                bigDecimal = (BigDecimal)this.lineTableModel.getValueAt(i, 3);
                MenuItem menuItem = list.stream().filter(m -> m.getDisplayName().equalsIgnoreCase(itemName) || m.getName().equalsIgnoreCase(itemName)).findFirst().orElse(null);
                int n2 = menuItem != null ? menuItem.getMenuItemId() : 0;
                OrderItem orderItem = new OrderItem(n2, itemName, n, bigDecimal2);
                orderItem.setLineTotal(bigDecimal);
                order.addItem(orderItem);
                bigDecimal3 = bigDecimal3.add(bigDecimal);
            }
            BigDecimal bigDecimal4 = this.parseDiscount();
            BigDecimal bigDecimal5 = this.parseDeliveryCharge();
            object = this.parseServiceChargePercent();
            bigDecimal2 = bigDecimal3.subtract(bigDecimal4).max(BigDecimal.ZERO);
            bigDecimal = bigDecimal2.multiply((BigDecimal)object).divide(BigDecimal.valueOf(100L));
            BigDecimal bigDecimal6 = bigDecimal2.add(this.deliveryChargeCheck.isSelected() ? bigDecimal5 : BigDecimal.ZERO).add(this.serviceChargeCheck.isSelected() ? bigDecimal : BigDecimal.ZERO);
            order.setSubtotal(bigDecimal3);
            order.setDiscount(bigDecimal4);
            order.setDeliveryCharge(this.deliveryChargeCheck.isSelected() ? bigDecimal5 : BigDecimal.ZERO);
            order.setServiceChargePercent((BigDecimal)(this.serviceChargeCheck.isSelected() ? object : BigDecimal.ZERO));
            order.setServiceCharge(this.serviceChargeCheck.isSelected() ? bigDecimal : BigDecimal.ZERO);
            order.setTotalAmount(bigDecimal6);
            this.orderDAO.saveOrder(order);
            BillPrinter.showOrderBill(this, order);
            this.lineTableModel.setRowCount(0);
            this.discountField.setText("0");
            this.deliveryChargeCheck.setSelected(false);
            this.deliveryChargeField.setText("0");
            this.serviceChargeCheck.setSelected(false);
            this.serviceChargeField.setText("0");
            this.recalcTotals();
            if (this.onSaved != null) {
                SwingUtilities.invokeLater(() -> {
                    try {
                        this.onSaved.run();
                    }
                    catch (Exception exception) {
                        System.err.println("Warning: post-save refresh non-critical error: " + exception.getMessage());
                    }
                });
            }
        }
        catch (Exception exception) {
            JOptionPane.showMessageDialog(this, "Failed to save order: " + exception.getMessage(), "Database Error", 0);
        }
    }
}

