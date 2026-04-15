package edu.iu.registration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.TimeZone;

@SpringBootApplication
public class RegistrationApplication {

    public static void main(String[] args) {
        // Force UTC before the JDBC driver opens any connections.
        // The PostgreSQL JDBC driver sends the JVM default timezone in the
        // startup packet; Alpine-based Postgres rejects "America/Indianapolis"
        // because it isn't in its tz database. UTC is always valid.
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(RegistrationApplication.class, args);
    }
}
