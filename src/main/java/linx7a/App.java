package linx7a;

import linx7a.config.AppConfig;
import linx7a.console.ConsoleListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {
    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            ConsoleListener consoleListener = context.getBean(ConsoleListener.class);
            consoleListener.start();
        }
    }
}