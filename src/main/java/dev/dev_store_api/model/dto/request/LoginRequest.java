package dev.dev_store_api.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Username or Email cannot be empty")
    @Size(min = 3, max = 100, message = "Identifier must be between 3 and 100 characters")
    private String identifier;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 3, max = 50, message = "Password must be between 3 and 50 characters")
    private String password;
}
