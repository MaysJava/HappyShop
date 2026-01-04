package ci553.happyshop.client.manager;

import javafx.stage.Stage;

public class ManagerClient {
    public void start(Stage window) {
        ManagerView view = new ManagerView();
        ManagerController controller = new ManagerController(view);
        view.controller = controller;
        view.start(window);
    }
}
