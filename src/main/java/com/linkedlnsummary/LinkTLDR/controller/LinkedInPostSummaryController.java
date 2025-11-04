package com.linkedlnsummary.LinkTLDR.controller;

import com.linkedlnsummary.LinkTLDR.dto.SummaryResponseDTO;
import com.linkedlnsummary.LinkTLDR.dto.UrlRequest;
import com.linkedlnsummary.LinkTLDR.exception.PostContentException;
import com.linkedlnsummary.LinkTLDR.service.SummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/summary")
public class LinkedInPostSummaryController {

    @Autowired
    private SummaryService summaryService;

    @PostMapping("/generate")
    public ResponseEntity<SummaryResponseDTO> generateSummary(@RequestBody UrlRequest urlRequest) throws PostContentException {
        return ResponseEntity.ok(summaryService.generateSummary(urlRequest.url()));
    }
}
