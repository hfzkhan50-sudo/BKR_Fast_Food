/*
 * Decompiled with CFR 0.152.
 */
package ui;

import dao.StockInDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDate;
import java.util.StringJoiner;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import model.StockIn;
import util.BillPrinter;
import util.UIHelper;

public class BillHistoryPanel
extends JPanel {
    private final StockInDAO stockInDAO = new StockInDAO();
    private DefaultTableModel tableModel;
    private JTable table;

    public BillHistoryPanel() {
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
        jPanel.setBorder(UIHelper.createCustomTitledBorder("Filter Purchase History"));

        JButton jButton = UIHelper.createButton("Today's Purchases", UIHelper.BKR_RED, Color.WHITE, 14);
        jButton.setPreferredSize(new Dimension(170, 36));
        jButton.addActionListener(actionEvent -> this.loadBills(LocalDate.now(), LocalDate.now()));

        JButton jButton2 = UIHelper.createButton("Show All (Last 90 Days)", new Color(60, 60, 80), Color.WHITE, 14);
        jButton2.setPreferredSize(new Dimension(210, 36));
        jButton2.addActionListener(actionEvent -> this.loadBills(LocalDate.now().minusDays(90L), LocalDate.now()));

        jPanel.add(jButton);
        jPanel.add(jButton2);
        return jPanel;
    }

    private JComponent buildTable() {
        Object[] objectArray = new String[]{"Stock-In ID", "Bill No", "Date", "Items", "Total Amount (Rs.)"};
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
        this.table.getColumnModel().getColumn(4).setCellRenderer(defaultTableCellRenderer);

        JPanel jPanel = new JPanel(new BorderLayout());
        jPanel.setBackground(UIHelper.PANEL_BG);
        jPanel.setBorder(UIHelper.createCustomTitledBorder("Purchase Bills List"));

        JScrollPane scrollPane = new JScrollPane(this.table);
        UIHelper.styleScrollPane(scrollPane);
        jPanel.add((Component)scrollPane, "Center");
        return jPanel;
    }

    private JComponent buildActionBar() {
        JPanel jPanel = new JPanel(new FlowLayout(2, 12, 8));
        jPanel.setBackground(UIHelper.DARK_BG);

        JButton jButton = UIHelper.createButton("View / Reprint Selected Bill", UIHelper.BKR_RED, Color.WHITE, 14);
        jButton.setPreferredSize(new Dimension(270, 38));
        jButton.addActionListener(actionEvent -> this.reprintSelected());
        jPanel.add(jButton);
        return jPanel;
    }

    public void refresh() {
        this.loadBills(LocalDate.now().minusDays(90L), LocalDate.now());
    }

    private void loadBills(LocalDate localDate, LocalDate localDate2) {
        this.tableModel.setRowCount(0);
        List<StockIn> list = this.stockInDAO.getBillsBetween(localDate, localDate2);
        for (StockIn stockIn : list) {
            StockIn detailedStockIn = this.stockInDAO.getBillWithItems(stockIn.getStockInId());
            StringJoiner items = new StringJoiner(", ");
            if (detailedStockIn != null && detailedStockIn.getItems() != null) {
                detailedStockIn.getItems().forEach(item -> items.add(item.getItemName() + " (" + item.getQuantity().stripTrailingZeros().toPlainString() + ")"));
            }
            this.tableModel.addRow(new Object[]{stockIn.getStockInId(), stockIn.getBillNo(), stockIn.getStockDate(), items.toString(), stockIn.getTotalAmount()});
        }
    }

    private void reprintSelected() {
        int n = this.table.getSelectedRow();
        if (n < 0) {
            JOptionPane.showMessageDialog(this, "Select a bill first.", "No Selection", 2);
            return;
        }
        int n2 = (Integer)this.tableModel.getValueAt(n, 0);
        StockIn stockIn = this.stockInDAO.getBillWithItems(n2);
        BillPrinter.showStockInBill(this, stockIn);
    }
}

