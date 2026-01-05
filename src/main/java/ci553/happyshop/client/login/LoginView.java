package ci553.happyshop.client.login;

import ci553.happyshop.utility.WinPosManager;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class LoginView {

    private Stage window;

    public LoginController loginController;

    private TextField tfUsername;
    private PasswordField tfPassword;
    private Label lbMsg;

    public void start(Stage window) {

        Label title = new Label("HappyShop Login");
        title.getStyleClass().add("title");

        this.window = window;


        tfUsername = new TextField();
        tfUsername.setPromptText("Username");
        tfUsername.getStyleClass().add("input");

        tfPassword = new PasswordField();
        tfPassword.setPromptText("Password");
        tfPassword.getStyleClass().add("input");

        Button btnLogin = new Button("Login");
        btnLogin.getStyleClass().addAll("button", "button-primary");
        btnLogin.setOnAction(e ->
                loginController.doLogin(tfUsername.getText().trim(), tfPassword.getText())
        );


        lbMsg = new Label("");
        lbMsg.getStyleClass().add("muted");

        VBox card = new VBox(12, title, tfUsername, tfPassword, btnLogin, lbMsg);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(24));
        card.getStyleClass().add("card");

        VBox root = new VBox(card);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.getStyleClass().add("root");

        Scene scene = new Scene(root, 620, 560); // bigger

        window.setMinWidth(620);
        window.setMinHeight(560);

        scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());

        window.setScene(scene);
        window.setTitle("HappyShop Login");
        WinPosManager.registerWindow(window, 620, 560);
        window.show();
    }

    private void clicked(ActionEvent e) {
        loginController.doLogin(tfUsername.getText(), tfPassword.getText());
    }

    public void showMessage(String msg) {
        lbMsg.setText(msg);
    }
    public Stage getWindow() {
        return window;
    }

}
