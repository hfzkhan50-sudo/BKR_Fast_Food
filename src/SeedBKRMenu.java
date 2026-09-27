package util;

import dao.MenuDAO;
import java.math.BigDecimal;
import model.MenuItem;
import java.util.List;

public class SeedBKRMenu {
    public static void main(String[] args) {
        try {
            System.out.println("Seeding BKR Fast Food Menu Items...");
            MenuDAO menuDAO = new MenuDAO();
            
            // Check existing menu items to avoid duplicates
            List<MenuItem> existing = menuDAO.getAllMenuItems();
            System.out.println("Current items count: " + existing.size());

            if (!existing.isEmpty()) {
                System.out.println("Menu already contains items. Skipping seed to prevent duplicates.");
                return;
            }

            int addedCount = 0;

            // Helper lambda
            Runnable seedAll = () -> {
                // --- PIZZA ---
                addPizza(menuDAO, "BKR Special Pizza", 899, 1599, 2099, 2950);
                addPizza(menuDAO, "Chicken Tikka", 650, 1150, 1599, 2499);
                addPizza(menuDAO, "Chicken Fajita", 650, 1150, 1599, 2499);
                addPizza(menuDAO, "Chicken Supreme", 850, 1450, 1699, 2899);
                addPizza(menuDAO, "Cheese Lover", 650, 1150, 1599, 2499);
                addPizza(menuDAO, "Hot & Spicy", 650, 1150, 1599, 2499);
                addPizza(menuDAO, "Malai Boti", 799, 1250, 1699, 2750);
                addPizza(menuDAO, "Crown Crust", -1, 1599, 2099, 2950); // No small
                addPizza(menuDAO, "BBQ- Smoky", 650, 1150, 1599, 2499);
                addPizza(menuDAO, "Behari Kabab", 899, 1550, 1950, 2850);
                addPizza(menuDAO, "Stuff Crust Pizza", 899, 1550, 1950, 2850);
                addPizza(menuDAO, "Extra Topping", 150, 220, 270, 330);

                // --- BURGER ---
                addItem(menuDAO, "BKR Special Burger", "Burger", 500);
                addItem(menuDAO, "Zinger Chees Burger", "Burger", 450);
                addItem(menuDAO, "Zinger Burger", "Burger", 400);
                addItem(menuDAO, "Patty Fatty Burger", "Burger", 250);
                addItem(menuDAO, "Tower Burger", "Burger", 550);
                addItem(menuDAO, "SP Chicken Burger", "Burger", 550);
                addItem(menuDAO, "Beef Burger", "Burger", 550);
                addItem(menuDAO, "Beef Jalapeno Burger", "Burger", 500);
                addItem(menuDAO, "One Daddy Fillet Burger", "Burger", 650);

                // --- CHICKEN BROAST ---
                addItem(menuDAO, "Quarter Broast 2 Pcs", "Chicken Broast", 600);
                addItem(menuDAO, "Half Broast 4 Pcs", "Chicken Broast", 1200);
                addItem(menuDAO, "Full Broast 8 Pcs", "Chicken Broast", 2350);

                // --- HOT WINGS ---
                addItem(menuDAO, "Hot Wings 10 Pcs", "Hot Wings", 599);
                addItem(menuDAO, "Honey Wings 6 Pcs", "Hot Wings", 450);
                addItem(menuDAO, "BBQ. Wings 6 Pcs", "Hot Wings", 400);
                addItem(menuDAO, "Oven Baked 6 Pcs", "Hot Wings", 400);
                addItem(menuDAO, "Nuggets 10 Pcs", "Hot Wings", 550);

                // --- FRIES ---
                addItem(menuDAO, "Loaded Fries", "Fries", 450);
                addItem(menuDAO, "Masala Fries", "Fries", 250);
                addItem(menuDAO, "Plain Fries", "Fries", 200);
                addItem(menuDAO, "Garlic Mayo Fries", "Fries", 250);

                // --- DRINKS ---
                addItem(menuDAO, "Regular", "Drinks", 80);
                addItem(menuDAO, "1 Ltr", "Drinks", 190);
                addItem(menuDAO, "1.5 Ltr", "Drinks", 220);
                addItem(menuDAO, "Water (small)", "Drinks", 60);
                addItem(menuDAO, "Water (large)", "Drinks", 120);
                addItem(menuDAO, "Ten Pack", "Drinks", 160);

                // --- JUICES ---
                addItem(menuDAO, "Mint By Garita", "Juices", 199);
                addItem(menuDAO, "Lemon Juice", "Juices", 199);

                // --- DEALS ---
                addItem(menuDAO, "Deal-1 (1 Small Pizza, 2 Zinger Burger, 1 Ltr Drink)", "Deals", 1499);
                addItem(menuDAO, "Deal-2 (2 Small Pizza, Quarter Broast, 1 Ltr Drink)", "Deals", 1999);
                addItem(menuDAO, "Deal-3 (2 Medium Pizza, 6 Pcs Wings, 1 Ltr Drink)", "Deals", 3100);
                addItem(menuDAO, "Family Deal (2 Large Pizza, 6 Pcs Wings, 2 Zinger Burger, 2 Crispy Burger, 1.5 Ltr Drink)", "Deals", 4399);
                addItem(menuDAO, "Student Deal (1 Burger, Regular Drink)", "Deals", 549);
                addItem(menuDAO, "BKR Super Platter", "Deals", 3450);

                // --- PASTA ---
                addItem(menuDAO, "BKR Special Pasta", "Pasta", 599);
                addItem(menuDAO, "Crunchy Pasta", "Pasta", 599);

                // --- PARATHA ROLL ---
                addItem(menuDAO, "Paratha Roll", "Paratha Roll", 350);
                addItem(menuDAO, "Cheese Roll", "Paratha Roll", 400);
                addItem(menuDAO, "Zinger Roll", "Paratha Roll", 450);
                addItem(menuDAO, "Bihari Roll", "Paratha Roll", 499);

                // --- SHAWARMA ---
                addItem(menuDAO, "Chicken Shawarma", "Shawarma", 150);
                addItem(menuDAO, "Cheese Shawarma", "Shawarma", 200);
                addItem(menuDAO, "Zinger Shawarma", "Shawarma", 400);

                // --- WRAP ---
                addItem(menuDAO, "Tikka Wrap", "Wrap", 399);
                addItem(menuDAO, "Fajita Wrap", "Wrap", 399);

                // --- BKR SPECIAL KARAHI (Half/Full) ---
                addSized(menuDAO, "Chicken Karahi", "BKR Special Karahi", 899, 1699);
                addSized(menuDAO, "Chicken White Karahi", "BKR Special Karahi", 999, 1899);
                addSized(menuDAO, "Chicken Makhni Karahi", "BKR Special Karahi", 999, 1899);
                addSized(menuDAO, "Chicken Achari Karahi", "BKR Special Karahi", 999, 1899);
                addSized(menuDAO, "Special Mutton Karahi", "BKR Special Karahi", 1700, 3400);

                // --- BKR HANDI (BONELESS) (Half/Full) ---
                addSized(menuDAO, "Chicken Handi", "BKR Handi (Boneless)", 1099, 1999);
                addSized(menuDAO, "Chicken White Handi", "BKR Handi (Boneless)", 1199, 2299);
                addSized(menuDAO, "Makhni Handi", "BKR Handi (Boneless)", 1199, 2299);
                addSized(menuDAO, "Chicken Achari Handi", "BKR Handi (Boneless)", 1199, 2299);

                // --- BKR SOUP (BONELESS) ---
                addItem(menuDAO, "Hot & Sour Soup", "BKR Soup (Boneless)", 399);
                addItem(menuDAO, "Chicken Corn Soup", "BKR Soup (Boneless)", 399);
                addItem(menuDAO, "Vegetable Soup", "BKR Soup (Boneless)", 399);
                addItem(menuDAO, "Family Bowl", "BKR Soup (Boneless)", 999);

                // --- BKR NAAN (ROTI) ---
                addItem(menuDAO, "Simple Naan", "BKR Naan (Roti)", 30);
                addItem(menuDAO, "Roti", "BKR Naan (Roti)", 30);
                addItem(menuDAO, "Kulcha Naan", "BKR Naan (Roti)", 40);
                addItem(menuDAO, "Roghni Naan", "BKR Naan (Roti)", 60);
            };

            seedAll.run();
            System.out.println("SUCCESS: BKR Fast Food Menu Items seeded!");
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void addPizza(MenuDAO menuDAO, String name, double small, double medium, double large, double xl) {
        if (small > 0) menuDAO.createMenuItem(name, "Pizza", BigDecimal.valueOf(small), "Small");
        if (medium > 0) menuDAO.createMenuItem(name, "Pizza", BigDecimal.valueOf(medium), "Medium");
        if (large > 0) menuDAO.createMenuItem(name, "Pizza", BigDecimal.valueOf(large), "Large");
        if (xl > 0) menuDAO.createMenuItem(name, "Pizza", BigDecimal.valueOf(xl), "XL");
    }

    private static void addSized(MenuDAO menuDAO, String name, String category, double half, double full) {
        if (half > 0) menuDAO.createMenuItem(name, category, BigDecimal.valueOf(half), "Half");
        if (full > 0) menuDAO.createMenuItem(name, category, BigDecimal.valueOf(full), "Full");
    }

    private static void addItem(MenuDAO menuDAO, String name, String category, double price) {
        menuDAO.createMenuItem(name, category, BigDecimal.valueOf(price), "None");
    }
}
