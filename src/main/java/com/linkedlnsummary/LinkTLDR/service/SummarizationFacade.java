package com.linkedlnsummary.LinkTLDR.service;

import com.linkedlnsummary.LinkTLDR.dto.ErrorResponseDTO;
import com.linkedlnsummary.LinkTLDR.dto.SummaryRequestDTO;
import com.linkedlnsummary.LinkTLDR.dto.SummaryResponseDTO;
import com.linkedlnsummary.LinkTLDR.dto.SummaryTextRequestDTO;
import com.linkedlnsummary.LinkTLDR.exception.PostContentException;
import org.springframework.http.ResponseEntity;

public interface SummarizationFacade {

    SummaryResponseDTO generateSummaryFromUrl(String url) throws PostContentException;

    SummaryResponseDTO generateSummaryFromText(String text);
}
