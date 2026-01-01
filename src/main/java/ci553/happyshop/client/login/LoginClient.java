package ci553.happyshop.client.login;

import javafx.application.Application;
import javafx.stage.Stage;

public class LoginClient extends Application {

    @Override
    public void start(Stage stage) {
        LoginView view = new LoginView();
        LoginModel model = new LoginModel();
        LoginController controller = new LoginController(view, model);

        view.loginController = controller;
        view.start(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}