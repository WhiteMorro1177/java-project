package org.alt.project;

import org.alt.project.repository.ScanResultRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableJpaRepositories
@EnableWebMvc
@SpringBootApplication
public class ProjectApplication {
    static ScanResultRepository repository;

    public static void main(String[] args) {
        SpringApplication.run(ProjectApplication.class, args); // start application
    }

    @Bean
    public CommandLineRunner init(ScanResultRepository rep) {
        repository = rep;
        return args -> {  };
    }
}
