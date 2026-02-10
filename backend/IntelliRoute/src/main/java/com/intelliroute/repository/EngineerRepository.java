package com.intelliroute.repository;

import com.intelliroute.model.Engineer;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EngineerRepository extends MongoRepository<Engineer, String> {
    List<Engineer> findByAvailableTrue();
}

