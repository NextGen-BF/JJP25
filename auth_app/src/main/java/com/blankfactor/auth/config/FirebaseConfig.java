package com.blankfactor.auth.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

@Configuration
public class FirebaseConfig {

    private static final String FIREBASE_CONFIG = "firebase-service-account.json";

    private static final Path path = FileSystems.getDefault().getPath(
            "src",
            "main",
            "resources",
            FIREBASE_CONFIG
    );

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        FileInputStream serviceAccount = new FileInputStream(path.toFile());

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        return FirebaseApp.initializeApp(options);
    }

}

