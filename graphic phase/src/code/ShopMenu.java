package code;

import code.inventory.Card;
import code.inventory.DataManager;
import code.inventory.Player;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.ArrayList;
import java.util.Comparator;

public class ShopMenu {
    private Main mainApp;
    private DataManager dataManager;
    private int page = 1;
    private final int numPerPage = 10;
    private VBox selectedCard;
    private Card target = null;

    @FXML
    StackPane root;
    @FXML
    VBox dialog;
    @FXML
    VBox upgradeDialog;
    @FXML
    Label dialogLabel;
    @FXML
    Label upgradeLabel;
    @FXML
    Label cardName;
    @FXML
    Label cardDescription;
    @FXML
    ComboBox<String> propertyCombo;
    @FXML
    ComboBox<String> orderCombo;
    @FXML
    HBox cardStack1;
    @FXML
    HBox cardStack2;

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
        this.dataManager = mainApp.getDataManager();
        run();
    }

    private void run() {
        dialog.setManaged(false);
        dialog.setVisible(false);
        upgradeDialog.setManaged(false);
        upgradeDialog.setVisible(false);
        propertyCombo.setValue("name");
        orderCombo.setValue("ascending");
        propertyCombo.valueProperty().addListener((observable, oldValue, newValue) -> showStack(1));
        orderCombo.valueProperty().addListener((observable, oldValue, newValue) -> showStack(1));
        showStack(0);
    }

    private void showStack(int n) {
        if (n == 1) {
            page = 1;
        }
        cardStack2.getChildren().clear();
        cardStack1.getChildren().clear();

        ArrayList<Card> something = new ArrayList<>();

        for (Card c : dataManager.getAllCards()) {
            if (dataManager.getCurrentPlayer().getCard(c.getName()) == null) {
                something.add(c);
            }
        }

        something.addAll(dataManager.getPlayerCards(dataManager.getCurrentPlayer().getInfo("username")));

        ArrayList<Card> cards = sortCardStack(new ArrayList<>(something), propertyCombo.getValue(),
                orderCombo.getValue());

        int count = 0;
        for (int m = (page - 1) * numPerPage; m < (page) * numPerPage && m < cards.size(); m++) {
            Card card = cards.get(m);

            StackPane cardPane = new StackPane();
            cardPane.setStyle("-fx-border-radius: 15;");
            cardPane.setPrefSize(200, 250);
            cardPane.setOnMouseClicked(e -> {
                cardName.setText(card.getName());
                cardDescription.setText("Lorem Ipsum");
            });

            // Apply the clip
            Rectangle clip = new Rectangle(200, 250);
            clip.setArcWidth(15);
            clip.setArcHeight(15);
            cardPane.setClip(clip);

            VBox vbox1 = new VBox();
            vbox1.setPrefSize(200, 250);
            vbox1.setStyle("-fx-border-width: 5; -fx-border-color: #857100;");

            if (m == (page - 1) * numPerPage) {
                cardName.setText(card.getName());
                cardDescription.setText("Type: " + card.getType() +
                        ", Cost:" + card.getCost() + ", Upgrade Cost:" + card.getUpgradeCost());
                vbox1.setStyle("-fx-border-width: 5; -fx-border-color: #1e90bd;");
                selectedCard = vbox1;
            }

            cardPane.setOnMouseClicked(e -> {
                if (selectedCard != null) {
                    selectedCard.setStyle("-fx-border-width: 5; -fx-border-color: #857100;");
                }
                // Set the border color of the newly selected card
                cardName.setText(card.getName());
                cardDescription.setText("Type: " + card.getType() +
                        ", Cost:" + card.getCost() + ", Upgrade Cost:" + card.getUpgradeCost());
                vbox1.setStyle("-fx-border-width: 5; -fx-border-color: #1e90bd;");
                selectedCard = vbox1;
            });

            HBox hbox1 = new HBox();
            hbox1.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            hbox1.setStyle("-fx-background-color: grey;");
            Label accuracyLabel = new Label(String.valueOf(card.getAccuracy()));
            accuracyLabel.setTextFill(Color.web("#ffff8a"));
            accuracyLabel.setStyle("-fx-font-size: 30; -fx-font-weight: bold;");
            hbox1.getChildren().add(accuracyLabel);
            HBox.setMargin(accuracyLabel, new Insets(0, 0, 0, 10));

            HBox hbox2 = new HBox();
            hbox2.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
            hbox2.setPadding(new Insets(0, 10, 0, 0));
            hbox2.setStyle("-fx-background-color: #36454F;");
            for (int i = 1; i <= 5; i++) {
                Pane pane = new Pane();
                pane.setPrefSize(10, 20);
                pane.setMaxSize(10, 20);
                pane.setMinSize(10, 20);
                if (i <= card.getDuration()) {
                    pane.setStyle("-fx-background-color: white;");
                } else {
                    pane.setStyle("-fx-background-color: grey;");
                }
                HBox.setMargin(pane, new Insets(0, 0, 0, 5));
                hbox2.getChildren().add(pane);
            }

            Label damageLabel = new Label(String.valueOf(card.getDamagePerSector()));
            damageLabel.setTextFill(Color.web("#5079c6"));
            damageLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-font-style: italic;");
            HBox.setMargin(damageLabel, new Insets(0, 0, 0, 10));
            hbox2.getChildren().add(damageLabel);

            StackPane stackPane = new StackPane();
            ImageView imageView = new ImageView(new Image(card.getImagePath()));
            imageView.setFitHeight(160);
            imageView.setFitWidth(190);
            imageView.setPickOnBounds(true);

            Pane levelPane = new Pane();
            levelPane.setPrefSize(40, 40);
            levelPane.setMaxSize(40, 40);
            levelPane.setMinSize(40, 40);
            levelPane.setStyle("-fx-background-color: grey; -fx-background-radius: 30;");
            VBox levelBox = new VBox();
            levelBox.setPrefSize(40, 40);
            levelBox.setMaxSize(40, 40);
            levelBox.setMinSize(40, 40);
            levelBox.setAlignment(javafx.geometry.Pos.CENTER);
            Label levelLabel;
            if (dataManager.getCurrentPlayer().getCard(card.getName()) == null) {
                levelLabel = new Label(String.valueOf(card.getLevel()));
            } else {
                levelLabel = new Label(
                        String.valueOf(dataManager.getCurrentPlayer().getCard(card.getName()).getLevel()));
            }
            levelLabel.setTextFill(Color.WHITE);
            levelLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");
            levelBox.getChildren().add(levelLabel);
            levelPane.getChildren().add(levelBox);
            levelPane.setOnMouseClicked(e -> upgradeCard(card));
            StackPane.setMargin(levelPane, new Insets(110, 140, 0, 0));

            boolean exists = false;
            for (Card c : dataManager.getCurrentPlayer().getCards()) {
                if (c.getName().equals(card.getName())) {
                    exists = true;
                    break;
                }
            }

            stackPane.getChildren().addAll(imageView, levelPane);

            vbox1.getChildren().addAll(hbox1, hbox2, stackPane);
            cardPane.getChildren().add(vbox1);

            if (!exists) {
                Pane pane = new Pane();
                BackgroundFill backgroundFill = new BackgroundFill(Color.rgb(189, 189, 189, 0.5), CornerRadii.EMPTY,
                        Insets.EMPTY);
                Background background = new Background(backgroundFill);
                pane.setBackground(background);

                HBox lockBox = new HBox();
                lockBox.setAlignment(javafx.geometry.Pos.CENTER);
                lockBox.setPrefWidth(cardPane.getPrefWidth());
                lockBox.setPadding(new Insets(40, 0, 0, 0));

                VBox vBox = new VBox();
                vBox.setAlignment(javafx.geometry.Pos.CENTER);
                vBox.setPrefHeight(cardPane.getPrefHeight());

                ImageView lockImage = new ImageView(new Image("file:src/res/img/lock.png"));
                lockImage.setFitWidth(50);
                lockImage.setFitHeight(50);
                lockImage.setOnMouseClicked(e -> {
                    buyCard(card);
                });

                lockBox.getChildren().add(lockImage);
                vBox.getChildren().add(lockBox);
                pane.getChildren().add(vBox);
                cardPane.getChildren().add(pane);
            }

            if (count > 4) {
                cardStack2.getChildren().add(cardPane);
            } else {
                cardStack1.getChildren().add(cardPane);
            }

            count++;
        }
    }

    public ArrayList<Card> sortCardStack(ArrayList<Card> cards, String property, String sortOrder) {
        Comparator<Card> comparator;

        switch (property.toLowerCase()) {
            case "name":
                comparator = Comparator.comparing(Card::getName);
                break;
            case "type":
                comparator = Comparator.comparing(Card::getType);
                break;
            case "accuracy":
                comparator = (m1, m2) -> {
                    Integer x = m1.getAccuracy();
                    Integer y = m2.getAccuracy();
                    return x.compareTo(y);
                };
                break;
            case "damage":
                comparator = (m1, m2) -> {
                    Integer x = m1.getDamagePerSector();
                    Integer y = m2.getDamagePerSector();
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

        cards.sort(comparator);
        return cards;
    }

    private void buyCard(Card card) {
        target = card;
        if (!dialog.isManaged()) {
            dialog.setManaged(true);
            dialog.setVisible(true);
        }
        HBox hBox = (HBox) root.getChildren().get(2);
        root.getChildren().remove(hBox);
        Pane pane = new Pane();
        BackgroundFill backgroundFill = new BackgroundFill(Color.rgb(189, 189, 189, 0.5), CornerRadii.EMPTY,
                Insets.EMPTY);
        Background background = new Background(backgroundFill);
        pane.setBackground(background);
        root.getChildren().add(pane);
        root.getChildren().add(hBox);
    }

    private void upgradeCard(Card card) {
        target = card;
        if (!upgradeDialog.isManaged()) {
            upgradeDialog.setManaged(true);
            upgradeDialog.setVisible(true);
        }
        HBox hBox = (HBox) root.getChildren().get(2);
        root.getChildren().remove(hBox);
        Pane pane = new Pane();
        BackgroundFill backgroundFill = new BackgroundFill(Color.rgb(189, 189, 189, 0.5), CornerRadii.EMPTY,
                Insets.EMPTY);
        Background background = new Background(backgroundFill);
        pane.setBackground(background);
        root.getChildren().add(pane);
        root.getChildren().add(hBox);
    }

    @FXML
    private void goBack() {
        mainApp.showMainMenu();
    }

    @FXML
    private void previousPage() {
        if (page > 1) {
            page--;
            showStack(0);
        }
    }

    @FXML
    private void nextPage() {
        double x = ((double) dataManager.getAllCards().size() / (double) numPerPage);
        if (page < Math.ceil(x)) {
            page++;
            showStack(0);
        }
    }

    @FXML
    private void buyNo() {
        dialog.setManaged(false);
        dialog.setVisible(false);
        target = null;
        root.getChildren().remove(2);
    }

    @FXML
    private void buyYes() {
        if (dataManager.getCurrentPlayer().getPlayerInfo("coins") < target.getCost()) {
            dialogLabel.setText("Not enough coins");
            return;
        }

        dataManager.addPlayerCard(dataManager.getCurrentPlayer().getInfo("username"), target);
        for (Card c : dataManager.getPlayerCards(dataManager.getCurrentPlayer().getInfo("username"))) {
            if (c.getName().equals(target.getName())) {
                dataManager.getCurrentPlayer().addCard(c);
                break;
            }
        }
        dataManager.getCurrentPlayer().setPlayerInfo("coins",
                dataManager.getCurrentPlayer().getPlayerInfo("coins") - target.getCost());
        System.out.println("Card successfully bought.");

        dialog.setManaged(false);
        dialog.setVisible(false);
        target = null;

        Parent parent = selectedCard.getParent();
        if (parent instanceof StackPane) {
            StackPane parentStackPane = (StackPane) parent;
            ObservableList<Node> children = parentStackPane.getChildren();
            if (!children.isEmpty()) {
                children.remove(children.size() - 1);
            }
        }
        root.getChildren().remove(2);
    }

    @FXML
    private void upgradeNo() {
        upgradeDialog.setManaged(false);
        upgradeDialog.setVisible(false);
        target = null;
        root.getChildren().remove(2);
    }

    @FXML
    private void upgradeYes() {
        if (target.getUpgradeCost() > dataManager.getCurrentPlayer().getPlayerInfo("coins")) {
            upgradeLabel.setText("Not enough coins");
            return;
        }

        if (target.getLevel() * 1.5 > dataManager.getCurrentPlayer().getPlayerInfo("level")) {
            upgradeLabel.setText("You need to upgrade your level first.");
            return;
        }

        Player temp = dataManager.getCurrentPlayer();

        temp.getCard(target.getName()).setLevel(target.getLevel() + 1);
        // dataManager.updatePlayerCard(temp.getInfo("username"), target);
        // temp.removeCard(target.getName());
        // for(Card c : dataManager.getPlayerCards(temp.getInfo("username"))) {
        // if(c.getName().equals(target.getName())) {
        // temp.addCard(c);
        // break;
        // }
        // }
        temp.setPlayerInfo("coins", temp.getPlayerInfo("coins")
                - target.getUpgradeCost());

        upgradeDialog.setManaged(false);
        upgradeDialog.setVisible(false);
        target = null;

        StackPane stackPane = (StackPane) selectedCard.getChildren().get(2);
        Pane pane = (Pane) stackPane.getChildren().get(1);
        VBox vBox = (VBox) pane.getChildren().get(0);
        Label label = (Label) vBox.getChildren().get(0);
        label.setText(Integer.toString(Integer.parseInt(label.getText()) + 1));

        root.getChildren().remove(2);
    }
}