package ci553.happyshop.client.login;

public class LoginModel {

    public enum Role { CUSTOMER, PICKER, WAREHOUSE, TRACKER, NONE }

    public Role authenticate(String username, String password) {
        if (username == null || password == null) return Role.NONE;

        username = username.trim();
        password = password.trim();

        // ✅ Simple hardcoded accounts (you can change these)
        if (username.equals("customer") && password.equals("1234")) return Role.CUSTOMER;
        if (username.equals("picker") && password.equals("1234")) return Role.PICKER;
        if (username.equals("warehouse") && password.equals("1234")) return Role.WAREHOUSE;
        if (username.equals("tracker") && password.equals("1234")) return Role.TRACKER;

        return Role.NONE;
    }
}