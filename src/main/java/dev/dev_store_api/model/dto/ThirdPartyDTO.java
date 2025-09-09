package dev.dev_store_api.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
public class ThirdPartyDTO implements Serializable {
    private Long id;
    private String name;
    private String link;
    private Integer status;
}
