package dev.dev_store_api.config;

import lombok.Getter;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Getter
public class LoadConfig {
    private String configPath;

    public LoadConfig(String configPath) {
        this.configPath = configPath;
    }

    public LoadConfig() {
        this.configPath = "private.conf";
    }

    public void loadPrivateConfig() {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(this.getConfigPath())) {
            properties.load(fis);
            properties.forEach((key, value) -> System.setProperty(key.toString(), value.toString()));
        } catch (IOException e) {
            throw new RuntimeException(String.format("Error load file config %s" , this.getConfigPath()), e);
        }
    }
}
