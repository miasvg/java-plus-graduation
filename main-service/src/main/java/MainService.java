import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {
        "com.example.mainservice",
        "ru.practicum.statserver"
})
@SpringBootApplication
public class MainService {
    public static void main(String[] args) {
        SpringApplication.run(MainService.class, args);
    }
}
