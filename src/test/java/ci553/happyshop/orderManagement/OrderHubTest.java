package ci553.happyshop.orderManagement;

import ci553.happyshop.catalogue.Product;
import org.junit.jupiter.api.Test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class OrderHubTest {

    @Test
    void newOrder_firesOrderMapPropertyChangeEvent() throws Exception {
        // Arrange
        OrderHub hub = OrderHub.getOrderHub();
        hub.initializeOrderMap();

        AtomicReference<PropertyChangeEvent> captured = new AtomicReference<>();

        PropertyChangeListener listener = evt -> {
            if ("orderMap".equals(evt.getPropertyName())) {
                captured.set(evt);
            }
        };

        hub.addPropertyChangeListener(listener);

        ArrayList<Product> trolley = new ArrayList<>();
        Product p = new Product("0001", "Test item", "img.jpg", 10.0, 999);
        p.setOrderedQuantity(1);
        trolley.add(p);

        // Act
        hub.newOrder(trolley);

        // Assert
        assertNotNull(captured.get(), "Expected an 'orderMap' change event to be fired");
        assertEquals("orderMap", captured.get().getPropertyName());
        assertNotNull(captured.get().getNewValue(), "Expected event to contain updated orderMap");
    }
}
