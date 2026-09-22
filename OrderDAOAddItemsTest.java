import dao.OrderDAO;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import model.OrderItem;

public class OrderDAOAddItemsTest {
    public static void main(String[] args) {
        OrderDAO dao = new OrderDAO();
        List<OrderItem> items = new ArrayList<>();
        OrderItem item = new OrderItem(0, "Tea", 2, new BigDecimal("60"));
        item.setLineTotal(new BigDecimal("120"));
        items.add(item);

        try {
            dao.getClass().getMethod("addItemsToOrder", int.class, List.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("addItemsToOrder API is missing", e);
        }

        System.out.println("OrderDAO addItemsToOrder method exists");
    }
}
