package properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@PropertySource("/app.properties")
public class PropertiesHolder {

    @Autowired
    private Environment environment;

    public <T> T get(String key, Class<T> expectedPropertyClass) {
        return environment.getProperty(key, expectedPropertyClass);
    }

    public String get(String key) {
        return environment.getProperty(key);
    }

}
	
