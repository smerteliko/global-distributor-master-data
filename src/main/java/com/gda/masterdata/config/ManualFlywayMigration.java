package com.gda.masterdata.config; // <--- БЫЛО com.globaldistributor...

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class ManualFlywayMigration {

    private final DataSource dataSource;

    @PostConstruct
    public void forceMigration() {
        try {
            Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .load();

            flyway.migrate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}