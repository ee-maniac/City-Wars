package menus;

import inventory.*;
import menus.ProfileClasses.*;
import java.util.*;
import java.util.regex.*;

public class Profile extends Menu {
    public Profile(DataManager dataManager) {
        super(dataManager);
    }

    private Matcher getCommandMatcher(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(input);
    }

    public GameState run() {
        Scanner scanner = new Scanner(System.in);
        String input;
        System.out.println("Welcome to Profile menu");

        while(!(input = scanner.nextLine()).equals("end")) {
            if(input.equals("current menu name")) {
                System.out.println("Profile Menu");
                continue;
            }

            if(input.equals("Show information")) {
                ShowInfo.showInfo(dataManager);
                continue;
            }

            if(input.matches("Profile change -u (?<username>.+)")) {
                Matcher matcher = getCommandMatcher(input, "Profile change -u (?<username>.+)");
                matcher.find();
                ChangeUsername.changeUsername(dataManager, matcher);
                continue;
            }

            if(input.matches("Profile change -n (?<nickname>.+)")) {
                Matcher matcher = getCommandMatcher(input, "Profile change -n (?<nickname>.+)");
                matcher.find();
                ChangeNickname.changeNickname(dataManager, matcher);
                continue;
            }

            if(input.matches("profile change password -o (?<oldPass>.+) -n (?<newPass>.+)")) {
                Matcher matcher = getCommandMatcher(input, "profile change password -o (?<oldPass>.+) -n (?<newPass>.+)");
                matcher.find();
                PasswordChange passwordChange = new PasswordChange(dataManager, matcher);
                passwordChange.run();
                continue;
            }

            if(input.matches("Profile change -e (?<email>.+)")) {
                Matcher matcher = getCommandMatcher(input, "Profile change -e (?<email>.+)");
                matcher.find();
                ChangeEmail.changeEmail(dataManager, matcher);
                continue;
            }

            if(input.equals("To Main menu")) {
                return GameState.MAIN_MENU;
            }

            System.out.println("Invalid input.");
        }

        return GameState.EXIT;
    }
}