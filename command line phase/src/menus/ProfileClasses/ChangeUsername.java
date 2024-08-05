package menus.ProfileClasses;

import inventory.*;
import java.util.regex.*;

public class ChangeUsername {
    public static void changeUsername(DataManager dataManager, Matcher matcher) {
        String username = matcher.group("username");

        if(username.matches("\\s*")) {
            System.out.println("The username field is empty.");
            return;
        }

        if(!username.matches("[A-Za-z0-9_]+")) {
            System.out.println("Usernames can only include letters, numbers and underscore.");
            return;
        }

        if(dataManager.getCurrentPlayer().getInfo("username").equals(username)) {
            System.out.println("Please enter a new username.");
            return;
        }

        if(dataManager.getUser(username) != null) {
            System.out.println("This username is already taken.");
            return;
        }

        dataManager.getCurrentPlayer().setInfo("username", username);
        dataManager.updatePlayer(dataManager.getCurrentPlayer());
        System.out.println("Username was successfully changed.");
    }
}