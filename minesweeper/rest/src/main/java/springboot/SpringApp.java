package springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This class is used to start the Spring Boot application.
 * When the application is started, the Spring Boot application
 * will automatically start the server.
 */
@SpringBootApplication
public class SpringApp {

  public static void main(String[] args) {
    SpringApplication.run(SpringApp.class, args);
  }
}