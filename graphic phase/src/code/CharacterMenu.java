package code;

import code.inventory.DataManager;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;

public class CharacterMenu {
    private Main mainApp;
    private DataManager dataManager;
    private String selectedChar1 = "";
    private String selectedChar2 = "";
    private int currentIndex1 = 0;
    private int currentIndex2 = 0;
    private boolean betMode = false;
    private int betAmount = 0;
    private int selectionsLeft = 2;

    private ArrayList<String> characterNames = new ArrayList<>();

    private HashMap<String, String> characterImages = new HashMap<>();

    @FXML
    ImageView image1;
    @FXML
    ImageView image2;
    @FXML
    Button button2;
    @FXML
    Button button1;
    @FXML
    Label label1;
    @FXML
    Label label2;
    @FXML
    Label char1;
    @FXML
    Label char2;

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
        dataManager = mainApp.getDataManager();
        run();
    }

    private void startGame() {
        selectionsLeft--;
        if (selectionsLeft == 0 && !selectedChar1.equals("") && !selectedChar2.equals("")) {
            int bettingAmount = (betMode) ? betAmount : -1;
            mainApp.showGame(selectedChar1, selectedChar2, bettingAmount, true); // must be fixed
        }
    }

    private void run() {
        char1.setText(dataManager.getCurrentPlayer().getInfo("nickname") + "  character");
        char2.setText(dataManager.getSecondaryPlayer().getInfo("nickname") + "  character");

        characterNames.add("ranger");
        characterNames.add("warrior");
        characterNames.add("sorcerer");
        characterNames.add("rogue");

        characterImages.put("ranger", "file:src/res/img/char1.png");
        characterImages.put("warrior", "file:src/res/img/char2.png");
        characterImages.put("sorcerer", "file:src/res/img/char3.png");
        characterImages.put("rogue", "file:src/res/img/char4.png");

        updateCharacterDisplay1();
        updateCharacterDisplay2();

        Platform.runLater(() -> {
            showBetModeDialog();
        });
    }

    private void showBetModeDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Bet Mode");

        Label messageLabel = new Label("Do you want to play in bet mode?");

        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));
        vbox.getChildren().add(messageLabel);

        dialog.getDialogPane().setContent(vbox);
        ButtonType yesButtonType = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButtonType = new ButtonType("No", ButtonBar.ButtonData.NO);
        dialog.getDialogPane().getButtonTypes().addAll(yesButtonType, noButtonType);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == yesButtonType) {
                showAmountDialog();
                return dialogButton;
            }
            return null;
        });

        Region spacer = new Region();
        ButtonBar.setButtonData(spacer, ButtonBar.ButtonData.BIG_GAP);
        HBox.setHgrow(spacer, Priority.ALWAYS);
        dialog.getDialogPane().applyCss();
        HBox hboxDialogPane = (HBox) dialog.getDialogPane().lookup(".container");
        hboxDialogPane.getChildren().add(spacer);

        dialog.showAndWait();
    }

    private void showAmountDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Enter Amount");

        Label messageLabel = new Label("Please enter the amount:");
        TextField amountField = new TextField();
        amountField.setPromptText("Amount");

        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));
        vbox.getChildren().addAll(messageLabel, amountField);

        dialog.getDialogPane().setContent(vbox);
        ButtonType goAheadButtonType = new ButtonType("Go Ahead", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(goAheadButtonType, cancelButtonType);

        final Button btOk = (Button) dialog.getDialogPane().lookupButton(goAheadButtonType);
        btOk.addEventFilter(
                ActionEvent.ACTION,
                event -> {
                    String amount = amountField.getText();

                    if (!amount.matches("\\d+") || Integer.parseInt(amount) < 1) {
                        messageLabel.setText("Please enter a valid amount");
                        event.consume();
                        return;
                    }

                    if (Integer.parseInt(amount) > dataManager.getCurrentPlayer().getPlayerInfo("coins") ||
                            Integer.parseInt(amount) > dataManager.getSecondaryPlayer().getPlayerInfo("coins")) {
                        messageLabel.setText("Not enough money");
                        event.consume();
                        return;
                    }

                    betAmount = Integer.parseInt(amount);
                    betMode = true;
                });

        Region spacer = new Region();
        ButtonBar.setButtonData(spacer, ButtonBar.ButtonData.BIG_GAP);
        HBox.setHgrow(spacer, Priority.ALWAYS);
        dialog.getDialogPane().applyCss();
        HBox hboxDialogPane = (HBox) dialog.getDialogPane().lookup(".container");
        hboxDialogPane.getChildren().add(spacer);

        dialog.showAndWait();
    }

    private void updateCharacterDisplay1() {
        String characterName = characterNames.get(currentIndex1);
        label1.setText(characterName);
        image1.setImage(new Image(characterImages.get(characterName)));
    }

    private void updateCharacterDisplay2() {
        String characterName = characterNames.get(currentIndex2);
        label2.setText(characterName);
        image2.setImage(new Image(characterImages.get(characterName)));
    }

    @FXML
    private void selectP1() {
        if (selectedChar1.isEmpty()) {
            button1.setStyle("-fx-background-color: #1895a6; -fx-background-radius: 30;");
            button1.setText("Selected");
            selectedChar1 = characterNames.get(currentIndex1);
        } else {
            selectedChar1 = "";
            button1.setStyle("-fx-background-color: #1ed760; -fx-background-radius: 30;");
            button1.setText("Select Character");
        }
        startGame();
    }

    @FXML
    private void selectP2() {
        if (selectedChar2.isEmpty()) {
            button2.setStyle("-fx-background-color: #1895a6; -fx-background-radius: 30;");
            button2.setText("Selected");
            selectedChar2 = characterNames.get(currentIndex2);

        } else {
            selectedChar2 = "";
            button2.setStyle("-fx-background-color: #1ed760; -fx-background-radius: 30;");
            button2.setText("Select Character");
        }
        startGame();
    }

    @FXML
    private void nextP1() {
        currentIndex1 = (currentIndex1 + 1) % characterNames.size();
        updateCharacterDisplay1();
    }

    @FXML
    private void previousP1() {
        currentIndex1 = (currentIndex1 - 1 + characterNames.size()) % characterNames.size();
        updateCharacterDisplay1();
    }

    @FXML
    private void nextP2() {
        currentIndex2 = (currentIndex2 + 1) % characterNames.size();
        updateCharacterDisplay2();
    }

    @FXML
    private void previousP2() {
        currentIndex2 = (currentIndex2 - 1 + characterNames.size()) % characterNames.size();
        updateCharacterDisplay2();
    }

    @FXML
    private void goBack() {
        mainApp.showOpLogin();
    }
}