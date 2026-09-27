/*
 * Decompiled with CFR 0.152.
 */
import dao.MenuDAO;

public class ClearMenuRunner {
    public static void main(String[] stringArray) {
        try {
            System.out.println("Clearing default menu items...");
            MenuDAO menuDAO = new MenuDAO();
            menuDAO.clearAllMenuItems();
            System.out.println("SUCCESS: All menu items have been deleted from database.");
        }
        catch (Exception exception) {
            System.err.println("ERROR: " + exception.getMessage());
            exception.printStackTrace();
        }
    }
}

