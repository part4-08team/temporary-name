package project.closet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ClosetApplication {

  public static void main(String[] args) {
    SpringApplication.run(ClosetApplication.class, args);
  }

}
