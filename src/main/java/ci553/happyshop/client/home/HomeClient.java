package ci553.happyshop.client.home;

import ci553.happyshop.client.customer.CustomerClient;
import ci553.happyshop.client.login.LoginClient;
import ci553.happyshop.client.picker.PickerClient;
import ci553.happyshop.client.warehouse.WarehouseClient;
import ci553.happyshop.client.orderTracker.OrderTrackerClient;
import ci553.happyshop.client.login.LoginModel;

import javafx.scene.control.Separator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;


import ci553.happyshop.client.createAccount.CreateAccountClient;


import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ci553.happyshop.utility.WindowManager;

public class HomeClient extends Application {

    @Override
    public void start(Stage stage) {
        Label title = new Label("HappyShop");
        title.getStyleClass().add("title");

        Label subtitle = new Label("Simple • Fast • Reliable");
        subtitle.getStyleClass().add("subtitle");

        Label footer = new Label("University of Brighton • CI553 HappyShop Project");
        footer.getStyleClass().add("footer");


        Button btnCustomerLogin = new Button("Customer Login");
        btnCustomerLogin.getStyleClass().addAll("button", "button-primary");

        Button btnGuest = new Button("Continue as Guest");
        btnGuest.getStyleClass().addAll("button", "button-secondary");

        Button btnCreate = new Button("Create Account");
        btnCreate.getStyleClass().addAll("button", "button-secondary");

        Button btnWarehouseLogin = new Button("Warehouse Login");
        btnWarehouseLogin.getStyleClass().addAll("button", "button-danger");


        VBox customerActions = new VBox(12, btnCustomerLogin, btnGuest);
        customerActions.setAlignment(Pos.CENTER);

        VBox secondaryActions = new VBox(10, btnCreate, btnWarehouseLogin);
        secondaryActions.setAlignment(Pos.CENTER);


        //  actions
        btnGuest.setOnAction(e -> {
            stage.hide();
            WindowManager.openCustomerSession(stage);
        });


        btnCustomerLogin.setOnAction(e -> {

            LoginClient.showLoginForRole(LoginModel.Role.CUSTOMER, () -> {

                    stage.hide();
                    WindowManager.openCustomerSession(stage);

            });
        });


        btnWarehouseLogin.setOnAction(e -> {

            LoginClient.showLoginForRole(LoginModel.Role.WAREHOUSE, () -> {
                try {
                    stage.hide();
                    new ci553.happyshop.client.warehouse.WarehouseClient().start(new Stage());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        });


        btnCreate.setOnAction(e -> {
            try {
                stage.hide();
                new CreateAccountClient(stage).start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Label staffLabel = new Label("Staff access");
        staffLabel.getStyleClass().add("muted");

        Separator staffDivider = new Separator();
        staffDivider.setMaxWidth(260);

        VBox staffActions = new VBox(10, staffLabel, staffDivider, btnWarehouseLogin);
        staffActions.setAlignment(Pos.CENTER);

        VBox card = new VBox(16, title, subtitle, customerActions, secondaryActions,staffActions, footer);


        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(24));
        card.getStyleClass().add("card");

        VBox root = new VBox(card);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.getStyleClass().addAll("root", "home-background");

        Scene scene = new Scene(root, 900, 700);
        scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());

        stage.setTitle("HappyShop");
        stage.setScene(scene);

        stage.setMinWidth(900);
        stage.setMinHeight(700);
        stage.setResizable(true);
        stage.show();

    }
}
