package menus;

import inventory.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Shop extends Menu {
    public Shop(DataManager dataManager) {
        super(dataManager);
    }

    private Matcher getCommandMatcher(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(input);
    }

    private void showAllCards() {
        for(Card c : dataManager.getAllCards()) {
            if(dataManager.getCurrentPlayer().getCard(c.getName()) == null) {
                c.print();
                System.out.println(" / Cost: " + c.getCost());
            }
        }
    }

    private void buyCard(Matcher matcher) {
        String name = matcher.group("name");
        Player temp = dataManager.getCurrentPlayer();

        boolean exists = false;
        for (Card card : dataManager.getAllCards()) {
            if (name.equals(card.getName())) {
                exists = true;
                break;
            }
        }
        if(!exists) {
            System.out.println("There is no card with this name.");
            return;
        }

        Card target = null;
        for(Card c : dataManager.getAllCards()) {
            if(dataManager.getCurrentPlayer().getCard(c.getName()) == null && c.getName().equals(name)) {
                target = c;
                break;
            }
            else if(c.getName().equals(name)){
                System.out.println("You already have this card.");
                return;
            }
        }

        if(temp.getPlayerInfo("coins") < target.getCost()) {
            System.out.println("Not enough coins.");
            return;
        }

        dataManager.addPlayerCard(temp.getInfo("username"), target);
        for(Card c : dataManager.getPlayerCards(temp.getInfo("username"))) {
            if(c.getName().equals(name)) {
                temp.addCard(c);
                break;
            }
        }
        temp.setPlayerInfo("coins", temp.getPlayerInfo("coins")-target.getCost());
        System.out.println("Card successfully bought.");
    }

    private void upgradeCard(Matcher matcher) {
        String name = matcher.group("name");
        Player temp = dataManager.getCurrentPlayer();

        if(temp.getCard(name) == null) {
            System.out.println("There is no card with this name in your stack.");
            return;
        }

        if(temp.getCard(name).getUpgradeCost() > temp.getPlayerInfo("coins")) {
            System.out.println("Not enough coins.");
            return;
        }

        if(temp.getCard(name).getLevel() * 1.5 > temp.getPlayerInfo("level")) {
            System.out.println("You need to upgrade your level first.");
            return;
        }

        temp.getCard(name).setLevel(temp.getCard(name).getLevel() + 1);
        dataManager.updatePlayerCard(temp.getInfo("username"), temp.getCard(name));
        temp.removeCard(name);
        for(Card c : dataManager.getPlayerCards(temp.getInfo("username"))) {
            if(c.getName().equals(name)) {
                temp.addCard(c);
                break;
            }
        }
        temp.setPlayerInfo("coins", temp.getPlayerInfo("coins")-temp.getCard(name).getCost());
        System.out.println("Card successfully upgraded.");
    }

    public GameState run() {
        Scanner scanner = new Scanner(System.in);
        String input;
        System.out.println("Welcome to Shop menu");

        while(!(input = scanner.nextLine()).equals("end")) {
            if(input.equals("current menu name")) {
                System.out.println("Shop menu");
                continue;
            }

            if(input.equals("show all cards")) {
                showAllCards();
                continue;
            }

            if(input.matches("buy card -n (?<name>.+)")) {
                Matcher matcher = getCommandMatcher(input, "buy card -n (?<name>.+)");
                matcher.find();
                buyCard(matcher);
                continue;
            }

            if(input.matches("upgrade card -n (?<name>.+)")) {
                Matcher matcher = getCommandMatcher(input, "upgrade card -n (?<name>.+)");
                matcher.find();
                upgradeCard(matcher);
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