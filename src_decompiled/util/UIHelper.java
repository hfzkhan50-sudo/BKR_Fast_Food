/*
 * Decompiled with CFR 0.152.
 */
package util;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.plaf.basic.BasicButtonUI;

public class UIHelper {
    public static JButton createButton(String string, final Color color, Color color2, int n) {
        final JButton jButton = new JButton(string);
        jButton.setUI(new BasicButtonUI());
        jButton.setFont(new Font("SansSerif", 1, n));
        jButton.setBackground(color);
        jButton.setForeground(color2);
        jButton.setFocusPainted(false);
        jButton.setOpaque(true);
        jButton.setContentAreaFilled(true);
        jButton.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(color.darker(), 1), BorderFactory.createEmptyBorder(6, 14, 6, 14)));
        jButton.setCursor(new Cursor(12));
        jButton.addMouseListener(new MouseAdapter(){

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                jButton.setBackground(color.brighter());
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                jButton.setBackground(color);
            }
        });
        return jButton;
    }
}

