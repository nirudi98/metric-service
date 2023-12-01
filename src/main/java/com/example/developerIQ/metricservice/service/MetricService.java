package com.example.developerIQ.metricservice.service;

import com.example.developerIQ.metricservice.common.OutdatedRequestBody;
import com.example.developerIQ.metricservice.common.PreviousProductivity;
import com.example.developerIQ.metricservice.common.productivityRequest.PreviousRequestBody;
import org.springframework.stereotype.Service;

@Service
public interface MetricService {
    public String saveAllMetrics();
    public String saveIssues();
    public String saveCommits();
    public String savePullRequests();
    public PreviousProductivity fetchPrevious(PreviousRequestBody body);
}
