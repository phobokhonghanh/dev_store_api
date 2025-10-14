package dev.dev_store_api.model.dto.response;
import dev.dev_store_api.model.type.EProvider;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

@ToString
@Getter
@Setter
public class AccountResponse implements Serializable {
    Long id;
    String username;
    String email;
    String fullName;
    String avatar;
    String status;
    EProvider authProvider;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
