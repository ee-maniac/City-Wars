package menus.SignupClasses;

import inventory.*;
import menus.Menu;
import java.util.*;
import java.util.regex.*;

public class FallbackMeasure extends Menu {
    private String question1 = "What is your father's name?";
    private String question2 = "What is your favourite color?";
    private String question3 = "What was the name of your first pet?";

    public FallbackMeasure(DataManager dataManager) {
        super(dataManager);
    }

    private Matcher getCommandMatcher(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(input);
    }

    private boolean chooseSecurityQ(Matcher matcher) {
        if(Integer.parseInt(matcher.group("number")) > 3 || Integer.parseInt(matcher.group("number")) < 1) {
            System.out.println("Invalid question number.");
            return false;
        }

        if(matcher.group("answer").matches("\\s*")) {
            System.out.println("The answer field is empty.");
            return false;
        }

        if(!matcher.group("answer").equals(matcher.group("answerConf"))) {
            System.out.println("The password confirmation does not match.");
            return false;
        }

        if(matcher.group("answer").equals("current menu name")) {
            System.out.println("Choose another answer.");
            return false;
        }

        if(Integer.parseInt(matcher.group("number")) == 1) {
            dataManager.getCurrentPlayer().setInfo("securityQ", question1);
        } else if(Integer.parseInt(matcher.group("number")) == 2) {
            dataManager.getCurrentPlayer().setInfo("securityQ", question2);
        } else {
            dataManager.getCurrentPlayer().setInfo("securityQ", question3);
        }

        dataManager.getCurrentPlayer().setInfo("securityA", matcher.group("answer"));

        return true;
    }

    public SignupState run() {
        Scanner scanner = new Scanner(System.in);
        String input;

        System.out.println("User created successfully. Please choose a security question :");
        System.out.println("1-" + question1);
        System.out.println("2-" + question2);
        System.out.println("3-" + question3);

        while(!(input = scanner.nextLine()).equals("end")) {
            if(input.equals("current menu name")) {
                System.out.println("Signup menu");
                continue;
            }

            if(input.matches("question pick -q (?<number>\\d+) -a (?<answer>.+) -c (?<answerConf>.+)")) {
                Matcher matcher = getCommandMatcher(input, "question pick -q (?<number>\\d+) -a (?<answer>.+) -c (?<answerConf>.+)");
                matcher.find();
                if(chooseSecurityQ(matcher)) {
                    return SignupState.CAPTCHA;
                }
                continue;
            }

            if(input.equals("To Login menu")) {
                return SignupState.LOGIN;
            }

            if(input.equals("To Signup menu")) {
                return SignupState.USER_REGISTER;
            }

            System.out.println("Invalid input.");
        }

        return SignupState.EXIT;
    }
}