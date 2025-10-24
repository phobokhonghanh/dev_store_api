package dev.dev_store_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class DevStoreApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(DevStoreApiApplication.class, args);
    }

}
