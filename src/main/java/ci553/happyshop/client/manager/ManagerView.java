package ci553.happyshop.client.manager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ManagerView {

    public ManagerController controller;

    public void start(Stage window) {

        Label title = new Label("HappyShop Manager Panel");
        title.getStyleClass().add("title");

        Button btnCustomer = new Button("Launch Customer");
        btnCustomer.getStyleClass().addAll("button", "button-primary");
        btnCustomer.setOnAction(e -> controller.launchCustomer());

        Button btnPicker = new Button("Launch Picker");
        btnPicker.getStyleClass().addAll("button", "button-primary");
        btnPicker.setOnAction(e -> controller.launchPicker());

        Button btnTracker = new Button("Launch Tracker");
        btnTracker.getStyleClass().addAll("button", "button-primary");
        btnTracker.setOnAction(e -> controller.launchTracker());

        Button btnWarehouse = new Button("Launch Warehouse");
        btnWarehouse.getStyleClass().addAll("button", "button-secondary");
        btnWarehouse.setOnAction(e -> controller.launchWarehouse());

        VBox card = new VBox(12, title, btnCustomer, btnPicker, btnTracker, btnWarehouse);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(24));
        card.getStyleClass().add("card");

        VBox root = new VBox(card);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.getStyleClass().add("root");

        Scene scene = new Scene(root, 420, 420);
        scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());

        window.setTitle("HappyShop Manager");
        window.setScene(scene);
        window.show();
    }
}


