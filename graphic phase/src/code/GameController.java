package code;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PopupControl;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Collections;
import java.util.stream.*;

import code.inventory.*;

public class GameController {

    private static final int BOARD_SIZE = 21;
    private Pane dragLayer;

    @FXML
    private VBox gameArea;
    @FXML
    private VBox player1Area;
    @FXML
    private VBox player2Area;
    @FXML
    private Label roundNumber;
    @FXML
    private Label player1username;
    @FXML
    private Label player2username;
    @FXML
    private Label player1LT;
    @FXML
    private Label player1LHP;
    @FXML
    private HBox player1BoardUI;
    @FXML
    private Label player2LT;
    @FXML
    private Label player2LHP;
    @FXML
    private HBox player2BoardUI;
    @FXML
    private HBox player1HandUI;
    @FXML
    private HBox player2HandUI;
    @FXML
    private Label player1ScoreLabel;
    @FXML
    private Label player2ScoreLabel;

    private Timeline timeline;
    private Player player1;
    private Player player2;
    private int player1LevelGain = 0;
    private int player2LevelGain = 0;
    private int player1XpGain = 0;
    private int player2XpGain = 0;
    private int player1CoinGain = 0;
    private int player2CoinGain = 0;
    private int player1Score;
    private int player2Score;
    private int player1Hp;
    private int player2Hp;
    private int player1LeftTurns;
    private int player2LeftTurns;
    private int reduceNextCardAcc = 0;
    private boolean player1QC = false;
    private boolean player2QC = true;
    private boolean showPlayer1Cards;
    private boolean showPlayer2Cards;
    private ArrayList<Card> player1Hand;
    private ArrayList<Card> player2Hand;
    private String player1Character;
    private String player2Character;
    private ArrayList<Sector> player1Board;
    private ArrayList<Sector> player2Board;
    private int currentRound;
    // private int currentTurn;
    private int turnsRemaining;
    private Random random;
    private boolean isBettingMode;
    private int betAmount;
    private String[] characters = { "ranger", "warrior", "sorcerer", "rogue" };
    private DataManager dataManager;
    private boolean startOver;
    private Main mainApp;

    private Card draggedCard;
    private boolean isPlayer1Turn;

    private Stage primaryStage;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setupGame(Main mainApp, DataManager dataManager, String player1Character, String player2character,
            int bettingAmount, boolean startOver) {
        // setup datamanager and random and starting status
        this.startOver = startOver;
        this.mainApp = mainApp;
        this.dataManager = dataManager;
        this.random = new Random();
        // set player1 and player2
        this.player1 = this.dataManager.getCurrentPlayer();
        System.out.println(this.player1);
        // dataManager.setSecondaryPlayer(dataManager.getPlayer(username2));
        this.player2 = this.dataManager.getSecondaryPlayer();
        System.out.println(this.player2);
        // set players' characters
        this.player1Character = player1Character;
        this.player2Character = player2character;
        // set isBetting mode
        if (betAmount != -1) {
            isBettingMode = true;
            betAmount = bettingAmount;
        } else {
            isBettingMode = false;
            betAmount = 0;
        }
        // initialize the game
        // startGame();
        gameInitialization();
        gameArea.setVisible(true);
        updateUI();
        dragLayer = new Pane();
        dragLayer.setMouseTransparent(true);
        gameArea.getChildren().add(dragLayer);
    }

    @FXML
    private void initialize() {

    }

    private void gameInitialization() {
        // dataManager.setCurrentPlayer(dataManager.getPlayer("user4"));
        // dataManager.setSecondaryPlayer(dataManager.getPlayer("user5"));
        // this.player1 = dataManager.getCurrentPlayer();
        // this.player2 = dataManager.getSecondaryPlayer();
        player1Score = 0;
        player2Score = 0;
        player1Hp = player1.getPlayerInfo("hp");
        player2Hp = player2.getPlayerInfo("hp");
        showPlayer1Cards = true;
        showPlayer2Cards = true;
        player1username.setText(player1.getInfo("nickname") + " (" + player1Character + ")");
        player2username.setText(player2.getInfo("nickname") + " (" + player2Character + ")");

        player1Board = initializeBoard();
        player2Board = initializeBoard();

        dealInitialCards(player1);
        dealInitialCards(player2);

        currentRound = 1;
        roundNumber.setText("Round " + currentRound);
        player1LeftTurns = 4;
        player2LeftTurns = 4;
        turnsRemaining = player1LeftTurns + player2LeftTurns;
        isPlayer1Turn = random.nextBoolean();

        // initializeBettingMode();

        System.out.println(
                "Game initialized. " + (isPlayer1Turn ? player1.getInfo("username") : player2.getInfo("username"))
                        + " starts.");

    }

    private void resetRound() {
        player1LeftTurns = 4;
        player2LeftTurns = 4;
        player1Score = 0;
        player2Score = 0;
        player1Board = initializeBoard();
        player2Board = initializeBoard();
        showPlayer1Cards = true;
        showPlayer2Cards = true;
        reduceNextCardAcc = 0;
        player1QC = false;
        player2QC = true;
        dealInitialCards(player1);
        dealInitialCards(player2);
        currentRound++;
        roundNumber.setText("Round " + currentRound);
        updateUI();
    }

    private boolean reduceHP(boolean player1, int amount) {
        if (player1) {
            player1Hp = Math.max(player1Hp - amount, 0);
        } else {
            player2Hp = Math.max(player2Hp - amount, 0);
        }

        if (player1Hp == 0 || player2Hp == 0) {
            if (timeline != null) {
                timeline.stop();
            }
            endGame();
        }
        return false;
    }

