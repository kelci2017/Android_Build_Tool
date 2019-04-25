package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import util.PropertiesConfig;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        PropertiesConfig.initConfiguration();
        SpringApplication.run(Application.class, args);
    }
}
