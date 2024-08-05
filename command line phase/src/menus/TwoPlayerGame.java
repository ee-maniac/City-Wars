package menus;

import inventory.*;
import java.util.*;
import java.util.stream.*;

public class TwoPlayerGame extends Menu {
    private Player player1;
    private Player player2;
    private int player1Score;
    private int player2Score;
    private int player1Hp;
    private int player2Hp;
    private int player1LeftTurns;
    private int player2LeftTurns;
    private boolean showPlayer1Cards;
    private boolean showPlayer2Cards;
    private ArrayList<Card> player1Hand;
    private ArrayList<Card> player2Hand;
    private String player1Character;
    private String player2Character;
    private ArrayList<Sector> player1Board;
    private ArrayList<Sector> player2Board;
    private int currentRound;
    private int currentTurn;
    private int turnsRemaining;
    private Random random;
    private boolean isBettingMode;
    private int betAmount;
    private String[] characters = { "ranger", "warrior", "sorcerer", "rogue" };

    public TwoPlayerGame(DataManager dataManager) {
        super(dataManager);
        this.random = new Random();
        this.player1 = dataManager.getCurrentPlayer();
        setupPlayer2();
        player1Score = 0;
        player2Score = 0;
        player1Hp = player1.getPlayerInfo("hp");
        player2Hp = player2.getPlayerInfo("hp");
        showPlayer1Cards = true;
        showPlayer2Cards = true;
        characterSelection();
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

    public void characterSelection() {
        Scanner scanner = new Scanner(System.in);
        int selectedCharacterNumber = 0;
        boolean pass = false;
        System.out.println("character selection for " + player1.getInfo("username"));
        for (int i = 1; i < characters.length + 1; i++) {
            System.out.println(i + ". " + characters[i - 1]);
        }
        while (!pass) {
            pass = true;
            selectedCharacterNumber = getIntInput(scanner, "select your desired character: ");
            if (selectedCharacterNumber < 1 || selectedCharacterNumber > characters.length) {
                pass = false;
                // System.out.println("wrong input! try again.");
            }
        }
        player1Character = characters[selectedCharacterNumber - 1];

        pass = false;
        System.out.println("character selection for " + player2.getInfo("username"));
        for (int i = 1; i < characters.length + 1; i++) {
            System.out.println(i + ". " + characters[i - 1]);
        }
        while (!pass) {
            pass = true;
            selectedCharacterNumber = getIntInput(scanner, "select your desired character: ");
            if (selectedCharacterNumber < 1 || selectedCharacterNumber > characters.length) {
                pass = false;
                // System.out.println("wrong input! try again.");
            }
        }
        player2Character = characters[selectedCharacterNumber - 1];
    }

    public void setupPlayer2() {
        String username;
        String passwd;
        Player potentialPlayer2 = null;
        boolean pass = (player2 == null) ? false : true;
        Scanner scanner = new Scanner(System.in);
        while (!pass) {
            pass = true;
            System.out.print("enter username for player2: ");
            username = scanner.nextLine();
            System.out.print("enter password for player2: ");
            passwd = scanner.nextLine();
            potentialPlayer2 = dataManager.getPlayer(username);
            if (potentialPlayer2 == null || !potentialPlayer2.getInfo("password").equals(passwd)
                    || potentialPlayer2.getInfo("username").equals(player1.getInfo("username"))) {
                System.out.println(
                        "username or password wrong! also current player can't play with itself. please try again.");
                System.out.println();
                pass = false;
            }
        }

        dataManager.setSecondaryPlayer(potentialPlayer2);
        this.player2 = potentialPlayer2;
    }

    public GameState run() {
        initializeGame();
        playGame();
        return endGame();
    }

    private void initializeGame() {

        player1Board = initializeBoard();
        player2Board = initializeBoard();

        dealInitialCards(player1);
        dealInitialCards(player2);

        currentRound = 1;
        currentTurn = random.nextInt(2) + 1; // Randomly choose starting player
        player1LeftTurns = 4;
        player2LeftTurns = 4;
        turnsRemaining = player1LeftTurns + player2LeftTurns;

        initializeBettingMode();

        System.out.println(
                "Game initialized. " + (currentTurn == 1 ? player1.getInfo("username") : player2.getInfo("username"))
                        + " starts.");
    }

    private void initializeBettingMode() {
        System.out.println("Do you want to play in Betting Mode? (y/n)");
        Scanner scanner = new Scanner(System.in);
        isBettingMode = scanner.nextLine().trim().equalsIgnoreCase("y");

        if (isBettingMode) {
            System.out.println("Enter the bet amount:");
            betAmount = scanner.nextInt();
            while (betAmount > player1.getPlayerInfo("coins") || betAmount > player2.getPlayerInfo("coins")) {
                System.out.println("Bet amount exceeds player's coins. Enter a lower amount:");
                betAmount = scanner.nextInt();
            }
            player1.setPlayerInfo("coins", player1.getPlayerInfo("coins") - betAmount);
            player2.setPlayerInfo("coins", player2.getPlayerInfo("coins") - betAmount);
            System.out.println("Betting Mode activated. Each player has bet " + betAmount + " coins.");
        }
    }

    private ArrayList<Sector> initializeBoard() {
        ArrayList<Sector> board = new ArrayList<>();
        for (int i = 0; i < 21; i++) {
            board.add(new Sector());
        }
        int holeIndex = random.nextInt(21);
        board.get(holeIndex).setHole(true);
        return board;
    }

    private void dealInitialCards(Player player) {
        ArrayList<Card> allPlayerCards = dataManager.getPlayerCards(player.getInfo("username"));
        ArrayList<Card> hand = new ArrayList<>();
        int spellCount;
        int iterations = 100;
        do {
            spellCount = 0;
            hand.clear();
            Collections.shuffle(allPlayerCards);
            int handSize = Math.min(5, allPlayerCards.size());
            for (int i = 0; i < handSize; i++) {
                Card cardToAdd = allPlayerCards.get(i);
                hand.add(cardToAdd);
                if (cardToAdd.getType().equals("spell")) {
                    spellCount++;
                }
            }
            iterations--;
        } while (spellCount > 2 && iterations > 0);

        if (player == player1) {
            player1Hand = hand;
        } else {
            player2Hand = hand;
        }
    }

    private void playGame() {
        while (player1Hp > 0 && player2Hp > 0) {
            player1LeftTurns = 4;
            player2LeftTurns = 4;
            player1Score = 0;
            player2Score = 0;
            player1Board = initializeBoard();
            player2Board = initializeBoard();
            showPlayer1Cards = true;
            showPlayer2Cards = true;
            dealInitialCards(player1);
            dealInitialCards(player2);
            playRound();
        }
    }

    private void playRound() {
        System.out.println("\nRound " + currentRound + " begins!");
        // turnsRemaining = 8;
        turnsRemaining = player1LeftTurns + player2LeftTurns;
        while (turnsRemaining > 0) {
            Player currentTurnPlayer = (currentTurn == 1) ? player1 : player2;
            ArrayList<Sector> currentBoard = (currentTurn == 1) ? player1Board : player2Board;
            ArrayList<Sector> opponentBoard = (currentTurn == 1) ? player2Board : player1Board;

            if (currentTurn == 1 && player1LeftTurns <= 0) {
                currentTurnPlayer = player2;
                currentBoard = player2Board;
                opponentBoard = player1Board;
            }

            if (currentTurn == 2 && player2LeftTurns <= 0) {
                currentTurnPlayer = player1;
                currentBoard = player1Board;
                opponentBoard = player2Board;
            }

            System.out.println("\n" + currentTurnPlayer.getInfo("username") + "'s turn:");
            displayBoard(currentBoard, opponentBoard);
            playTurn(currentTurnPlayer, currentBoard, opponentBoard);

            // changing turns
            if (currentTurn == 1) {
                player1LeftTurns--;
                currentTurn = 2;
            } else {
                player2LeftTurns--;
                currentTurn = 1;
            }
            turnsRemaining = player1LeftTurns + player2LeftTurns;
        }
        performTimeLine();
        currentRound++;
    }

    private void playTurn(Player player, ArrayList<Sector> playerBoard, ArrayList<Sector> opponentBoard) {
        ArrayList<Card> playerHand;
        if (player == player1) {
            playerHand = player1Hand;
        } else {
            playerHand = player2Hand;
        }

        displayHand(player);
        int cardIndex = getCardChoice(player);
        if (cardIndex == -1)
            return; // Player chose to skip turn

        Card chosenCard = (player == player1) ? player1Hand.get(cardIndex) : player2Hand.get(cardIndex);
        System.out.println("chosen: " + chosenCard.getName());
        if (isSpecialCard(chosenCard)) {
            handleSpecialCard(chosenCard, player, playerBoard, opponentBoard); // tofix
            playerHand.remove(chosenCard);
            drawNewCard(player);
        } else {
            int sectorIndex = getSectorChoice(playerBoard, chosenCard);
            if (sectorIndex == -1)
                return; // Player chose to go back

            if (placeCard(chosenCard, sectorIndex, playerBoard)) {
                playerHand.remove(chosenCard);
                drawNewCard(player);
                System.out.println(player.getInfo("username") + " played " + chosenCard.getName());
                System.out.println();
                System.out.println("***********turn results***********");
                // Check for card type bonus
                applyCardTypeBonus(player, chosenCard);
                // Check for middle card buffing
                checkAndBuffMiddleCard(playerHand, chosenCard);
            } else {
                System.out.println("Failed to place card. Try again.");
                playTurn(player, playerBoard, opponentBoard);
                return;
            }
        }

        updateDominantSectors();
        checkAndRewardNewNeu(player1);
        checkAndRewardNewNeu(player2);
        updateScores(); // needs changes after shield is implemented
    }

    private boolean isSpecialCard(Card card) {
        return card.getType().equalsIgnoreCase("spell");
    }

    private void handleSpecialCard(Card card, Player player, ArrayList<Sector> playerBoard,
            ArrayList<Sector> opponentBoard) {
        int sectorIndex;
        int cardIndex;
        ArrayList<Card> playerHand = (player == player1) ? player1Hand : player2Hand;
        switch (card.getName().toLowerCase()) {
            case "shield": // done
                sectorIndex = getSectorChoice(playerBoard, card);
                if (sectorIndex == -1)
                    return; // Player chose to go back

                if (placeCard(card, sectorIndex, playerBoard)) {
                    System.out.println("shield applied!");
                } else {
                    System.out.println("Failed to place card. Try again.");
                    playTurn(player, playerBoard, opponentBoard);
                    return;
                }
                break;
            case "heal": // done
                healPlayer(player);
                break;
            case "powerup":
                powerUpRandomCard(player); // test it
                break;
            case "holereplace": // done
                relocateHoles(playerBoard, opponentBoard);
                break;
            case "holeammend": // done
                repairHole(playerBoard);
                break;
            case "reduceround": // done
                reduceTurns(player);
                break;
            case "steal": // test it
                stealOpponentCard(player, (player == player1) ? player2 : player1);
                break;
            case "rivalcarddowngrade": // done
                weakenOpponentCards((player == player1) ? player2 : player1);
                break;
            case "copycard": // test it
                cardIndex = getCardChoice(player);
                if (cardIndex == -1)
                    return;
                Card cardToCopy = new GameReadyCard(playerHand.get(cardIndex), playerHand.get(cardIndex).getLevel());
                playerHand.add(cardToCopy);
                System.out.println(
                        player.getInfo("username") + " copied " + cardToCopy.getName()
                                + " and added it to their hand.");

                break;
            case "hiderivalcards": // test it
                hideRivalCards(player);
                break;
            default:
                System.out.println("Unknown special card: " + card.getName());
        }
    }

    private void hideRivalCards(Player castingPlayer) {
        if (castingPlayer == player1) {
            showPlayer2Cards = false;
            Collections.shuffle(player2Hand);
            System.out.println("player 2 cards became hidden!");
        } else {
            showPlayer1Cards = false;
            Collections.shuffle(player1Hand);
            System.out.println("player 1 cards became hidden!");
        }
    }

    private void healPlayer(Player player) {
        int healAmount = 20;
        if (player == player1) {
            player1Hp += healAmount;
        } else {
            player2Hp += healAmount;
        }
        System.out.println(player.getInfo("username") + " healed for " + healAmount + " HP!");
    }

    private void powerUpRandomCard(Player player) {
        ArrayList<Card> playerHand;
        ArrayList<Card> playerHandNonSpellCards = new ArrayList<>();
        if (player == player1) {
            playerHand = player1Hand;
        } else {
            playerHand = player2Hand;
        }
        for (Card card : playerHand) {
            if (!card.getType().equals("spell")) {
                playerHandNonSpellCards.add(card);
            }
        }
        Card randomCard = playerHandNonSpellCards.get(random.nextInt(playerHandNonSpellCards.size()));
        randomCard.setAccuracy(randomCard.getAccuracy() + 5);
        randomCard.setDamagePerSector(randomCard.getDamagePerSector() + 2);
        System.out.println(randomCard.getName() + " has been powered up!");
    }

    private void relocateHoles(ArrayList<Sector> playerBoard, ArrayList<Sector> opponentBoard) {
        relocateHoleForBoard(playerBoard, "Player");
        relocateHoleForBoard(opponentBoard, "Opponent");
        System.out.println("Holes have been relocated on both boards!");
    }

    private void relocateHoleForBoard(ArrayList<Sector> board, String boardName) {
        // Find the current hole position
        int currentHolePosition = -1;
        for (int i = 0; i < board.size(); i++) {
            if (board.get(i).isHole()) {
                currentHolePosition = i;
                break;
            }
        }

        // Remove existing hole
        if (currentHolePosition != -1) {
            board.get(currentHolePosition).setHole(false);
        }

        // Create a list of available sectors (those without cards and not the previous
        // hole position)
        List<Integer> availableSectors = new ArrayList<>();
        for (int i = 0; i < board.size(); i++) {
            if (board.get(i).getCard() == null && i != currentHolePosition) {
                availableSectors.add(i);
            }
        }

        // If there are available sectors, place a hole in a random available sector
        if (!availableSectors.isEmpty()) {
            int randomIndex = random.nextInt(availableSectors.size());
            int newHolePosition = availableSectors.get(randomIndex);
            board.get(newHolePosition).setHole(true);
            System.out.println(
                    boardName + " board: Hole moved from position " + currentHolePosition + " to " + newHolePosition);
        } else {
            System.out.println("Warning: No available sectors to place a hole on the " + boardName
                    + " board. Hole remains at position " + currentHolePosition);
            // If no available sectors, put the hole back where it was
            if (currentHolePosition != -1) {
                board.get(currentHolePosition).setHole(true);
            }
        }
    }

    private void repairHole(ArrayList<Sector> board) {
        for (Sector sector : board) {
            if (sector.isHole()) {
                sector.setHole(false);
                System.out.println("hole has been repaired!");
                return;
            }
        }
    }

    private void reduceTurns(Player castingPlayer) {
        if (castingPlayer == player1) {
            if (player2LeftTurns > 0)
                player2LeftTurns--;
            System.out.println("reduced remaining turns for " + player2.getInfo("username"));
        } else {
            if (player1LeftTurns > 0)
                player1LeftTurns--;
            System.out.println("reduced remaining turns for " + player1.getInfo("username"));
        }
        turnsRemaining = player1LeftTurns + player2LeftTurns;
    }

    private void stealOpponentCard(Player player, Player opponent) {
        ArrayList<Card> playerHand;
        ArrayList<Card> opponentHand;
        if (player == player1) {
            playerHand = player1Hand;
            opponentHand = player2Hand;
        } else {
            playerHand = player2Hand;
            opponentHand = player1Hand;
        }

        Card stolenCard = opponentHand.remove(random.nextInt(opponentHand.size()));
        playerHand.add(stolenCard);
        System.out.println(
                player.getInfo("username") + " stole " + stolenCard.getName() + " from "
                        + opponent.getInfo("username"));
    }

    private void weakenOpponentCards(Player opponent) {
        ArrayList<Card> opponentHand = (opponent == player1) ? player1Hand : player2Hand;

        if (opponentHand.size() < 2) {
            System.out.println("Opponent doesn't have enough cards to weaken.");
            return;
        }

        // Create a list of non-spell cards
        List<Card> nonSpellCards = opponentHand.stream()
                .filter(card -> !card.getType().equalsIgnoreCase("spell"))
                .collect(Collectors.toList());

        if (nonSpellCards.size() < 2) {
            System.out.println("Opponent doesn't have enough non-spell cards to weaken.");
            return;
        }

        // Randomly select two distinct cards
        Card card1 = nonSpellCards.remove(random.nextInt(nonSpellCards.size()));
        Card card2 = nonSpellCards.get(random.nextInt(nonSpellCards.size()));

        // Weaken damage of the first card
        int damagereduction = Math.min(2, card1.getDamagePerSector());
        card1.setDamagePerSector(card1.getDamagePerSector() - damagereduction);
        System.out.println(opponent.getInfo("username") + "'s card " + card1.getName() + " had its damage reduced by "
                + damagereduction);

        // Weaken accuracy of the second card
        int accuracyReduction = Math.min(5, card2.getAccuracy());
        card2.setAccuracy(card2.getAccuracy() - accuracyReduction);
        System.out.println(opponent.getInfo("username") + "'s card " + card2.getName() + " had its accuracy reduced by "
                + accuracyReduction);
    }

    private void displayBoard(ArrayList<Sector> playerBoard, ArrayList<Sector> opponentBoard) {
        String playerCharacter;
        String opponentCharacter;
        if (playerBoard == player1Board) {
            playerCharacter = player1Character;
            opponentCharacter = player2Character;
        } else {
            playerCharacter = player2Character;
            opponentCharacter = player1Character;
        }
        System.out.println("Your Board (" + playerCharacter + ") ↓");
        displayDetailedBoard(playerBoard, true);
        displayDetailedBoard(opponentBoard, false);
        System.out.println("Opponent's Board (" + opponentCharacter + ") ↑");
        System.out.println();
    }

    private void displayDetailedBoard(ArrayList<Sector> board, boolean isPlayerBoard) {
        int playerHp;
        int leftTurns;
        if (board == player1Board) {
            playerHp = player1Hp;
            leftTurns = player1LeftTurns;
        } else {
            playerHp = player2Hp;
            leftTurns = player2LeftTurns;
        }
        StringBuilder topBar = new StringBuilder("   ");
        StringBuilder topLine = new StringBuilder("ACC|");
        StringBuilder middleLine = new StringBuilder("   |");
        StringBuilder bottomLine = new StringBuilder("DMG|");
        StringBuilder bottomBar = new StringBuilder(
                "   (LT: " + numberToFittingString(leftTurns) + ")#######(HP: " + numberToFittingString(playerHp)
                        + ")");

        for (int i = 0; i < board.size(); i++) {
            Sector sector = board.get(i);
            String sectorStr = numberToFittingString(i + 1);

            if (sector.isHole()) {
                topLine.append(" H   |");
                middleLine.append(" ").append(sectorStr).append(" |");
                bottomLine.append(" H   |");
            } else if (sector.getCard() != null) {
                Card card = sector.getCard();
                String accuracyStr = sector.isNeutralized() ? " N   |"
                        : (sector.isShielded() ? " S   |" : " " + numberToFittingString(card.getAccuracy()) + " |");
                String damageStr = sector.isNeutralized() ? " N   |"
                        : (sector.isShielded() ? " S   |"
                                : " " + numberToFittingString(card.getDamagePerSector()) + " |");

                topLine.append(accuracyStr);
                middleLine.append(" ").append(sectorStr).append(" |");
                bottomLine.append(damageStr);
            } else {
                topLine.append(" _   |");
                middleLine.append(" ").append(sectorStr).append(" |");
                bottomLine.append(" _   |");
            }
        }
        for (int i = 0; i < middleLine.length() - 3; i++) {
            topBar = topBar.append("#");
        }

        for (int i = 0; i < middleLine.length() - 40; i++) {
            bottomBar = bottomBar.append("#");
        }

        bottomBar = bottomBar.append("(Score: ").append(numberToFittingString(calculateScore(board)) + ")");

        System.out.println(topBar);
        System.out.println(topLine);
        System.out.println(middleLine);
        System.out.println(bottomLine);
        System.out.println(bottomBar);
    }

    private String numberToFittingString(int number) {
        String fitString;
        if (number < 10) {
            fitString = String.valueOf(number) + "  ";
        } else if (number < 100) {
            fitString = String.valueOf(number) + " ";
        } else {
            fitString = String.valueOf(number);
        }
        return fitString;
    }

    private void displayHand(Player player) {
        boolean showHand;
        ArrayList<Card> playerHand;
        String playerCharacter = "";
        if (player == player1) {
            playerHand = player1Hand;
            showHand = showPlayer1Cards;
            playerCharacter = player1Character;
        } else {
            playerHand = player2Hand;
            showHand = showPlayer2Cards;
            playerCharacter = player2Character;
        }
        System.out.println("Your hand:");
        if (showHand) {
            for (int i = 0; i < playerHand.size(); i++) {
                Card card = playerHand.get(i);
                if (!card.getType().equals("spell")) {
                    if (playerCharacter.equals(card.getCharacter())) {
                        System.out.println((i + 1) + ". " + card.getName() +
                                "* (Accuracy: " + card.getAccuracy() +
                                ", Duration: " + card.getDuration() +
                                ", Damage Per Sector: " + card.getDamagePerSector() + ")");
                    } else {
                        System.out.println((i + 1) + ". " + card.getName() +
                                " (Accuracy: " + card.getAccuracy() +
                                ", Duration: " + card.getDuration() +
                                ", Damage Per Sector: " + card.getDamagePerSector() + ")");
                    }
                } else {
                    System.out.println((i + 1) + ". " + card.getName() +
                            " (Type: spell" + ")");
                }
            }
        } else {
            for (int i = 0; i < playerHand.size(); i++) {
                Card card = playerHand.get(i);
                System.out.println((i + 1) + ". Type: " + card.getType());
            }

        }
    }

    private int getCardChoice(Player player) {
        ArrayList<Card> playerHand;
        if (player == player1) {
            playerHand = player1Hand;
        } else {
            playerHand = player2Hand;
        }
        System.out.println("Choose a card to play (1-" + playerHand.size() + "), or 0 to skip turn:");
        int choice = getUserInput(0, playerHand.size());
        return choice - 1;
    }

    private int getSectorChoice(ArrayList<Sector> board, Card card) {
        System.out.println("Choose a sector to place your card (1-21), or 0 to go back:");
        int choice = getUserInput(0, 21);
        if (choice == 0)
            return -1;

        int sectorIndex = choice - 1;
        if (!canPlaceCard(card, sectorIndex, board)) {
            System.out.println("Invalid sector. Try again.");
            return getSectorChoice(board, card);
        }
        return sectorIndex;
    }

    private boolean canPlaceCard(Card card, int sectorIndex, ArrayList<Sector> board) {
        if (sectorIndex + card.getDuration() > board.size()) {
            return false;
        }
        for (int i = sectorIndex; i < sectorIndex + card.getDuration(); i++) {
            if (board.get(i).isHole() || board.get(i).getCard() != null) {
                return false;
            }
        }
        return true;
    }

    private int getUserInput(int min, int max) {
        Scanner scanner = new Scanner(System.in);
        int choice;
        do {
            System.out.print("Enter your choice: ");
            while (!scanner.hasNextInt()) {
                System.out.println("That's not a number. Try again.");
                scanner.next();
            }
            choice = scanner.nextInt();
        } while (choice < min || choice > max);
        return choice;
    }

    private boolean placeCard(Card card, int sectorIndex, ArrayList<Sector> board) {
        if (!canPlaceCard(card, sectorIndex, board)) {
            return false;
        }
        for (int i = sectorIndex; i < sectorIndex + card.getDuration(); i++) {
            board.get(i).setCard(card);
            if (card.getName().equals("shield")) {
                board.get(i).setShielded(true);
            }
        }
        return true;
    }

    private void drawNewCard(Player player) {
        ArrayList<Card> currentPlayerHand = (player == player1) ? player1Hand : player2Hand;
        ArrayList<Card> allPlayerCards = dataManager.getPlayerCards(player.getInfo("username"));
        Card replacementCard = allPlayerCards.get(random.nextInt(allPlayerCards.size()));
        int currentSpellsInHand;
        int iterations = 100;
        boolean repetetiveCard;
        do {
            currentSpellsInHand = 0;
            repetetiveCard = false;
            for (Card card : currentPlayerHand) {
                if (card.getType().equals("spell")) {
                    currentSpellsInHand++;
                }
                if (replacementCard.getName().equals(card.getName())) {
                    repetetiveCard = true;
                }
            }
            replacementCard = allPlayerCards.get(random.nextInt(allPlayerCards.size()));
            if (replacementCard.getType().equals("spell"))
                currentSpellsInHand++;
            iterations--;
        } while ((repetetiveCard || currentSpellsInHand > 2) && iterations > 0);
        if (player == player1) {
            if (replacementCard.getCharacter().equals(player1Character)) {
                replacementCard = new GameReadyCard(replacementCard, replacementCard.getLevel() + 1);
            }
        } else {
            if (replacementCard.getCharacter().equals(player1Character)) {
                replacementCard = new GameReadyCard(replacementCard, replacementCard.getLevel() + 1);
            }

        }
        currentPlayerHand.add(replacementCard);
    }

    private void applyCardTypeBonus(Player player, Card playedCard) {
        String playerCharacter = "";
        if (player == player1) {
            playerCharacter = player1Character;
        } else {
            playerCharacter = player2Character;
        }
        if (playedCard.getCharacter().equals(playerCharacter)) {
            playedCard.setDamagePerSector(playedCard.getDamagePerSector() + 5);
            System.out.println("Character type bonus applied to " + playedCard.getName());
        }
    }

    private void checkAndBuffMiddleCard(ArrayList<Card> board, Card chosenCard) {
        if (board.size() % 2 == 0)
            return;
        int middleIndex = (int) (board.size() / 2);
        if (chosenCard.getCharacter().equals(board.get(middleIndex).getCharacter())) {
            int randomInt = random.nextInt(1001);
            if (chosenCard.getCharacter().equals("ranger")) {
                if (randomInt % 2 == 0) {
                    board.get(middleIndex).setAccuracy(board.get(middleIndex).getAccuracy() + 5);
                    board.get(middleIndex).setDamagePerSector(board.get(middleIndex).getDamagePerSector() + 5);
                    System.out.println("middle card buffed!");
                }
            } else if (chosenCard.getCharacter().equals("warrior")) {
                if (randomInt % 3 == 0) {
                    board.get(middleIndex).setAccuracy(board.get(middleIndex).getAccuracy() + 5);
                    board.get(middleIndex).setDamagePerSector(board.get(middleIndex).getDamagePerSector() + 5);
                    System.out.println("middle card buffed!");
                }
            } else if (chosenCard.getCharacter().equals("sorcerer")) {
                if (randomInt % 3 == 0) {
                    board.get(middleIndex).setAccuracy(board.get(middleIndex).getAccuracy() + 5);
                    board.get(middleIndex).setDamagePerSector(board.get(middleIndex).getDamagePerSector() + 5);
                    System.out.println("middle card buffed!");
                }
            } else if (chosenCard.getCharacter().equals("rogue")) {
                if (randomInt % 2 == 0) {
                    board.get(middleIndex).setAccuracy(board.get(middleIndex).getAccuracy() + 5);
                    board.get(middleIndex).setDamagePerSector(board.get(middleIndex).getDamagePerSector() + 5);
                    System.out.println("middle card buffed!");
                }
            }
        }
    }

    private void updateDominantSectors() {

        // Update neutralization status
        for (int i = 0; i < player1Board.size(); i++) {
            Sector sector1 = player1Board.get(i);
            Sector sector2 = player2Board.get(i);

            if (sector1.getCard() != null && sector2.getCard() != null) {
                if (sector1.getCard().getAccuracy() > sector2.getCard().getAccuracy()) {
                    if (!sector2.isShielded()) {
                        sector2.setNeutralized(true);
                        sector1.setNeutralized(false);
                    } else {
                        sector2.setNeutralized(false);
                        sector1.setNeutralized(true);
                    }
                } else if (sector2.getCard().getAccuracy() > sector1.getCard().getAccuracy()) {
                    if (!sector1.isShielded()) {
                        sector1.setNeutralized(true);
                        sector2.setNeutralized(false);
                    } else {
                        sector1.setNeutralized(false);
                        sector2.setNeutralized(true);
                    }
                } else {
                    sector1.setNeutralized(true);
                    sector2.setNeutralized(true);
                }
            } else {
                sector1.setNeutralized(false);
                sector2.setNeutralized(false);
            }
        }
    }

    private void checkAndRewardNewNeu(Player currentTurnPlayer) {
        ArrayList<Sector> oppBoard = (currentTurnPlayer == player1) ? player2Board : player1Board;
        ArrayList<Card> neuCards = new ArrayList<>();
        ArrayList<ArrayList<Sector>> neuCardsSectors = new ArrayList<>();

        int cardDuration = 0;
        for (int i = 0; i < oppBoard.size(); i++) {
            Card sectorCard = oppBoard.get(i).getCard();
            if (cardDuration > 1) {
                neuCardsSectors.get(neuCardsSectors.size() - 1).add(oppBoard.get(i));
                cardDuration--;
                continue;
            }
            if (sectorCard != null) {
                neuCards.add(sectorCard);
                cardDuration = sectorCard.getDuration();
                ArrayList<Sector> cardSectors = new ArrayList<>();
                cardSectors.add(oppBoard.get(i));
                neuCardsSectors.add(cardSectors);
            }
        }

        // // test the algorithm above
        // System.out.println("check first loop");
        // for (int i = 0; i < neuCards.size(); i++) {
        // Card currentCard = neuCards.get(i);
        // System.out.println("card name: " + currentCard.getName() + " duration: " +
        // currentCard.getDuration());
        // for (Sector sector : neuCardsSectors.get(i)) {
        // System.out.println(oppBoard.indexOf(sector) + 1);
        // }
        // }

        // first filter on the cards: cards that have been previously neu
        ArrayList<Integer> cardsToRemove = new ArrayList<>();
        for (int i = 0; i < neuCards.size(); i++) {
            Sector firstCardSector = neuCardsSectors.get(i).get(0);
            if (firstCardSector.isPreNeutralized() && !cardsToRemove.contains(i)) {
                cardsToRemove.add(i);
            }
        }
        Collections.reverse(cardsToRemove);
        for (Integer index : cardsToRemove) {
            neuCards.remove(index.intValue());
            neuCardsSectors.remove(index.intValue());
        }

        // // test algo above
        // System.out.println("check first filter");
        // for (int i = 0; i < neuCards.size(); i++) {
        // Card currentCard = neuCards.get(i);
        // System.out.println("card name: " + currentCard.getName() + " duration: " +
        // currentCard.getDuration());
        // for (Sector sector : neuCardsSectors.get(i)) {
        // System.out.println(oppBoard.indexOf(sector) + 1);
        // }
        // }

        // second filter on the cards: cards that have not been completely neu
        cardsToRemove.clear();
        for (int i = 0; i < neuCards.size(); i++) {
            for (Sector sector : neuCardsSectors.get(i)) {
                if (!sector.isNeutralized() && !cardsToRemove.contains(i)) {
                    cardsToRemove.add(i);
                    break;
                }
            }
        }
        Collections.reverse(cardsToRemove);
        for (Integer index : cardsToRemove) {
            neuCards.remove(index.intValue());
            neuCardsSectors.remove(index.intValue());
        }

        // // test algo above
        // System.out.println("check second filter");
        // for (int i = 0; i < neuCards.size(); i++) {
        // Card currentCard = neuCards.get(i);
        // System.out.println("card name: " + currentCard.getName() + " duration: " +
        // currentCard.getDuration());
        // for (Sector sector : neuCardsSectors.get(i)) {
        // System.out.println(oppBoard.indexOf(sector) + 1);
        // }
        // }
        if (neuCards.size() != 0) {
            System.out.println(currentTurnPlayer.getInfo("username")
                    + " has successfully neutralized these cards of the opponent in this turn:");
            for (int i = 0; i < neuCards.size(); i++) {
                for (Sector sector : neuCardsSectors.get(i)) {
                    sector.setPreNeutralized(true);
                }
                System.out.println(neuCards.get(i).getName());
                rewardPlayerForNeutralization(currentTurnPlayer);
            }
        }

    }

    private void rewardPlayerForNeutralization(Player player) {
        boolean giveCoins = random.nextBoolean();
        if (giveCoins) {
            int coinsReward = 10; // You can adjust this value
            player.setPlayerInfo("coins", player.getPlayerInfo("coins") + coinsReward);
            System.out.println(player.getInfo("username") + " has been rewarded with " + coinsReward
                    + " coins for neutralizing a card!");
        } else {
            int xpReward = 20; // You can adjust this value
            player.setPlayerInfo("xp", player.getPlayerInfo("xp") + xpReward);
            System.out.println(
                    player.getInfo("username") + " has been rewarded with " + xpReward
                            + " XP for neutralizing a card!");
        }
    }

    private void updateScores() {
        int player1Score = calculateScore(player1Board);
        int player2Score = calculateScore(player2Board);

        player1.setPlayerInfo("score", player1Score);
        player2.setPlayerInfo("score", player2Score);
    }

    private int calculateScore(ArrayList<Sector> board) {
        int score = 0;
        for (Sector sector : board) {
            if (sector.getCard() != null && !sector.isNeutralized() && !sector.isShielded()) {
                score += sector.getCard().getDamagePerSector();
            }
        }
        return score;
    }

    private void delay(int miliSeconds) {
        try {
            Thread.sleep(miliSeconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void performTimeLine() {
        for (int i = 0; i < player1Board.size(); i++) {
            Sector player1Sector = player1Board.get(i);
            Sector player2Sector = player2Board.get(i);
            if (!player1Sector.isNeutralized() && player1Sector.getCard() != null) {
                player2Hp = Math.max(player2Hp - player1Sector.getCard().getDamagePerSector(), 0);
            }
            if (!player2Sector.isNeutralized() && player2Sector.getCard() != null) {
                player1Hp = Math.max(player1Hp - player2Sector.getCard().getDamagePerSector(), 0);
            }
            System.out.println(player1.getInfo("username") + " status ↓");
            printResultsStringsUntilSector(i + 1, player1Board, player1Hp);
            printResultsStringsUntilSector(i + 1, player2Board, player2Hp);
            System.out.println(player2.getInfo("username") + " status ↑");
            System.out.println();
            System.out.println();
            delay(1500);
            if (player1Hp <= 0 || player2Hp <= 0) {
                return;
            }
        }

    }

    private void printResultsStringsUntilSector(int sectorNumber, ArrayList<Sector> board, int playerHp) {
        sectorNumber = Math.min(21, sectorNumber);
        StringBuilder topBar = new StringBuilder("   ");
        StringBuilder topLine = new StringBuilder("ACC|");
        StringBuilder middleLine = new StringBuilder("   |");
        StringBuilder bottomLine = new StringBuilder("DMG|");
        StringBuilder bottomBar = new StringBuilder("   ");

        for (int i = 0; i < sectorNumber; i++) {
            Sector sector = board.get(i);
            String sectorStr = numberToFittingString(i + 1);

            if (sector.isHole()) {
                topLine.append(" H   |");
                middleLine.append(" ").append(sectorStr).append(" |");
                bottomLine.append(" H   |");
            } else if (sector.getCard() != null) {
                Card card = sector.getCard();
                String accuracyStr = sector.isNeutralized() ? " N   |"
                        : (sector.isShielded() ? " S   |" : " " + numberToFittingString(card.getAccuracy()) + " |");
                String damageStr = sector.isNeutralized() ? " N   |"
                        : (sector.isShielded() ? " S   |"
                                : " " + numberToFittingString(card.getDamagePerSector()) + " |");

                topLine.append(accuracyStr);
                middleLine.append(" ").append(sectorStr).append(" |");
                bottomLine.append(damageStr);
            } else {
                topLine.append(" _   |");
                middleLine.append(" ").append(sectorStr).append(" |");
                bottomLine.append(" _   |");
            }
        }
        for (int i = 0; i < middleLine.length() - 3; i++) {
            topBar = topBar.append("#");
        }

        for (int i = 0; i < Math.max(middleLine.length() - 12, 0); i++) {
            bottomBar = bottomBar.append("#");
        }

        bottomBar = bottomBar.append("(Hp: ").append(numberToFittingString(playerHp) + ")");
        System.out.println(topBar);
        System.out.println(topLine);
        System.out.println(middleLine);
        System.out.println(bottomLine);
        System.out.println(bottomBar);
    }

    private GameState endGame() {
        Player winner;
        player1Hp -= player2Score;
        player2Hp -= player1Score;
        if (player1Hp <= 0) {
            winner = player2;
        } else if (player2Hp <= 0) {
            winner = player1;
        } else {
            int player1FinalScore = calculateScore(player1Board);
            int player2FinalScore = calculateScore(player2Board);
            winner = (player1FinalScore > player2FinalScore) ? player1 : player2;
        }

        System.out.println("\nGame Over! " + winner.getInfo("username") + " wins!");

        // Update player stats
        updatePlayerStats(winner, (winner == player1) ? player2 : player1);

        return GameState.MAIN_MENU;
    }

    private void updatePlayerStats(Player winner, Player loser) {
        // Update winner stats
        int xpGain = 100 + (winner.getPlayerInfo("level") * 10);
        int coinGain = 50 + (winner.getPlayerInfo("level") * 5);
        winner.setPlayerInfo("xp", winner.getPlayerInfo("xp") + xpGain);
        winner.setPlayerInfo("coin", winner.getPlayerInfo("coins") + coinGain);

        // Update loser stats
        int loserXpGain = 25 + (loser.getPlayerInfo("level") * 5);
        loser.setPlayerInfo("xp", loser.getPlayerInfo("xp") + loserXpGain);

        // Handle betting mode
        if (isBettingMode) {
            winner.setPlayerInfo("coins", winner.getPlayerInfo("coins") + (betAmount * 2));
            System.out.println(winner.getInfo("username") + " won " + (betAmount * 2) + " coins from the bet!");
        }

        // Check for level ups
        checkAndApplyLevelUp(winner);
        checkAndApplyLevelUp(loser);

        // Update database
        dataManager.updatePlayer(winner);
        dataManager.updatePlayer(loser);

        System.out.println("Player stats updated.");
        System.out.println(winner.getInfo("username") + " gained " + xpGain + " XP and " + coinGain + " coins.");
        System.out.println(loser.getInfo("username") + " gained " + loserXpGain + " XP.");
    }

    private void checkAndApplyLevelUp(Player player) { // works like charm
        int xpNeededForNextLevel = player.getPlayerInfo("level") * 1000;
        while (player.getPlayerInfo("xp") >= xpNeededForNextLevel) {
            player.setPlayerInfo("level", player.getPlayerInfo("level") + 1);
            player.setPlayerInfo("xp", player.getPlayerInfo("xp") - xpNeededForNextLevel);
            int bonusCoins = player.getPlayerInfo("level") * 20;
            player.setPlayerInfo("coins", player.getPlayerInfo("coins") + bonusCoins);
            System.out.println(player.getInfo("username") + " leveled up to level " + player.getPlayerInfo("level")
                    + " and received "
                    + bonusCoins + " bonus coins!");
            xpNeededForNextLevel = player.getPlayerInfo("level") * 1000;
        }
    }

    private class Sector {
        private boolean isHole;
        private Card card;
        private boolean shielded;
        private boolean neutralized;
        private boolean preNeutralized;

        public Sector() {
            this.isHole = false;
            this.card = null;
            this.shielded = false;
        }

        public boolean isNeutralized() {
            return neutralized;
        }

        public void setNeutralized(boolean neutralized) {
            this.neutralized = neutralized;
        }

        public boolean isPreNeutralized() {
            return preNeutralized;
        }

        public void setPreNeutralized(boolean neutralized) {
            this.preNeutralized = neutralized;
        }

        public boolean isHole() {
            return isHole;
        }

        public void setHole(boolean hole) {
            isHole = hole;
        }

        public Card getCard() {
            return card;
        }

        public void setCard(Card card) {
            this.card = card;
        }

        public boolean isShielded() {
            return shielded;
        }

        public void setShielded(boolean shielded) {
            this.shielded = shielded;
        }
    }
}