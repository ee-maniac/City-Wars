package menus;

import inventory.*;

public abstract class Menu {
    public DataManager dataManager;

    public Menu(DataManager dataManager) {
        this.dataManager = dataManager;
    }
}