package dev.dev_store_api.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
@Getter
public class LoginRequest {
    @NotBlank(message = "Username cannot be empty")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    String username;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 3, max = 100, message = "Password must be between 3 and 10 characters")
    String password;
}
