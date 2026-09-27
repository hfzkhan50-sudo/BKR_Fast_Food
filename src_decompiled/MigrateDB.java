/*
 * Decompiled with CFR 0.152.
 */
import db.DBConnection;
import java.sql.Connection;
import java.sql.Statement;

public class MigrateDB {
    public static void main(String[] stringArray) {
        try {
            Connection connection = DBConnection.getConnection();
            Statement statement = connection.createStatement();
            try {
                statement.executeUpdate("ALTER TABLE inventory_items DROP COLUMN unit");
                System.out.println("SUCCESS: Dropped unit column from inventory_items");
            }
            catch (Exception exception) {
                System.out.println("Note on inventory_items: " + exception.getMessage());
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}

