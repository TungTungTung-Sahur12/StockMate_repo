package com.stockmate.stockmate_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UpdateStatusRequest {

    @JsonProperty("isActive")
    private boolean isActive;
}