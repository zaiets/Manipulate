package config;

import controller.MainViewController;
import input.JoystickEventReader;
import org.springframework.context.annotation.*;
import properties.PropertiesHolder;


@Configuration
@ComponentScan(basePackages = "java.*")
@Import(PropertiesHolder.class)
public class AppConfig {

	@Bean
	protected MainViewController mainViewController() {
		return new MainViewController();
	}

	@Bean
	@Scope("prototype")
	protected JoystickEventReader joystickEventReader () {return new JoystickEventReader();}

}

