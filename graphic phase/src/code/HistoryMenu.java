package code;

import code.inventory.Card;
import code.inventory.DataManager;
import code.inventory.MatchInfo;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import java.text.SimpleDateFormat;
import java.util.*;

public class HistoryMenu {
    private Main mainApp;
    private DataManager dataManager;
    private int page = 1;
    private final int numPerPage = 10;
    private String gProperty = "rival name";

    @FXML
    VBox historyHolder;
    @FXML
    ComboBox<String> orderCombo;

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
        dataManager = mainApp.getDataManager();
        run();
    }

    private void run() {
        dataManager.getCurrentPlayer().setMatchHistory(
                dataManager.getMatchInfosByHostName(dataManager.getCurrentPlayer().getInfo("username")));
        orderCombo.setValue("ascending");
        orderCombo.valueProperty().addListener((observable, oldValue, newValue) -> showHistory(1));
        showHistory(0);
    }

    private void showHistory(int n) {
        if (n == 1) {
            page = 1;
        }

        historyHolder.getChildren().clear();

        ArrayList<MatchInfo> history = sortHistory(new ArrayList<>(dataManager.getCurrentPlayer().getMatchHistory()),
                gProperty, orderCombo.getValue());

        for (int m = (page - 1) * numPerPage; m < (page) * numPerPage && m < history.size(); m++) {
            MatchInfo matchInfo = history.get(m);

            HBox mainHBox = new HBox();
            mainHBox.setStyle("-fx-background-color: #501d1d;");
            mainHBox.setPadding(new Insets(5, 5, 5, 5));

            HBox dateHBox = createHistoryPropertyHBox(matchInfo.getDate(), "#2fb3d0", 18);
            dateHBox.setOnMouseClicked(event -> setDate());

            HBox resultHBox = createHistoryPropertyHBox(matchInfo.getResult(), "#2fb3d0", 18);
            resultHBox.setOnMouseClicked(event -> setResult());

            HBox nameHBox = createHistoryPropertyHBox(matchInfo.getRivalName(), "#2fb3d0", 18);
            nameHBox.setOnMouseClicked(event -> setName());

            HBox levelHBox = createHistoryPropertyHBox(matchInfo.getRivalLevel(), "#2fb3d0", 18);
            levelHBox.setOnMouseClicked(event -> setLevel());

            HBox aftermathHBox = createHistoryPropertyHBox(matchInfo.getAftermath(), "#2fb3d0", 18, 580);
            aftermathHBox.setOnMouseClicked(event -> setAftermath());

            mainHBox.getChildren().addAll(dateHBox, resultHBox, nameHBox, levelHBox, aftermathHBox);
            historyHolder.getChildren().add(mainHBox);
        }
    }

    private HBox createHistoryPropertyHBox(String labelText, String textFillColor, double fontSize) {
        return createHistoryPropertyHBox(labelText, textFillColor, fontSize, 200);
    }

    private HBox createHistoryPropertyHBox(String labelText, String textFillColor, double fontSize, double prefWidth) {
        HBox hbox = new HBox();
        hbox.setPrefWidth(prefWidth);
        hbox.getStyleClass().add("history-property");

        Label label = new Label(labelText);
        label.setTextFill(javafx.scene.paint.Color.web(textFillColor));
        label.setFont(Font.font("System", javafx.scene.text.FontWeight.BOLD, fontSize));

        hbox.getChildren().add(label);

        return hbox;
    }

    public ArrayList<MatchInfo> sortHistory(ArrayList<MatchInfo> history, String property, String sortOrder) {
        Comparator<MatchInfo> comparator;

        switch (property.toLowerCase()) {
            case "date":
                comparator = (m1, m2) -> {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        Date date1 = sdf.parse(m1.getDate());
                        Date date2 = sdf.parse(m2.getDate());
                        return date1.compareTo(date2);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                };
                break;
            case "result":
                comparator = Comparator.comparing(MatchInfo::getResult);
                break;
            case "rival name":
                comparator = Comparator.comparing(MatchInfo::getRivalName);
                break;
            case "rival level":
                comparator = (m1, m2) -> {
                    Integer level1 = Integer.parseInt(m1.getRivalLevel());
                    Integer level2 = Integer.parseInt(m2.getRivalLevel());
                    return level1.compareTo(level2);
                };
                break;
            default:
                throw new IllegalArgumentException("Invalid property: " + property);
        }

        if (sortOrder.equalsIgnoreCase("descending")) {
            comparator = comparator.reversed();
        }

        history.sort(comparator);
        return history;
    }

    @FXML
    private void goBack() {
        mainApp.showMainMenu();
    }

    @FXML
    private void previousPage() {
        if (page > 1) {
            page--;
            showHistory(0);
        }
    }

    @FXML
    private void nextPage() {
        double x = ((double) dataManager.getCurrentPlayer().getMatchHistory().size() / (double) numPerPage);
        if (page < Math.ceil(x)) {
            page++;
            showHistory(0);
        }
    }

    @FXML
    private void setDate() {
        gProperty = "date";
        showHistory(1);
    }

    @FXML
    private void setResult() {
        gProperty = "result";
        showHistory(1);
    }

    @FXML
    private void setName() {
        gProperty = "rival name";
        showHistory(1);
    }

    @FXML
    private void setLevel() {
        gProperty = "rival level";
        showHistory(1);
    }

    @FXML
    private void setAftermath() {
        gProperty = "aftermath";
        showHistory(1);
    }
}