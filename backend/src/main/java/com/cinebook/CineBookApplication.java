package com.cinebook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * CineBook Web - Main Application Entrypoint.
 * 
 * OOP & System Architecture:
 * - Employs Spring Boot's dependency injection container (IoC) to wire
 *   stateless, thread-safe service and DAO singletons.
 * - Enables background scheduling (@EnableScheduling) to coordinate
 *   asynchronous seat hold releases alongside database-level triggers/events.
 */
@SpringBootApplication
@EnableScheduling
public class CineBookApplication {

    public static void main(String[] args) {
        SpringApplication.run(CineBookApplication.class, args);
    }
}

