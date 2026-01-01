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

    public LoginController(LoginView view, LoginModel model) {
        this.view = view;
        this.model = model;
    }

    public void doLogin(String username, String password) {

        LoginModel.Role role = model.authenticate(username, password);

        if (role == LoginModel.Role.NONE) {
            view.showMessage("Wrong login. Try: customer / 1234");
            return;
        }

        // ✅ close login window
        view.getWindow().close();

        // ✅ open the chosen client
        switch (role) {
            case CUSTOMER -> startCustomerClient();
            case PICKER -> startPickerClient();
            case WAREHOUSE -> startWarehouseClient();
            case TRACKER -> startOrderTrackerClient();
        }
    }

    private void startCustomerClient() {
        CustomerView cusView = new CustomerView();
        CustomerController cusController = new CustomerController();
        CustomerModel cusModel = new CustomerModel();
        DatabaseRW databaseRW = DatabaseRWFactory.createDatabaseRW();

        cusView.cusController = cusController;
        cusController.cusModel = cusModel;
        cusModel.cusView = cusView;
        cusModel.databaseRW = databaseRW;

        cusView.start(new Stage());
    }

    private void startPickerClient() {
        PickerModel pickerModel = new PickerModel();
        PickerView pickerView = new PickerView();
        PickerController pickerController = new PickerController();

        pickerView.pickerController = pickerController;
        pickerController.pickerModel = pickerModel;
        pickerModel.pickerView = pickerView;

        pickerModel.registerWithOrderHub();
        pickerView.start(new Stage());

        // optional but good: ensure orderhub map exists
        OrderHub.getOrderHub().initializeOrderMap();
    }

    private void startWarehouseClient() {
        WarehouseView view = new WarehouseView();
        WarehouseController controller = new WarehouseController();
        WarehouseModel model = new WarehouseModel();
        DatabaseRW databaseRW = DatabaseRWFactory.createDatabaseRW();

        view.controller = controller;
        controller.model = model;
        model.view = view;
        model.databaseRW = databaseRW;

        view.start(new Stage());
    }

    private void startOrderTrackerClient() {
        OrderTracker orderTracker = new OrderTracker();
        orderTracker.registerWithOrderHub();

        // optional but good: ensure orderhub map exists
        OrderHub.getOrderHub().initializeOrderMap();
    }
}

