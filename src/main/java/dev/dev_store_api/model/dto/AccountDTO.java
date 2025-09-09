package dev.dev_store_api.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class AccountDTO {
    @NotBlank(message = "Username cannot be empty")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    String username;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    String email;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 3, max = 100, message = "Password must be between 3 and 10 characters")
    String password;

    @NotBlank(message = "Full name cannot be empty")
    @Size(min = 3, max = 100, message = "Full name must be between 3 and 100 characters")
    String fullName;

    String avatar;

    @NotNull(message = "Third party cannot be null")
    Long thirdParty;
}
