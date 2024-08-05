package menus.ProfileClasses;

import inventory.*;
import menus.Menu;
import java.util.regex.*;

public class PasswordChange extends Menu {
    Matcher matcher;

    public PasswordChange(DataManager dataManager, Matcher matcher) {
        super(dataManager);
        this.matcher = matcher;
    }

    public GameState run() {
        if(matcher.group("oldPass").matches("\\s*")) {
            System.out.println("The current password field is empty.");
            return GameState.PROFILE_MENU;
        }

        if(!dataManager.getCurrentPlayer().validatePassword(matcher.group("oldPass"))) {
            System.out.println("The current password is incorrect.");
            return GameState.PROFILE_MENU;
        }

        if(matcher.group("newPass").matches("\\s*")) {
            System.out.println("The new password field is empty.");
            return GameState.PROFILE_MENU;
        }

        if(matcher.group("newPass").length() < 8) {
            System.out.println("Password length must be at least 8 characters.");
            return GameState.PROFILE_MENU;
        }

        if(!matcher.group("newPass").matches("(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&#+-])[A-Za-z\\d@$!%*?&]+")) {
            System.out.println("Passwords should contain at least one lower case letter, one uppercase letter, one digit and one special character.");
            return GameState.PROFILE_MENU;
        }

        if(matcher.group("newPass").equals("current menu name")) {
            System.out.println("Please enter another password");
            return GameState.PROFILE_MENU;
        }

        if(matcher.group("newPass").equals(matcher.group("oldPass"))) {
            System.out.println("Please enter a new password!");
            return GameState.PROFILE_MENU;
        }

        Captcha captcha = new Captcha(dataManager);

        if(!captcha.run()) {
            System.out.println("Please enter your new password again");
            return GameState.PROFILE_MENU;
        }

        dataManager.getCurrentPlayer().setInfo("password", matcher.group("newPass"));
        dataManager.updatePlayer(dataManager.getCurrentPlayer());
        System.out.println("Password successfully changed.");
        return GameState.PROFILE_MENU;
    }
}