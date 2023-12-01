package com.example.developerIQ.metricservice.utils.constants;

public class Constants {
    public static final String PULL_REQUESTS = "https://api.github.com/repos/bitcoin/bitcoin/pulls";
    public static final String PULL_REQUESTS_MERGE_CHECK = "https://api.github.com/repos/bitcoin/bitcoin/pulls/{pull_number}/merge";
    public static final String COMMITS = "https://api.github.com/repos/bitcoin/bitcoin/commits";
    public static final String PRODUCTIVITY_SERVICE_PREVIOUS_SPRINT_URL = "/previous/sprint/display/overview";
    public static final String ISSUES = "https://api.github.com/repos/bitcoin/bitcoin/issues?state={state}";
    public static final String AUTHORIZATION = "Bearer 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";
    public static final String VERSION = "2022-11-28";
    public static final String HEADERS = "application/vnd.github+json";
}
