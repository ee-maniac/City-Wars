package code;

import code.inventory.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.util.Timer;
import java.util.TimerTask;

public class LoginMenu {
    private Main mainApp;
    private DataManager dataManager;
    private int incorrect = 0;
    private Timer timer = new Timer();

    @FXML
    TextField userField;
    @FXML
    PasswordField passField;
    @FXML
    Label userLabel;
    @FXML
    Label passLabel;
    @FXML
    Button login;

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
        dataManager = mainApp.getDataManager();
    }

    @FXML
    private void handleLogin() {
        String username = userField.getText();
        String password = passField.getText();
        User tempUser;

        if (username.isEmpty()) {
            userLabel.setText("Username cannot be empty.");
            return;
        } else {
            userLabel.setText("");
        }

        if (password.isEmpty()) {
            passLabel.setText("Password cannot be empty.");
            return;
        } else {
            passLabel.setText("");
        }

        if ((tempUser = dataManager.getUser(username)) == null) {
            userLabel.setText("There is no such username.");
            return;
        } else if (!tempUser.validatePassword(password)) {
            userLabel.setText("");
            passLabel.setText("Username and password do not match.");
            incorrect++;
            handleIncorrectPassword(incorrect * 5L);
            return;
        }

        User user = dataManager.getUser(username);
        dataManager.setCurrentPlayer(dataManager.getPlayer(user));
        mainApp.showMainMenu();
    }

    @FXML
    private void goToForgotPass() {
        mainApp.showForgotPass();
    }

    @FXML
    private void goToSignup() {
        mainApp.showSignup();
    }

    private void handleIncorrectPassword(long seconds) {
        login.setDisable(true);
        login.setText("Try in " + seconds + " seconds");
        login.setStyle("-fx-background-color: red; -fx-background-radius: 30;");

        TimerTask task = new TimerTask() {
            private long countdown = seconds;

            @Override
            public void run() {
                Platform.runLater(() -> {
                    countdown--;
                    if (countdown > 0) {
                        login.setText("Try in " + countdown + " seconds");
                    } else {
                        login.setDisable(false);
                        login.setText("Log In");
                        login.setStyle("-fx-background-color: #1ed760; -fx-background-radius: 30;");
                        this.cancel();
                    }
                });
            }
        };

        timer.scheduleAtFixedRate(task, 1000, 1000);
    }
}