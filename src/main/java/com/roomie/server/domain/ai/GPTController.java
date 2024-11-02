package com.roomie.server.domain.ai;

import com.roomie.server.domain.roomie.dto.CompareRequestDto;
import com.roomie.server.domain.roomie.dto.CompareResponseDto;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.IOException;

@RestController
public class GPTController {

    @Autowired
    private GPTService gptService;

    @PostMapping("/compare-images")
    public CompareResponseDto compareImages(@RequestBody CompareRequestDto request) {
        try {
            return gptService.compareImages(request.getCompareType().getContent(), request.getBeforeUrl(), request.getAfterUrl());

        } catch (IOException e) {
            return new CompareResponseDto(0.0, "Error processing images: " + e.getMessage());
        }
    }
}

