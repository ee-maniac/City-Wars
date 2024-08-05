package menus;

import inventory.*;
import menus.SignupClasses.*;

public class Signup extends Menu{
    private SignupState currentState;

    public Signup(DataManager dataManager) {
        super(dataManager);
        this.currentState = SignupState.USER_REGISTER;
    }

    public GameState run() {
        while(true) {
            switch(currentState) {
                case USER_REGISTER:
                    UserCreation userCreation = new UserCreation(dataManager);
                    currentState = userCreation.run();

                    switch(currentState) {//only checks special states
                        case LOGIN:
                            return GameState.LOGIN_MENU;//upon login attempt
                        case EXIT:
                            return GameState.EXIT;//upon exit
                    }
                    break;

                case PASSWORD_CONFIRM:
                    PassConfirm passConfirm = new PassConfirm(dataManager);
                    currentState = passConfirm.run();

                    switch(currentState) {//only checks special states
                        case LOGIN:
                            return GameState.LOGIN_MENU;//upon login attempt
                        case EXIT:
                            return GameState.EXIT;//upon exit
                    }
                    break;

                case FALLBACK_MEASURE:
                    FallbackMeasure fallbackMeasure = new FallbackMeasure(dataManager);
                    currentState = fallbackMeasure.run();

                    switch(currentState) {//only checks special states
                        case LOGIN:
                            return GameState.LOGIN_MENU;//upon login attempt
                        case EXIT:
                            return GameState.EXIT;//upon exit
                    }
                    break;

                case CAPTCHA:
                    Captcha captcha = new Captcha(dataManager);
                    currentState = captcha.run();

                    switch(currentState) {//only checks special states
                        case MAIN_MENU:
                            return GameState.MAIN_MENU;//upon successful registration
                        case LOGIN:
                            return GameState.LOGIN_MENU;//upon login attempt
                        case EXIT:
                            return GameState.EXIT;//upon exit
                    }
                    break;

                default://EXIT value
                    return GameState.EXIT;
            }
        }
    }
}