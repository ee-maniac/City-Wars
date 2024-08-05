package menus.ProfileClasses;

import inventory.*;
import java.util.regex.*;

public class ChangeNickname {
    public static void changeNickname(DataManager dataManager, Matcher matcher) {
        if(matcher.group("nickname").matches("\\s*")) {
            System.out.println("The nickname field is empty.");
            return;
        }

        if(dataManager.getCurrentPlayer().getInfo("nickname").equals(matcher.group("nickname"))) {
            System.out.println("Please enter a new nickname.");
            return;
        }

        dataManager.getCurrentPlayer().setInfo("nickname", matcher.group("nickname"));
        dataManager.updatePlayer(dataManager.getCurrentPlayer());
        System.out.println("Nickname was successfully changed.");
    }
}