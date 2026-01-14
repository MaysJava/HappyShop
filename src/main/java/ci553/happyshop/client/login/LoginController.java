package ci553.happyshop.client.login;

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

    public void setRequiredRole(LoginModel.Role role) {
        this.requiredRole = role;
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

        // If a role is required (e.g. WAREHOUSE), block wrong roles
        if (requiredRole != null && role != requiredRole) {
            view.showMessage("Access denied. This login is for " + requiredRole);
            return;
        }


        view.showMessage("Login successful!");

        // Close login window
        Stage loginStage = view.getWindow();
        loginStage.hide();
        if (onSuccess != null) {
            javafx.application.Platform.runLater(onSuccess);
        }

    }
}
