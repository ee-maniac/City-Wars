package menus.ProfileClasses;

import inventory.*;

public class ShowInfo {
    public static void showInfo(DataManager dataManager) {
        Player tempPlayer = dataManager.getCurrentPlayer();
        System.out.println("Username: " + tempPlayer.getInfo("username"));
        System.out.println("Password: " + tempPlayer.getInfo("password"));
        System.out.println("Nickname: " + tempPlayer.getInfo("nickname"));
        System.out.println("Security Question: " + tempPlayer.getInfo("securityQ"));
        System.out.println("Security Answer: " + tempPlayer.getInfo("securityA"));
        System.out.println("Level: " + tempPlayer.getPlayerInfo("level"));
        System.out.println("XP: " + tempPlayer.getPlayerInfo("xp"));
        System.out.println("HP: " + tempPlayer.getPlayerInfo("hp"));
        System.out.println("Coins: " + tempPlayer.getPlayerInfo("coins"));
    }
}