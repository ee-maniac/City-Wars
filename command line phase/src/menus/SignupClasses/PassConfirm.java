package menus.SignupClasses;

import inventory.*;
import menus.Menu;
import java.util.Scanner;

public class PassConfirm extends Menu {
    private String password;

    public PassConfirm(DataManager dataManager) {
        super(dataManager);
        password = dataManager.getCurrentPlayer().getInfo("password");
    }

    public SignupState run() {
        System.out.println("Your random generated password is " + password + ". Please enter your new Password to proceed.");

        Scanner scanner = new Scanner(System.in);
        String input;

        while(!(input = scanner.nextLine()).equals("end")) {
            if(input.equals("current menu name")) {
                System.out.println("Signup menu");
                continue;
            }

            if(!input.equals(password) && !input.equals("To Register menu") && !input.equals("To Login menu")) {
                System.out.println("The password confirmation does not match.");
                continue;
            }
            else if(input.equals(password)){
                return SignupState.FALLBACK_MEASURE;
            }

            if(input.equals("To Login menu")) {
                return SignupState.LOGIN;
            }

            return SignupState.USER_REGISTER;//upon writing To Register Menu
        }

        return SignupState.EXIT;
    }
}