package com.tellyouiam.alittlebitaboutspring.example;

import com.tellyouiam.alittlebitaboutspring.dto.filestorage.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
        FileStorageProperties.class
})
public class FileDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(FileStorageProperties.class, args);
    }
}
