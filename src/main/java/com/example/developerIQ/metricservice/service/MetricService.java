package com.example.developerIQ.metricservice.service;

import com.example.developerIQ.metricservice.common.OutdatedRequestBody;
import com.example.developerIQ.metricservice.common.PreviousProductivity;
import com.example.developerIQ.metricservice.common.productivityRequest.PreviousRequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface MetricService {
    public ResponseEntity<String> saveAllMetrics(String start, String end, String git);
    public ResponseEntity<String> saveIssues(String start, String end, String git);
    public ResponseEntity<String> saveCommits(String start, String end, String git);
    public ResponseEntity<String> savePullRequests(String start, String end, String git);
    public PreviousProductivity fetchPrevious(PreviousRequestBody body);
}
