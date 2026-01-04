package ci553.happyshop.client.manager;

import ci553.happyshop.client.customer.CustomerClient;
import ci553.happyshop.client.login.LoginClient;
import ci553.happyshop.client.login.LoginModel;
import ci553.happyshop.client.orderTracker.OrderTrackerClient;
import ci553.happyshop.client.picker.PickerClient;
import ci553.happyshop.client.warehouse.WarehouseClient;
import javafx.stage.Stage;

public class ManagerController {

    private final ManagerView view;

    public ManagerController(ManagerView view) {
        this.view = view;
    }

    public void launchCustomer() {
        LoginClient.showLoginForRole(LoginModel.Role.CUSTOMER, () -> {
            try { new CustomerClient().start(new Stage()); } catch (Exception e) { e.printStackTrace(); }
        });
    }

    public void launchPicker() {
        LoginClient.showLoginForRole(LoginModel.Role.PICKER, () -> {
            try { new PickerClient().start(new Stage()); } catch (Exception e) { e.printStackTrace(); }
        });
    }

    public void launchTracker() {
        LoginClient.showLoginForRole(LoginModel.Role.TRACKER, () -> {
            try { new OrderTrackerClient().start(new Stage()); } catch (Exception e) { e.printStackTrace(); }
        });
    }

    public void launchWarehouse() {
        LoginClient.showLoginForRole(LoginModel.Role.WAREHOUSE, () -> {
            try { new WarehouseClient().start(new Stage()); } catch (Exception e) { e.printStackTrace(); }
        });
    }
}

