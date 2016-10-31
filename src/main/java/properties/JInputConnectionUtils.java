package properties;


import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;


public final class JInputConnectionUtils {
    static final Logger logger = LoggerFactory.getLogger(JInputConnectionUtils.class);

    private JInputConnectionUtils() {
    }

    /**
     * Search (and save) for controllers of type Controller.Type.???.
     */
    public static Map<String, Controller> searchForControllers(String dllPath) {
        logger.info("Scan system controllers.");
        Map<String, Controller> foundControllers = new HashMap<>();
        try {
            addDir(dllPath);
            Controller[] controllers = createDefaultEnvironment().getControllers();

            for (int i = 0; i < controllers.length; i++) {
                Controller controller = controllers[i];
                if (controller.getType() == Controller.Type.STICK ||
                        controller.getType() == Controller.Type.GAMEPAD ||
                        controller.getType() == Controller.Type.WHEEL ||
                        controller.getType() == Controller.Type.FINGERSTICK) {
                    foundControllers.put(controller.getName(), controller);
                }
            }
        } catch (ReflectiveOperationException ex) {
            logger.warn("Failed to scan system controllers cause: {}", ex.getLocalizedMessage());
        }
        logger.info("Scan done. Controllers found: {}", foundControllers);
        return foundControllers;
    }

    //TODO delete this
    public static void initInfo(String dllPath) {
        logger.info("Scan system controllers.");
        try {
            addDir(dllPath);
            Controller[] ca = createDefaultEnvironment().getControllers();
            for (int i = 0; i < ca.length; i++) {
                logger.info(ca[i].getName());
            }
            logger.info("Scan done.");
        } catch (ReflectiveOperationException ex) {
            logger.warn("Failed to scan system controllers cause: {}", ex.getLocalizedMessage());
        }
    }

    private static void addDir(String dirName) {
        try {
            // This enables the java.library.path to be modified at runtime
            // From a Sun engineer at http://forums.sun.com/thread.jspa?threadID=707176
            Field field = ClassLoader.class.getDeclaredField("usr_paths");
            field.setAccessible(true);
            String[] paths = (String[]) field.get(null);
            for (int i = 0; i < paths.length; i++) {
                if (dirName.equals(paths[i])) {
                    return;
                }
            }
            String[] tmp = new String[paths.length + 1];
            System.arraycopy(paths, 0, tmp, 0, paths.length);
            tmp[paths.length] = dirName;
            field.set(null, tmp);
            System.setProperty("java.library.path", System.getProperty("java.library.path") + File.pathSeparator + dirName);
        } catch (IllegalAccessException e) {
            logger.warn("Failed to get permissions to set library path");
        } catch (NoSuchFieldException e) {
            logger.warn("Failed to get field handle to set library path");
        }
    }

    private static ControllerEnvironment createDefaultEnvironment() throws ReflectiveOperationException {
        // Find constructor (class is package private, so we can't access it directly)
        Constructor<ControllerEnvironment> constructor = (Constructor<ControllerEnvironment>)
                Class.forName("net.java.games.input.DefaultControllerEnvironment").getDeclaredConstructors()[0];
        // Constructor is package private, so we have to deactivate access control checks
        constructor.setAccessible(true);
        // Create object with default constructor
        return constructor.newInstance();
    }

}
