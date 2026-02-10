package com.intelliroute.controller;

import com.intelliroute.model.Assignment;
import com.intelliroute.repository.AssignmentRepository;
import com.intelliroute.service.AssignmentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final AssignmentRepository assignmentRepository;

    @PostMapping("/run")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void triggerAssignmentCycle() {
        assignmentService.runAssignmentCycle();
    }

    @GetMapping
    public List<Assignment> listAssignments() {
        return assignmentRepository.findAll();
    }
}

