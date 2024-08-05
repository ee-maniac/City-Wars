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

public class OpLoginMenu {
    private Main mainApp;
    private DataManager dataManager;

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
            return;
        }

        if (tempUser.getInfo("username").equals(dataManager.getCurrentPlayer().getInfo("username"))) {
            userLabel.setText("You must play with someone other than yourself!");
            return;
        }

        User user = dataManager.getUser(username);
        dataManager.setSecondaryPlayer(dataManager.getPlayer(user));
        mainApp.showCharacterMenu();
    }

    @FXML
    private void goBack() {
        mainApp.showMainMenu();
    }
}