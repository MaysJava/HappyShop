package ci553.happyshop.catalogue;
import ci553.happyshop.orderManagement.OrderState;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Order class
 */
class OrderTest {

    @Test
    void orderStartsInOrderedState() {

        // Arrange (set up test data)
        ArrayList<Product> products = new ArrayList<>();

        Order order = new Order(
                1,                              // orderId
                OrderState.Ordered,             // initial state
                "2025-01-01 10:00",              // ordered date/time
                products                        // product list
        );

        // Act + Assert (check behaviour)
        assertEquals(OrderState.Ordered, order.getState());
    }
}
