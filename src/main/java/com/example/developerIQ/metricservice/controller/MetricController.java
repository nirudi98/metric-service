package com.example.developerIQ.metricservice.controller;

import com.example.developerIQ.metricservice.common.PreviousProductivity;
import com.example.developerIQ.metricservice.common.SprintDate;
import com.example.developerIQ.metricservice.common.productivityRequest.PreviousRequestBody;
import com.example.developerIQ.metricservice.service.MetricService;
import com.example.developerIQ.metricservice.service.ValidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/metrics")
public class MetricController {

    @Autowired
    private MetricService metricService;

    @Autowired
    private ValidateService validateService;

    @PostMapping(value = "/fetch/all-metrics")
    public ResponseEntity<String> saveAllMetrics(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                 @RequestBody SprintDate sprintDates){
        String token = authorizationHeader;
        String start = sprintDates.getGivenStartDate();
        String end = sprintDates.getGivenEndDate();

        String git = sprintDates.getToken();

        if(token!= null && token.startsWith("Bearer ")){
            token = token.substring(7);
        }
        if(validateService.validateToken(token)){
            return metricService.saveAllMetrics(start, end, git);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("validation failed");
    }

    @PostMapping(value = "/pulls")
    public ResponseEntity<String> savePRMetrics(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                @RequestBody SprintDate sprintDates){
        String token = authorizationHeader;
        String start = sprintDates.getGivenStartDate();
        String git = sprintDates.getToken();
        String end = sprintDates.getGivenEndDate();

        if(token!= null && token.startsWith("Bearer ")){
            token = token.substring(7);
        }
        if(validateService.validateToken(token)){
            return metricService.savePullRequests(start, end, git);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("validation failed");
    }

    @PostMapping(value = "/commits")
    public ResponseEntity<String> saveCommitsMetrics(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                     @RequestBody SprintDate sprintDates){
        String token = authorizationHeader;

        String start = sprintDates.getGivenStartDate();
        String git = sprintDates.getToken();
        String end = sprintDates.getGivenEndDate();

        if(token!= null && token.startsWith("Bearer ")){
            token = token.substring(7);
        }
        if(validateService.validateToken(token)){
            return metricService.saveCommits(start, end, git);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("validation failed");
    }

    @PostMapping(value = "/issues")
    public ResponseEntity<String> saveIssuesMetrics(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                    @RequestBody SprintDate sprintDates){
        String token = authorizationHeader;

        String start = sprintDates.getGivenStartDate();
        String git = sprintDates.getToken();
        String end = sprintDates.getGivenEndDate();

        if(token!= null && token.startsWith("Bearer ")){
            token = token.substring(7);
        }
        if(validateService.validateToken(token)){
            return metricService.saveIssues(start, end, git);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("validation failed");
    }

    @PostMapping(value = "/fetch/previous-sprint-stats")
    public ResponseEntity<PreviousProductivity> fetchPreviousSprintStats(@RequestBody PreviousRequestBody body,
                                                                         @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){
        String token = authorizationHeader;

        if(token!= null && token.startsWith("Bearer ")){
            token = token.substring(7);
        }
        if(validateService.validateToken(token)){
            return ResponseEntity.ok(metricService.fetchPrevious(body));
        }
        PreviousProductivity previousProductivity = new PreviousProductivity();
        return ResponseEntity.ok(previousProductivity);
    }
}
