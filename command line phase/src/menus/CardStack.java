package menus;

import inventory.*;
import java.util.*;
import java.util.regex.*;

public class CardStack extends Menu {
    int page = 1;
    final int numPerPage = 10;
    String gProperty = "name";
    String gSortOrder = "ascending";

    private static final Set<String> validProperties = new HashSet<>();

    static {
        validProperties.add("name");
        validProperties.add("type");
        validProperties.add("accuracy");
        validProperties.add("duration");
        validProperties.add("level");
        validProperties.add("upgrade cost");
        validProperties.add("price");
    }

    public CardStack(DataManager dataManager) {
        super(dataManager);
    }

    private Matcher getCommandMatcher(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(input);
    }

    private void showStack(String property, String sortOrder) {
        dataManager.getCurrentPlayer().sortCardStack(property, sortOrder);
        ArrayList<Card> tempArray = dataManager.getCurrentPlayer().getCards();

        System.out.println("page " + page);
        for (int i = (page - 1) * numPerPage; i < (page) * numPerPage && i < tempArray.size(); i++) {
            Card x = tempArray.get(i);
            System.out.print(i + 1 + " - name: " + x.getName() + " / ");
            System.out.print("type : " + x.getType() + " / ");
            System.out.print("accuracy: " + x.getAccuracy() + " / ");
            System.out.print("duration: " + x.getDuration() + " / ");
            System.out.print("level: " + x.getLevel() + " / ");
            System.out.print("upgrade cost: " + x.getUpgradeCost() + " / ");
            System.out.println("price: " + x.getCost() + " / ");
        }
    }

    private void jumpToPage(Matcher matcher) {
        double x = ((double) dataManager.getCurrentPlayer().getCards().size()) / ((double) numPerPage);
        if (Integer.parseInt(matcher.group("page")) < 0 || Integer.parseInt(matcher.group("page")) >= Math.ceil(x)) {
            System.out.println("Invalid page");
        } else {
            page = Integer.parseInt(matcher.group("page"));
            showStack(gProperty, gSortOrder);
        }
    }

    private void setSearch(Matcher matcher) {
        String property = matcher.group("property");
        String order = matcher.group("order");

        if (!validProperties.contains(property.toLowerCase())) {
            System.out.println("Invalid property");
            return;
        }

        if (!order.equalsIgnoreCase("ascending") && !order.equalsIgnoreCase("descending")) {
            System.out.println("Invalid order");
            return;
        }

        gProperty = property;
        gSortOrder = order;
        showStack(gProperty, gSortOrder);
    }

    public GameState run() {
        Scanner scanner = new Scanner(System.in);
        String input;

        showStack(gProperty, gSortOrder);

        while (!(input = scanner.nextLine()).equals("end")) {
            if (input.equals("current menu name")) {
                System.out.println("Card inventory menu");
                continue;
            }

            if (input.equals("next page")) {
                double x = ((double) dataManager.getCurrentPlayer().getCards().size()) / ((double) numPerPage);
                if (page < Math.ceil(x)) {
                    page++;
                    showStack(gProperty, gSortOrder);
                } else {
                    System.out.println("This is the last page");
                }
                continue;
            }

            if (input.equals("previous page")) {
                if (page > 1) {
                    page--;
                    showStack(gProperty, gSortOrder);
                } else {
                    System.out.println("This is the first page");
                }
                continue;
            }

            if (input.matches("page no (?<page>\\d+)")) {
                Matcher matcher = getCommandMatcher(input, "page no. (?<page>\\d)");
                matcher.find();
                jumpToPage(matcher);
                continue;
            }

            if (input.matches("Show stack -p (?<property>.+) -o (?<order>.+)")) {
                Matcher matcher = getCommandMatcher(input, "Show stack -p (?<property>.+) -o (?<order>.+)");
                matcher.find();
                setSearch(matcher);
                continue;
            }

            if (input.equals("To Main menu")) {
                return GameState.MAIN_MENU;
            }

            System.out.println("Invalid input.");
        }

        return GameState.EXIT;
    }
}