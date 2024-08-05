package code;

import code.inventory.*;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import java.util.regex.Pattern;

public class ProfileMenu {
    private Main mainApp;
    private DataManager dataManager;
    private String captchaText;

    @FXML
    StackPane captchaHolder;
    @FXML
    HBox captchaHBox;
    @FXML
    TextField emailField;
    @FXML
    TextField userField;
    @FXML
    TextField nicknameField;
    @FXML
    TextField passField;
    @FXML
    ComboBox<String> qCombo;
    @FXML
    TextField answerField;
    @FXML
    TextField captchaField;

    @FXML
    Label emailLabel;
    @FXML
    Label userLabel;
    @FXML
    Label nicknameLabel;
    @FXML
    Label passLabel;
    @FXML
    Label questionLabel;
    @FXML
    Label answerLabel;
    @FXML
    Label captchaLabel;

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
        this.dataManager = mainApp.getDataManager();
        run();
    }

    private void run() {
        emailField.setText(dataManager.getCurrentPlayer().getInfo("email"));
        userField.setText(dataManager.getCurrentPlayer().getInfo("username"));
        nicknameField.setText(dataManager.getCurrentPlayer().getInfo("nickname"));
        passField.setText(dataManager.getCurrentPlayer().getInfo("password"));
        qCombo.setValue(dataManager.getCurrentPlayer().getInfo("securityQ"));
        answerField.setText(dataManager.getCurrentPlayer().getInfo("securityA"));

        emailField.textProperty().addListener((observable, oldValue, newValue) -> validateEmail(newValue));
        userField.textProperty().addListener((observable, oldValue, newValue) -> validateUsername(newValue));
        nicknameField.textProperty().addListener((observable, oldValue, newValue) -> validateNickname(newValue));
        passField.textProperty().addListener((observable, oldValue, newValue) -> validatePassword(newValue));
        qCombo.valueProperty().addListener((observable, oldValue, newValue) -> validateQ(newValue));
        answerField.textProperty().addListener((observable, oldValue, newValue) -> validateA(newValue));

        captchaHBox.setVisible(false);
        captchaHBox.setManaged(false);
    }

    @FXML
    private void handleUpdate() {
        String email = emailField.getText();
        String username = userField.getText();
        String nickname = nicknameField.getText();
        String password = passField.getText();
        String securityQ = qCombo.getValue();
        String securityA = answerField.getText();
        String captcha = captchaField.getText();

        if (validateInput(email, username, nickname, password, securityQ, securityA, captcha)) {
            dataManager.getCurrentPlayer().setInfo("email", email);
            dataManager.getCurrentPlayer().setInfo("username", username);
            dataManager.getCurrentPlayer().setInfo("nickname", nickname);
            dataManager.getCurrentPlayer().setInfo("password", password);
            dataManager.getCurrentPlayer().setInfo("securityQ", securityQ);
            dataManager.getCurrentPlayer().setInfo("securityA", securityA);

            dataManager.updatePlayer(dataManager.getCurrentPlayer());
            System.out.println("ss");
            mainApp.showProfile();
        }
    }

    private boolean validateInput(String email, String username, String nickname, String password, String securityQ, String securityA, String captcha) {
        boolean valid = true;

        if(!validateEmail(email)) {
            valid = false;
        }

        if(!validateUsername(username)) {
            valid = false;
        }

        if(!validateNickname(nickname)) {
            valid = false;
        }

        if(!validatePassword(password)) {
            valid = false;
        }

        if(!validateQ(securityQ)) {
            valid = false;
        }

        if(!validateA(securityA)) {
            valid = false;
        }

        if(!password.equals(dataManager.getCurrentPlayer().getInfo("password"))) {
            if(!validateCaptcha(captcha)) {
                valid = false;
            }
        }

        return valid;
    }

    @FXML
    private void goBack() {
        mainApp.showMainMenu();
    }

    @FXML
    private void reCaptcha() {
        captchaHolder.getChildren().clear();
        captchaText = CaptchaGenerator.generateCaptchaText();
        Canvas captchaCanvas = CaptchaGenerator.generateCaptchaImage(captchaText);
        captchaHolder.getChildren().add(captchaCanvas);
    }

    private boolean validateEmail(String email) {
        if(email.isEmpty()) {
            emailLabel.setText("Email field is empty.");
            return false;
        } else if(!isValidEmail(email)) {
            emailLabel.setText("Invalid email format. Should be name@domain.com.");
            return false;
        } else {
            emailLabel.setText("");
            return true;
        }
    }

    private boolean validateUsername(String username) {
        if(username.isEmpty()) {
            userLabel.setText("Username field is empty.");
            return false;
        } else {
            userLabel.setText("");
            return true;
        }
    }

    private boolean validateNickname(String nickname) {
        if(nickname.isEmpty()) {
            nicknameLabel.setText("Nickname field is empty.");
            return false;
        } else {
            nicknameLabel.setText("");
            return true;
        }
    }

    private boolean validatePassword(String password) {
        if(!password.equals(dataManager.getCurrentPlayer().getInfo("password"))) {
            if(!captchaHBox.isManaged()) {
                captchaHBox.setVisible(true);
                captchaHBox.setManaged(true);
                reCaptcha();
            }
        }
        else {
            if(captchaHBox.isManaged()) {
                captchaHBox.setVisible(false);
                captchaHBox.setManaged(false);
            }
        }

        if(password.isEmpty()) {
            passLabel.setText("Password field is empty.");
            return false;
        } else if(!isValidPassword(password)) {
            passLabel.setText("Password must be at least 10 characters long, contain at least one digit, one letter, and one special character (#).");
            return false;
        } else {
            passLabel.setText("");
            return true;
        }
    }

    private boolean validateQ(String question) {
        if(question == null || question.isEmpty()) {
            questionLabel.setText("Question must be selected.");
            return false;
        } else{
            questionLabel.setText("");
            return true;
        }
    }

    private boolean validateA(String answer) {
        if(answer.isEmpty()) {
            answerLabel.setText("Security answer field is empty.");
            return false;
        } else {
            answerLabel.setText("");
            return true;
        }
    }

    private boolean validateCaptcha(String answer) {
        if(answer.isEmpty()) {
            captchaLabel.setText("Captcha field is empty.");
            return false;
        } else if(!answer.equals(captchaText)) {
            captchaLabel.setText("Captcha is incorrect.");
            return false;
        } else {
            captchaLabel.setText("");
            return true;
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return Pattern.matches(emailRegex, email);
    }

    private boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[#@!$%^&*+-])[A-Za-z\\d#@!$%^&*+-]{10,}$";
        return Pattern.matches(passwordRegex, password);
    }
}
