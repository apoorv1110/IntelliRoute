package com.intelliroute.service;

import com.intelliroute.dto.EngineerRequest;
import com.intelliroute.model.Engineer;
import com.intelliroute.repository.EngineerRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EngineerService {

    private final EngineerRepository engineerRepository;

    public Engineer createEngineer(EngineerRequest request) {
        Engineer engineer = Engineer.builder()
                .name(request.getName())
                .designation(request.getDesignation())
                .capacity(request.getCapacity())
                .available(request.isAvailable())
                .skills(request.getSkills())
                .timezone(request.getTimezone())
                .build();
        return engineerRepository.save(engineer);
    }

    public List<Engineer> listEngineers() {
        return engineerRepository.findAll();
    }
}

