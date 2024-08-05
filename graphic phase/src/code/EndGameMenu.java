package code;

import code.inventory.DataManager;
import code.inventory.Player;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class EndGameMenu {
    private Main mainApp;
    private DataManager dataManager;
    private Player loserPlayer;
    private Player winnerPlayer;
    // private boolean winnerUpgrade;
    // private boolean loserUpgrade;
    // private int wGiftNum;
    // private int lGiftNum;
    private int betAmount;
    int winnerXpGain;
    int loserXpGain;
    int winnerCoinGain;
    int loserCoinGain;
    int winnerLevelGain;
    int loserLevelGain;

    @FXML
    Label winnerLabel;
    @FXML
    Label loserLabel;
    @FXML
    Label wXPLabel;
    @FXML
    Label lXPLabel;
    @FXML
    Label wLvLabel;
    @FXML
    Label lLvLabel;
    @FXML
    Label wLevelup;
    @FXML
    Label lLevelup;
    @FXML
    Label wGift;
    @FXML
    Label lGift;
    @FXML
    HBox wLevelHBox;
    @FXML
    HBox lLevelHBox;
    @FXML
    HBox wGiftHBox;
    @FXML
    HBox lGiftHBox;

    public void setMainApp(Main mainApp, Player winnerPlayer, Player loserPlayer, int winnerLevelGain,
            int loserLevelGain, int winnerXpGain, int loserXpGain, int winnerCoinGain, int loserCoinGain,
            int betAmount) {
        this.mainApp = mainApp;
        dataManager = mainApp.getDataManager();
        this.winnerPlayer = winnerPlayer;
        this.loserPlayer = loserPlayer;
        this.winnerLevelGain = winnerLevelGain;
        this.loserLevelGain = loserLevelGain;
        this.winnerXpGain = winnerXpGain;
        this.loserXpGain = loserXpGain;
        this.winnerCoinGain = winnerCoinGain;
        this.loserCoinGain = loserCoinGain;
        this.betAmount = betAmount;
        run();
    }

    public void setWinnerPlayer(Player winnerPlayer) {
        this.winnerPlayer = winnerPlayer;
    }

    public void setLoserPlayer(Player loserPlayer) {
        this.loserPlayer = loserPlayer;
    }

    // public void setUpgrade(boolean uW, boolean uL) {
    // winnerUpgrade = uW;
    // loserUpgrade = uL;
    // }

    // public void setWGiftNum(int wGiftNum) {
    // this.wGiftNum = wGiftNum;
    // run();
    // }

    // public void setLGiftNum(int lGiftNum) {
    // this.lGiftNum = lGiftNum;
    // run();
    // }

    public void setBetAmount(int betAmount) {
        this.betAmount = betAmount;
        run();
    }

    private void run() {
        winnerLabel.setText(winnerPlayer.getInfo("nickname") + " won the match!");
        loserLabel.setText(loserPlayer.getInfo("nickname") + " lost the match!");
        wXPLabel.setText(winnerPlayer.getInfo("nickname") + " XP +" + winnerXpGain);
        lXPLabel.setText(loserPlayer.getInfo("nickname") + " XP +" + loserXpGain);
        wLvLabel.setText(winnerPlayer.getInfo("nickname") + " Level +" + winnerLevelGain);
        lLvLabel.setText(loserPlayer.getInfo("nickname") + " Level +" + loserLevelGain);
        wGift.setText(winnerPlayer.getInfo("nickname") + " Coins +" + winnerCoinGain);
        lGift.setText(winnerPlayer.getInfo("nickname") + " Coins +" + loserCoinGain);
        if (betAmount != -1) {
            wLevelup.setText("You won " + betAmount + " just by betting in this match!");
            lLevelup.setText("you lost " + betAmount);
        } else {
            lLevelHBox.setManaged(false);
            lLevelHBox.setVisible(false);
            wLevelHBox.setManaged(false);
            wLevelHBox.setVisible(false);
        }
        // if (winnerUpgrade) {
        // } else {
        // wLevelHBox.setManaged(false);
        // wLevelHBox.setVisible(false);
        // }
        // if (loserUpgrade) {
        // lGift.setText(Integer.toString(lGiftNum));
        // } else {
        // lLevelHBox.setManaged(false);
        // lLevelHBox.setVisible(false);
        // }
    }

    @FXML
    private void goHome() {
        dataManager.setSecondaryPlayer(null);
        mainApp.showMainMenu();
    }

    @FXML
    private void replay() {
        if (betAmount == -1) {
            // replay
        } else if (betAmount > dataManager.getCurrentPlayer().getPlayerInfo("coins") ||
                betAmount > dataManager.getSecondaryPlayer().getPlayerInfo("coins")) {

            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Error");

            Label messageLabel = new Label("Not enough money to replay bet mode");

            VBox vbox = new VBox(10);
            vbox.setAlignment(Pos.CENTER);
            vbox.setPadding(new Insets(20));
            vbox.getChildren().addAll(messageLabel);

            dialog.getDialogPane().setContent(vbox);
            ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().add(okButtonType);

            Region spacer = new Region();
            ButtonBar.setButtonData(spacer, ButtonBar.ButtonData.BIG_GAP);
            HBox.setHgrow(spacer, Priority.ALWAYS);
            dialog.getDialogPane().applyCss();
            HBox hboxDialogPane = (HBox) dialog.getDialogPane().lookup(".container");
            hboxDialogPane.getChildren().add(spacer);

            dialog.showAndWait();
        }
    }
}