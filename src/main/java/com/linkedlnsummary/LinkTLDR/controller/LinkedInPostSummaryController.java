package com.linkedlnsummary.LinkTLDR.controller;

import com.linkedlnsummary.LinkTLDR.dto.*;
import com.linkedlnsummary.LinkTLDR.exception.PostContentException;
import com.linkedlnsummary.LinkTLDR.service.SummarizationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/summary")
public class LinkedInPostSummaryController {

    private final SummarizationFacade summarizationFacade;

    public LinkedInPostSummaryController(SummarizationFacade summarizationFacade) {
        this.summarizationFacade = summarizationFacade;
    }

    @PostMapping("/generate")
    public ResponseEntity<SummaryResponseDTO> generateSummaryByUrl(@RequestBody SummaryRequestDTO req) throws PostContentException {
        return ResponseEntity.ok(summarizationFacade.generateSummaryFromUrl(req.url()));
    }

    @PostMapping("/generateText")
    public ResponseEntity<?> summarizeText(@RequestBody SummaryTextRequestDTO req) {
        if (req == null || req.text() == null || req.text().isBlank()) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO("invalid_input", "`text` cannot be empty."));
        }
        if (req.text().length() > 50_000) { // protect from very large payloads
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                    .body(new ErrorResponseDTO("text_too_large", "The text is too long. Limit to 50k characters."));
        }

        try {
            SummaryResponseDTO res = summarizationFacade.generateSummaryFromText(req.text());
            return ResponseEntity.ok(res);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponseDTO("server_error", ex.getMessage()));
        }
    }
}
