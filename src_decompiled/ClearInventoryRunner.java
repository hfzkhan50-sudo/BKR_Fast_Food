/*
 * Decompiled with CFR 0.152.
 */
import dao.InventoryDAO;

public class ClearInventoryRunner {
    public static void main(String[] stringArray) {
        try {
            System.out.println("Clearing inventory items...");
            InventoryDAO inventoryDAO = new InventoryDAO();
            inventoryDAO.clearAllItems();
            System.out.println("SUCCESS: All inventory items have been deleted from database.");
        }
        catch (Exception exception) {
            System.err.println("ERROR: " + exception.getMessage());
            exception.printStackTrace();
        }
    }
}

