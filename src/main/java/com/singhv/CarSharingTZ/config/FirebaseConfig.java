package com.singhv.CarSharingTZ.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Configuration
public class FirebaseConfig {

    @Autowired
    private Environment environment;

    @PostConstruct
    public void init() throws IOException {
        String databaseUrl = environment.getProperty("firebase.database.url");
        String configPath = environment.getProperty("firebase.config.path");
        log.info("databaseUrl-->" + databaseUrl);

        // Initialize Firebase only if it's not already initialized
        if (FirebaseApp.getApps().isEmpty()) {
            InputStream serviceAccount;

            if (configPath.startsWith("classpath:")) {
                // Load from classpath
                String resourcePath = configPath.substring("classpath:".length());
                serviceAccount = getClass().getClassLoader().getResourceAsStream(resourcePath);
            } else {
                // Load from file system
                serviceAccount = new FileInputStream(configPath);
            }

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl(databaseUrl)
                    .build();
            FirebaseApp.initializeApp(options);
        }
    }

    @Bean
    public DatabaseReference firebaseDatabase() {
        return FirebaseDatabase.getInstance().getReference();
    }
}