package ci553.happyshop.client.customer;

import ci553.happyshop.catalogue.Product;
import org.junit.jupiter.api.Test;
import ci553.happyshop.catalogue.UnderMinimumPaymentException;
import ci553.happyshop.catalogue.ExcessiveOrderQuantityException;


import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CustomerModelBusinessRulesTest {

    @Test
    void validateTrolley_throwsUnderMinimumPayment_whenTotalBelow5() {
        CustomerModel model = new CustomerModel();

        ArrayList<Product> trolley = new ArrayList<>();
        Product p = new Product("0001", "Test item", "img.jpg", 1.00, 999);
        p.setOrderedQuantity(1); // total = 1.00
        trolley.add(p);

        assertThrows(UnderMinimumPaymentException.class,
                () -> model.validateTrolley(trolley));
    }

    @Test
    void validateTrolley_throwsExcessiveOrderQuantity_whenQtyAbove50() {
        CustomerModel model = new CustomerModel();

        ArrayList<Product> trolley = new ArrayList<>();
        Product p = new Product("0002", "Too many item", "img.jpg", 2.00, 999);
        p.setOrderedQuantity(51); // > 50
        trolley.add(p);

        assertThrows(ExcessiveOrderQuantityException.class,
                () -> model.validateTrolley(trolley));
    }

    @Test
    void validateTrolley_passes_whenRulesSatisfied() {
        CustomerModel model = new CustomerModel();

        ArrayList<Product> trolley = new ArrayList<>();
        Product p = new Product("0003", "Valid", "img.jpg", 5.00, 999);
        p.setOrderedQuantity(1); // total = 5.00, qty <= 50
        trolley.add(p);

        assertDoesNotThrow(() -> model.validateTrolley(trolley));
    }
    @Test
    void validateTrolley_passes_whenTotalIsExactly5() {
        CustomerModel model = new CustomerModel();

        ArrayList<Product> trolley = new ArrayList<>();
        Product p = new Product("0100", "Boundary total", "img.jpg", 5.00, 999);
        p.setOrderedQuantity(1); // total = 5.00 exactly
        trolley.add(p);

        assertDoesNotThrow(() -> model.validateTrolley(trolley));
    }

    @Test
    void validateTrolley_passes_whenQuantityIsExactly50() {
        CustomerModel model = new CustomerModel();

        ArrayList<Product> trolley = new ArrayList<>();
        Product p = new Product("0200", "Boundary quantity", "img.jpg", 0.10, 999);
        p.setOrderedQuantity(50); // quantity = 50 exactly (allowed)

        // total = 50 * 0.10 = 5.00, also meets minimum payment
        trolley.add(p);

        assertDoesNotThrow(() -> model.validateTrolley(trolley));
    }

}




