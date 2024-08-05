package menus;

import inventory.*;
import menus.LoginClasses.*;

public class Login extends Menu{
    private LoginState currentState;

    public Login(DataManager dataManager) {
        super(dataManager);
        currentState = LoginState.AUTH;
    }

    public GameState run() {
        while(true) {
            switch(currentState) {
                case AUTH:
                    Authentication authentication = new Authentication(dataManager);
                    currentState = authentication.run();

                    switch(currentState) {//only checks special states
                        case SIGNUP:
                            return GameState.SIGNUP_MENU;//upon register attempt
                        case MAIN_MENU:
                            return GameState.MAIN_MENU;//upon successful login
                        case EXIT:
                            return GameState.EXIT;//upon exit
                    }
                    break;

                case FALLBACK:
                    Fallback fallback = new Fallback(dataManager);
                    currentState = fallback.run();

                    switch(currentState) {//only checks special states
                        case SIGNUP:
                            return GameState.SIGNUP_MENU;//upon register attempt
                        case MAIN_MENU:
                            return GameState.MAIN_MENU;//upon successful login
                        case EXIT:
                            return GameState.EXIT;//upon exit
                    }
                    break;

                default://exit value
                    return GameState.EXIT;
            }
        }
    }
}