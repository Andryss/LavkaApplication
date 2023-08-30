package ru.andryss.lavka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class LavkaApplication {

    public static void main(String[] args) {
        SpringApplication.run(LavkaApplication.class, args);
    }

}
