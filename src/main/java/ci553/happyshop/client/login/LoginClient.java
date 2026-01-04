package ci553.happyshop.client.login;

import ci553.happyshop.client.manager.ManagerClient;
import javafx.stage.Stage;

public class LoginClient {

    // App start: login first, then open manager panel
    public static void showLoginAndOpenManager() {
        showLoginForRole(null, () -> {
            try {
                new ManagerClient().start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // Reusable login popup for a specific role (or null = any role)
    public static void showLoginForRole(LoginModel.Role requiredRole, Runnable onSuccess) {
        try {
            LoginView view = new LoginView();
            LoginModel model = new LoginModel();
            LoginController controller = new LoginController(view, model);

            controller.setRequiredRole(requiredRole);
            controller.setOnSuccess(onSuccess);

            view.loginController = controller;
            view.start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
