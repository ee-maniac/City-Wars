package menus;

import inventory.*;
import java.util.*;
import java.util.regex.*;

public class MatchHistory extends Menu{
    int page = 1;
    final int numPerPage = 10;
    String gProperty = "date";
    String gSortOrder = "ascending";

    private static final Set<String> validProperties = new HashSet<>();

    static {
        validProperties.add("date");
        validProperties.add("result");
        validProperties.add("rival name");
        validProperties.add("rival level");
    }

    public MatchHistory(DataManager dataManager) {
        super(dataManager);
    }

    private Matcher getCommandMatcher(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(input);
    }

    private void showHistory(String property, String sortOrder) {
        dataManager.getCurrentPlayer().setMatchHistory(dataManager.getMatchInfosByHostName(dataManager.getCurrentPlayer().getInfo("username")));
        dataManager.getCurrentPlayer().sortMatchHistory(property, sortOrder);
        ArrayList<MatchInfo> tempArray = dataManager.getCurrentPlayer().getMatchHistory();

        System.out.println("page " + page);
        for(int i = (page-1)*numPerPage; i < (page)*numPerPage && i < tempArray.size(); i++) {
            MatchInfo x = tempArray.get(i);
            System.out.print(i+1 + " - date: " + x.getDate() + " / ");
            System.out.print("result : " + x.getResult() + " / ");
            System.out.print("rival name: " + x.getRivalName() + " / ");
            System.out.print("rival level: " + x.getRivalLevel() + " / ");
            System.out.println("aftermath: " + x.getAftermath() + " / ");
        }
    }

    private void jumpToPage(Matcher matcher) {
        double x = ((double) dataManager.getCurrentPlayer().getMatchHistory().size())/((double) numPerPage);
        if(Integer.parseInt(matcher.group("page")) < 0 || Integer.parseInt(matcher.group("page")) >= Math.ceil(x)) {
            System.out.println("Invalid page");
        }
        else {
            page = Integer.parseInt(matcher.group("page"));
            showHistory(gProperty, gSortOrder);
        }
    }

    private void setSearch(Matcher matcher) {
        String property = matcher.group("property");
        String order = matcher.group("order");

        if(!validProperties.contains(property.toLowerCase())) {
            System.out.println("Invalid property");
            return;
        }

        if(!order.equalsIgnoreCase("ascending") && !order.equalsIgnoreCase("descending")) {
            System.out.println("Invalid order");
            return;
        }

        gProperty = property;
        gSortOrder = order;
        showHistory(gProperty, gSortOrder);
    }

    public GameState run() {
        Scanner scanner = new Scanner(System.in);
        String input;
        System.out.println("Welcome to Math history menu");

        showHistory(gProperty, gSortOrder);

        while(!(input = scanner.nextLine()).equals("end")) {
            if(input.equals("current menu name")) {
                System.out.println("Match history Menu");
                continue;
            }

            if(input.equals("next page")) {
                double x = ((double) dataManager.getCurrentPlayer().getCards().size())/((double) numPerPage);
                if(page < Math.ceil(x)) {
                    page++;
                    showHistory(gProperty, gSortOrder);
                }
                else {
                    System.out.println("This is the last page");
                }
                continue;
            }

            if(input.equals("previous page")) {
                if(page > 1) {
                    page--;
                    showHistory(gProperty, gSortOrder);
                }
                else {
                    System.out.println("This is the first page");
                }
                continue;
            }

            if(input.matches("page no (?<page>\\d+)")) {
                Matcher matcher = getCommandMatcher(input, "page no. (?<page>\\d)");
                matcher.find();
                jumpToPage(matcher);
                continue;
            }

            if(input.matches("Show history -p (?<property>.+) -o (?<order>.+)")) {
                Matcher matcher = getCommandMatcher(input, "Show history -p (?<property>.+) -o (?<order>.+)");
                matcher.find();
                setSearch(matcher);
                continue;
            }

            if(input.equals("To Main Menu")) {
                return GameState.MAIN_MENU;
            }

            System.out.println("Invalid input.");
        }

        return GameState.EXIT;
    }
}