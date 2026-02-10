package com.intelliroute.model;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "queries")
public class SupportQuery {

    @Id
    private String id;

    @NotBlank
    private String description;

    private Double complexityScore;

    @Builder.Default
    private QueryStatus status = QueryStatus.PENDING;

    @Builder.Default
    private Priority priority = Priority.P3;

    @Builder.Default
    private List<String> tags = List.of();

    private String domain;

    @Field("created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Field("updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Field("sla_due_at")
    private LocalDateTime slaDueAt;
}

