package code;

import code.inventory.DataManager;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class MainMenu {
    private Main mainApp;
    private DataManager dataManager;
    private StackPane selectedStackPane = null;

    @FXML
    ImageView background;
    @FXML
    StackPane profileHolder;

    @FXML
    Label hpLabel;
    @FXML
    Label xpLabel;
    @FXML
    Label coinsLabel;
    @FXML
    Label levelLabel;

    @FXML
    HBox hbox1;
    @FXML
    HBox hbox2;
    @FXML
    HBox hbox3;
    @FXML
    HBox hbox4;

    @FXML
    HBox settingBox;
    @FXML
    StackPane changeBackg;
    @FXML
    StackPane changeMusic;
    @FXML
    StackPane changeVolume;

    @FXML
    VBox backgHolder;
    @FXML
    VBox musicHolder;
    @FXML
    VBox volumeHolder;

    @FXML
    ImageView speaker;
    @FXML
    Slider slider;

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
        this.dataManager = mainApp.getDataManager();
        run();
    }

    private void run() {
        slider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() == 0) {
                    speaker.setImage(new Image("file:src/res/img/mute.png"));
                } else if (oldValue.intValue() == 0 && newValue.intValue() != 0) {
                    speaker.setImage(new Image("file:src/res/img/volume.png"));
                }
            }
        });

        mainApp.mediaPlayer.volumeProperty().bind(slider.valueProperty().divide(100));
        mainApp.mediaPlayer.play();

        hpLabel.setText(Integer.toString(dataManager.getCurrentPlayer().getPlayerInfo("hp")));
        xpLabel.setText(Integer.toString(dataManager.getCurrentPlayer().getPlayerInfo("xp")));
        coinsLabel.setText(Integer.toString(dataManager.getCurrentPlayer().getPlayerInfo("coins")));
        levelLabel.setText(Integer.toString(dataManager.getCurrentPlayer().getPlayerInfo("level")));

        Platform.runLater(() -> {
            DoubleProperty maxWidthProperty = new SimpleDoubleProperty();

            maxWidthProperty.bind(Bindings.createDoubleBinding(
                    () -> Math.max(Math.max(hbox1.getWidth(), hbox2.getWidth()),
                            Math.max(hbox3.getWidth(), hbox4.getWidth())),
                    hbox1.widthProperty(), hbox2.widthProperty(), hbox3.widthProperty(), hbox4.widthProperty()));

            hbox1.prefWidthProperty().bind(maxWidthProperty);
            hbox2.prefWidthProperty().bind(maxWidthProperty);
            hbox3.prefWidthProperty().bind(maxWidthProperty);
            hbox4.prefWidthProperty().bind(maxWidthProperty);

            if (mainApp.newPlayer) {
                mainApp.newPlayer = false;
                showGift();
            }
        });

        settingBox.setVisible(false);
        settingBox.setManaged(false);
        changeBackg();
    }

    @FXML
    private void showGift() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Welcome");

        ImageView imageView = new ImageView(new Image("file:src/res/img/gift.png"));
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);

        Label messageLabel = new Label("Welcome new player, you are gifted 20 cards. You can view them in the shop.");

        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));
        vbox.getChildren().addAll(imageView, messageLabel);

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

    @FXML
    private void changeBackg() {
        if (selectedStackPane != null) {
            selectedStackPane.setStyle("-fx-background-color: #505050; -fx-background-radius: 5;");
        }
        selectedStackPane = changeBackg;
        changeBackg.setStyle("-fx-background-color: grey; -fx-background-radius: 5;");

        backgHolder.setManaged(true);
        backgHolder.setVisible(true);
        musicHolder.setManaged(false);
        musicHolder.setVisible(false);
        volumeHolder.setManaged(false);
        volumeHolder.setVisible(false);
    }

    @FXML
    private void changeMusic() {
        if (selectedStackPane != null) {
            selectedStackPane.setStyle("-fx-background-color: #505050; -fx-background-radius: 5;");
        }
        selectedStackPane = changeMusic;
        changeMusic.setStyle("-fx-background-color: grey; -fx-background-radius: 5;");

        musicHolder.setManaged(true);
        musicHolder.setVisible(true);
        backgHolder.setManaged(false);
        backgHolder.setVisible(false);
        volumeHolder.setManaged(false);
        volumeHolder.setVisible(false);
    }

    @FXML
    private void changeVolume() {
        if (selectedStackPane != null) {
            selectedStackPane.setStyle("-fx-background-color: #505050; -fx-background-radius: 5;");
        }
        selectedStackPane = changeVolume;
        changeVolume.setStyle("-fx-background-color: grey; -fx-background-radius: 5;");

        backgHolder.setManaged(false);
        backgHolder.setVisible(false);
        musicHolder.setManaged(false);
        musicHolder.setVisible(false);
        volumeHolder.setManaged(true);
        volumeHolder.setVisible(true);
    }

    @FXML
    private void dayBackg() {
        background.setImage(new Image("file:src/res/img/2309955.jpg"));
    }

    @FXML
    private void nightBackg() {
        background.setImage(new Image("file:src/res/img/308791.jpg"));
    }

    @FXML
    private void playOne() {
        mainApp.mediaPlayer.dispose();
        mainApp.mediaPlayer = new MediaPlayer((new Media(new File("src/res/audio/one.m4a").toURI().toString())));
        mainApp.mediaPlayer.volumeProperty().bind(slider.valueProperty().divide(100));
        mainApp.mediaPlayer.play();
    }

    @FXML
    private void playTwo() {
        mainApp.mediaPlayer.dispose();
        mainApp.mediaPlayer = new MediaPlayer((new Media(new File("src/res/audio/two.mp3").toURI().toString())));
        mainApp.mediaPlayer.volumeProperty().bind(slider.valueProperty().divide(100));
        mainApp.mediaPlayer.play();
    }

    @FXML
    private void toProfile() {
        mainApp.showProfile();
    }

    @FXML
    private void logout() {
        dataManager.setCurrentPlayer(null);
        // mainApp.mediaPlayer.dispose();
        mainApp.showLogin();
    }

    @FXML
    private void toSetting() {
        if (settingBox.isManaged()) {
            settingBox.setVisible(false);
            settingBox.setManaged(false);
        } else {
            settingBox.setVisible(true);
            settingBox.setManaged(true);
        }
    }

    @FXML
    private void toLogin() {
        mainApp.showLogin();
    }

    @FXML
    private void toShop() {
        mainApp.showShop();
    }

    @FXML
    private void toHistory() {
        mainApp.showHistory();
    }

    @FXML
    private void toLobby() {
        mainApp.mediaPlayer.stop();
        mainApp.showOpLogin();
    }
}