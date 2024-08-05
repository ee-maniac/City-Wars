package menus;

import inventory.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainMenu extends Menu {
    public MainMenu(DataManager dataManager) {
        super(dataManager);
    }

    private Matcher getCommandMatcher(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(input);
    }

    private GameMode startGame(Matcher matcher) {
        if (matcher.group("mode").equals("two player")) {
            return GameMode.TWO_PLAYER;
        }
        return GameMode.INVALID;
    }

    public GameState run() {
        Scanner scanner = new Scanner(System.in);
        String input;
        System.out.println("Welcome to Main menu");

        while (!(input = scanner.nextLine()).equals("end")) {
            if (input.equals("current menu name")) {
                System.out.println("Main Menu");
                continue;
            }

            if (input.equals("Enter profile")) {
                return GameState.PROFILE_MENU;
            }

            if (input.equals("Enter match history")) {
                return GameState.HISTORY_MENU;
            }

            if (input.equals("Enter card inventory")) {
                return GameState.CARD_MENU;
            }

            if (input.equals("Enter shop")) {
                return GameState.SHOP_MENU;
            }

            if (input.equals("Enter game")) {
                return GameState.GAME_MENU;
            }

            if (input.equals("Logout")) {
                dataManager.setCurrentPlayer(null);
                return GameState.LOGIN_MENU;
            }

            System.out.println("Invalid input.");
        }

        return GameState.EXIT;
    }
}