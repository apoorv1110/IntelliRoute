package com.intelliroute.dto;

import com.intelliroute.model.Designation;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.Data;

@Data
public class EngineerRequest {

    @NotBlank
    private String name;

    @NotNull
    private Designation designation;

    @Min(1)
    private int capacity = 1;

    private boolean available = true;

    private Set<String> skills = Set.of();

    private String timezone;
}

