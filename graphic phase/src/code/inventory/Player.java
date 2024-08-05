package code.inventory;

import java.text.SimpleDateFormat;
import java.util.*;

public class Player extends User {
    private HashMap<String, Integer> playerInfo = new HashMap<>();
    private ArrayList<Card> cards = new ArrayList<>();
    private ArrayList<MatchInfo> matchHistory = new ArrayList<>();

    public Player(User user, int level, int xp, int hp, int coins) {
        super(user.getInfo("username"), user.getInfo("password"), user.getInfo("nickname"),
                user.getInfo("email"), user.getInfo("securityQ"), user.getInfo("securityA"));
        playerInfo.put("level", level);
        playerInfo.put("xp", xp);
        playerInfo.put("hp", hp);
        playerInfo.put("coins", coins);
    }

    public int getPlayerInfo(String key) {
        return playerInfo.get(key);
    }

    public void setPlayerInfo(String key, Integer value) {
        playerInfo.put(key, value);
    }

    private void levelUp() {
        if (playerInfo.get("xp") >= playerInfo.get("level") * 1000) {
            playerInfo.put("xp", playerInfo.get("xp") - playerInfo.get("level") * 1000);
            playerInfo.put("level", playerInfo.get("level") + 1);
        }
    }

    public Card getCard(String name) {
        for (Card c : cards) {
            if (c.getName().equals(name)) {
                return c;
            }
        }
        return null;
    }

    public void addCard(Card c) {
        cards.add(c);
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public void setCards(ArrayList<Card> cards) {
        this.cards = cards;
    }

    public void setMatchHistory(ArrayList<MatchInfo> matchHistory) {
        this.matchHistory = matchHistory;
    }

    public ArrayList<MatchInfo> getMatchHistory() {
        return matchHistory;
    }

    public void sortMatchHistory(String property, String sortOrder) {
        Comparator<MatchInfo> comparator;

        switch (property.toLowerCase()) {
            case "date":
                comparator = (m1, m2) -> {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        Date date1 = sdf.parse(m1.getDate());
                        Date date2 = sdf.parse(m2.getDate());
                        return date1.compareTo(date2);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                };
                break;
            case "result":
                comparator = Comparator.comparing(MatchInfo::getResult);
                break;
            case "rival name":
                comparator = Comparator.comparing(MatchInfo::getRivalName);
                break;
            case "rival level":
                comparator = (m1, m2) -> {
                    Integer level1 = Integer.parseInt(m1.getRivalLevel());
                    Integer level2 = Integer.parseInt(m2.getRivalLevel());
                    return level1.compareTo(level2);
                };
                break;
            default:
                throw new IllegalArgumentException("Invalid property: " + property);
        }

        if (sortOrder.equalsIgnoreCase("descending")) {
            comparator = comparator.reversed();
        }

        Collections.sort(matchHistory, comparator);
    }

    public void sortCardStack(String property, String sortOrder) {
        Comparator<Card> comparator;

        switch (property.toLowerCase()) {
            case "name":
                comparator = Comparator.comparing(Card::getName);
                break;
            case "type":
                comparator = Comparator.comparing(Card::getType);
                break;
            case "strength":
                comparator = (m1, m2) -> {
                    Integer x = m1.getAccuracy();
                    Integer y = m2.getAccuracy();
                    return x.compareTo(y);
                };
                break;
            case "duration":
                comparator = (m1, m2) -> {
                    Integer x = m1.getDuration();
                    Integer y = m2.getDuration();
                    return x.compareTo(y);
                };
                break;
            case "level":
                comparator = (m1, m2) -> {
                    Integer x = m1.getLevel();
                    Integer y = m2.getLevel();
                    return x.compareTo(y);
                };
                break;
            case "upgrade cost":
                comparator = (m1, m2) -> {
                    Integer x = m1.getUpgradeCost();
                    Integer y = m2.getUpgradeCost();
                    return x.compareTo(y);
                };
                break;
            case "price":
                comparator = (m1, m2) -> {
                    Integer x = m1.getCost();
                    Integer y = m2.getCost();
                    return x.compareTo(y);
                };
                break;
            default:
                throw new IllegalArgumentException("Invalid property: " + property);
        }

        if (sortOrder.equalsIgnoreCase("descending")) {
            comparator = comparator.reversed();
        }

        Collections.sort(cards, comparator);
    }

    public void removeCard(String name) {
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).getName().equals(name)) {
                cards.remove(i);
                break;
            }
        }
    }
}