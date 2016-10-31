package controller;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Team;
import net.java.games.input.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

@org.springframework.stereotype.Controller
public class MainViewController {
    static final Logger logger = LoggerFactory.getLogger(MainViewController.class);
    private Stage stage;

    private Map<String, Controller> controllerList;
    private Controller controllerOne;
    private Controller controllerTwo;

    @FXML
    private Text teamRed, teamBlue, scoreTextRed, scoreTextBlue;

    @FXML
    private AnchorPane scoreBoxRed, scoreBoxBlue;

    @FXML
    private Text currentScoreTextRed, previousScoreTextRed, oldScoreTextRed;

    @FXML
    private Text currentScoreTextBlue, previousScoreTextBlue, oldScoreTextBlue;

//    {
//        scoreTextRed.setText(DEFAULT_TEXT);
//        scoreTextBlue.setText(DEFAULT_TEXT);
//        currentScoreTextRed.setText(DEFAULT_TEXT);
//        previousScoreTextRed.setText(DEFAULT_TEXT);
//        oldScoreTextRed.setText(DEFAULT_TEXT);
//        currentScoreTextBlue.setText(DEFAULT_TEXT);
//        previousScoreTextBlue.setText(DEFAULT_TEXT);
//        oldScoreTextBlue.setText(DEFAULT_TEXT);
//        logger.info("Set default values");
//    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void show() {
        logger.info("Show main scene");
        controllerOne = showDefineControllersDialog(Team.RED);
        controllerTwo = showDefineControllersDialog(Team.BLUE);
        stage.show();
    }

    private Controller showDefineControllersDialog(Team team) {
        String [] array = new String [controllerList.keySet().size()];
        array = controllerList.keySet().toArray(array);
        Dialog<String> dialog = new ChoiceDialog<>("none", array);
        dialog.setTitle("Choose joystick for ".concat(team.getTeamName()));
        dialog.setHeaderText("List of available devices");
        Optional<String> optional = dialog.showAndWait();
        if (optional.isPresent()) {
            return controllerList.remove(optional.get());
        }
        return null;
    }

    public void addNewMark(Integer newMark, final String teamName) {
        logger.info("add mark: ", newMark);
        if (newMark == null) return;
        try {
            Text scoreText = (Text) (stage.getScene().lookup("#scoreText".concat(teamName)));
            Text currentScoreText = (Text) (stage.getScene().lookup("#currentScoreText".concat(teamName)));
            Text previousScoreText = (Text) (stage.getScene().lookup("#previousScoreText".concat(teamName)));
            Text oldScoreText = (Text) (stage.getScene().lookup("#oldScoreText".concat(teamName)));
            final Pane pane = (Pane) (stage.getScene().lookup("#scoreBox".concat(teamName)));
            int current = Integer.valueOf(scoreText.getText());
            scoreText.setText(String.valueOf(current + newMark));
            oldScoreText.setText(previousScoreText.getText());
            previousScoreText.setText(currentScoreText.getText());
            currentScoreText.setText(String.valueOf(newMark));
//            Platform.runLater(() -> {
//                Border old = pane.getBorder();
//                pane.setBorder(new Border(new BorderStroke(Paint.valueOf("yellow"), BorderStrokeStyle.SOLID, null, null)));
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                pane.setBorder(old);
//            });
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void setWarningMessage(String message, String team) {
        try {
            Text text = (Text) (stage.getScene().lookup("#team".concat(team)));
            text.setFont(Font.font(12.0));
            text.setText(message);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            ex.printStackTrace();
        }
    }

    public Map<String, Controller> getControllerList() {
        return controllerList;
    }

    public void setControllerList(Map<String, Controller> controllerList) {
        this.controllerList = controllerList;
    }

    public Controller getControllerOne() {
        return controllerOne;
    }

    public void setControllerOne(Controller controllerOne) {
        this.controllerOne = controllerOne;
    }

    public Controller getControllerTwo() {
        return controllerTwo;
    }

    public void setControllerTwo(Controller controllerTwo) {
        this.controllerTwo = controllerTwo;
    }
}
