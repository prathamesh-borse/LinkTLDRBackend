package com.linkedlnsummary.LinkTLDR.serviceImpl;

import com.linkedlnsummary.LinkTLDR.dto.ErrorResponseDTO;
import com.linkedlnsummary.LinkTLDR.dto.SummaryRequestDTO;
import com.linkedlnsummary.LinkTLDR.dto.SummaryResponseDTO;
import com.linkedlnsummary.LinkTLDR.dto.SummaryTextRequestDTO;
import com.linkedlnsummary.LinkTLDR.exception.PostContentException;
import com.linkedlnsummary.LinkTLDR.service.AISummaryService;
import com.linkedlnsummary.LinkTLDR.service.LinkedInPostScrapperService;
import com.linkedlnsummary.LinkTLDR.service.SummarizationFacade;
import com.linkedlnsummary.LinkTLDR.utils.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class SummarizationFacadeImpl implements SummarizationFacade {

    @Autowired
    private LinkedInPostScrapperService linkedInPostScrapperService;

    @Autowired
    private AISummaryService aiSummaryService;

//    @Autowired
//    private SummaryCacheService summaryCache; // optional


    // From URL (existing flow) with caching
    public SummaryResponseDTO generateSummaryFromUrl(String url) throws PostContentException {
        // Attempt cache first
//        SummaryResponseDTO cached = summaryCache.getIfPresent(url);
//        if (cached != null) {
//            return new SummaryResponseDTO(cached.summary(), "url", true);
//        }

        // Scrape
        String postText = linkedInPostScrapperService.scrapePost(url);
        if (postText == null || postText.isBlank()) {
            throw new PostContentException("No text found on target LinkedIn post.");
        }

        // Summarize
        String summarizePost = aiSummaryService.summarizePost(postText);

        SummaryResponseDTO dto = new SummaryResponseDTO(summarizePost, "url", false);
//        summaryCache.put(url, dto);
        return dto;
    }

    // From raw text (extension fallback)
    public SummaryResponseDTO generateSummaryFromText(String text) {
        // Optionally create a cache key from hash(text)
        String summary = aiSummaryService.summarizePost(text);
        return new SummaryResponseDTO(summary, "text", false);
    }
}
