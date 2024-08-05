package menus.ProfileClasses;

import inventory.*;
import java.util.regex.*;

public class ChangeEmail {
    public static void changeEmail(DataManager dataManager, Matcher matcher) {
        if(!matcher.group("email").matches("[^@]+@[^@]+\\.[cC][oO][mM]")) {
            System.out.println("Email format is invalid.");
            return;
        }

        if(dataManager.getCurrentPlayer().getInfo("email").equals(matcher.group("email"))) {
            System.out.println("Please enter a new email.");
            return;
        }

        dataManager.getCurrentPlayer().setInfo("email", matcher.group("email"));
        dataManager.updatePlayer(dataManager.getCurrentPlayer());
        System.out.println("Email was successfully changed.");
    }
}