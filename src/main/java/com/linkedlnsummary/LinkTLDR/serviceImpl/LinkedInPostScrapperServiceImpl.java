package com.linkedlnsummary.LinkTLDR.serviceImpl;

import com.linkedlnsummary.LinkTLDR.exception.PostContentException;
import com.linkedlnsummary.LinkTLDR.service.LinkedInPostScrapperService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class LinkedInPostScrapperServiceImpl implements LinkedInPostScrapperService {

    @Override
    public String scrapePost(String url) throws PostContentException {
        try {
            Document document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.")
                    .get();
            return document.select("meta[property=og:description]").attr("content");
        } catch (IOException e) {
            throw new PostContentException("Failed to fetch content");
        }
    }
}
