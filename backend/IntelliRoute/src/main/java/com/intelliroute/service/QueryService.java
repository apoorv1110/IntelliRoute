package com.intelliroute.service;

import com.intelliroute.dto.CreateQueryRequest;
import com.intelliroute.model.QueryStatus;
import com.intelliroute.model.SupportQuery;
import com.intelliroute.repository.SupportQueryRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueryService {

    private final SupportQueryRepository supportQueryRepository;

    public SupportQuery createQuery(CreateQueryRequest request) {
        SupportQuery query = SupportQuery.builder()
                .description(request.getDescription())
                .priority(request.getPriority())
                .tags(request.getTags())
                .domain(request.getDomain())
                .slaDueAt(request.getSlaDueAt())
                .build();
        return supportQueryRepository.save(query);
    }

    public List<SupportQuery> listAll() {
        return supportQueryRepository.findAll();
    }

    public List<SupportQuery> findPending() {
        return supportQueryRepository.findByStatusOrderByCreatedAtAsc(QueryStatus.PENDING);
    }

    public List<SupportQuery> findPastSla() {
        return supportQueryRepository.findByStatusAndSlaDueAtBefore(QueryStatus.PENDING, LocalDateTime.now());
    }

    public Optional<SupportQuery> findById(String id) {
        return supportQueryRepository.findById(id);
    }

    public SupportQuery save(SupportQuery query) {
        query.setUpdatedAt(LocalDateTime.now());
        return supportQueryRepository.save(query);
    }
}

