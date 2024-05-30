package example.com.moneymergebe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MoneymergeBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoneymergeBeApplication.class, args);
    }

}
