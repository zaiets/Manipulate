package input;

import model.Team;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import properties.PropertiesHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class JoystickEventReader {
    static final Logger logger = LoggerFactory.getLogger(JoystickEventReader.class);

    @Autowired
    private PropertiesHolder propertiesHolder;

    private final ExecutorService service = Executors.newSingleThreadExecutor();

    private Controller controller;
    private Team team;

    private int result = -1;
    private boolean isStopRequested = false;
    private final List<JoystickEventListener> listeners = new ArrayList<>();

    public void startWatch() {
        service.submit(new Runnable() {
            @Override
            public void run() {
                do {
                    if (!controller.poll()) {
                        //TODO catch error or else
                        logger.error("No joystick is responding here.");
                        throw new Error();
                    }
                    Component[] components = controller.getComponents();
                    for (int i = 0; i < components.length; i++) {
                        Component component = components[i];
                        Component.Identifier componentIdentifier = component.getIdentifier();
                        if (componentIdentifier.getName().matches("^[0-9]*$")) {
                            // If the component identifier name contains only numbers, then this is a button.
                            // Is button pressed?
                            if (component.getPollData() != 0.0f) {
                                logger.info("Button pressed: {}, identifier is: {}", component.getName(), componentIdentifier.getName());
                                try {
                                    result = Integer.valueOf(componentIdentifier.getName());
                                } catch (Exception ex) {
                                    logger.error("Can't define value of: {}", componentIdentifier.getName());
                                }
                                fireResultChangeEvent();
                                //pause of  current thread after new value detected
                                try {
                                    Thread.sleep(propertiesHolder.get("joystick.sensitivity", Integer.class));
                                } catch (InterruptedException e) {
                                    logger.error(e.getLocalizedMessage());
                                }
                            }
                        }
                    }
                } while (!isStopRequested);
            }
            private void fireResultChangeEvent() {
                listeners.forEach(JoystickEventListener::onReadingChange);
            }
        });
    }


    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Controller getController() {
        return controller;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public int getResult() {
        return result;
    }

    public void shutdown() {
        isStopRequested = true;
        service.shutdown();
    }

    public void addListener(JoystickEventListener listener) {
        if (listener != null) listeners.add(listener);
    }
}
