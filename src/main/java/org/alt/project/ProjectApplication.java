package org.alt.project;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.alt.project.http.ScanService;
import org.alt.project.repository.ScanResultRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

import java.util.concurrent.Executors;

@EnableJpaRepositories
@EnableWebMvc
@SpringBootApplication
public class ProjectApplication {
    static ScanResultRepository repository;


    public static void main(String[] args) {
        SpringApplication.run(ProjectApplication.class, args); // start application
        try {
            // Scanner sc = new Scanner(System.in); // temporary input system
            // System.out.print("Path or ID: ");
            // String command = sc.nextLine();

            Gson gson = new GsonBuilder().setLenient().create(); // create gson converter with .setLenient()
            RestAdapter adapter = new RestAdapter.Builder() // calls to API
                    .setEndpoint("http://localhost:8080/")
                    .setConverter(new GsonConverter(gson))
                    .build();

            ScanService service = adapter.create(ScanService.class); // service, that create calls

            // while button onClick()
            final String textInField = "C:\\Users\\jackf";  // textField.get()

            var exe = Executors.newSingleThreadExecutor().submit(() -> service.SetTask(textInField));

            if (exe.get()) {
                System.out.println("New task set");
            }

            /*
            while (!command.equals("\n")) {
                if (command.startsWith("scan")) {
                    command = command.replace("scan", "").trim();

                    var exe = Executors.newSingleThreadExecutor()
                            .submit(() -> {
                                return service.SetTask(command);
                                System.out.println("New task set");
                            });

                } else if (command.startsWith("select")) {
                    command = command.replace("select", "").trim();
                    Thread new_thread = new Thread(ProjectApplication::Select);
                    new_thread.setName(command);
                    new_thread.start();
                }
                System.out.print("\nPath or ID: ");
                command = sc.nextLine();
            }
            */

        } catch (Exception e) {
            System.out.println("Exception in Main: " + e.getMessage());
        }
    }

    @Bean
    public CommandLineRunner init(ScanResultRepository rep) {
        repository = rep;
        return args -> {  };
    }
}
