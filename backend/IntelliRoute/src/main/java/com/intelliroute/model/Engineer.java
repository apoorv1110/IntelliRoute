package com.intelliroute.model;

import java.time.LocalDateTime;
import java.util.Set;
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
@Document(collection = "engineers")
public class Engineer {

    @Id
    private String id;

    private String name;

    private Designation designation;

    private int capacity;

    @Builder.Default
    private int currentLoad = 0;

    @Builder.Default
    private boolean available = true;

    @Builder.Default
    private Set<String> skills = Set.of();

    private String timezone;

    @Field("created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}

