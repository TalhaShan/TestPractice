package FoodKart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LldsApplication {

    public static void main(String[] args) {
      //  SpringApplication.run(LldsApplication.class, args);
        SpringApplication app = new SpringApplication(LldsApplication.class);

        // Hardcode the active profile(s) here
        app.setAdditionalProfiles("foodkart");

        app.run(args);
    }

}
