package ci553.happyshop.client.createAccount;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ci553.happyshop.client.login.LoginClient;
import ci553.happyshop.client.login.LoginModel;
import ci553.happyshop.client.customer.CustomerClient;
import javafx.stage.Stage;
import ci553.happyshop.utility.WindowManager;



public class CreateAccountClient extends Application {

    private final Stage homeStage;

    public CreateAccountClient(Stage homeStage) {
        this.homeStage = homeStage;
    }

    @Override
    public void start(Stage stage) {

        Label title = new Label("Create Account");

        TextField tfUsername = new TextField();
        tfUsername.setPromptText("Username");

        PasswordField pfPassword = new PasswordField();
        pfPassword.setPromptText("Password");

        PasswordField pfConfirm = new PasswordField();
        pfConfirm.setPromptText("Confirm Password");

        Button btnCreate = new Button("Create Account");

        Label lblMessage = new Label();

        btnCreate.setOnAction(e -> {
            if (tfUsername.getText().isEmpty()
                    || pfPassword.getText().isEmpty()
                    || pfConfirm.getText().isEmpty()) {

                lblMessage.setText("Please fill in all fields.");
                return;
            }

            if (!pfPassword.getText().equals(pfConfirm.getText())) {
                lblMessage.setText("Passwords do not match.");
                return;
            }

            lblMessage.setText("Account created successfully! Redirecting to login...");

// Close create account window
            Stage currentStage = (Stage) btnCreate.getScene().getWindow();
            currentStage.hide();

// Open login window for CUSTOMER
            LoginClient.showLoginForRole(LoginModel.Role.CUSTOMER, () -> {
                // ✅ Always open clients via WindowManager so positioning/reset applies
                WindowManager.openCustomerSession(homeStage);
            });




        });

        VBox root = new VBox(10,
                title,
                tfUsername,
                pfPassword,
                pfConfirm,
                btnCreate,
                lblMessage
        );

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 350, 300);

        stage.setTitle("Create Account");
        stage.setScene(scene);
        stage.show();
    }
}
