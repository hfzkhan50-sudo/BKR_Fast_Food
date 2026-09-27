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
        this.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        this.add((Component)this.buildFilterBar(), "North");
        this.add((Component)this.buildTable(), "Center");
        this.add((Component)this.buildActionBar(), "South");
        this.refresh();
    }

    private JComponent buildFilterBar() {
        JPanel jPanel = new JPanel(new FlowLayout(0, 12, 8));
        TitledBorder titledBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1), "Filter Purchase History", 1, 2, new Font("SansSerif", 1, 14), new Color(33, 33, 33));
        jPanel.setBorder(titledBorder);
        JButton jButton = UIHelper.createButton("Today's Purchases", new Color(25, 118, 210), Color.WHITE, 14);
        jButton.setPreferredSize(new Dimension(170, 36));
        jButton.addActionListener(actionEvent -> this.loadBills(LocalDate.now(), LocalDate.now()));
        JButton jButton2 = UIHelper.createButton("Show All (Last 90 Days)", new Color(69, 90, 100), Color.WHITE, 14);
        jButton2.setPreferredSize(new Dimension(210, 36));
        jButton2.addActionListener(actionEvent -> this.loadBills(LocalDate.now().minusDays(90L), LocalDate.now()));
        jPanel.add(jButton);
        jPanel.add(jButton2);
        return jPanel;
    }

    private JComponent buildTable() {
        Object[] objectArray = new String[]{"Stock-In ID", "Bill No", "Date", "Supplier", "Total Amount (Rs.)"};
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
        this.table.getColumnModel().getColumn(4).setCellRenderer(defaultTableCellRenderer);
        JPanel jPanel = new JPanel(new BorderLayout());
        TitledBorder titledBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1), "Purchase Bills List", 1, 2, new Font("SansSerif", 1, 14), new Color(33, 33, 33));
        jPanel.setBorder(titledBorder);
        jPanel.add((Component)new JScrollPane(this.table), "Center");
        return jPanel;
    }

    private JComponent buildActionBar() {
        JPanel jPanel = new JPanel(new FlowLayout(2, 12, 8));
        JButton jButton = UIHelper.createButton("View / Reprint Selected Bill", new Color(46, 125, 50), Color.WHITE, 14);
        jButton.setPreferredSize(new Dimension(260, 38));
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
            this.tableModel.addRow(new Object[]{stockIn.getStockInId(), stockIn.getBillNo(), stockIn.getStockDate(), stockIn.getSupplierName() == null ? "-" : stockIn.getSupplierName(), stockIn.getTotalAmount()});
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

