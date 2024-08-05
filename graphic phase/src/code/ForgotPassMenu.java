package code;

import code.inventory.DataManager;
import code.inventory.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class ForgotPassMenu {
    private Main mainApp;
    private DataManager dataManager;

    @FXML
    TextField userField;
    @FXML
    Label userLabel;
    @FXML
    HBox questionHBox;
    @FXML
    Label questionLabel;
    @FXML
    TextField securityAField;
    @FXML
    Label answerLabel;

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
        dataManager = mainApp.getDataManager();
    }

    @FXML
    private void initialize() {
        questionHBox.setVisible(false);
        questionHBox.setDisable(true);
        userField.textProperty().addListener((observable, oldValue, newValue) -> validateUsername(newValue));
    }

    @FXML
    private void handleLogin() {
        String username = userField.getText();
        String answer = securityAField.getText();

        if(!validateUsername(username)) {
            return;
        }
        if(!validateAnswer(answer)) {
            return;
        }

        User user = dataManager.getUser(username);
        dataManager.setCurrentPlayer(dataManager.getPlayer(user));
        mainApp.showMainMenu();
    }

    @FXML
    private void goToLogin() {
        mainApp.showLogin();
    }

    private boolean validateUsername(String username) {
        if(username.isEmpty()) {
            userLabel.setText("Username field is empty.");
            showHBox(false);
            return false;
        }
        else if(dataManager.getUser(username) == null) {
            userLabel.setText("There is no such username.");
            showHBox(false);
            return false;
        }
        else {
            userLabel.setText("");
            showHBox(true);
            return true;
        }
    }

    private void showHBox(boolean b) {
        questionHBox.setVisible(b);
        questionHBox.setDisable(!b);

        if(b) {
            questionLabel.setText(dataManager.getUser(userField.getText()).getInfo("securityQ"));
        }
    }

    private boolean validateAnswer(String answer) {
        if(answer.isEmpty()) {
            answerLabel.setText("Security answer field is empty.");
            return false;
        }
        else if(!dataManager.getUser(userField.getText()).getInfo("securityA").equals(answer)) {
            answerLabel.setText("Security answer and question do not match.");
            return false;
        }
        else {
            answerLabel.setText("");
            return true;
        }
    }
}
