package util;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import javax.swing.JPanel;

/** Hand-painted circular emblem approximating the BKR Bacha Khan Restaurant badge. */
public class BKRLogoPanel extends JPanel {

    public BKRLogoPanel() {
        setOpaque(false);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(92, 92));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        int w = getWidth();
        int h = getHeight();
        int size = Math.min(w, h) - 4;
        int x = (w - size) / 2;
        int y = (h - size) / 2;
        int cx = x + size / 2;
        int cy = y + size / 2;

        // Soft drop shadow beneath the badge
        g2.setColor(new Color(0, 0, 0, 40));
        g2.fillOval(x + 2, y + 4, size, size);

        // Outer gold ring with a bevel gradient for a metallic, embossed look
        RadialGradientPaint goldPaint = new RadialGradientPaint(
            cx - size / 4f, cy - size / 4f, size,
            new float[]{0f, 0.6f, 1f},
            new Color[]{new Color(255, 235, 170), new Color(197, 149, 38), new Color(140, 100, 20)}
        );
        g2.setPaint(goldPaint);
        g2.fillOval(x, y, size, size);

        // Deep red dome with radial highlight to suggest a curved, glossy surface
        int margin = Math.max(3, size / 22);
        int rSize = size - margin * 2;
        RadialGradientPaint redPaint = new RadialGradientPaint(
            cx - rSize / 5f, cy - rSize / 3f, rSize,
            new float[]{0f, 0.7f, 1f},
            new Color[]{new Color(200, 60, 50), new Color(150, 28, 24), new Color(95, 16, 14)}
        );
        g2.setPaint(redPaint);
        g2.fillOval(x + margin, y + margin, rSize, rSize);

        // Thin inner gold hairline
        g2.setPaint(new Color(255, 221, 140));
        g2.setStroke(new BasicStroke(1.4f));
        int hairMargin = margin + 3;
        g2.drawOval(x + hairMargin, y + hairMargin, size - hairMargin * 2, size - hairMargin * 2);

        int innerRadius = rSize / 2 - 6;

        // Arched banner text following the inner ring
        g2.setFont(new Font("SansSerif", Font.BOLD, Math.max(8, size / 11)));
        drawArcText(g2, "BACHA KHAN RESTAURANT", cx, cy, innerRadius, 200, -220, Color.WHITE);
        drawArcText(g2, "AND FAST FOOD", cx, cy, innerRadius, -20, -220, new Color(255, 221, 140));

        // Chef bust silhouette in the center (toque + rounded collar)
        g2.setColor(Color.WHITE);
        int hatW = size / 3, hatH = size / 5;
        g2.fillOval(cx - hatW / 2, cy - size / 4 - hatH / 2, hatW, hatH);
        g2.fillRoundRect(cx - hatW / 2 + 3, cy - size / 4 + hatH / 4, hatW - 6, hatH / 2, 6, 6);
        int collarW = size / 3 + 6;
        g2.fillArc(cx - collarW / 2, cy - size / 20, collarW, size / 5, 0, 180);

        // "BKR" wordmark across the middle
        g2.setFont(new Font("Serif", Font.BOLD, Math.max(16, size / 3)));
        FontMetrics fm = g2.getFontMetrics();
        String bkr = "BKR";
        int bw = fm.stringWidth(bkr);
        g2.setColor(new Color(0, 0, 0, 70));
        g2.drawString(bkr, cx - bw / 2f + 1, cy + size / 7f + 1);
        g2.setColor(new Color(255, 221, 140));
        g2.drawString(bkr, cx - bw / 2f, cy + size / 7f);

        // Crossed fork & spoon crest at the base of the badge
        g2.setColor(new Color(255, 221, 140));
        g2.setStroke(new BasicStroke(2f));
        int crestY = y + size - margin - 2;
        g2.drawLine(cx - 9, crestY - 10, cx - 2, crestY + 2);
        g2.drawLine(cx + 9, crestY - 10, cx + 2, crestY + 2);
        g2.fillOval(cx + 4, crestY - 13, 6, 6);

        g2.dispose();
    }

    /** Draws text curved along a circular arc centered at (cx, cy). */
    private void drawArcText(Graphics2D g2, String text, int cx, int cy, int radius, double startAngleDeg, double sweepDeg, Color color) {
        FontMetrics fm = g2.getFontMetrics();
        double totalWidth = fm.stringWidth(text);
        double maxSweep = Math.toRadians(sweepDeg);
        double angleStep = totalWidth == 0 ? 0 : maxSweep / totalWidth;
        double angle = Math.toRadians(startAngleDeg);
        g2.setColor(color);
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            double charWidth = fm.charWidth(c);
            angle += angleStep * (charWidth / 2.0);
            AffineTransform old = g2.getTransform();
            double px = cx + radius * Math.cos(angle);
            double py = cy + radius * Math.sin(angle);
            g2.translate(px, py);
            g2.rotate(angle + Math.PI / 2);
            g2.drawString(String.valueOf(c), -(float) charWidth / 2f, 0);
            g2.setTransform(old);
            angle += angleStep * (charWidth / 2.0);
        }
    }
}
