package ci553.happyshop;

import ci553.happyshop.client.home.HomeClient;
import javafx.application.Application;

/**
 * The Launcher class serves as the main entry point of the system.
 * It calls the launch() method of the Main class to start the JavaFX application.
 * This class is intentionally kept simple to isolate the bootstrapping logic.
 *
 * @author Shine Shan University of Brighton
 * @version 1.0
 */

public class Launcher {
    public static void main(String[] args) {
        Application.launch(HomeClient.class, args);
    }
}