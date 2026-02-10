package com.intelliroute.repository;

import com.intelliroute.model.Priority;
import com.intelliroute.model.QueryStatus;
import com.intelliroute.model.SupportQuery;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SupportQueryRepository extends MongoRepository<SupportQuery, String> {
    List<SupportQuery> findByStatusOrderByCreatedAtAsc(QueryStatus status);

    List<SupportQuery> findByStatusAndSlaDueAtBefore(QueryStatus status, LocalDateTime time);

    List<SupportQuery> findByPriorityOrderByCreatedAtAsc(Priority priority);
}

