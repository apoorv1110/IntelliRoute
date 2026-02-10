package com.intelliroute.controller;

import com.intelliroute.dto.EngineerRequest;
import com.intelliroute.model.Engineer;
import com.intelliroute.service.EngineerService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/engineers")
@RequiredArgsConstructor
public class EngineerController {

    private final EngineerService engineerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Engineer createEngineer(@Valid @RequestBody EngineerRequest request) {
        return engineerService.createEngineer(request);
    }

    @GetMapping
    public List<Engineer> listEngineers() {
        return engineerService.listEngineers();
    }
}

