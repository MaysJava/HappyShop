package ci553.happyshop.client.login;

import ci553.happyshop.client.customer.CustomerController;
import ci553.happyshop.client.customer.CustomerModel;
import ci553.happyshop.client.customer.CustomerView;
import ci553.happyshop.client.orderTracker.OrderTracker;
import ci553.happyshop.client.picker.PickerController;
import ci553.happyshop.client.picker.PickerModel;
import ci553.happyshop.client.picker.PickerView;
import ci553.happyshop.client.warehouse.WarehouseController;
import ci553.happyshop.client.warehouse.WarehouseModel;
import ci553.happyshop.client.warehouse.WarehouseView;
import ci553.happyshop.storageAccess.DatabaseRW;
import ci553.happyshop.storageAccess.DatabaseRWFactory;
import ci553.happyshop.orderManagement.OrderHub;
import javafx.stage.Stage;

public class LoginController {

    private final LoginView view;
    private final LoginModel model;

    private LoginModel.Role requiredRole = null; // null = allow any
    private Runnable onSuccess = null;

    public LoginController(LoginView view, LoginModel model) {
        this.view = view;
        this.model = model;
    }

    public void setRequiredRole(LoginModel.Role requiredRole) {
        this.requiredRole = requiredRole;
    }

    public void setOnSuccess(Runnable onSuccess) {
        this.onSuccess = onSuccess;
    }

    public void doLogin(String username, String password) {

        LoginModel.Role role = model.authenticate(username, password);

        if (role == LoginModel.Role.NONE) {
            view.showMessage("Wrong login. Try: customer / 1234");
            return;
        }

        // If this login was opened for a specific role (from manager buttons)
        if (requiredRole != null && role != requiredRole) {
            view.showMessage("This button requires: " + requiredRole);
            return;
        }

        // Close login window
        Stage loginStage = view.getWindow();
        loginStage.close();

        // Run success action (open manager or open client)
        if (onSuccess != null) {
            onSuccess.run();
        }
    }
}
