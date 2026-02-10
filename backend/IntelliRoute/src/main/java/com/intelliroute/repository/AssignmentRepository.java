package com.intelliroute.repository;

import com.intelliroute.model.Assignment;
import com.intelliroute.model.AssignmentStatus;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AssignmentRepository extends MongoRepository<Assignment, String> {
    List<Assignment> findByEngineerIdAndStatus(String engineerId, AssignmentStatus status);

    List<Assignment> findByQueryId(String queryId);
}

