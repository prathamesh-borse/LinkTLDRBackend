package com.linkedlnsummary.LinkTLDR.serviceImpl;

import com.linkedlnsummary.LinkTLDR.dto.SummaryResponseDTO;
import com.linkedlnsummary.LinkTLDR.exception.PostContentException;
import com.linkedlnsummary.LinkTLDR.service.AISummaryService;
import com.linkedlnsummary.LinkTLDR.service.LinkedInPostScrapperService;
import com.linkedlnsummary.LinkTLDR.service.SummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SummaryServiceImpl implements SummaryService {

    @Autowired
    private LinkedInPostScrapperService linkedInPostScrapperService;

    @Autowired
    private AISummaryService aiSummaryService;

    @Override
    public SummaryResponseDTO generateSummary(String url) throws PostContentException {
        String postText = linkedInPostScrapperService.scrapePost(url);
        String summarizePost = aiSummaryService.summarizePost(postText);
        return new SummaryResponseDTO(summarizePost);
    }
}
