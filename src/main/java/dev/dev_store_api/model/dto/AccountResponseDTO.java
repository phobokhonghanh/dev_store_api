package dev.dev_store_api.model.dto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

@ToString
@Getter
@Setter
public class AccountResponseDTO implements Serializable {
    Long id;
    String username;
    String email;
    String fullName;
    String avatar;
    String status;
    ThirdPartyDTO thirdParty;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
