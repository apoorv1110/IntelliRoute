package com.intelliroute.service;

import com.intelliroute.model.Assignment;
import com.intelliroute.model.AssignmentStatus;
import com.intelliroute.model.Designation;
import com.intelliroute.model.Engineer;
import com.intelliroute.model.Priority;
import com.intelliroute.model.QueryStatus;
import com.intelliroute.model.SupportQuery;
import com.intelliroute.repository.AssignmentRepository;
import com.intelliroute.repository.EngineerRepository;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssignmentService {

    private final QueryService queryService;
    private final EngineerRepository engineerRepository;
    private final AssignmentRepository assignmentRepository;
    private final AIClient aiClient;

    @Scheduled(fixedDelayString = "${assignment.scheduler.delay-ms:5000}")
    public void runAssignmentCycle() {
        List<SupportQuery> pending = queryService.findPending();
        if (pending.isEmpty()) {
            return;
        }
        log.debug("Running assignment cycle for {} pending queries", pending.size());
        for (SupportQuery query : pending) {
            double complexity = query.getComplexityScore() != null
                    ? query.getComplexityScore()
                    : aiClient.predictComplexity(query.getDescription());
            query.setComplexityScore(complexity);

            Optional<Engineer> engineer = selectEngineer(query);
            if (engineer.isEmpty()) {
                query.setStatus(QueryStatus.ESCALATED);
                queryService.save(query);
                log.warn("No available engineer; escalated query {}", query.getId());
                continue;
            }
            assign(query, engineer.get());
        }
    }

    @Scheduled(fixedDelayString = "${assignment.sla.check-ms:60000}")
    public void escalateSlaBreaches() {
        List<SupportQuery> pastSla = queryService.findPastSla();
        for (SupportQuery query : pastSla) {
            query.setStatus(QueryStatus.ESCALATED);
            queryService.save(query);
            log.warn("Query {} breached SLA and has been escalated", query.getId());
        }
    }

    public Optional<Assignment> assign(SupportQuery query, Engineer engineer) {
        if (engineer.getCurrentLoad() >= engineer.getCapacity()) {
            log.warn("Engineer {} is at capacity; skipping assignment", engineer.getId());
            return Optional.empty();
        }

        query.setStatus(QueryStatus.ASSIGNED);
        queryService.save(query);

        engineer.setCurrentLoad(engineer.getCurrentLoad() + 1);
        engineerRepository.save(engineer);

        Assignment assignment = Assignment.builder()
                .engineerId(engineer.getId())
                .queryId(query.getId())
                .allocationPercent(1.0)
                .assignedAt(LocalDateTime.now())
                .status(AssignmentStatus.ACTIVE)
                .build();
        Assignment saved = assignmentRepository.save(assignment);
        log.info("Assigned query {} (score {}) to engineer {} ({})",
                query.getId(), query.getComplexityScore(), engineer.getId(), engineer.getDesignation());
        return Optional.of(saved);
    }

    private Optional<Engineer> selectEngineer(SupportQuery query) {
        List<Engineer> available = engineerRepository.findByAvailableTrue();
        if (available.isEmpty()) {
            return Optional.empty();
        }
        Designation target = targetDesignation(query.getComplexityScore());
        Set<String> queryTags = Set.copyOf(query.getTags() == null ? List.of() : query.getTags());

        return available.stream()
                .filter(e -> e.getCurrentLoad() < e.getCapacity())
                .max(Comparator.comparingDouble((Engineer e) ->
                        scoreEngineer(e, target, queryTags, query.getPriority(), query.getComplexityScore()))
                        .thenComparingInt(Engineer::getCurrentLoad))
                .map(Optional::of)
                .orElse(Optional.empty());
    }

    private double scoreEngineer(Engineer engineer, Designation target, Set<String> queryTags,
                                 Priority priority, double complexity) {
        int freeCapacity = engineer.getCapacity() - engineer.getCurrentLoad();
        int skillMatches = CollectionUtils.isEmpty(queryTags) || CollectionUtils.isEmpty(engineer.getSkills())
                ? 0
                : (int) engineer.getSkills().stream().filter(queryTags::contains).count();

        double designationFit = 0.0;
        if (engineer.getDesignation() == target) {
            designationFit = 3.0;
        } else if (isHigherSeniority(engineer.getDesignation(), target)) {
            designationFit = 2.0;
        } else {
            designationFit = -1.0;
        }

        double priorityBoost = priority == Priority.P1 ? 2.0 : priority == Priority.P2 ? 1.0 : 0.0;
        double complexityBuffer = complexity >= 3.6 && engineer.getDesignation() == Designation.TECH_LEAD ? 1.0 : 0.0;

        return skillMatches * 2.0 + freeCapacity + designationFit + priorityBoost + complexityBuffer;
    }

    private Designation targetDesignation(Double complexityScore) {
        if (complexityScore == null) {
            return Designation.MID;
        }
        if (complexityScore <= 2.0) {
            return Designation.JUNIOR;
        } else if (complexityScore <= 3.5) {
            return Designation.MID;
        } else {
            return Designation.SENIOR;
        }
    }

    private boolean isHigherSeniority(Designation candidate, Designation target) {
        return rank(candidate) > rank(target);
    }

    private int rank(Designation designation) {
        return switch (designation) {
            case JUNIOR -> 1;
            case MID -> 2;
            case SENIOR -> 3;
            case TECH_LEAD -> 4;
        };
    }
}

