package com.intelliroute.controller;

import com.intelliroute.dto.CreateQueryRequest;
import com.intelliroute.model.SupportQuery;
import com.intelliroute.service.QueryService;
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
@RequestMapping("/api/queries")
@RequiredArgsConstructor
public class QueryController {

    private final QueryService queryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SupportQuery createQuery(@Valid @RequestBody CreateQueryRequest request) {
        return queryService.createQuery(request);
    }

    @GetMapping
    public List<SupportQuery> listQueries() {
        return queryService.listAll();
    }
}

