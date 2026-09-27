/*
 * Decompiled with CFR 0.152.
 */
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import ui.MainFrame;

public class Main {
    public static void main(String[] stringArray) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            catch (Exception exception) {
                // empty catch block
            }
            new MainFrame().setVisible(true);
        });
    }
}

