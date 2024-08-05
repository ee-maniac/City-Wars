package menus.LoginClasses;

import inventory.*;
import java.util.*;
import menus.Menu;

public class Fallback extends Menu {
    public Fallback(DataManager dataManager) {
        super(dataManager);
    }

    public LoginState run() {
        Scanner scanner = new Scanner(System.in);
        String input;

        System.out.println(dataManager.getCurrentPlayer().getInfo("securityQ"));

        while(!(input = scanner.nextLine()).equals("end")) {
            if(input.equals("current menu name")) {
                System.out.println("Login menu");
                continue;
            }

            if(input.equals("To Signup menu")) {
                return LoginState.SIGNUP;
            }

            if(dataManager.getCurrentPlayer().validateSecurityQuestion(input)) {
                System.out.println("user logged in successfully!");
                return LoginState.MAIN_MENU;
            }
            else {
                System.out.println("Security answer does not match.");
            }
        }

        return LoginState.EXIT;
    }
}