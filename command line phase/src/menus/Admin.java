package menus;

import inventory.*;
import java.util.*;

public class Admin extends Menu {
    public Admin(DataManager dataManager) {
        super(dataManager);
    }

    private int getIntInput(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("back!")) {
                return -1;
            }
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    public GameState run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Admin Menu:");
            System.out.println("1. Add Card");
            System.out.println("2. Edit Card");
            System.out.println("3. Delete Card");
            System.out.println("4. View All Players");
            System.out.println("5. Exit Admin Menu");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine();

            if (choice.equalsIgnoreCase("back!")) {
                return GameState.MAIN_MENU;
            }

            switch (choice) {
                case "1":
                    addCard();
                    break;
                case "2":
                    editCard();
                    break;
                case "3":
                    deleteCard();
                    break;
                case "4":
                    viewAllPlayers();
                    break;
                case "5":
                    return GameState.MAIN_MENU;
                default:
                    System.out.println("Invalid Input");
            }
        }
    }

    private void addCard() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Adding a new card:");

        boolean dontPass = false;
        String name = null;
        String type = null;
        String character = null;
        int accuracy = 0;
        int damagePerSector = 0;
        int duration = 0;

        do {
            dontPass = false;
            System.out.print("Name: ");
            name = scanner.nextLine();
            if (name.equalsIgnoreCase("back!"))
                return;

            ArrayList<Card> allCards = dataManager.getAllCards();
            for (Card card : allCards) {
                if (name.equals(card.getName())) {
                    System.out.println("Two cards cant have a same name!");
                    dontPass = true;
                }
            }
        } while (dontPass);

        do {
            dontPass = false;
            System.out.print("Type: ");
            type = scanner.nextLine();
            if (type.equalsIgnoreCase("back!"))
                return;

            if (!type.equals("spell") && !type.equals("ordinary")) {
                System.out.println("Type can whether be 'spell' or 'ordinary'!");
                dontPass = true;
            }

        } while (dontPass);

        // adding character
        int selectedCharacterNumber = 0;
        String[] characters = { "all", "ranger", "warrior", "sorcerer", "rogue" };
        boolean pass = true;
        for (int i = 1; i < characters.length; i++) {
            System.out.println(i + ". " + characters[i - 1]);
        }
        while (!pass) {
            pass = true;
            selectedCharacterNumber = getIntInput(scanner, "Character(select a number from above): ");
            if (selectedCharacterNumber < 1 || selectedCharacterNumber > characters.length) {
                pass = false;
                System.out.println("wrong input! try again.");
            }
        }
        character = characters[selectedCharacterNumber - 1];

        do {
            dontPass = false;
            accuracy = getIntInput(scanner, "Accuracy (10-100): ");
            if (accuracy == -1)
                return;

            if (!(accuracy >= 10 && accuracy <= 100)) {
                System.out.println("Between 10 and 100!");
                dontPass = true;
            }

        } while (dontPass);

        do {
            dontPass = false;
            damagePerSector = getIntInput(scanner, "Damage per sector (10-50): ");
            if (damagePerSector == -1)
                return;
            if (!(damagePerSector >= 10 && damagePerSector <= 50)) {
                System.out.println("Between 10 and 50!");
                dontPass = true;
            }

        } while (dontPass);

        do {
            dontPass = false;
            duration = getIntInput(scanner, "Duration (1-5): ");
            if (duration == -1)
                return;
            if (!(duration >= 1 && duration <= 5)) {
                System.out.println("Between 1 and 5!");
                dontPass = true;
            }

        } while (dontPass);

        int level = getIntInput(scanner, "Upgrade level: ");
        if (level == -1)
            return;

        int upgradeCost = getIntInput(scanner, "Upgrade cost: ");
        if (upgradeCost == -1)
            return;

        int cost = getIntInput(scanner, "Cost: ");
        if (cost == -1)
            return;

        Card newCard = new Card(name, type, character, accuracy, damagePerSector, duration, level, upgradeCost, cost);
        dataManager.addCard(newCard);
        System.out.println("Card added successfully!");
    }

    private void editCard() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Editing a card:");

        ArrayList<Card> allCards = dataManager.getAllCards();
        for (int i = 0; i < allCards.size(); i++) {
            System.out.println((i + 1) + ". " + allCards.get(i).getName());
        }

        int cardIndex = getIntInput(scanner, "Enter the number of the card to edit: ") - 1;
        if (cardIndex == -2)
            return; // -2 because we subtracted 1 from the input

        if (cardIndex < 0 || cardIndex >= allCards.size()) {
            System.out.println("Invalid card number.");
            return;
        }

        Card cardToEdit = allCards.get(cardIndex);
        System.out.println("Editing card: " + cardToEdit.getName());

        while (true) {
            System.out.println("1. Edit Name");
            System.out.println("2. Edit Accuracy");
            System.out.println("3. Edit Damage per Sector");
            System.out.println("4. Edit Duration");
            System.out.println("5. Edit Level");
            System.out.println("6. Edit Upgrade Cost");
            System.out.println("7. Edit Cost");
            System.out.println("8. Save and Exit");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine();
            if (choice.equalsIgnoreCase("back!"))
                return;
            boolean dontPass = false;

            switch (choice) {
                case "1":
                    String newName = null;
                    do {
                        dontPass = false;
                        System.out.print("New Name: ");
                        newName = scanner.nextLine();
                        if (newName.equalsIgnoreCase("back!"))
                            return;
                        for (Card card : allCards) {
                            if (newName.equals(card.getName())) {
                                System.out.println("Two cards cant have a same name!");
                                dontPass = true;
                            }
                        }
                    } while (dontPass);
                    cardToEdit.setName(newName);
                    break;
                case "2":
                    int newAccuracy = 0;
                    do {
                        dontPass = false;
                        newAccuracy = getIntInput(scanner, "New Accuracy (10-100): ");
                        if (newAccuracy == -1)
                            return;
                        if (!(newAccuracy >= 10 && newAccuracy <= 100)) {
                            System.out.println("Between 10 and 100!");
                            dontPass = true;
                        }
                    } while (dontPass);
                    cardToEdit.setAccuracy(newAccuracy);
                    break;
                case "3":
                    int newDamage = 0;
                    do {
                        dontPass = false;
                        newDamage = getIntInput(scanner, "New Damage per Sector (10-50): ");
                        if (newDamage == -1)
                            return;
                        if (!(newDamage >= 10 && newDamage <= 50)) {
                            System.out.println("Between 10 and 50!");
                            dontPass = true;
                        }
                    } while (dontPass);
                    cardToEdit.setDamagePerSector(newDamage);
                    break;
                case "4":
                    int newDuration = 0;
                    do {
                        dontPass = false;
                        newDuration = getIntInput(scanner, "New Duration (1-5): ");
                        if (newDuration == -1)
                            return;
                        if (!(newDuration >= 1 && newDuration <= 5)) {
                            System.out.println("Between 1 and 5!");
                            dontPass = true;
                        }
                    } while (dontPass);
                    cardToEdit.setDuration(newDuration);
                    break;
                case "5":
                    int newLevel = getIntInput(scanner, "New Level: ");
                    if (newLevel == -1)
                        return;
                    cardToEdit.setLevel(newLevel);
                    break;
                case "6":
                    int newUpgradeCost = getIntInput(scanner, "New Upgrade Cost: ");
                    if (newUpgradeCost == -1)
                        return;
                    cardToEdit.setUpgradeCost(newUpgradeCost);
                    break;
                case "7":
                    int newCost = getIntInput(scanner, "New Cost: ");
                    if (newCost == -1)
                        return;
                    cardToEdit.setCost(newCost);
                    break;
                case "8":
                    System.out.print("Are you sure you want to edit this card? (y/n): ");
                    String confirm = scanner.nextLine();
                    if (confirm.equalsIgnoreCase("y")) {
                        dataManager.updateCard(cardToEdit);
                        System.out.println("Card successfully edited");
                        return;
                    } else if (confirm.equalsIgnoreCase("back!")) {
                        return;
                    } else {
                        System.out.println("Edit cancelled");
                        return;
                    }
                default:
                    System.out.println("Invalid Input");
            }
        }
    }

    private void deleteCard() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Deleting a card:");

        ArrayList<Card> allCards = dataManager.getAllCards();
        for (int i = 0; i < allCards.size(); i++) {
            System.out.println((i + 1) + ". " + allCards.get(i).getName());
        }

        int cardIndex = getIntInput(scanner, "Enter the number of the card to delete: ") - 1;
        if (cardIndex == -2)
            return; // -2 because we subtracted 1 from the input

        if (cardIndex < 0 || cardIndex >= allCards.size()) {
            System.out.println("Invalid card number.");
            return;
        }

        Card cardToDelete = allCards.get(cardIndex);
        System.out.println("Are you sure you want to delete this card? (y/n): ");
        String confirm = scanner.nextLine();

        if (confirm.equalsIgnoreCase("y")) {
            dataManager.deleteCard(cardToDelete);
            System.out.println("Card '" + cardToDelete.getName() + "' has been deleted.");
        } else if (confirm.equalsIgnoreCase("back!")) {
            return;
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private void viewAllPlayers() {
        ArrayList<Player> allPlayers = dataManager.getAllPlayers();
        System.out.println("All Players:");
        for (Player player : allPlayers) {
            System.out.println("Username: " + player.getInfo("username") +
                    ", Level: " + player.getPlayerInfo("level") +
                    ", Coins: " + player.getPlayerInfo("coins"));
        }
    }
}