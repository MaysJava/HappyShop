package ci553.happyshop.utility;

import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public final class WindowManager {

    private static Stage homeStage;

    private static Stage customerStage;
    private static Stage pickerStage;
    private static Stage trackerStage;

    private static final List<Stage> sessionStages = new ArrayList<>();
    private static boolean sessionClosing = false;

    private WindowManager() {}

    public static void openCustomerSession(Stage home) {
        homeStage = home;

        WinPosManager.resetSession();

        sessionStages.clear();

        customerStage = new Stage();
        pickerStage   = new Stage();
        trackerStage  = new Stage();

    // All customer sessions (guest, login, create account) MUST enter here

        try {
            new ci553.happyshop.client.customer.CustomerClient().start(customerStage);
            new ci553.happyshop.client.picker.PickerClient().start(pickerStage);
            new ci553.happyshop.client.orderTracker.OrderTrackerClient().start(trackerStage);
        } catch (Exception e) {
            e.printStackTrace();
            backToHome();
            return;
        }

        sessionStages.add(customerStage);
        sessionStages.add(pickerStage);
        sessionStages.add(trackerStage);

        //  ONLY customer window ends the whole session
        customerStage.setOnCloseRequest(evt -> {
            if (sessionClosing) return;
            evt.consume();
            backToHome();
        });

        //  Picker & tracker close normally (no special handler)
        // (pressing X on them closes only that window)
    }

    public static void backToHome() {
        sessionClosing = true;

        for (Stage s : new ArrayList<>(sessionStages)) {
            if (s != null) s.hide();
        }
        sessionStages.clear();

        sessionClosing = false;

        if (homeStage != null) {
            homeStage.show();
            homeStage.toFront();
        }
    }
}


