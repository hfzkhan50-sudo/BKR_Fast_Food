/*
 * Decompiled with CFR 0.152.
 */
package util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class WrapLayout
extends FlowLayout {
    public WrapLayout() {
    }

    public WrapLayout(int n) {
        super(n);
    }

    public WrapLayout(int n, int n2, int n3) {
        super(n, n2, n3);
    }

    @Override
    public Dimension preferredLayoutSize(Container container) {
        return this.layoutSize(container, true);
    }

    @Override
    public Dimension minimumLayoutSize(Container container) {
        Dimension dimension = this.layoutSize(container, false);
        dimension.width -= this.getHgap() + 1;
        return dimension;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Dimension layoutSize(Container container, boolean bl) {
        Object object = container.getTreeLock();
        synchronized (object) {
            int n = container.getWidth();
            if (n == 0) {
                n = Integer.MAX_VALUE;
            }
            int n2 = this.getHgap();
            int n3 = this.getVgap();
            Insets insets = container.getInsets();
            int n4 = insets.left + insets.right + n2 * 2;
            int n5 = n - n4;
            Dimension dimension = new Dimension(0, 0);
            int n6 = 0;
            int n7 = 0;
            int n8 = container.getComponentCount();
            for (int i = 0; i < n8; ++i) {
                Dimension dimension2;
                Component component = container.getComponent(i);
                if (!component.isVisible()) continue;
                Dimension dimension3 = dimension2 = bl ? component.getPreferredSize() : component.getMinimumSize();
                if (n6 + dimension2.width > n5 && n6 > 0) {
                    dimension.width = Math.max(dimension.width, n6);
                    dimension.height += n7 + n3;
                    n6 = 0;
                    n7 = 0;
                }
                if (n6 != 0) {
                    n6 += n2;
                }
                n6 += dimension2.width;
                n7 = Math.max(n7, dimension2.height);
            }
            dimension.width = Math.max(dimension.width, n6);
            dimension.height += n7;
            dimension.width += n4;
            dimension.height += insets.top + insets.bottom + n3 * 2;
            Container container2 = SwingUtilities.getAncestorOfClass(JScrollPane.class, container);
            if (container2 != null && container.isValid()) {
                dimension.width -= n2 + 1;
            }
            return dimension;
        }
    }
}

