package app;

import config.AppConfig;
import controller.MainViewController;
import input.JoystickEventReader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import properties.JInputConnectionUtils;
import properties.PropertiesHolder;

@Configuration
@Import(AppConfig.class)
public class MainApp extends Application {
    public static final String TITLE = "'Manipulate' app (by Zaiets A.Y. v.0.0.1a)";
    static final Logger logger = LoggerFactory.getLogger(MainApp.class);
    private MainViewController mainViewController;
    private JoystickEventReader joystickEventReaderRed;
    private JoystickEventReader joystickEventReaderBlue;

    private void formView(Stage stage) throws Exception {
        Parent parent = FXMLLoader.load(getClass().getResource("/mainView.fxml"));
        Scene scene = new Scene(parent);
        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setOnCloseRequest(event -> {
                       joystickEventReaderRed.shutdown();
                       joystickEventReaderBlue.shutdown();
        });
    }


    @Override
    public void start(Stage stage) throws Exception {
        formView(stage);
        logger.info("Register listeners and controllers");
        //init context
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        mainViewController = context.getBean(MainViewController.class);
        PropertiesHolder propertiesHolder = context.getBean(PropertiesHolder.class);
        joystickEventReaderRed = context.getBean(JoystickEventReader.class);
        joystickEventReaderBlue = context.getBean(JoystickEventReader.class);
        //analyse controllers
        String path = propertiesHolder.get("dll.address");
        logger.debug("Path with libs is {}", path);
        //start work
        mainViewController.setControllerList(JInputConnectionUtils.searchForControllers(path));
        mainViewController.setStage(stage);
        mainViewController.show();
        if (mainViewController.getControllerOne() != null) {
            joystickEventReaderRed.setController(mainViewController.getControllerOne());
            joystickEventReaderRed.setTeam(Team.RED);
            joystickEventReaderRed.addListener(() -> {
                appendNewJoystickData(joystickEventReaderRed);
            });
            logger.info("joystickEventRed.startWatch()");
            joystickEventReaderRed.startWatch();
        }
        if (mainViewController.getControllerTwo() != null) {
            joystickEventReaderBlue.setController(mainViewController.getControllerTwo());
            joystickEventReaderBlue.setTeam(Team.BLUE);
            joystickEventReaderBlue.addListener(() -> {
                appendNewJoystickData(joystickEventReaderBlue);
            });
            logger.info("joystickEventBlue.startWatch()");
            joystickEventReaderBlue.startWatch();
        }
    }

    private void appendNewJoystickData(JoystickEventReader joystickEventReader) {
        logger.info("Joystick {} action: {}", joystickEventReader.getTeam().getTeamName(), joystickEventReader.getResult());
        Platform.runLater(() -> mainViewController.addNewMark(joystickEventReader.getResult(), joystickEventReader.getTeam().getTeamName()));
    }


    public static void main(String[] args) {
        launch(args);
    }

}