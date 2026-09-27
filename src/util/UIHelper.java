/*
 * Decompiled with CFR 0.152.
 */
package util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public class UIHelper {

    // BKR Theme Colors matching the flyer menu
    public static final Color DARK_BG = new Color(244, 239, 231);
    public static final Color PANEL_BG = new Color(255, 252, 247);
    public static final Color CARD_BG = new Color(255, 248, 236);
    public static final Color BKR_RED = new Color(157, 38, 32);
    public static final Color BKR_RED_DARK = new Color(102, 25, 22);
    public static final Color BKR_RED_HOVER = new Color(190, 53, 43);
    public static final Color BKR_GOLD = new Color(190, 132, 26);
    public static final Color BKR_GOLD_BRIGHT = new Color(166, 108, 13);
    public static final Color TEXT_WHITE = new Color(45, 36, 31);
    public static final Color TEXT_MUTED = new Color(116, 101, 88);
    public static final Color BORDER_DARK = new Color(218, 205, 190);

    public static final Font FONT_BASE = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);

    public static JButton createButton(String string, final Color color, Color color2, int n) {
        final JButton jButton = new JButton(string);
        jButton.setFont(new Font("Segoe UI", Font.BOLD, n));
        jButton.setBackground(color);
        jButton.setForeground(color2);
        jButton.setFocusPainted(false);
        jButton.setOpaque(false);
        jButton.setContentAreaFilled(false);
        jButton.setRolloverEnabled(true);
        jButton.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        jButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        jButton.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                JButton b = (JButton) c;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color base = b.getBackground();
                if (b.getModel().isPressed()) {
                    base = base.darker();
                } else if (b.getModel().isRollover()) {
                    base = base.brighter();
                }
                if (!b.isEnabled()) {
                    base = new Color(base.getRed(), base.getGreen(), base.getBlue(), 110);
                }

                int w = b.getWidth();
                int h = b.getHeight();
                int arc = 12;

                // Soft drop shadow
                g2.setColor(new Color(60, 30, 20, 26));
                g2.fillRoundRect(2, 3, w - 4, h - 3, arc, arc);

                // Gradient body (lighter at top, brand color at bottom)
                g2.setPaint(new GradientPaint(0, 0, base.brighter(), 0, h, base));
                g2.fillRoundRect(1, 1, w - 3, h - 4, arc, arc);

                // Outline
                g2.setColor(base.darker());
                g2.drawRoundRect(1, 1, w - 3, h - 4, arc, arc);

                // Centered label
                g2.setColor(b.getForeground());
                g2.setFont(b.getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (w - fm.stringWidth(b.getText())) / 2;
                int y = (h - 2 + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(b.getText(), x, y);
                g2.dispose();
            }
        });
        return jButton;
    }

    public static Border createCustomTitledBorder(String title) {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER_DARK, 1),
                " " + title + " ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                BKR_GOLD
            ),
            BorderFactory.createEmptyBorder(6, 6, 6, 6)
        );
    }

    public static void styleTable(JTable table) {
        table.setBackground(Color.WHITE);
        table.setForeground(TEXT_WHITE);
        table.setGridColor(BORDER_DARK);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(new Color(245, 225, 204));
        table.setSelectionForeground(new Color(70, 45, 32));
        table.setFont(FONT_BASE);
        table.setRowHeight(32);
        table.setFillsViewportHeight(true);

        // Zebra striping with padded cells
        DefaultTableCellRenderer zebra = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    c.setBackground(new Color(245, 225, 204));
                    c.setForeground(new Color(70, 45, 32));
                } else {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 244, 234));
                    c.setForeground(TEXT_WHITE);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return c;
            }
        };
        table.setDefaultRenderer(Object.class, zebra);

        JTableHeader header = table.getTableHeader();
        header.setBackground(BKR_RED);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(t, value, false, false, row, column);
                label.setOpaque(true);
                label.setBackground(BKR_RED);
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Segoe UI", Font.BOLD, 14));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return label;
            }
        });
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BKR_GOLD));
        header.setPreferredSize(new Dimension(0, 36));
        header.setReorderingAllowed(false);
    }

    public static void styleTextField(final JTextField field) {
        field.setBackground(CARD_BG);
        field.setForeground(TEXT_WHITE);
        field.setCaretColor(BKR_GOLD);
        field.setFont(FONT_BASE);
        final Border normalBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_DARK, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        );
        final Border focusBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BKR_GOLD, 2),
            BorderFactory.createEmptyBorder(4, 9, 4, 9)
        );
        field.setBorder(normalBorder);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(focusBorder);
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(normalBorder);
            }
        });
    }


    /** Slim, rounded, modern scrollbars for scroll panes. */
    public static void styleScrollPane(JScrollPane pane) {
        pane.setBorder(BorderFactory.createLineBorder(BORDER_DARK, 1));
        pane.getViewport().setBackground(PANEL_BG);
        JScrollBar vertical = pane.getVerticalScrollBar();
        vertical.setUI(new ModernScrollBarUI());
        vertical.setUnitIncrement(16);
        vertical.setPreferredSize(new Dimension(10, 0));
        JScrollBar horizontal = pane.getHorizontalScrollBar();
        horizontal.setUI(new ModernScrollBarUI());
        horizontal.setPreferredSize(new Dimension(0, 10));
    }

    /** Consistent themed look for dropdowns. */
    public static void styleComboBox(JComboBox<?> combo) {
        combo.setFont(FONT_BASE);
        combo.setBackground(CARD_BG);
        combo.setForeground(TEXT_WHITE);
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_DARK, 1),
            BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));
        combo.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /** Turns a label into a small stat card with a colored accent strip. */
    public static void styleSummaryCard(JLabel label, Color accentColor) {
        label.setOpaque(true);
        label.setBackground(CARD_BG);
        label.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor),
                BorderFactory.createLineBorder(BORDER_DARK, 1)
            ),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
    }

    private static class ModernScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = new Color(198, 178, 152);
            this.trackColor = new Color(244, 239, 231);
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        private JButton createZeroButton() {
            JButton button = new JButton();
            Dimension zero = new Dimension(0, 0);
            button.setPreferredSize(zero);
            button.setMinimumSize(zero);
            button.setMaximumSize(zero);
            return button;
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(thumbColor);
            g2.fillRoundRect(thumbBounds.x + 1, thumbBounds.y + 1, thumbBounds.width - 2, thumbBounds.height - 2, 8, 8);
            g2.dispose();
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            g.setColor(trackColor);
            g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
        }
    }
}


