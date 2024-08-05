package menus.SignupClasses;

import inventory.*;
import menus.Menu;
import java.util.*;
import java.util.regex.*;

public class UserCreation extends Menu {
    public UserCreation(DataManager dataManager) {
        super(dataManager);
    }

    private Matcher getCommandMatcher(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(input);
    }

    private boolean validate(Matcher matcher) {
        String username = matcher.group("username");
        String password = matcher.group("password");
        String passwordConf = matcher.group("passwordConf");
        String email = matcher.group("email");
        String nickname = matcher.group("nickname");

        if (username.matches("\\s*")) {
            System.out.println("The username field is empty.");
            return false;
        }
        if (password.matches("\\s*")) {
            System.out.println("The password field is empty.");
            return false;
        }
        if (!password.equals(passwordConf)) {
            System.out.println("The password confirmation does not match.");
            return false;
        }
        if (email.matches("\\s*")) {
            System.out.println("The email field is empty.");
            return false;
        }
        if (nickname.matches("\\s*")) {
            System.out.println("The nickname field is empty.");
            return false;
        }

        if (!username.matches("[A-Za-z0-9_]+")) {
            System.out.println("Usernames can only include letters, numbers and underscore.");
            return false;
        }

        if (dataManager.getUser(username) != null) {
            System.out.println("This username is already taken.");
            return false;
        }

        if (password.length() < 8) {
            System.out.println("Password length must be at least 8 characters.");
            return false;
        }

        if (!password.matches("(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&#+-])[A-Za-z\\d@$!%*?&]+")) {
            System.out.println(
                    "Passwords should contain at least one lower case letter, one uppercase letter, one digit and one special character.");
            return false;
        }

        if (password.equals("current menu name")) {
            System.out.println("Please enter another password");
            return false;
        }

        if (!email.matches("[^@]+@[^@]+\\.[cC][oO][mM]")) {
            System.out.println("Email format is invalid.");
            return false;
        }

        User user = new User(username, password, nickname, email, null, null);
        dataManager.setCurrentPlayer(new Player(user, 1, 0, 500, 100));

        return true;
    }

    private boolean randomValidate(Matcher matcher) {
        String username = matcher.group("username");
        String email = matcher.group("email");
        String nickname = matcher.group("nickname");

        if (username.matches("\\s*")) {
            System.out.println("The username field is empty.");
            return false;
        }
        if (email.matches("\\s*")) {
            System.out.println("The email field is empty.");
            return false;
        }
        if (nickname.matches("\\s*")) {
            System.out.println("The nickname field is empty.");
            return false;
        }

        if (!username.matches("[A-Za-z0-9_]+")) {
            System.out.println("Usernames can only include letters, numbers and underscore.");
            return false;
        }

        if (dataManager.getUser(username) != null) {
            System.out.println("This username is already taken.");
            return false;
        }

        if (!email.matches("[^@]+@[^@]+\\.[cC][oO][mM]")) {
            System.out.println("Email format is invalid.");
            return false;
        }

        final int length = 12;
        String password = PasswordGenerator.generateRandomPassword(length);

        User user = new User(username, password, nickname, email, null, null);
        dataManager.setCurrentPlayer(new Player(user, 1, 0, 500, 100));

        return true;
    }

    public SignupState run() {
        Scanner scanner = new Scanner(System.in);
        String input;
        System.out.println("Welcome to Signup menu");

        while (!(input = scanner.nextLine()).equals("end")) {
            if (input.equals("current menu name")) {
                System.out.println("Signup menu");
                continue;
            }

            if (input.matches(
                    "user create -u (?<username>.+) -p (?<password>.+) (?<passwordConf>.+) –email (?<email>.+) -n (?<nickname>.+)")) {
                Matcher matcher = getCommandMatcher(input,
                        "user create -u (?<username>.+) -p (?<password>.+) (?<passwordConf>.+) –email (?<email>.+) -n (?<nickname>.+)");
                matcher.find();
                if (validate(matcher)) {
                    return SignupState.FALLBACK_MEASURE;
                }
                continue;
            }

            if (input.matches("user create -u (?<username>.+) -p random –email (?<email>.+) -n (?<nickname>.+)")) {
                Matcher matcher = getCommandMatcher(input,
                        "user create -u (?<username>.+) -p random –email (?<email>.+) -n (?<nickname>.+)");
                matcher.find();
                if (randomValidate(matcher)) {
                    return SignupState.PASSWORD_CONFIRM;
                }
                continue;
            }

            if (input.equals("To Login menu")) {
                return SignupState.LOGIN;
            }

            System.out.println("Invalid input.");
        }

        return SignupState.EXIT;
    }
}