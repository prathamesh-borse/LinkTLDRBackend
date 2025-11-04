package com.linkedlnsummary.LinkTLDR.service;

import com.linkedlnsummary.LinkTLDR.exception.PostContentException;

public interface LinkedInPostScrapperService {
    String scrapePost(String url) throws PostContentException;
}
