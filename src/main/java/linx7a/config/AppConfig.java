package linx7a.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("linx7a")
@PropertySource("classpath:application.properties")
public class AppConfig {
}
