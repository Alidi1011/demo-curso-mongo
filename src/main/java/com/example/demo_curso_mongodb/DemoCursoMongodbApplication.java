package com.example.demo_curso_mongodb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class DemoCursoMongodbApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoCursoMongodbApplication.class, args);
    }

}
