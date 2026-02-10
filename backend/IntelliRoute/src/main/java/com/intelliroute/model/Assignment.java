package com.intelliroute.model;

import java.time.LocalDateTime;
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
@Document(collection = "assignments")
public class Assignment {

    @Id
    private String id;

    private String engineerId;

    private String queryId;

    @Builder.Default
    private double allocationPercent = 1.0;

    @Builder.Default
    private AssignmentStatus status = AssignmentStatus.ACTIVE;

    @Field("assigned_at")
    @Builder.Default
    private LocalDateTime assignedAt = LocalDateTime.now();
}