    public String getCurrentDate() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        return currentDate.format(formatter);
    }

    private void endGame() {
        System.out.println("gameEnded");
        Player winner = (player1Hp > player2Hp) ? player1 : player2;
        Player loser = (player1Hp > player2Hp) ? player2 : player1;
        updatePlayerStats(winner, loser);
        // insert matchinfo
        String date = getCurrentDate();
        String hostName = player1.getInfo("username");
        String result = winner.getInfo("nickname") + " won";
        String rivalName = player2.getInfo("username");
        String rivalLevel = String.valueOf(player2.getPlayerInfo("level"));
        String aftermath = "+" + player1CoinGain + " coins" + " & " + "+" + player1XpGain + " xp";
        MatchInfo currentMatchInfo = new MatchInfo(date, result, hostName, rivalName, rivalLevel, aftermath);
        dataManager.addMatchInfo(currentMatchInfo);
        int winnerXpGain = (winner == player1) ? player1XpGain : player2XpGain;
        int loserXpGain = (winner == player1) ? player2XpGain : player1XpGain;
        int winnerCoinGain = (winner == player1) ? player1CoinGain : player2CoinGain;
        int loserCoinGain = (winner == player1) ? player2CoinGain : player1CoinGain;
        int winnerLevelGain = (winner == player1) ? player1LevelGain : player2LevelGain;
        int loserLevelGain = (winner == player1) ? player2LevelGain : player1LevelGain;
        mainApp.showEndGame(winner, loser, winnerLevelGain, loserLevelGain, winnerXpGain, loserXpGain, winnerCoinGain,
                loserCoinGain, betAmount);
    }

    private void updatePlayerStats(Player winner, Player loser) {
        int winnerXpGain = (winner == player1) ? player1XpGain : player2XpGain;
        int loserXpGain = (winner == player1) ? player2XpGain : player1XpGain;
        int winnerCoinGain = (winner == player1) ? player1CoinGain : player2CoinGain;
        int loserCoinGain = (winner == player1) ? player2CoinGain : player1CoinGain;
        int winnerLevelGain = 0;
        int loserLevelGain = 0;
        // Update winner stats
        winnerXpGain += (100 + (winner.getPlayerInfo("level") * 10));
        winnerCoinGain += 50 + (winner.getPlayerInfo("level") * 5);
        winner.setPlayerInfo("xp", winner.getPlayerInfo("xp") + winnerXpGain);
        winner.setPlayerInfo("coin", winner.getPlayerInfo("coins") + winnerCoinGain);

        // Update loser stats
        loserXpGain += 25 + (loser.getPlayerInfo("level") * 5);
        loser.setPlayerInfo("xp", loser.getPlayerInfo("xp") + loserXpGain);

        // Handle betting mode
        if (isBettingMode) {
            // winnerCoinGain += betAmount * 2;
            winner.setPlayerInfo("coins", winner.getPlayerInfo("coins") + (betAmount * 2));
            loser.setPlayerInfo("coins", loser.getPlayerInfo("coins") - (betAmount));
            System.out.println(winner.getInfo("username") + " won " + (betAmount * 2) + " coins from the bet!");
        }

        // Check for level ups
        // checkAndApplyLevelUp(winner);
        int wXpNeededForNextLevel = winner.getPlayerInfo("level") * 1000;
        while (winner.getPlayerInfo("xp") >= wXpNeededForNextLevel) {
            winnerLevelGain++;
            winner.setPlayerInfo("level", winner.getPlayerInfo("level") + 1);
            winner.setPlayerInfo("hp", winner.getPlayerInfo("hp") + 50);
            winner.setPlayerInfo("xp", winner.getPlayerInfo("xp") - wXpNeededForNextLevel);
            int bonusCoins = winner.getPlayerInfo("level") * 20;
            winnerCoinGain += bonusCoins;// new
            winner.setPlayerInfo("coins", winner.getPlayerInfo("coins") + bonusCoins);
            wXpNeededForNextLevel = winner.getPlayerInfo("level") * 1000;
        }
        // checkAndApplyLevelUp(loser);
        int lXpNeededForNextLevel = loser.getPlayerInfo("level") * 1000;
        while (loser.getPlayerInfo("xp") >= lXpNeededForNextLevel) {
            loserLevelGain++;
            loser.setPlayerInfo("level", loser.getPlayerInfo("level") + 1);
            loser.setPlayerInfo("hp", loser.getPlayerInfo("hp") + 50);
            loser.setPlayerInfo("xp", loser.getPlayerInfo("xp") - lXpNeededForNextLevel);
            int bonusCoins = loser.getPlayerInfo("level") * 20;
            loserCoinGain += bonusCoins;
            loser.setPlayerInfo("coins", loser.getPlayerInfo("coins") + bonusCoins);
            lXpNeededForNextLevel = loser.getPlayerInfo("level") * 1000;
        }

        // update local variables
        player1LevelGain = (player1 == winner) ? winnerLevelGain : loserLevelGain;
        player2LevelGain = (player2 == winner) ? winnerLevelGain : loserLevelGain;
        player1XpGain = (player1 == winner) ? winnerXpGain : loserXpGain;
        player2XpGain = (player2 == winner) ? winnerXpGain : loserXpGain;
        player1CoinGain = (player1 == winner) ? winnerCoinGain : loserCoinGain;
        player2CoinGain = (player2 == winner) ? winnerCoinGain : loserCoinGain;
        // Update database
        dataManager.updatePlayer(winner);
        dataManager.updatePlayer(loser);

        // System.out.println("Player stats updated.");
        // System.out.println(winner.getInfo("username") + " gained " + winnerXpGain + "
        // XP and " + winnerCoinGain + " coins.");
        // System.out.println(loser.getInfo("username") + " gained " + loserXpGain + "
        // XP.");
    }

    // private void checkAndApplyLevelUp(Player loser) { // works like charm
    // int lXpNeededForNextLevel = loser.getPlayerInfo("level") * 1000;
    // while (loser.getPlayerInfo("xp") >= lXpNeededForNextLevel) {
    // loser.setPlayerInfo("level", loser.getPlayerInfo("level") + 1);
    // loser.setPlayerInfo("xp", loser.getPlayerInfo("xp") - lXpNeededForNextLevel);
    // int bonusCoins = loser.getPlayerInfo("level") * 20;
    // loser.setPlayerInfo("coins", loser.getPlayerInfo("coins") + bonusCoins);
    // System.out.println(loser.getInfo("username") + " leveled up to level " +
    // loser.getPlayerInfo("level")
    // + " and received "
    // + bonusCoins + " bonus coins!");
    // lXpNeededForNextLevel = loser.getPlayerInfo("level") * 1000;
    // }
    // }

    public Timeline getTimeline() {
        return timeline;
    }

    private void performTimeLine() {
        timeline = new Timeline(new KeyFrame(
                Duration.millis(800),
                event -> bringTimelineForward()));

        timeline.setCycleCount(Timeline.INDEFINITE);

        // Play the timeline (start the timer)
        timeline.play();

    }

    private int currentTimelineCycle = 0;

    private void bringTimelineForward() {
        if (currentTimelineCycle < BOARD_SIZE) {
            player1HandUI.setVisible(false);
            player2HandUI.setVisible(false);

            player1LT.getParent().setVisible(false);
            // player1ScoreLabel.getParent().setVisible(false);
            player2LT.getParent().setVisible(false);
            // player2ScoreLabel.getParent().setVisible(false);

            player1BoardUI.getStyleClass().remove("active-turn");
            player2BoardUI.getStyleClass().remove("active-turn");
            updateBoardUI(player1BoardUI, player1Board, currentTimelineCycle + 1);
            updateBoardUI(player2BoardUI, player2Board, currentTimelineCycle + 1);
            Sector player1Sector = player1Board.get(currentTimelineCycle);
            Sector player2Sector = player2Board.get(currentTimelineCycle);
            if (!player1Sector.isNeutralized() && player1Sector.getCard() != null) {
                // player2Hp = Math.max(player2Hp -
                // player1Sector.getCard().getDamagePerSector(), 0);
                reduceHP(false, player1Sector.getCard().getDamagePerSector());
                player1Score = Math.max(player1Score - player1Sector.getCard().getDamagePerSector(), 0);
            }
            if (!player2Sector.isNeutralized() && player2Sector.getCard() != null) {
                // player1Hp = Math.max(player1Hp -
                // player2Sector.getCard().getDamagePerSector(), 0);
                reduceHP(true, player2Sector.getCard().getDamagePerSector());
                player2Score = Math.max(player2Score - player2Sector.getCard().getDamagePerSector(), 0);
            }
            updateLeftHPUi();
            updateScoresUI();
            currentTimelineCycle++;
        } else {
            String usernameWithMoreScore = (player1Score > player2Score) ? player1.getInfo("username")
                    : player2.getInfo("username");
            String titleTextString = "Bonus Damage Dealing opportunity for " + usernameWithMoreScore;

            timeline.stop();
            ProgressBarGame.runProgressBarGame(primaryStage, titleTextString, result -> {
                System.out.println("Result: " + result);
                if (player1Score > player2Score) {
                    // player2Hp = Math.max(player2Hp - (int) (result * 20), 0);
                    reduceHP(false, (int) (result * 20));
                } else {
                    // player1Hp = Math.max(player1Hp - (int) (result * 20), 0);
                    reduceHP(true, (int) (result * 20));
                }
                player1HandUI.setVisible(true);
                player2HandUI.setVisible(true);

                player1LT.getParent().setVisible(true);
                // player1ScoreLabel.getParent().setVisible(true);
                player2LT.getParent().setVisible(true);
                // player2ScoreLabel.getParent().setVisible(true);
                resetRound();
                currentTimelineCycle = 0;
            });
        }
    }

    class ProgressBarGame {

        public interface GameCallback {
            void onGameFinished(double result);
        }

        public static void runProgressBarGame(Stage primaryStage, String titleTextString, GameCallback callback) {
            Platform.runLater(() -> {
                Stage gameStage = new Stage();
                gameStage.initModality(Modality.APPLICATION_MODAL);
                gameStage.initOwner(primaryStage);
                gameStage.initStyle(StageStyle.UNDECORATED);

                Text titleText = new Text(titleTextString);
                titleText.setFont(Font.font("SF Pro Display", FontWeight.BOLD, 18));

                ProgressBar progressBar = new ProgressBar(0);
                progressBar.setPrefWidth(250);
                progressBar.setStyle("-fx-accent: #007AFF;");

                Button stopButton = new Button("Stop");
                stopButton.setStyle(
                        "-fx-background-color: #007AFF; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 16; -fx-background-radius: 20;");
                stopButton.setOnMouseEntered(e -> stopButton.setStyle(
                        "-fx-background-color: #0056B3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 16; -fx-background-radius: 20;"));
                stopButton.setOnMouseExited(e -> stopButton.setStyle(
                        "-fx-background-color: #007AFF; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 16; -fx-background-radius: 20;"));

                VBox vbox = new VBox(20);
                vbox.setAlignment(Pos.CENTER);
                vbox.setPadding(new Insets(30, 40, 30, 40));
                vbox.getChildren().addAll(titleText, progressBar, stopButton);
                vbox.setStyle("-fx-background-color: #F5F5F7; -fx-background-radius: 10;");

                DropShadow dropShadow = new DropShadow();
                dropShadow.setColor(Color.rgb(0, 0, 0, 0.2));
                dropShadow.setRadius(10);
                dropShadow.setOffsetX(0);
                dropShadow.setOffsetY(5);
                vbox.setEffect(dropShadow);

                Scene scene = new Scene(vbox);
                scene.setFill(Color.TRANSPARENT);
                gameStage.setScene(scene);

                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        double progress = 0;
                        boolean increasing = true;

                        while (!isCancelled()) {
                            if (increasing) {
                                progress += 0.01;
                                if (progress >= 1) {
                                    increasing = false;
                                }
                            } else {
                                progress -= 0.01;
                                if (progress <= 0) {
                                    increasing = true;
                                }
                            }

                            final double displayProgress = progress;
                            Platform.runLater(() -> progressBar.setProgress(displayProgress));

                            Thread.sleep(20);
                        }
                        return null;
                    }
                };

                stopButton.setOnAction(event -> {
                    task.cancel();
                    double result = Math.abs(progressBar.getProgress() - 0.5);
                    gameStage.close();
                    callback.onGameFinished(result);
                });

                Thread thread = new Thread(task);
                thread.setDaemon(true);
                thread.start();

                gameStage.show();
            });
        }
    }

    class PauseHelper {

        public static void synchronousPause(long duration) {
            try {
                // Sleep for the specified duration (blocking pause)
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void delay(int miliSeconds) {
        try {
            Thread.sleep(miliSeconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
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
        List<Card> allPlayerCards = dataManager.getPlayerCards(player.getInfo("username"));
        allPlayerCards = allPlayerCards.stream()
                .filter(card -> !(card.getName().equals("copyCard") || card.getName().equals("hideRivalCards")))
                .collect(Collectors.toList());
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
                applyCardTypeBonus(player, cardToAdd);
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

    private void startGame() {
        // initializeGame();
        // gameInitialization();
        // gameArea.setVisible(true);
        // updateUI();
        // playGame();
    }

    private void handleSpecialCard(Card card) {
        // int sectorIndex;
        // int cardIndex;
        Player player = (isPlayer1Turn) ? player1 : player2;
        ArrayList<Sector> playerBoard = (isPlayer1Turn) ? player1Board : player2Board;
        ArrayList<Sector> opponentBoard = (isPlayer1Turn) ? player2Board : player1Board;
        ArrayList<Card> playerHand = (player == player1) ? player1Hand : player2Hand;
        if (!playerHand.contains(card))
            return;
        playerHand.remove(card);
        switch (card.getName().toLowerCase()) {
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
            // case "copycard": // test it
            // cardIndex = getCardChoice(player);
            // if (cardIndex == -1)
            // return;
            // Card cardToCopy = new GameReadyCard(playerHand.get(cardIndex),
            // playerHand.get(cardIndex).getLevel());
            // playerHand.add(cardToCopy);
            // System.out.println(
            // player.getInfo("username") + " copied " + cardToCopy.getName()
            // + " and added it to their hand.");

            // break;
            case "hiderivalcards": // test it
                hideRivalCards(player);
                break;
            case "reducenextcardacc":
                reduceNextCardAcc = (isPlayer1Turn ? 1 : 2);
                break;
            case "queencards":
                if (player == player1) {
                    player1QC = true;
                } else {
                    player2QC = true;
                }
                break;
            case "nullrival":
                if (isPlayer1Turn) {
                    player2LeftTurns = 0;
                } else {
                    player1LeftTurns = 0;
                }
                System.out.println("p1:" + player1LeftTurns + " p2:" + player2LeftTurns);
                break;
            default:
                System.out.println("Unknown special card: " + card.getName());
        }
        switchTurn();
        updateUI();
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

    // private void initializeGame() {
    // initializeBoards();
    // initializeHands();
    // isPlayer1Turn = currentTurn % 2 != 0;
    // }

    // private void initializeBoards() {
    // Random random = new Random();
    // int player1Hole = random.nextInt(BOARD_SIZE);
    // int player2Hole = random.nextInt(BOARD_SIZE);

    // for (int i = 0; i < BOARD_SIZE; i++) {
    // player1Board.add(new Sector(i == player1Hole));
    // player2Board.add(new Sector(i == player2Hole));
    // }
    // }

    // private void initializeHands() {
    // for (int i = 0; i < HAND_SIZE; i++) {
    // player1Hand.add(generateRandomCard());
    // player2Hand.add(generateRandomCard());
    // }
    // }

    // private GCard generateRandomCard() {
    // Random random = new Random();
    // int accuracy = random.nextInt(30) + 1;
    // int duration = random.nextInt(4);
    // int damagePerSector = random.nextInt(5) + 1;
    // Color color = Color.rgb(random.nextInt(256), random.nextInt(256),
    // random.nextInt(256));
    // return new GCard(accuracy, duration, damagePerSector, color);
    // }

    private void updateUI() {
        updateBoardUI(player1BoardUI, player1Board);
        updateBoardUI(player2BoardUI, player2Board);
        updateHandUI(player1HandUI, player1Hand, true);
        updateHandUI(player2HandUI, player2Hand, false);
        updateLeftTurnsUi();
        updateLeftHPUi();
        updateScores();
        updateTurnIndicator();
    }

    private void updateLeftTurnsUi() {
        player1LT.setText(String.valueOf(player1LeftTurns));
        player2LT.setText(String.valueOf(player2LeftTurns));
    }

    private void updateLeftHPUi() {
        player1LHP.setText("HP: " + String.valueOf(player1Hp));
        player2LHP.setText("HP: " + String.valueOf(player2Hp));
    }

    private void updateBoardUI(HBox boardUI, List<Sector> board, int renderUntil) {
        boardUI.getChildren().clear();
        double sectorWidth = 50; // Adjust this value to change the size of sectors
        double sectorHeight = sectorWidth * 1.5; // 2:3 aspect ratio

        for (int i = 0; i < Math.min(renderUntil, 21); i++) {
            Sector sector = board.get(i);
            VBox sectorUI = new VBox(2);
            sectorUI.setAlignment(Pos.CENTER);

            if (sector.isHole()) {
                // For holes, use a black rectangle
                Rectangle holeRect = new Rectangle(sectorWidth, sectorHeight);
                holeRect.setFill(Color.BLACK);
                sectorUI.getChildren().add(holeRect);
            } else if (sector.getCard() != null) {
                // For sectors with cards, use the new sector card UI
                StackPane cardUI = createSectorCardUI(sector.getCard(), sectorWidth);

                // If the sector is neutralized, reduce the opacity
                if (sector.isNeutralized()) {
                    cardUI.setOpacity(0.6);
                }

                sectorUI.getChildren().add(cardUI);
            } else {
                // For empty sectors, use a gray rectangle
                Rectangle emptyRect = new Rectangle(sectorWidth, sectorHeight);
                emptyRect.setFill(Color.GRAY);
                emptyRect.setStroke(Color.BLACK);
                emptyRect.setArcWidth(10);
                emptyRect.setArcHeight(10);
                sectorUI.getChildren().add(emptyRect);
            }

            // Add some spacing between sectors
            HBox.setMargin(sectorUI, new Insets(0, 1, 0, 1));

            boardUI.getChildren().add(sectorUI);
        }
    }

    private void updateBoardUI(HBox boardUI, List<Sector> board) {
        boardUI.getChildren().clear();
        double sectorWidth = 50; // Adjust this value to change the size of sectors
        double sectorHeight = sectorWidth * 1.5; // 2:3 aspect ratio

        for (int i = 0; i < board.size(); i++) {
            Sector sector = board.get(i);
            VBox sectorUI = new VBox(2);
            sectorUI.setAlignment(Pos.CENTER);

            if (sector.isHole()) {
                // For holes, use a black rectangle
                Rectangle holeRect = new Rectangle(sectorWidth, sectorHeight);
                holeRect.setFill(Color.BLACK);
                sectorUI.getChildren().add(holeRect);
            } else if (sector.getCard() != null) {
                // For sectors with cards, use the new sector card UI
                StackPane cardUI = createSectorCardUI(sector.getCard(), sectorWidth);

                // If the sector is neutralized, reduce the opacity
                if (sector.isNeutralized()) {
                    cardUI.setOpacity(0.6);
                }

                sectorUI.getChildren().add(cardUI);
            } else {
                // For empty sectors, use a gray rectangle
                Rectangle emptyRect = new Rectangle(sectorWidth, sectorHeight);
                emptyRect.setFill(Color.GRAY);
                emptyRect.setStroke(Color.BLACK);
                emptyRect.setArcWidth(10);
                emptyRect.setArcHeight(10);
                sectorUI.getChildren().add(emptyRect);
            }

            // Add some spacing between sectors
            HBox.setMargin(sectorUI, new Insets(0, 1, 0, 1));

            boardUI.getChildren().add(sectorUI);
        }
    }

    private StackPane createSectorCardUI(Card card, double sectorWidth) {
        // Calculate height based on 2:3 aspect ratio
        double sectorHeight = sectorWidth * 1.5;

        StackPane cardPane = new StackPane();
        cardPane.setPrefSize(sectorWidth, sectorHeight);

        // Create a background with rounded corners
        Rectangle background = new Rectangle(sectorWidth, sectorHeight);
        background.setArcWidth(10);
        background.setArcHeight(10);
        background.setFill(card.getColor());

        // Add a subtle gradient effect
        Stop[] stops = new Stop[] {
                new Stop(0, Color.rgb(255, 255, 255, 0.3)),
                new Stop(1, Color.rgb(0, 0, 0, 0.1))
        };
        LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops);
        Rectangle gradientOverlay = new Rectangle(sectorWidth, sectorHeight);
        gradientOverlay.setArcWidth(10);
        gradientOverlay.setArcHeight(10);
        gradientOverlay.setFill(gradient);

        VBox contentBox = new VBox();
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setSpacing(sectorHeight * 0.1); // 10% of height as spacing

        // Accuracy label (top)
        Label accuracyLabel;
        if (card.getName().equals("shield")) {
            accuracyLabel = new Label("SHLD");
        } else {
            accuracyLabel = new Label(String.valueOf(card.getAccuracy()));
        }
        accuracyLabel.setStyle("-fx-font-size: " + (sectorWidth * 0.3) + ";" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: white;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 2, 0, 0, 1);");

        // Middle line
        Line middleLine = new Line(0, sectorHeight / 2, sectorWidth, sectorHeight / 2);
        middleLine.setStroke(Color.WHITE);
        middleLine.setStrokeWidth(1);
        middleLine.setOpacity(0.7);

        // Damage label (bottom)
        Label damageLabel = new Label(String.valueOf(card.getDamagePerSector()));
        damageLabel.setStyle("-fx-font-size: " + (sectorWidth * 0.3) + ";" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: white;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 2, 0, 0, 1);");

        contentBox.getChildren().addAll(accuracyLabel, middleLine, damageLabel);

        // Add all elements to the card pane
        cardPane.getChildren().addAll(background, gradientOverlay, contentBox);

        // Add a subtle shadow effect to the whole card
        cardPane.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.5)));

        addCardInfoHover(cardPane, card);
        return cardPane;
    }
    // private StackPane createSectorCardUI(Card card, double sectorWidth) {
    // // Calculate height based on 2:3 aspect ratio
    // double sectorHeight = sectorWidth * 1.5;

    // StackPane cardPane = new StackPane();
    // cardPane.setPrefSize(sectorWidth, sectorHeight);
    // cardPane.setMinSize(sectorWidth, sectorHeight);
    // cardPane.setStyle("-fx-background-color: " + toRGBCode(card.getColor()) + ";"
    // +
    // "-fx-border-color: black;" +
    // "-fx-border-width: 1;" +
    // "-fx-border-radius: 5;");

    // VBox contentBox = new VBox();
    // contentBox.setAlignment(Pos.CENTER);
    // contentBox.setSpacing(sectorHeight * 0.1); // 10% of height as spacing

    // // Accuracy label (top)
    // Label accuracyLabel = new Label(String.valueOf(card.getAccuracy()));
    // accuracyLabel.setStyle("-fx-font-size: " + (sectorWidth * 0.3) + ";" +
    // "-fx-font-weight: bold;" +
    // "-fx-text-fill: white;");

    // // Damage label (bottom)
    // Label damageLabel = new Label(String.valueOf(card.getDamagePerSector()));
    // damageLabel.setStyle("-fx-font-size: " + (sectorWidth * 0.3) + ";" +
    // "-fx-font-weight: bold;" +
    // "-fx-text-fill: white;");

    // contentBox.getChildren().addAll(accuracyLabel, damageLabel);
    // cardPane.getChildren().add(contentBox);

    // return cardPane;
    // }

    // Helper method to convert Color to RGB code
    private String toRGBCode(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
    // private void updateBoardUI(HBox boardUI, List<Sector> board) {
    // boardUI.getChildren().clear();
    // double cardScale = 0.25; // Scale for the modern cards
    // double cardWidth = 200 * cardScale; // Based on the 2:3 aspect ratio card
    // width
    // double cardHeight = 300 * cardScale; // Based on the 2:3 aspect ratio card
    // height

    // for (int i = 0; i < board.size(); i++) {
    // Sector sector = board.get(i);
    // VBox sectorUI = new VBox(2);
    // sectorUI.setAlignment(javafx.geometry.Pos.CENTER);

    // if (sector.isHole()) {
    // // For holes, we'll use a black rectangle with the same width as the cards
    // Rectangle holeRect = new Rectangle(cardWidth, cardHeight);
    // holeRect.setFill(Color.BLACK);
    // sectorUI.getChildren().add(holeRect);
    // } else if (sector.getCard() != null) {
    // // For sectors with cards, use the modern card UI
    // StackPane cardUI = createModernCardUI(sector.getCard(), cardScale);

    // // If the sector is neutralized, reduce the opacity
    // if (sector.isNeutralized()) {
    // cardUI.setOpacity(0.8);
    // }

    // sectorUI.getChildren().add(cardUI);
    // } else {
    // // For empty sectors, use a gray rectangle with the same width as the cards
    // Rectangle emptyRect = new Rectangle(cardWidth, cardHeight);
    // emptyRect.setFill(Color.GRAY);
    // emptyRect.setStroke(Color.BLACK);
    // emptyRect.setArcWidth(15 * cardScale);
    // emptyRect.setArcHeight(15 * cardScale);
    // sectorUI.getChildren().add(emptyRect);
    // }

    // // Add some spacing between sectors
    // HBox.setMargin(sectorUI, new Insets(0, 1, 0, 1));

    // boardUI.getChildren().add(sectorUI);
    // }
    // }
    // private void updateBoardUI(HBox boardUI, List<Sector> board) {
    // boardUI.getChildren().clear();
    // for (int i = 0; i < board.size(); i++) {
    // Sector sector = board.get(i);
    // VBox sectorUI = new VBox(2);
    // sectorUI.setAlignment(javafx.geometry.Pos.CENTER);

    // if (sector.isHole()) {
    // // For holes, we'll use a simple black rectangle
    // Rectangle holeRect = new Rectangle(80, 100);
    // holeRect.setFill(Color.BLACK);
    // sectorUI.getChildren().add(holeRect);
    // } else if (sector.getCard() != null) {
    // // For sectors with cards, use the modern card UI
    // StackPane cardUI = createModernCardUI(sector.getCard(), .4);

    // // If the sector is neutralized, reduce the opacity
    // if (sector.isNeutralized()) {
    // cardUI.setOpacity(0.7);
    // }

    // sectorUI.getChildren().add(cardUI);
    // } else {
    // // For empty sectors, use a gray rectangle
    // Rectangle emptyRect = new Rectangle(80, 100);
    // emptyRect.setFill(Color.GRAY);
    // emptyRect.setStroke(Color.BLACK);
    // sectorUI.getChildren().add(emptyRect);
    // }

    // boardUI.getChildren().add(sectorUI);
    // }
    // }
    // private void updateBoardUI(HBox boardUI, List<Sector> board) {
    // boardUI.getChildren().clear();
    // for (int i = 0; i < board.size(); i++) {
    // Sector sector = board.get(i);
    // VBox sectorUI = new VBox(2);
    // sectorUI.setMinHeight(120);
    // sectorUI.setAlignment(javafx.geometry.Pos.CENTER);
    // Rectangle sectorRect = new Rectangle(40, 60);
    // if (sector.isHole()) {
    // sectorRect.setFill(Color.BLACK);
    // } else if (sector.getCard() != null) {
    // addCardInfoHover(sectorRect, sector.getCard());
    // sectorRect.setFill(sector.getCard().getColor());
    // Label infoLabel = new Label(String.format("A:%d\nD:%d",
    // sector.getCard().getAccuracy(),
    // sector.getCard().getDamagePerSector()));
    // infoLabel.getStyleClass().add("sector-info");
    // sectorUI.getChildren().add(infoLabel);
    // if (sector.isNeutralized()) {
    // sectorRect.setOpacity(0.3); // Neutralized sector
    // }
    // } else {
    // sectorRect.setFill(Color.GRAY);
    // }
    // sectorRect.setStroke(Color.BLACK);
    // sectorUI.getChildren().add(0, sectorRect);
    // boardUI.getChildren().add(sectorUI);
    // }
    // }

    // private void updateBoardUI(HBox boardUI, List<Sector> board, int renderUntil)
    // {
    // boardUI.getChildren().clear();
    // for (int i = 0; i < Math.min(renderUntil, 21); i++) {
    // System.out.println("Sector: " + renderUntil);
    // Sector sector = board.get(i);
    // VBox sectorUI = new VBox(2);
    // sectorUI.setMinHeight(120);
    // sectorUI.setAlignment(javafx.geometry.Pos.CENTER);
    // Rectangle sectorRect = new Rectangle(40, 60);
    // if (sector.isHole()) {
    // sectorRect.setFill(Color.BLACK);
    // } else if (sector.getCard() != null) {
    // sectorRect.setFill(sector.getCard().getColor());
    // Label infoLabel = new Label(String.format("A:%d\nD:%d",
    // sector.getCard().getAccuracy(),
    // sector.getCard().getDamagePerSector()));
    // infoLabel.getStyleClass().add("sector-info");
    // sectorUI.getChildren().add(infoLabel);
    // if (sector.isNeutralized()) {
    // sectorRect.setOpacity(0.3); // Neutralized sector
    // }
    // } else {
    // sectorRect.setFill(Color.GRAY);
    // }
    // sectorRect.setStroke(Color.BLACK);
    // sectorUI.getChildren().add(0, sectorRect);
    // boardUI.getChildren().add(sectorUI);
    // }
    // }

    private void updateHandUI(HBox handUI, List<Card> hand, boolean isPlayer1) {
        handUI.getChildren().clear();
        for (int i = 0; i < hand.size(); i++) { // change this variable to be as much as playerHand.size()
            StackPane cardSlot = new StackPane();
            cardSlot.setPrefSize(60, 80);
            cardSlot.getStyleClass().add("card-slot");

            if (i < hand.size()) {
                Card card = hand.get(i);
                // StackPane cardUI = createCardUI(card);
                StackPane cardUI = createModernCardUI(card, .6);
                if (card.getDuration() == 0) {
                    cardUI.setOnMousePressed(event -> handleSpecialCard(card));
                } else {
                    cardUI.setOnMousePressed(event -> startDrag(event, card, isPlayer1, cardUI));
                    cardUI.setOnMouseDragged(event -> drag(event));
                    cardUI.setOnMouseReleased(event -> endDrag(event, card, isPlayer1, cardUI));
                }
                cardSlot.getChildren().add(cardUI);
            }

            handUI.getChildren().add(cardSlot);
        }
    }

    private StackPane createCardUI(Card card) {
        StackPane cardPane = new StackPane();
        cardPane.getStyleClass().add("card");
        Rectangle cardRect = new Rectangle(50, 70);
        cardRect.setFill(card.getColor());
        cardRect.setStroke(Color.BLACK);

        Label infoLabel = new Label(
                String.format("A:%d\nD:%d\nDPS:%d", card.getAccuracy(), card.getDuration(), card.getDamagePerSector()));
        infoLabel.getStyleClass().add("card-info");

        cardPane.getChildren().addAll(cardRect, infoLabel);
        if (card.getDuration() == 0) {
            cardPane.setOnMousePressed(event -> handleSpecialCard(card));
        } else {
            cardPane.setOnMousePressed(event -> startDrag(event, card, isPlayer1Turn, cardPane));
            cardPane.setOnMouseDragged(this::drag);
            cardPane.setOnMouseReleased(event -> endDrag(event, card, isPlayer1Turn, cardPane));

        }
        addCardInfoHover(cardPane, card);

        return cardPane;
    }

    private StackPane createModernCardUI(Card card, double scale) {
        // New dimensions for 2:3 aspect ratio (vertically tall)
        double cardWidth = 200 * scale;
        double cardHeight = 300 * scale;

        StackPane cardPane = new StackPane();
        cardPane.setStyle("-fx-border-radius: " + (15 * scale) + ";");
        cardPane.setPrefSize(cardWidth, cardHeight);

        // Apply the clip
        Rectangle clip = new Rectangle(cardWidth, cardHeight);
        clip.setArcWidth(15 * scale);
        clip.setArcHeight(15 * scale);
        cardPane.setClip(clip);

        VBox vbox1 = new VBox();
        vbox1.setPrefSize(cardWidth, cardHeight);
        vbox1.setStyle("-fx-border-width: " + (5 * scale) + "; -fx-border-color: #857100;");

        // adding accuracy label
        HBox accuracyBar = new HBox();
        accuracyBar.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        accuracyBar.setStyle("-fx-background-color: grey;");
        Label accuracyLabel = new Label(String.valueOf(card.getAccuracy()));
        accuracyLabel.setTextFill(Color.web("#ffff8a"));
        accuracyLabel.setStyle("-fx-font-size: " + (30 * scale) + "; -fx-font-weight: bold;");
        Label nameLabel = new Label("  " + card.getName().substring(0, Math.min(13, card.getName().length())));
        nameLabel.setTextFill(Color.web("#ffff8a"));
        nameLabel.setStyle("-fx-font-size: " + (20 * scale) + "; -fx-font-weight: bold;");
        if (!card.getType().equals("spell")) {
            accuracyBar.getChildren().add(accuracyLabel);
            HBox.setMargin(accuracyLabel, new Insets(0, 0, 0, 5 * scale));
        } else {
            nameLabel.setStyle("-fx-font-size: " + (27 * scale) + "; -fx-font-weight: bold;");
            accuracyBar.setStyle("-fx-background-color: silver;");
        }
        accuracyBar.getChildren().add(nameLabel);

        HBox durationBar = new HBox();
        durationBar.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        durationBar.setPadding(new Insets(0, 10 * scale, 0, 0));
        durationBar.setStyle("-fx-background-color: #36454F;");
        for (int i = 1; i <= 5; i++) {
            Pane durationSector = new Pane();
            durationSector.setPrefSize(10 * scale, 20 * scale);
            durationSector.setMaxSize(10 * scale, 20 * scale);
            durationSector.setMinSize(10 * scale, 20 * scale);
            if (i <= card.getDuration()) {
                durationSector.setStyle("-fx-background-color: white;");
            } else {
                durationSector.setStyle("-fx-background-color: grey;");
            }
            HBox.setMargin(durationSector, new Insets(0, 0, 0, 5 * scale));
            if (card.getType().equals("spell") && !card.getName().equalsIgnoreCase("shield")) {
                durationSector.setVisible(false);
            }
            durationBar.getChildren().add(durationSector);
        }

        // adding damage label
        Label damageLabel = new Label(String.valueOf(card.getDamagePerSector()));
        damageLabel.setTextFill(Color.web("#5079c6"));
        damageLabel.setStyle("-fx-font-size: " + (24 * scale) + "; -fx-font-weight: bold; -fx-font-style: italic;");
        HBox.setMargin(damageLabel, new Insets(0, 0, 0, 10 * scale));
        durationBar.getChildren().add(damageLabel);
        if (card.getDamagePerSector() <= 0) {
            damageLabel.setVisible(false);
        }

        StackPane stackPane = new StackPane();
        ImageView imageView = new ImageView();
        String imagePath = card.getImagePath();
        Image image = new Image(imagePath);
        imageView.setImage(image);
        imageView.setFitWidth(190 * scale);
        imageView.setFitHeight(210 * scale);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setPickOnBounds(true);
        // loadImageAsync(card, imageView, scale);
        // ImageView imageView = card.getImageView();
        // imageView.setFitWidth(190 * scale);
        // imageView.setFitHeight(210 * scale);
        // imageView.setPreserveRatio(true);
        // imageView.setSmooth(true);
        // imageView.setPickOnBounds(true);

        Pane levelPane = new Pane();
        levelPane.setPrefSize(40 * scale, 40 * scale);
        levelPane.setMaxSize(40 * scale, 40 * scale);
        levelPane.setMinSize(40 * scale, 40 * scale);
        levelPane.setStyle("-fx-background-color: grey; -fx-background-radius: " + (20 * scale) + ";");
        VBox levelBox = new VBox();
        levelBox.setPrefSize(40 * scale, 40 * scale);
        levelBox.setMaxSize(40 * scale, 40 * scale);
        levelBox.setMinSize(40 * scale, 40 * scale);
        levelBox.setAlignment(javafx.geometry.Pos.CENTER);
        Label levelLabel = new Label(String.valueOf(card.getLevel()));
        levelLabel.setTextFill(Color.WHITE);
        levelLabel.setStyle("-fx-font-size: " + (24 * scale) + "; -fx-font-weight: bold;");
        levelBox.getChildren().add(levelLabel);
        levelPane.getChildren().add(levelBox);
        StackPane.setMargin(levelPane, new Insets(10 * scale, 10 * scale, 0, 0));
        StackPane.setAlignment(levelPane, Pos.TOP_RIGHT);

        if (card.getType().equals("spell")) {
            levelPane.setVisible(false);
        }

        stackPane.getChildren().addAll(imageView, levelPane);

        vbox1.getChildren().addAll(accuracyBar, durationBar, stackPane);
        cardPane.getChildren().add(vbox1);

        addCardInfoHover(cardPane, card);
        return cardPane;
    }

    private void loadImageAsync(Card card, ImageView imageView, double scale) {
        String imagePath = card.getImagePath(); // Assume this method exists to get the image path
        Image image = new Image(imagePath); // Load the image asynchronously

        image.progressProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() == 1.0) {
                Platform.runLater(() -> {
                    imageView.setImage(image);
                    imageView.setFitWidth(190 * scale);
                    imageView.setFitHeight(210 * scale);
                    imageView.setPreserveRatio(true);
                    imageView.setSmooth(true);
                    imageView.setPickOnBounds(true);

                    System.out.println("Image loaded. Dimensions: " + image.getWidth() + " x " + image.getHeight());
                    System.out.println("ImageView dimensions: " + imageView.getBoundsInLocal().getWidth() + " x "
                            + imageView.getBoundsInLocal().getHeight());
                });
            }
        });
    }
    // private StackPane createModernCardUI(Card card, double scale) {
    // // Scale factor

    // StackPane cardPane = new StackPane();
    // cardPane.setStyle("-fx-border-radius: " + (15 * scale) + ";");
    // cardPane.setPrefSize(200 * scale, 250 * scale);

    // // Apply the clip
    // Rectangle clip = new Rectangle(200 * scale, 250 * scale);
    // clip.setArcWidth(15 * scale);
    // clip.setArcHeight(15 * scale);
    // cardPane.setClip(clip);

    // VBox vbox1 = new VBox();
    // vbox1.setPrefSize(200 * scale, 250 * scale);
    // vbox1.setStyle("-fx-border-width: " + (5 * scale) + "; -fx-border-color:
    // #857100;");

    // // adding accuracy label
    // HBox hbox1 = new HBox();
    // hbox1.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
    // hbox1.setStyle("-fx-background-color: grey;");
    // Label accuracyLabel = new Label(String.valueOf(card.getAccuracy()));
    // accuracyLabel.setTextFill(Color.web("#ffff8a"));
    // accuracyLabel.setStyle("-fx-font-size: " + (30 * scale) + "; -fx-font-weight:
    // bold;");
    // hbox1.getChildren().add(accuracyLabel);
    // HBox.setMargin(accuracyLabel, new Insets(0, 0, 0, 10 * scale));

    // HBox hbox2 = new HBox();
    // hbox2.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
    // hbox2.setPadding(new Insets(0, 10 * scale, 0, 0));
    // hbox2.setStyle("-fx-background-color: #36454F;");
    // for (int i = 1; i <= 5; i++) {
    // Pane pane = new Pane();
    // pane.setPrefSize(10 * scale, 20 * scale);
    // pane.setMaxSize(10 * scale, 20 * scale);
    // pane.setMinSize(10 * scale, 20 * scale);
    // if (i <= card.getDuration()) {
    // pane.setStyle("-fx-background-color: white;");
    // } else {
    // pane.setStyle("-fx-background-color: grey;");
    // }
    // HBox.setMargin(pane, new Insets(0, 0, 0, 5 * scale));
    // hbox2.getChildren().add(pane);
    // }

    // // adding damage label
    // Label damageLabel = new Label(String.valueOf(card.getDamagePerSector()));
    // damageLabel.setTextFill(Color.web("#5079c6"));
    // damageLabel.setStyle("-fx-font-size: " + (24 * scale) + "; -fx-font-weight:
    // bold; -fx-font-style: italic;");
    // HBox.setMargin(damageLabel, new Insets(0, 0, 0, 10 * scale));
    // hbox2.getChildren().add(damageLabel);

    // StackPane stackPane = new StackPane();
    // ImageView imageView = card.getImageView(); // get this from the card itself
    // imageView.setFitHeight(160 * scale);
    // imageView.setFitWidth(190 * scale);
    // imageView.setPickOnBounds(true);

    // Pane levelPane = new Pane();
    // levelPane.setPrefSize(40 * scale, 40 * scale);
    // levelPane.setMaxSize(40 * scale, 40 * scale);
    // levelPane.setMinSize(40 * scale, 40 * scale);
    // levelPane.setStyle("-fx-background-color: grey; -fx-background-radius: " +
    // (30 * scale) + ";");
    // VBox levelBox = new VBox();
    // levelBox.setPrefSize(40 * scale, 40 * scale);
    // levelBox.setMaxSize(40 * scale, 40 * scale);
    // levelBox.setMinSize(40 * scale, 40 * scale);
    // levelBox.setAlignment(javafx.geometry.Pos.CENTER);
    // Label levelLabel = new Label(String.valueOf(card.getLevel()));
    // levelLabel.setTextFill(Color.WHITE);
    // levelLabel.setStyle("-fx-font-size: " + (24 * scale) + "; -fx-font-weight:
    // bold;");
    // levelBox.getChildren().add(levelLabel);
    // levelPane.getChildren().add(levelBox);
    // StackPane.setMargin(levelPane, new Insets(110 * scale, 140 * scale, 0, 0));

    // // boolean exists = false;
    // // for (Card c : dataManager.getCurrentPlayer().getCards()) {
    // // if (c.getName().equals(card.getName())) {
    // // exists = true;
    // // break;
    // // }
    // // }

    // stackPane.getChildren().addAll(imageView, levelPane);

    // vbox1.getChildren().addAll(hbox1, hbox2, stackPane);
    // cardPane.getChildren().add(vbox1);

    // addCardInfoHover(cardPane, card);
    // return cardPane;
    // }
    // private StackPane createModernCardUI(Card card) {
    // // starts creating card ui from here

    // StackPane cardPane = new StackPane();
    // cardPane.setStyle("-fx-border-radius: 15;");
    // cardPane.setPrefSize(200, 250);

    // // Apply the clip
    // Rectangle clip = new Rectangle(200, 250);
    // clip.setArcWidth(15);
    // clip.setArcHeight(15);
    // cardPane.setClip(clip);

    // VBox vbox1 = new VBox();
    // vbox1.setPrefSize(200, 250);
    // vbox1.setStyle("-fx-border-width: 5; -fx-border-color: #857100;");

    // // adding accuracy label
    // HBox hbox1 = new HBox();
    // hbox1.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
    // hbox1.setStyle("-fx-background-color: grey;");
    // Label accuracyLabel = new Label(String.valueOf(card.getAccuracy()));
    // accuracyLabel.setTextFill(Color.web("#ffff8a"));
    // accuracyLabel.setStyle("-fx-font-size: 30; -fx-font-weight: bold;");
    // hbox1.getChildren().add(accuracyLabel);
    // HBox.setMargin(accuracyLabel, new Insets(0, 0, 0, 10));

    // HBox hbox2 = new HBox();
    // hbox2.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
    // hbox2.setPadding(new Insets(0, 10, 0, 0));
    // hbox2.setStyle("-fx-background-color: #36454F;");
    // for (int i = 1; i <= 5; i++) {
    // Pane pane = new Pane();
    // pane.setPrefSize(10, 20);
    // pane.setMaxSize(10, 20);
    // pane.setMinSize(10, 20);
    // if (i <= card.getDuration()) {
    // pane.setStyle("-fx-background-color: white;");
    // } else {
    // pane.setStyle("-fx-background-color: grey;");
    // }
    // HBox.setMargin(pane, new Insets(0, 0, 0, 5));
    // hbox2.getChildren().add(pane);
    // }

    // // adding damage label
    // Label damageLabel = new Label(String.valueOf(card.getDamagePerSector()));
    // damageLabel.setTextFill(Color.web("#5079c6"));
    // damageLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;
    // -fx-font-style: italic;");
    // HBox.setMargin(damageLabel, new Insets(0, 0, 0, 10));
    // hbox2.getChildren().add(damageLabel);

    // StackPane stackPane = new StackPane();
    // ImageView imageView = card.getImageView(); // get this from the card itself
    // imageView.setFitHeight(160);
    // imageView.setFitWidth(190);
    // imageView.setPickOnBounds(true);

    // Pane levelPane = new Pane();
    // levelPane.setPrefSize(40, 40);
    // levelPane.setMaxSize(40, 40);
    // levelPane.setMinSize(40, 40);
    // levelPane.setStyle("-fx-background-color: grey; -fx-background-radius: 30;");
    // VBox levelBox = new VBox();
    // levelBox.setPrefSize(40, 40);
    // levelBox.setMaxSize(40, 40);
    // levelBox.setMinSize(40, 40);
    // levelBox.setAlignment(javafx.geometry.Pos.CENTER);
    // Label levelLabel = new Label(String.valueOf(card.getLevel()));
    // levelLabel.setTextFill(Color.WHITE);
    // levelLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");
    // levelBox.getChildren().add(levelLabel);
    // levelPane.getChildren().add(levelBox);
    // StackPane.setMargin(levelPane, new Insets(110, 140, 0, 0));

    // boolean exists = false;
    // for (Card c : dataManager.getCurrentPlayer().getCards()) {
    // if (c.getName().equals(card.getName())) {
    // exists = true;
    // break;
    // }
    // }

    // stackPane.getChildren().addAll(imageView, levelPane);

    // vbox1.getChildren().addAll(hbox1, hbox2, stackPane);
    // cardPane.getChildren().add(vbox1);
    // return cardPane;

    // // if (!exists) {
    // // Pane pane = new Pane();
    // // BackgroundFill backgroundFill = new BackgroundFill(Color.rgb(189, 189,
    // 189,
    // // 0.5), CornerRadii.EMPTY,
    // // Insets.EMPTY);
    // // Background background = new Background(backgroundFill);
    // // pane.setBackground(background);

    // // HBox lockBox = new HBox();
    // // lockBox.setAlignment(javafx.geometry.Pos.CENTER);
    // // lockBox.setPrefWidth(cardPane.getPrefWidth());
    // // lockBox.setPadding(new Insets(40, 0, 0, 0));

    // // VBox vBox = new VBox();
    // // vBox.setAlignment(javafx.geometry.Pos.CENTER);
    // // vBox.setPrefHeight(cardPane.getPrefHeight());

    // // ImageView lockImage = new ImageView(new
    // Image("file:src/res/img/lock.png"));
    // // lockImage.setFitWidth(50);
    // // lockImage.setFitHeight(50);
    // // lockImage.setOnMouseClicked(e -> {
    // // buyCard(card);
    // // });

    // // lockBox.getChildren().add(lockImage);
    // // vBox.getChildren().add(lockBox);
    // // pane.getChildren().add(vBox);
    // // cardPane.getChildren().add(pane);
    // // }
    // // seems it ends creating the card ui in here
    // }

    public void addCardInfoHover(Node node, Card card) {
        AppleStyleCardPopup popup = new AppleStyleCardPopup(card);

        node.setOnMouseEntered(event -> {
            popup.show(node, event.getScreenX() + 15, event.getScreenY() + 15);
        });

        node.setOnMouseExited(event -> {
            popup.hide();
        });

        node.setOnMouseMoved(event -> {
            popup.setX(event.getScreenX() + 15);
            popup.setY(event.getScreenY() + 15);
        });
    }

    class AppleStyleCardPopup extends PopupControl {

        public AppleStyleCardPopup(Card card) {
            VBox content = new VBox(10);
            content.setPadding(new Insets(15));
            content.setStyle("-fx-background-color: rgba(255, 255, 255, 0.95); " +
                    "-fx-background-radius: 10; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 10, 0, 0, 2);");

            // Card name
            Label nameLabel = createLabel(card.getName(), 18, FontWeight.BOLD);

            // Color indicator
            Rectangle colorRect = new Rectangle(40, 4);
            colorRect.setFill(card.getColor());
            colorRect.setArcWidth(2);
            colorRect.setArcHeight(2);

            // Card details
            VBox detailsBox = new VBox(5);
            detailsBox.getChildren().addAll(
                    createDetailLabel("Type", card.getType()),
                    createDetailLabel("Character", card.getCharacter()),
                    createDetailLabel("Accuracy", String.valueOf(card.getAccuracy())),
                    createDetailLabel("Damage/Sector", String.valueOf(card.getDamagePerSector())),
                    createDetailLabel("Duration", String.valueOf(card.getDuration())),
                    createDetailLabel("Level", String.valueOf(card.getLevel())));

            content.getChildren().addAll(nameLabel, colorRect, detailsBox);

            setAutoHide(true);
            getScene().setRoot(content);

            // Apply a subtle drop shadow to the entire popup
            DropShadow dropShadow = new DropShadow();
            dropShadow.setColor(Color.rgb(0, 0, 0, 0.2));
            dropShadow.setRadius(10);
            dropShadow.setOffsetY(2);
            content.setEffect(dropShadow);
        }

        private Label createLabel(String text, int fontSize, FontWeight fontWeight) {
            Label label = new Label(text);
            label.setFont(Font.font("SF Pro Display", fontWeight, fontSize));
            return label;
        }

        private Label createDetailLabel(String key, String value) {
            Label label = new Label(key + ": " + value);
            label.setFont(Font.font("SF Pro Text", FontWeight.NORMAL, 14));
            return label;
        }
    }

    class CardDetailsWindow {

        public static void showCardDetails(Stage primaryStage, Card card) {
            Platform.runLater(() -> {
                Stage detailStage = new Stage();
                detailStage.initOwner(primaryStage);
                detailStage.initStyle(StageStyle.UNDECORATED);

                Text titleText = new Text(card.getName());
                titleText.setFont(Font.font("SF Pro Display", FontWeight.BOLD, 24));

                Rectangle colorRect = new Rectangle(50, 50);
                colorRect.setFill(card.getColor());

                GridPane detailsGrid = new GridPane();
                detailsGrid.setHgap(10);
                detailsGrid.setVgap(10);
                detailsGrid.addRow(0, createLabel("Type:"), createValue(card.getType()));
                detailsGrid.addRow(1, createLabel("Character:"), createValue(card.getCharacter()));
                detailsGrid.addRow(2, createLabel("Accuracy:"), createValue(String.valueOf(card.getAccuracy())));
                detailsGrid.addRow(3, createLabel("Damage/Sector:"),
                        createValue(String.valueOf(card.getDamagePerSector())));
                detailsGrid.addRow(4, createLabel("Duration:"), createValue(String.valueOf(card.getDuration())));
                detailsGrid.addRow(5, createLabel("Level:"), createValue(String.valueOf(card.getLevel())));
                detailsGrid.addRow(6, createLabel("Upgrade Cost:"), createValue(String.valueOf(card.getUpgradeCost())));
                detailsGrid.addRow(7, createLabel("Cost:"), createValue(String.valueOf(card.getCost())));

                VBox vbox = new VBox(20);
                vbox.setAlignment(Pos.CENTER);
                vbox.setPadding(new Insets(30, 40, 30, 40));
                vbox.getChildren().addAll(titleText, colorRect, detailsGrid);
                vbox.setStyle("-fx-background-color: #F5F5F7; -fx-background-radius: 10;");

                DropShadow dropShadow = new DropShadow();
                dropShadow.setColor(Color.rgb(0, 0, 0, 0.2));
                dropShadow.setRadius(10);
                dropShadow.setOffsetX(0);
                dropShadow.setOffsetY(5);
                vbox.setEffect(dropShadow);

                Scene scene = new Scene(vbox);
                scene.setFill(Color.TRANSPARENT);
                detailStage.setScene(scene);

                // Close the window when clicked anywhere
                scene.setOnMouseClicked(event -> detailStage.close());

                detailStage.show();
            });
        }

        private static Label createLabel(String text) {
            Label label = new Label(text);
            label.setFont(Font.font("SF Pro Text", FontWeight.BOLD, 14));
            return label;
        }

        private static Label createValue(String text) {
            Label label = new Label(text);
            label.setFont(Font.font("SF Pro Text", 14));
            return label;
        }
    }

    private void startDrag(javafx.scene.input.MouseEvent event, Card card, boolean isPlayer1, StackPane cardUI) {
        if (isPlayer1 != isPlayer1Turn)
            return;
        draggedCard = card;

        // Create a copy of the card UI for dragging
        StackPane draggedCardUI = createModernCardUI(card, .4);
        draggedCardUI.setOpacity(0.9);

        // Position the dragged card copy at the mouse cursor
        Point2D localPoint = dragLayer.sceneToLocal(event.getSceneX(), event.getSceneY());
        draggedCardUI.setLayoutX(localPoint.getX() - draggedCardUI.getWidth() / 2);
        draggedCardUI.setLayoutY(localPoint.getY() - draggedCardUI.getHeight() / 2);

        // Add the dragged card copy to the drag layer
        dragLayer.getChildren().add(draggedCardUI);

        // Hide the original card UI
        cardUI.setVisible(false);
    }

    private void drag(javafx.scene.input.MouseEvent event) {
        if (draggedCard == null || dragLayer.getChildren().isEmpty())
            return;

        StackPane draggedCardUI = (StackPane) dragLayer.getChildren().get(0);
        Point2D localPoint = dragLayer.sceneToLocal(event.getSceneX(), event.getSceneY());
        draggedCardUI.setLayoutX(localPoint.getX() - draggedCardUI.getWidth() / 2);
        draggedCardUI.setLayoutY(localPoint.getY() - draggedCardUI.getHeight() / 2);
    }

    private void endDrag(javafx.scene.input.MouseEvent event, Card card, boolean isPlayer1, StackPane originalCardUI) {
        if (draggedCard == null || dragLayer.getChildren().isEmpty())
            return;

        StackPane draggedCardUI = (StackPane) dragLayer.getChildren().get(0);
        dragLayer.getChildren().clear();

        HBox targetBoard = isPlayer1 ? player1BoardUI : player2BoardUI;
        List<Sector> board = isPlayer1 ? player1Board : player2Board;

        int targetSector = -1;
        for (javafx.scene.Node node : targetBoard.getChildren()) {
            if (node instanceof VBox) {
                VBox sectorUI = (VBox) node;
                if (sectorUI.getBoundsInParent()
                        .contains(targetBoard.sceneToLocal(event.getSceneX(), event.getSceneY()))) {
                    targetSector = targetBoard.getChildren().indexOf(sectorUI);
                    break;
                }
            }
        }

        if (targetSector != -1 && placeCard(card, targetSector, board)) {
            ArrayList<Card> hand = (isPlayer1 ? player1Hand : player2Hand);
            checkAndBuffMiddleCard(hand, card);
            hand.remove(card);
            updateUI();
            switchTurn();
        } else {
            // If the card wasn't placed, make the original card visible again
            originalCardUI.setVisible(true);
        }

        draggedCard = null;
    }

    private boolean placeCard(Card card, int sector, List<Sector> board) {
        if (card.getDuration() == 0) {
            // card.applySpecialEffect(board, isPlayer1Turn ? player2Board : player1Board);
            // special cards effects must be implemented here
            return true;
        }

        if (sector + card.getDuration() > BOARD_SIZE)
            return false;

        for (int i = sector; i < sector + card.getDuration(); i++) {
            if (board.get(i).isHole() || board.get(i).getCard() != null) {
                return false;
            }
        }

        for (int i = sector; i < sector + card.getDuration(); i++) {
            board.get(i).setCard(card);
            if (card.getName().equals("shield")) {
                board.get(i).setShielded(true);
            }
        }

        return true;
    }

    private void switchTurn() {
        System.out.println(isPlayer1Turn);
        Player currentTurnPlayer = (isPlayer1Turn) ? player1 : player2;
        if (isPlayer1Turn) {
            player1LeftTurns = Math.max(player1LeftTurns - 1, 0);
        } else {
            player2LeftTurns = Math.max(player2LeftTurns - 1, 0);
        }
        turnsRemaining = player1LeftTurns + player2LeftTurns;
        if ((isPlayer1Turn && player2LeftTurns > 0) || (!isPlayer1Turn && player1LeftTurns > 0))
            isPlayer1Turn = !isPlayer1Turn;
        updateTurnIndicator();
        drawNewCard(currentTurnPlayer);
        if (player1QC) {
            ArrayList<Card> newHand = new ArrayList<>();
            for (Card card : player1Hand) {
                newHand.add(new GameReadyCard(card, card.getLevel() + 1));
            }
            player1Hand = newHand;
        }
        if (player2QC) {
            ArrayList<Card> newHand = new ArrayList<>();
            for (Card card : player2Hand) {
                newHand.add(new GameReadyCard(card, card.getLevel() + 1));
            }
            player2Hand = newHand;
        }
        updateDominantSectors();
        checkAndRewardNewNeu(player1);
        checkAndRewardNewNeu(player2);
        updateUI();

        if (turnsRemaining <= 0) {
            System.out.println("round Over");
            performTimeLine();
        }
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

    private void checkAndBuffMiddleCard(ArrayList<Card> hand, Card chosenCard) {
        if (hand.size() % 2 == 0)
            return;
        int middleIndex = (int) (hand.size() / 2);
        if (chosenCard.getCharacter().equals(hand.get(middleIndex).getCharacter())) {
            int randomInt = random.nextInt(1001);
            if (chosenCard.getCharacter().equals("ranger")) {
                if (randomInt % 2 == 0) {
                    hand.get(middleIndex).setAccuracy(hand.get(middleIndex).getAccuracy() + 5);
                    hand.get(middleIndex).setDamagePerSector(hand.get(middleIndex).getDamagePerSector() + 5);
                    System.out.println("middle card buffed!");
                }
            } else if (chosenCard.getCharacter().equals("warrior")) {
                if (randomInt % 3 == 0) {
                    hand.get(middleIndex).setAccuracy(hand.get(middleIndex).getAccuracy() + 5);
                    hand.get(middleIndex).setDamagePerSector(hand.get(middleIndex).getDamagePerSector() + 5);
                    System.out.println("middle card buffed!");
                }
            } else if (chosenCard.getCharacter().equals("sorcerer")) {
                if (randomInt % 3 == 0) {
                    hand.get(middleIndex).setAccuracy(hand.get(middleIndex).getAccuracy() + 5);
                    hand.get(middleIndex).setDamagePerSector(hand.get(middleIndex).getDamagePerSector() + 5);
                    System.out.println("middle card buffed!");
                }
            } else if (chosenCard.getCharacter().equals("rogue")) {
                if (randomInt % 2 == 0) {
                    hand.get(middleIndex).setAccuracy(hand.get(middleIndex).getAccuracy() + 5);
                    hand.get(middleIndex).setDamagePerSector(hand.get(middleIndex).getDamagePerSector() + 5);
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
            int coinsReward = 10;
            if (player == player1) {
                player1CoinGain += coinsReward;
            } else {
                player2CoinGain += coinsReward;
            }
            // player.setPlayerInfo("coins", player.getPlayerInfo("coins") + coinsReward);
            System.out.println(player.getInfo("username") + " has been rewarded with " + coinsReward
                    + " coins for neutralizing a card!");
        } else {
            int xpReward = 20;
            if (player == player1) {
                player1XpGain += xpReward;
            } else {
                player2XpGain += xpReward;
            }
            // player.setPlayerInfo("xp", player.getPlayerInfo("xp") + xpReward);
            System.out.println(
                    player.getInfo("username") + " has been rewarded with " + xpReward
                            + " XP for neutralizing a card!");
        }
    }

    private void drawNewCard(Player player) {
        ArrayList<Card> currentPlayerHand = (player == player1) ? player1Hand : player2Hand;
        ArrayList<Card> allPlayerCards = dataManager.getPlayerCards(player.getInfo("username"));

        // Create a list of valid cards to choose from
        List<Card> validCards = new ArrayList<>();

        for (Card card : allPlayerCards) {
            boolean isValidCard = true;

            // Check if the card is repetitive, hideRivalCard, or copyCard
            for (Card handCard : currentPlayerHand) {
                if (card.getName().equals(handCard.getName()) ||
                        card.getName().equals("copyCard") ||
                        card.getName().equals("hideRivalCards")) {
                    isValidCard = false;
                    break;
                }
            }

            // Check if adding this card would exceed the spell limit
            int spellCount = 0;
            for (Card handCard : currentPlayerHand) {
                if (handCard.getType().equals("spell")) {
                    spellCount++;
                }
            }
            if (card.getType().equals("spell") && spellCount >= 2) {
                isValidCard = false;
            }

            if (isValidCard) {
                validCards.add(card);
            }
        }

        Card replacementCard;

        // If there are no valid cards, choose a random card from the hand
        if (validCards.isEmpty()) {
            System.out.println("No valid cards available to draw. Duplicating a card from hand.");
            replacementCard = currentPlayerHand.get(random.nextInt(currentPlayerHand.size()));
            // Create a new instance of the chosen card to avoid reference issues
            replacementCard = new Card(replacementCard); // Assuming Card has a copy constructor
        } else {
            // Choose a random card from the valid cards
            replacementCard = validCards.get(random.nextInt(validCards.size()));
        }

        // upgrade by character
        applyCardTypeBonus(player, replacementCard);

        // perform special act
        if ((reduceNextCardAcc == 1 && player == player1) || (reduceNextCardAcc == 2 && player == player2)) {
            reduceNextCardAcc = 0;
            replacementCard.setAccuracy(replacementCard.getAccuracy() - 5);
        }

        // Add the chosen card to the player's hand
        currentPlayerHand.add(replacementCard);

        // Optional: Upgrade the card if it matches the player's character
        // if (player == player1 &&
        // replacementCard.getCharacter().equals(player1Character)) {
        // replacementCard = new GameReadyCard(replacementCard,
        // replacementCard.getLevel() + 1);
        // } else if (player == player2 &&
        // replacementCard.getCharacter().equals(player2Character)) {
        // replacementCard = new GameReadyCard(replacementCard,
        // replacementCard.getLevel() + 1);
        // }
    }
    // private void drawNewCard(Player player) {
    // ArrayList<Card> currentPlayerHand = (player == player1) ? player1Hand :
    // player2Hand;
    // ArrayList<Card> allPlayerCards =
    // dataManager.getPlayerCards(player.getInfo("username"));
    // Card replacementCard =
    // allPlayerCards.get(random.nextInt(allPlayerCards.size()));
    // int currentSpellsInHand;
    // int iterations = 1000;
    // boolean repetetiveCard;
    // do {
    // currentSpellsInHand = 0;
    // repetetiveCard = false;
    // for (Card card : currentPlayerHand) {
    // if (card.getType().equals("spell")) {
    // currentSpellsInHand++;
    // }
    // if (replacementCard.getName().equals(card.getName()) ||
    // replacementCard.getName().equals("copyCard")
    // || replacementCard.getName().equals("hideRivalCards")) {
    // repetetiveCard = true;
    // }
    // }
    // replacementCard = allPlayerCards.get(random.nextInt(allPlayerCards.size()));
    // if (replacementCard.getType().equals("spell"))
    // currentSpellsInHand++;
    // iterations--;
    // } while ((repetetiveCard || currentSpellsInHand > 3));
    // // if (player == player1) {
    // // if (replacementCard.getCharacter().equals(player1Character)) {
    // // replacementCard = new GameReadyCard(replacementCard,
    // // replacementCard.getLevel() + 1);
    // // }
    // // } else {
    // // if (replacementCard.getCharacter().equals(player1Character)) {
    // // replacementCard = new GameReadyCard(replacementCard,
    // // replacementCard.getLevel() + 1);
    // // }
    // // }
    // currentPlayerHand.add(replacementCard);
    // }

    private void updateTurnIndicator() {
        player1BoardUI.getStyleClass().remove("active-turn");
        player2BoardUI.getStyleClass().remove("active-turn");
        (isPlayer1Turn ? player1BoardUI : player2BoardUI).getStyleClass().add("active-turn");
    }

    private void updateScores() {
        player1Score = calculateScore(player1Board);
        player2Score = calculateScore(player2Board);

        // player1.setPlayerInfo("score", player1Score);
        // player2.setPlayerInfo("score", player2Score);

        updateScoresUI();
    }

    private void updateScoresUI() {
        player1ScoreLabel.setText(numberToFittingString(player1Score));
        player2ScoreLabel.setText(numberToFittingString(player2Score));
    }

    private String numberToFittingString(int number) {
        String fitString;
        if (number < 10) {
            fitString = "  " + String.valueOf(number);
        } else if (number < 100) {
            fitString = " " + String.valueOf(number);
        } else {
            fitString = String.valueOf(number);
        }
        return fitString;
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
    // private int calculateScore(List<Sector> playerBoard, List<Sector>
    // opponentBoard) {
    // int score = 0;
    // for (int i = 0; i < BOARD_SIZE; i++) {
    // Sector playerSector = playerBoard.get(i);
    // Sector opponentSector = opponentBoard.get(i);

    // if (playerSector.getCard() != null && (opponentSector.getCard() == null
    // || playerSector.getCard().getAccuracy() >
    // opponentSector.getCard().getAccuracy())) {
    // score += playerSector.getCard().getDamagePerSector();
    // }
    // }
    // return score;
    // }

    // private static class GCard {
    // private final int accuracy;
    // private final int duration;
    // private final int damagePerSector;
    // private final Color color;

    // public GCard(int accuracy, int duration, int damagePerSector, Color color) {
    // this.accuracy = accuracy;
    // this.duration = duration;
    // this.damagePerSector = damagePerSector;
    // this.color = color;
    // }

    // public int getAccuracy() {
    // return accuracy;
    // }

    // public int getDuration() {
    // return duration;
    // }

    // public int getDamagePerSector() {
    // return damagePerSector;
    // }

    // public Color getColor() {
    // return color;
    // }

    // public void applySpecialEffect(List<Sector> playerBoard, List<Sector>
    // opponentBoard) {
    // // Implement special card effects here
    // Random random = new Random();
    // int sectorToClear = random.nextInt(BOARD_SIZE);
    // opponentBoard.get(sectorToClear).setCard(null);
    // }
    // }

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