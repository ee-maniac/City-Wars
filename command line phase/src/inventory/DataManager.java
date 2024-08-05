package inventory;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DataManager {
    public static Database database = new Database();
    private Player currentPlayer;
    private Player secondaryPlayer;

    public DataManager() {
    }

    public ArrayList<Card> getAllCards() {
        ArrayList<Card> cards = new ArrayList<>();
        try {
            Statement statement = database.getStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM Cards");
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String type = rs.getString("type");
                String character = rs.getString("character");
                int accuracy = rs.getInt("accuracy");
                int damagePerSector = rs.getInt("damagePerSector");
                int duration = rs.getInt("duration");
                int level = rs.getInt("level");
                int upgradeCost = rs.getInt("upgradeCost");
                int cost = rs.getInt("cost");
                Card card = new Card(id, name, type, character, accuracy, damagePerSector, duration, level, upgradeCost,
                        cost);
                cards.add(card);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cards;
    }

    public ArrayList<Card> getPlayerCards(String username) {
        ArrayList<Card> allCards = getAllCards();
        ArrayList<Card> playerCards = new ArrayList<>();
        try {
            Statement statement = database.getStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM PlayerCards WHERE username='" + username + "'");
            while (rs.next()) {
                int card_id = rs.getInt("card_id");
                int level = rs.getInt("level");
                Card playerCard = null;
                for (Card card : allCards) {
                    if (card.getId() == card_id) {
                        playerCard = new GameReadyCard(card, level);
                    }
                }
                if (playerCard != null) {
                    playerCards.add(playerCard);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return playerCards;
    }

    public void addPlayerCard(String username, Card playerCard) {
        String insertCardSQL = "INSERT INTO PlayerCards(username, card_id, level) VALUES (?, ?, ?)";
        try (Connection conn = database.getConnection();
             PreparedStatement pstmtCard = conn.prepareStatement(insertCardSQL)) {
            pstmtCard.setString(1, username);
            pstmtCard.setInt(2, playerCard.getId());
            pstmtCard.setInt(3, playerCard.getLevel());
            pstmtCard.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePlayerCard(String username, Card playerCard) {
        String updateCardSQL = "UPDATE PlayerCards SET level = ? WHERE username = ? AND card_id = ?";
        try (Connection conn = database.getConnection();
             PreparedStatement pstmtCard = conn.prepareStatement(updateCardSQL)) {
            pstmtCard.setInt(1, playerCard.getLevel());
            pstmtCard.setString(2, username);
            pstmtCard.setInt(3, playerCard.getId());
            pstmtCard.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addCard(Card card) {
        String insertCardSQL = "INSERT INTO Cards(name, type, character, accuracy, damagePerSector, duration, level, upgradeCost, cost) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = database.getConnection();
             PreparedStatement pstmtCard = conn.prepareStatement(insertCardSQL)) {
            pstmtCard.setString(1, card.getName());
            pstmtCard.setString(2, card.getType());
            pstmtCard.setString(3, card.getCharacter());
            pstmtCard.setInt(4, card.getAccuracy());
            pstmtCard.setInt(5, card.getDamagePerSector());
            pstmtCard.setInt(6, card.getDuration());
            pstmtCard.setInt(7, card.getLevel());
            pstmtCard.setInt(8, card.getUpgradeCost());
            pstmtCard.setInt(9, card.getCost());
            pstmtCard.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateCard(Card card) {
        String updateCardSQL = "UPDATE Cards SET type = ?, character = ?, accuracy = ?, damagePerSector = ?, duration = ?, level = ?, upgradeCost = ?, cost = ?, name = ? WHERE id = ?";
        try (Connection conn = database.getConnection();
             PreparedStatement pstmtCard = conn.prepareStatement(updateCardSQL)) {
            pstmtCard.setString(1, card.getType());
            pstmtCard.setString(2, card.getCharacter());
            pstmtCard.setInt(3, card.getAccuracy());
            pstmtCard.setInt(4, card.getDamagePerSector());
            pstmtCard.setInt(5, card.getDuration());
            pstmtCard.setInt(6, card.getLevel());
            pstmtCard.setInt(7, card.getUpgradeCost());
            pstmtCard.setInt(8, card.getCost());
            pstmtCard.setString(9, card.getName());
            pstmtCard.setInt(10, card.getId());
            pstmtCard.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteCard(Card card) {
        try {
            Statement stmt1 = database.getStatement();
            stmt1.executeUpdate("DELETE FROM Cards WHERE id = " + card.getId());
            stmt1.close();
            Statement stmt2 = database.getStatement();
            stmt2.executeUpdate("DELETE FROM PlayerCards WHERE card_id = " + card.getId());
            stmt2.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User getUser(String username) {
        User user = null;
        try {
            Statement statement = database.getStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM Users WHERE username='" + username + "'");
            while (rs.next()) {
                if (username.equals(rs.getString("username"))) {
                    String password = rs.getString("password");
                    String nickname = rs.getString("nickname");
                    String email = rs.getString("email");
                    String securityQuestion = rs.getString("securityQuestion");
                    String securityAnswer = rs.getString("securityAnswer");
                    user = new User(username, password, nickname, email, securityQuestion, securityAnswer);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public void addUser(User user) {
        String insertUserSQL = "INSERT INTO Users(username, password, nickname, email, securityQuestion, securityAnswer) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = database.getConnection();
             PreparedStatement pstmtUser = conn.prepareStatement(insertUserSQL)) {
            pstmtUser.setString(1, user.getInfo("username"));
            pstmtUser.setString(2, user.getInfo("password"));
            pstmtUser.setString(3, user.getInfo("nickname"));
            pstmtUser.setString(4, user.getInfo("email"));
            pstmtUser.setString(5, user.getInfo("securityQ"));
            pstmtUser.setString(6, user.getInfo("securityA"));
            pstmtUser.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateUser(User user) {
        String updateUserSQL = "UPDATE Users SET password = ?, nickname = ?, email = ?, securityQuestion = ?, securityAnswer = ? WHERE username = ?";
        try (Connection conn = database.getConnection();
             PreparedStatement pstmtUser = conn.prepareStatement(updateUserSQL)) {
            pstmtUser.setString(1, user.getInfo("username"));
            pstmtUser.setString(2, user.getInfo("password"));
            pstmtUser.setString(3, user.getInfo("nickname"));
            pstmtUser.setString(4, user.getInfo("email"));
            pstmtUser.setString(5, user.getInfo("securityQ"));
            pstmtUser.setString(6, user.getInfo("securityA"));
            pstmtUser.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Player getPlayer(User user) {
        Player player = null;
        try {
            Statement statement = database.getStatement();
            if (user == null)
                return null;
            ResultSet rs = statement
                    .executeQuery("SELECT * FROM Players WHERE username='" + user.getInfo("username") + "'");
            while (rs.next()) {
                if (user.getInfo("username").equals(rs.getString("username"))) {
                    int level = rs.getInt("level");
                    int xp = rs.getInt("xp");
                    int hp = rs.getInt("hp");
                    int coins = rs.getInt("coins");
                    player = new Player(user, level, xp, hp, coins);
                    player.setCards(getPlayerCards(player.getInfo("username")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return player;
    }

    public Player getPlayer(String username) {
        Player player = null;
        try {
            User user = this.getUser(username);
            if (user == null)
                return null;
            player = this.getPlayer(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return player;
    }

    public ArrayList<Player> getAllPlayers() {
        ArrayList<Player> players = new ArrayList<>();
        try {
            Statement statement = database.getStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM Players");
            while (rs.next()) {
                String username = rs.getString("username");
                Player player = getPlayer(username);
                players.add(player);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return players;
    }

    public void addPlayer(Player player) {
        String insertUserSQL = "INSERT INTO Users(username, password, nickname, email, securityQuestion, securityAnswer) VALUES (?, ?, ?, ?, ?, ?)";
        String insertPlayerSQL = "INSERT INTO Players(username, level, xp, hp, coins) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = database.getConnection();
             PreparedStatement pstmtUser = conn.prepareStatement(insertUserSQL);
             PreparedStatement pstmtPlayer = conn.prepareStatement(insertPlayerSQL)) {
            conn.setAutoCommit(false);
            // Insert into Users table
            pstmtUser.setString(1, player.getInfo("username"));
            pstmtUser.setString(2, player.getInfo("password"));
            pstmtUser.setString(3, player.getInfo("nickname"));
            pstmtUser.setString(4, player.getInfo("email"));
            pstmtUser.setString(5, player.getInfo("securityQ"));
            pstmtUser.setString(6, player.getInfo("securityA"));
            pstmtUser.executeUpdate();
            // Insert into Players table
            pstmtPlayer.setString(1, player.getInfo("username"));
            pstmtPlayer.setInt(2, player.getPlayerInfo("level"));
            pstmtPlayer.setInt(3, player.getPlayerInfo("xp"));
            pstmtPlayer.setInt(4, player.getPlayerInfo("hp"));
            pstmtPlayer.setInt(5, player.getPlayerInfo("coins"));
            pstmtPlayer.executeUpdate();
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updatePlayer(Player player) {
        String updateUserSQL = "UPDATE Users SET password = ?, nickname = ?, email = ?, securityQuestion = ?, securityAnswer = ? WHERE username = ?";
        String updatePlayerSQL = "UPDATE Players SET level = ?, xp = ?, hp = ?, coins = ? WHERE username = ?";
        try (Connection conn = database.getConnection();
             PreparedStatement pstmtUser = conn.prepareStatement(updateUserSQL);
             PreparedStatement pstmtPlayer = conn.prepareStatement(updatePlayerSQL)) {
            conn.setAutoCommit(false);
            // Update Users table
            pstmtUser.setString(1, player.getInfo("password"));
            pstmtUser.setString(2, player.getInfo("nickname"));
            pstmtUser.setString(3, player.getInfo("email"));
            pstmtUser.setString(4, player.getInfo("securityQ"));
            pstmtUser.setString(5, player.getInfo("securityA"));
            pstmtUser.setString(6, player.getInfo("username"));
            pstmtUser.executeUpdate();
            // Update Players table
            pstmtPlayer.setInt(1, player.getPlayerInfo("level"));
            pstmtPlayer.setInt(2, player.getPlayerInfo("xp"));
            pstmtPlayer.setInt(3, player.getPlayerInfo("hp"));
            pstmtPlayer.setInt(4, player.getPlayerInfo("coins"));
            pstmtPlayer.setString(5, player.getInfo("username"));
            pstmtPlayer.executeUpdate();
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Player getSecondaryPlayer() {
        return secondaryPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public void setSecondaryPlayer(Player secondaryPlayer) {
        this.secondaryPlayer = secondaryPlayer;
    }

    public ArrayList<MatchInfo> getMatchInfosByHostName(String hostName) {
        ArrayList<MatchInfo> matchInfos = new ArrayList<>();
        String sql = "SELECT * FROM MatchInfos WHERE hostName = ?";

        try (Connection conn = database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, hostName);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                MatchInfo matchInfo = new MatchInfo(
                        rs.getString("date"),
                        rs.getString("result"),
                        rs.getString("hostName"),
                        rs.getString("rivalName"),
                        rs.getString("rivalLevel"),
                        rs.getString("aftermath"));
                matchInfos.add(matchInfo);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving match infos: " + e.getMessage());
        }

        return matchInfos;
    }

    public boolean addMatchInfo(MatchInfo matchInfo) {
        String sql = "INSERT INTO MatchInfos (date, result, hostName, rivalName, rivalLevel, aftermath) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, matchInfo.getDate());
            pstmt.setString(2, matchInfo.getResult());
            pstmt.setString(3, matchInfo.getHostName());
            pstmt.setString(4, matchInfo.getRivalName());
            pstmt.setString(5, matchInfo.getRivalLevel());
            pstmt.setString(6, matchInfo.getAftermath());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Error adding match info: " + e.getMessage());
            return false;
        }
    }
}