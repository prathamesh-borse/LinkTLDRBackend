package com.linkedlnsummary.LinkTLDR.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

public final class UrlValidator {

    private static final Pattern LINKEDIN_PATTERN = Pattern.compile(
            "^(https?://)?([\\w.-]+\\.)?linkedin\\.com/.+(/posts/|/feed/update/|activity-\\d+).*$",
            Pattern.CASE_INSENSITIVE
    );

    private UrlValidator() {
    }

    public static boolean isValidLinkedInPost(String url) {
        if (url == null || url.isBlank()) return false;
        try {
            URI uri = new URI(url.trim());
            if (uri.getHost() == null) return false;
            String host = uri.getHost().toLowerCase();
            boolean hostOk = host.endsWith("linkedin.com");
            boolean pathOk = LINKEDIN_PATTERN.matcher(url).matches();
            return hostOk && pathOk;
        } catch (URISyntaxException e) {
            return false;
        }
    }
}