package com.linkedlnsummary.LinkTLDR.service;

import com.linkedlnsummary.LinkTLDR.dto.SummaryResponseDTO;
import com.linkedlnsummary.LinkTLDR.exception.PostContentException;

public interface SummaryService {

    SummaryResponseDTO generateSummary(String url) throws PostContentException;
}
