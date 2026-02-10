package com.intelliroute.dto;

import com.intelliroute.model.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class CreateQueryRequest {

    @NotBlank
    private String description;

    @NotNull
    private Priority priority = Priority.P3;

    private List<String> tags = List.of();

    private String domain;

    private LocalDateTime slaDueAt;
}

