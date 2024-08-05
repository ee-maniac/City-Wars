package code;

import code.inventory.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {
    private DataManager dataManager = new DataManager();
    private Stage primaryStage;
    private boolean firstTime = true;
    public boolean newPlayer = false;
    public MediaPlayer mediaPlayer = new MediaPlayer((new Media(new File("src/res/audio/one.m4a").toURI().toString())));

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("City wars: Mahroom regions");
        primaryStage.setMaximized(true);
        primaryStage.setWidth(2880);
        primaryStage.setHeight(1620);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.setOnEndOfMedia(() -> {
            mediaPlayer.seek(mediaPlayer.getStartTime());
            mediaPlayer.stop();
        });
        showLogin();
    }

    public void showSignup() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/Signup.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (Exception e) {
            System.out.println(e);
            return;
        }

        SignupMenu signupMenu = loader.getController();
        signupMenu.setMainApp(this);

        setScene(root, "/res/styles.css");
    }

    public void showLogin() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/Login.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (Exception e) {
            System.out.println(e);
            return;
        }

        LoginMenu loginMenu = loader.getController();
        loginMenu.setMainApp(this);

        setScene(root, "/res/styles.css");
    }

    public void showForgotPass() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/ForgotPass.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (Exception e) {
            System.out.println(e);
            return;
        }

        ForgotPassMenu forgotPassMenu = loader.getController();
        forgotPassMenu.setMainApp(this);

        setScene(root, "/res/styles.css");
    }

    public void showMainMenu() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/Main.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (Exception e) {
            System.out.println(e);
            return;
        }

        MainMenu mainMenu = loader.getController();
        mainMenu.setMainApp(this);

        setScene(root, "/res/styles.css");
    }

    public void showProfile() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/Profile.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (Exception e) {
            System.out.println(e);
            return;
        }

        ProfileMenu profileMenu = loader.getController();
        profileMenu.setMainApp(this);
        setScene(root, "/res/styles.css");
    }

    public void showShop() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/Shop.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (Exception e) {
            System.out.println(e);
            return;
        }

        ShopMenu shopMenu = loader.getController();
        shopMenu.setMainApp(this);
        setScene(root, "/res/styles.css");
    }

    public void showHistory() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/History.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (Exception e) {
            System.out.println(e);
            return;
        }

        HistoryMenu historyMenu = loader.getController();
        historyMenu.setMainApp(this);
        setScene(root, "/res/styles.css");
    }

    public void showOpLogin() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/OpLogin.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (Exception e) {
            System.out.println(e);
            return;
        }

        OpLoginMenu opLoginMenu = loader.getController();
        opLoginMenu.setMainApp(this);
        setScene(root, "/res/styles.css");
    }

    public void showCharacterMenu() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/Character.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (Exception e) {
            System.out.println(e);
            return;
        }

        CharacterMenu characterMenu = loader.getController();
        characterMenu.setMainApp(this);
        setScene(root, "/res/styles.css");
    }

    public void showGame(String player1Character, String player2character,
            int bettingAmount, boolean startOver) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/game.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (Exception e) {
            System.out.println(e);
            return;
        }

        GameController game = loader.getController();
        game.setupGame(this, dataManager, player1Character, player2character, bettingAmount, startOver);
        ;
        setScene(root, "/res/gstyles.css");
    }

    public void showEndGame(Player winnerPlayer, Player loserPlayer, int winnerLevelGain,
            int loserLevelGain, int winnerXpGain, int loserXpGain, int winnerCoinGain, int loserCoinGain,
            int betAmount) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/res/EndGame.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (Exception e) {
            System.out.println(e);
            return;
        }

        EndGameMenu endGameMenu = loader.getController();
        endGameMenu.setMainApp(this, winnerPlayer, loserPlayer, winnerLevelGain, loserLevelGain, winnerXpGain,
                loserXpGain, winnerCoinGain, loserCoinGain, betAmount);
        ;
        // appropriate fillers from the showEndGame(param)
        // endGameMenu.setWinnerPlayer(winnerPlayer);
        // endGameMenu.setLoserPlayer(loserPlayer);
        setScene(root, "/res/styles.css");
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    private void setScene(Parent root, String path) {
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource(path).toExternalForm());
        primaryStage.setScene(scene);

        if (firstTime) {
            primaryStage.show();
            firstTime = false;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}