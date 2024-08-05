package code;

import code.inventory.*;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.regex.Pattern;

public class SignupMenu {
    private Main mainApp;
    private DataManager dataManager;
    private String captchaText;

    @FXML
    StackPane captchaHolder;
    @FXML
    TextField emailField;
    @FXML
    TextField userField;
    @FXML
    TextField nicknameField;
    @FXML
    TextField passField;
    @FXML
    TextField passConfField;
    @FXML
    ComboBox<String> qCombo;
    @FXML
    TextField securityAField;
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
    Label passConfLabel;
    @FXML
    Label questionLabel;
    @FXML
    Label answerLabel;
    @FXML
    Label captchaLabel;
    @FXML
    Label loginLabel;

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
        this.dataManager = mainApp.getDataManager();
    }

    @FXML
    private void initialize() {
        emailField.textProperty().addListener((observable, oldValue, newValue) -> validateEmail(newValue));
        userField.textProperty().addListener((observable, oldValue, newValue) -> validateUsername(newValue));
        nicknameField.textProperty().addListener((observable, oldValue, newValue) -> validateNickname(newValue));
        passField.textProperty().addListener((observable, oldValue, newValue) -> validatePassword(newValue));
        passConfField.textProperty()
                .addListener((observable, oldValue, newValue) -> validatePasswordConfirmation(newValue));
        qCombo.valueProperty().addListener((observable, oldValue, newValue) -> validateQ(newValue));
        securityAField.textProperty().addListener((observable, oldValue, newValue) -> validateA(newValue));
        reCaptcha();
    }

    @FXML
    private void handleSignup() {
        String email = emailField.getText();
        String username = userField.getText();
        String nickname = nicknameField.getText();
        String password = passField.getText();
        String passConfirm = passConfField.getText();
        String securityQ = qCombo.getValue();
        String securityA = securityAField.getText();
        String captcha = captchaField.getText();

        if (validateInput(email, username, nickname, password, passConfirm, securityQ, securityA, captcha)) {
            User user = new User(username, password, nickname, email, securityQ, securityA);
            dataManager.setCurrentPlayer(new Player(user, 1, 0, 80, 100));

            Random random = new Random();
            dataManager.addPlayer(dataManager.getCurrentPlayer());
            ArrayList<Card> cards = dataManager.getAllCards();
            Collections.shuffle(cards, random);
            for (int i = 0; i < 20; i++) {
                dataManager.addPlayerCard(dataManager.getCurrentPlayer().getInfo("username"), cards.get(i));
                dataManager.getCurrentPlayer().addCard(cards.get(i));
            }

            mainApp.newPlayer = true;
            mainApp.showMainMenu();
        }
    }

    @FXML
    private void goToLogin() {
        mainApp.showLogin();
    }

    @FXML
    public void generateRandom() {
        passField.setText(PasswordGenerator.generateRandomPassword(12));
    }

    @FXML
    private void reCaptcha() {
        captchaHolder.getChildren().clear();
        captchaText = CaptchaGenerator.generateCaptchaText();
        Canvas captchaCanvas = CaptchaGenerator.generateCaptchaImage(captchaText);
        captchaHolder.getChildren().add(captchaCanvas);
    }

    private boolean validateEmail(String email) {
        if (email.isEmpty()) {
            emailLabel.setText("Email field is empty.");
            return false;
        } else if (!isValidEmail(email)) {
            emailLabel.setText("Invalid email format. Should be name@domain.com.");
            return false;
        } else {
            emailLabel.setText("");
            return true;
        }
    }

    private boolean validateUsername(String username) {
        if (username.isEmpty()) {
            userLabel.setText("Username field is empty.");
            return false;
        } else {
            userLabel.setText("");
            return true;
        }
    }

    private boolean validateNickname(String nickname) {
        if (nickname.isEmpty()) {
            nicknameLabel.setText("Nickname field is empty.");
            return false;
        } else {
            nicknameLabel.setText("");
            return true;
        }
    }

    private boolean validatePassword(String password) {
        if (password.isEmpty()) {
            passLabel.setText("Password field is empty.");
            return false;
        } else if (!isValidPassword(password)) {
            passLabel.setText(
                    "Password must be at least 10 characters long, contain at least one digit, one letter, and one special character (#).");
            return false;
        } else {
            passLabel.setText("");
            return true;
        }
    }

    private boolean validatePasswordConfirmation(String passConf) {
        String password = passField.getText();
        if (passConf.isEmpty()) {
            passConfLabel.setText("Password confirmation field is empty.");
            return false;
        } else if (!passConf.equals(password)) {
            passConfLabel.setText("Password and password confirmation do not match.");
            return false;
        } else {
            passConfLabel.setText("");
            return true;
        }
    }

    private boolean validateQ(String question) {
        if (question == null || question.isEmpty()) {
            questionLabel.setText("Question must be selected.");
            return false;
        } else {
            questionLabel.setText("");
            return true;
        }
    }

    private boolean validateA(String answer) {
        if (answer.isEmpty()) {
            answerLabel.setText("Security answer field is empty.");
            return false;
        } else {
            answerLabel.setText("");
            return true;
        }
    }

    private boolean validateCaptcha(String answer) {
        if (answer.isEmpty()) {
            captchaLabel.setText("Captcha field is empty.");
            return false;
        } else if (!answer.equals(captchaText)) {
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

    private boolean validateInput(String email, String username, String nickname, String password, String passConf,
            String securityQ, String securityA, String captcha) {
        boolean valid = true;

        if (!validateEmail(email)) {
            valid = false;
        }

        if (!validateUsername(username)) {
            valid = false;
        }

        if (!validateNickname(nickname)) {
            valid = false;
        }

        if (!validatePassword(password)) {
            valid = false;
        }

        if (!validatePasswordConfirmation(passConf)) {
            valid = false;
        }

        if (!validateQ(securityQ)) {
            valid = false;
        }

        if (!validateA(securityA)) {
            valid = false;
        }

        if (!validateCaptcha(captcha)) {
            valid = false;
        }

        return valid;
    }
}