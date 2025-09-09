package dev.dev_store_api;

import dev.dev_store_api.config.LoadConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DevStoreApiApplication {

    public static void main(String[] args) {
        new LoadConfig().loadPrivateConfig();
        SpringApplication.run(DevStoreApiApplication.class, args);
    }

}
