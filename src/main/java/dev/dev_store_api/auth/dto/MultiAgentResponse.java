package dev.dev_store_api.auth.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class MultiAgentResponse {
    private Long id;
    private String agent;
    private String ipAddress;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
