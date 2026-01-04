package ci553.happyshop.client.customer;

import java.io.IOException;
import java.sql.SQLException;

public class CustomerController {
    public CustomerModel cusModel;

    public void doAction(String action) throws SQLException, IOException {
        switch (action) {
            case "Search":
                cusModel.search();
                break;
            case "Add to Trolley":
                cusModel.addToTrolley();
                break;
            case "Cancel":
                cusModel.cancel();
                break;
            case "Check Out":
                cusModel.checkOut();
                break;
            case "OK & Close":
                cusModel.closeReceipt();
                break;
        }
    }
    public void removeItem(String productId) {
        cusModel.removeFromTrolley(productId);
    }

    public void changeQty(String productId, int qty) {
        cusModel.setQuantity(productId, qty);
    }


}
