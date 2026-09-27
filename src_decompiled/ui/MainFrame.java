/*
 * Decompiled with CFR 0.152.
 */
package ui;

import api.ApiClient;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import ui.BillHistoryPanel;
import ui.MenuManagementPanel;
import ui.NewOrderPanel;
import ui.OrderHistoryPanel;
import ui.SalesReportPanel;
import ui.StockInPanel;
import ui.ViewInventoryPanel;

public class MainFrame
extends JFrame {
    private ViewInventoryPanel viewInventoryPanel;
    private StockInPanel stockInPanel;
    private BillHistoryPanel billHistoryPanel;
    private NewOrderPanel newOrderPanel;
    private OrderHistoryPanel orderHistoryPanel;
    private SalesReportPanel salesReportPanel;
    private MenuManagementPanel menuManagementPanel;
    private JLabel serverStatusLabel;

    public MainFrame() {
        this.setTitle("BKR Fast Food Management System");
        this.setDefaultCloseOperation(3);
        this.setSize(1240, 800);
        this.setMinimumSize(new Dimension(1050, 680));
        this.setLocationRelativeTo(null);
        JTabbedPane jTabbedPane = new JTabbedPane();
        jTabbedPane.setFont(new Font("SansSerif", 1, 14));
        this.viewInventoryPanel = new ViewInventoryPanel();
        this.billHistoryPanel = new BillHistoryPanel();
        this.stockInPanel = new StockInPanel(() -> {
            this.viewInventoryPanel.refresh();
            this.billHistoryPanel.refresh();
        });
        this.orderHistoryPanel = new OrderHistoryPanel();
        this.salesReportPanel = new SalesReportPanel();
        this.newOrderPanel = new NewOrderPanel(() -> {
            this.orderHistoryPanel.refresh();
            this.salesReportPanel.refresh();
            this.viewInventoryPanel.refresh();
        });
        this.menuManagementPanel = new MenuManagementPanel(() -> this.newOrderPanel.refreshMenuComboPublic());
        jTabbedPane.addTab("New Order", this.newOrderPanel);
        jTabbedPane.addTab("Order History", this.orderHistoryPanel);
        jTabbedPane.addTab("Detailed Sales Report", this.salesReportPanel);
        jTabbedPane.addTab("Menu Management", this.menuManagementPanel);
        jTabbedPane.addTab("Current Inventory", this.viewInventoryPanel);
        jTabbedPane.addTab("Add Stock (Purchase)", this.stockInPanel);
        jTabbedPane.addTab("Purchase Bill History", this.billHistoryPanel);
        jTabbedPane.addChangeListener(changeEvent -> {
            int n = jTabbedPane.getSelectedIndex();
            switch (n) {
                case 0: {
                    this.newOrderPanel.refreshMenuComboPublic();
                    break;
                }
                case 1: {
                    this.orderHistoryPanel.refresh();
                    break;
                }
                case 2: {
                    this.salesReportPanel.refresh();
                    break;
                }
                case 3: {
                    this.menuManagementPanel.refresh();
                    break;
                }
                case 4: {
                    this.viewInventoryPanel.refresh();
                    break;
                }
                case 6: {
                    this.billHistoryPanel.refresh();
                }
            }
        });
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add((Component)this.buildHeaderPanel(), "North");
        this.getContentPane().add((Component)jTabbedPane, "Center");
    }

    private JPanel buildHeaderPanel() {
        JPanel jPanel = new JPanel(new BorderLayout());
        jPanel.setBackground(new Color(26, 35, 126));
        jPanel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        JLabel jLabel = new JLabel("BKR Fast Food Management System", 2);
        jLabel.setFont(new Font("SansSerif", 1, 22));
        jLabel.setForeground(Color.WHITE);
        JPanel jPanel2 = new JPanel(new FlowLayout(2, 12, 0));
        jPanel2.setOpaque(false);
        this.serverStatusLabel = new JLabel("Server: " + ApiClient.getServerHost() + ":" + ApiClient.getServerPort());
        this.serverStatusLabel.setFont(new Font("SansSerif", 0, 13));
        this.serverStatusLabel.setForeground(new Color(200, 220, 255));
        JButton jButton = new JButton("\u2699 Server Connection");
        jButton.setFont(new Font("SansSerif", 1, 12));
        jButton.setBackground(new Color(40, 53, 147));
        jButton.setForeground(Color.WHITE);
        jButton.setFocusPainted(false);
        jButton.setCursor(new Cursor(12));
        jButton.addActionListener(actionEvent -> this.showServerSettingsDialog());
        jPanel2.add(this.serverStatusLabel);
        jPanel2.add(jButton);
        jPanel.add((Component)jLabel, "West");
        jPanel.add((Component)jPanel2, "East");
        return jPanel;
    }

    private void showServerSettingsDialog() {
        JPanel jPanel = new JPanel(new BorderLayout(10, 10));
        jPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JLabel jLabel = new JLabel("<html><b>Server Address (Local IP or Live Cloud URL):</b><br><font color='#555555'>Examples: <code>http://192.168.1.100:8080</code> or <code>https://api.bkrfastfood.com</code></font></html>");
        JTextField jTextField = new JTextField(ApiClient.getBaseUrl(), 28);
        jTextField.setFont(new Font("SansSerif", 0, 13));
        jPanel.add((Component)jLabel, "North");
        jPanel.add((Component)jTextField, "Center");
        int n = JOptionPane.showConfirmDialog(this, jPanel, "\u2699 Server Connection Settings", 2, -1);
        if (n == 0) {
            String string = jTextField.getText().trim();
            if (string.isEmpty()) {
                string = "http://localhost:8080/api";
            }
            ApiClient.saveConfig(string, 0);
            this.serverStatusLabel.setText("Server: " + ApiClient.getServerHost() + ":" + ApiClient.getServerPort());
            if (ApiClient.testConnection()) {
                JOptionPane.showMessageDialog(this, "Successfully connected to BKR Backend Server!\n" + ApiClient.getBaseUrl(), "Connected", 1);
            } else {
                JOptionPane.showMessageDialog(this, "Could not connect to server at:\n" + ApiClient.getBaseUrl() + "\n\nPlease check if backend server is online and URL is correct.", "Connection Warning", 2);
            }
            this.newOrderPanel.refreshMenuComboPublic();
            this.viewInventoryPanel.refresh();
        }
    }
}

