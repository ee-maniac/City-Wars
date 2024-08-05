package menus.LoginClasses;

import inventory.*;
import menus.Menu;
import java.util.*;
import java.util.regex.*;

public class Authentication extends Menu {
    private int incorrect = 0;
    private long prisonTime = 0;

    public Authentication(DataManager dataManager) {
        super(dataManager);
    }

    private Matcher getCommandMatcher(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(input);
    }

    private boolean validate(Matcher matcher) {
        long a = prisonTime - java.time.Instant.now().getEpochSecond();
        if(a > 0) {
            System.out.println("Try again in " + a + " seconds");
            return false;
        }
        else {
            prisonTime = 0;
        }

        User tempUser;
        if((tempUser = dataManager.getUser(matcher.group("username"))) == null) {
            System.out.println("Username doesn't exist!");
            return false;
        }

        if(!tempUser.validatePassword(matcher.group("password"))) {
            System.out.println("Password and Username don't match!");
            incorrect++;
            prisonTime = java.time.Instant.now().getEpochSecond() + incorrect*5;
            return false;
        }

        dataManager.setCurrentPlayer(dataManager.getPlayer(tempUser));
        System.out.println("user logged in successfully!");
        return true;
    }

    private boolean fallbackMeasure(Matcher matcher) {
        User tempUser = dataManager.getUser(matcher.group("username"));
        if(tempUser != null) {
            dataManager.setCurrentPlayer(dataManager.getPlayer(tempUser));
            return true;
        }
        else {
            System.out.println("Username doesn’t exist!");
            return false;
        }
    }

    public LoginState run() {
        Scanner scanner = new Scanner(System.in);
        String input;
        System.out.println("Welcome to Login menu");

        while(!(input = scanner.nextLine()).equals("end")) {
            if(input.equals("current menu name")) {
                System.out.println("Login menu");
                continue;
            }

            if(input.matches("user login -u (?<username>.+) -p (?<password>.+)")) {
                Matcher matcher = getCommandMatcher(input, "user login -u (?<username>.+) -p (?<password>.+)");
                matcher.find();
                if(validate(matcher)) {
                    return LoginState.MAIN_MENU;
                }
                continue;
            }

            if(input.matches("Forgot my password -u (?<username>.+)")) {
                Matcher matcher = getCommandMatcher(input, "Forgot my password -u (?<username>.+)");
                matcher.find();
                if(fallbackMeasure(matcher)) {
                    return LoginState.FALLBACK;
                }
                continue;
            }

            if(input.equals("To Signup menu")) {
                return LoginState.SIGNUP;
            }

            System.out.println("Invalid input.");
        }

        return LoginState.EXIT;
    }
}