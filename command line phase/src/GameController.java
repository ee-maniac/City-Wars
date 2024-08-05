import inventory.*;
import menus.*;

public class GameController {
    public void run() {
        DataManager dataManager = new DataManager();
        GameState currentState = GameState.SIGNUP_MENU;

        while (true) {
            switch (currentState) {
                case SIGNUP_MENU:
                    Signup signup = new Signup(dataManager);
                    currentState = signup.run();
                    break;

                case LOGIN_MENU:
                    Login login = new Login(dataManager);
                    currentState = login.run();
                    break;

                case MAIN_MENU:
                    MainMenu mainMenu = new MainMenu(dataManager);
                    currentState = mainMenu.run();
                    break;

                case PROFILE_MENU:
                    Profile profile = new Profile(dataManager);
                    currentState = profile.run();
                    break;

                case HISTORY_MENU:
                    MatchHistory matchHistory = new MatchHistory(dataManager);
                    currentState = matchHistory.run();
                    break;

                case CARD_MENU:
                    CardStack cardStack = new CardStack(dataManager);
                    currentState = cardStack.run();
                    break;

                case SHOP_MENU:
                    Shop shop = new Shop(dataManager);
                    currentState = shop.run();
                    break;

                case ADMIN_MENU:
                    Admin admin = new Admin(dataManager);
                    currentState = admin.run();
                    break;

                case GAME_MENU:
                    TwoPlayerGame game = new TwoPlayerGame(dataManager);
                    currentState = game.run();
                    break;

                default:// EXIT value
                    return;
            }
        }
    }
}