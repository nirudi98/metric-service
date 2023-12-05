package com.example.developerIQ.metricservice.service.impl;

import com.example.developerIQ.metricservice.common.PreviousProductivity;
import com.example.developerIQ.metricservice.common.productivityRequest.PreviousRequestBody;
import com.example.developerIQ.metricservice.entity.CommitEntity;
import com.example.developerIQ.metricservice.entity.IssueEntity;
import com.example.developerIQ.metricservice.entity.PullRequestEntity;
import com.example.developerIQ.metricservice.repository.CommitRepository;
import com.example.developerIQ.metricservice.repository.IssueRepository;
import com.example.developerIQ.metricservice.repository.PullRequestRepository;
import com.example.developerIQ.metricservice.service.GithubService;
import com.example.developerIQ.metricservice.service.MetricService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.example.developerIQ.metricservice.utils.constants.Constants.PRODUCTIVITY_SERVICE_PREVIOUS_SPRINT_URL;

@Service
public class MetricServiceImpl implements MetricService {

    private static final Logger logger = LoggerFactory.getLogger(MetricServiceImpl.class);


    @Value("${productivity-service.url}")
    private String productivity_service_url;

    @Autowired
    private GithubService githubService;

    @Autowired
    private PullRequestRepository pullRequestRepository;

    @Autowired
    private CommitRepository commitRepository;

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public ResponseEntity<String> saveAllMetrics(String start, String end, String token_git) {
        try{
            LocalDateTime started = LocalDateTime.parse(start + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
            LocalDateTime ended = LocalDateTime.parse(end + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
            // fetch PR details
            List<PullRequestEntity> pullRequestEntityList = githubService.fetchPullRequests(started, ended, token_git);
            // fetch commit details
            List<CommitEntity> commitEntityList = githubService.fetchCommits(started, ended, token_git);
            // fetch open and closed issue details
            List<IssueEntity> openIssueEntityList = githubService.fetchOpenIssues(started, ended, token_git);
            List<IssueEntity> closeIssueEntityList = githubService.fetchCloseIssues(started, ended, token_git);

            // saving PR details
            if(pullRequestEntityList == null || pullRequestEntityList.isEmpty()) {
                throw new NullPointerException("final PR Entity list is null and saving failed");
            }
//            for(PullRequestEntity pr: pullRequestEntityList) {
//                pullRequestRepository.save(pr);
//            }
            for(PullRequestEntity pr: pullRequestEntityList) {
                // filter out PR belonging to specific date range
                if(pr.getCreated_date().isAfter(started) || pr.getCreated_date().isEqual(started) &&
                        pr.getCreated_date().isBefore(ended) || pr.getCreated_date().isEqual(ended)){
                    pullRequestRepository.save(pr);
                    continue;
                }
                logger.info("PR with id {} created by {} is not within the specified time range", pr.getId(), pr.getUsername());
            }
            // saving commit details
            if(commitEntityList == null || commitEntityList.isEmpty()) {
                throw new NullPointerException("final Commit Entity list is null and saving failed");
            }
            for(CommitEntity commit: commitEntityList) {
                if(commit.getCommitted_date().isAfter(started) || commit.getCommitted_date().isEqual(started) &&
                        commit.getCommitted_date().isBefore(ended) || commit.getCommitted_date().isEqual(ended)){
                    commitRepository.save(commit);
                    continue;
                }
                logger.info("Commit with id {} committed by {} is not within the specified time range", commit.getCommit_id(),
                        commit.getCommitter_name());
            }

            // saving issues details
            List<IssueEntity> allIssues = new ArrayList<>();
            allIssues.addAll(openIssueEntityList);
            allIssues.addAll(closeIssueEntityList);

            if(allIssues.isEmpty()) {
                throw new NullPointerException("final Issue Entity list is null and saving failed");
            }
            for(IssueEntity issue: allIssues) {
                if(issue.getCreated_date().isAfter(started) || issue.getCreated_date().isEqual(started) &&
                        issue.getCreated_date().isBefore(ended) || issue.getCreated_date().isEqual(ended)){
                    issueRepository.save(issue);
                    continue;
                }
                logger.info("Issue with id {} created by {} is not within the specified time range", issue.getIssue_id(),
                        issue.getUsername());
            }

            return ResponseEntity.ok("Pull Requests/ Commits/ Issues Saved Successfully");

        } catch(RuntimeException e){
            logger.error("pull requests/ commits/ issues save failed ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("pull requests/ commits/ issues save failed");

        }
    }

    @Override
    public ResponseEntity<String> savePullRequests(String start, String end, String token_git) {
        try{
            LocalDateTime started = LocalDateTime.parse(start + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
            LocalDateTime ended = LocalDateTime.parse(end + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));

            // fetch PR details
            List<PullRequestEntity> pullRequestEntityList = githubService.fetchPullRequests(started, ended, token_git);

            if(pullRequestEntityList == null || pullRequestEntityList.isEmpty()) {
                logger.error("final PR Entity list is null and saving failed");
                return ResponseEntity.ok("PR list is empty, nothing saved");
            }
            for(PullRequestEntity pr: pullRequestEntityList) {
                // filter out PR belonging to specific date range
                if(pr.getCreated_date().isAfter(started) || pr.getCreated_date().isEqual(started) &&
                        pr.getCreated_date().isBefore(ended) || pr.getCreated_date().isEqual(ended)){
                    pullRequestRepository.save(pr);
                    continue;
                }
                logger.info("PR with id {} created by {} is not within the specified time range", pr.getId(), pr.getUsername());
            }
            return ResponseEntity.ok("Pull Requests Saved Successfully");

        } catch(RuntimeException e){
            logger.error("pull requests save failed ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("pull requests save failed");
        }
    }

    @Override
    public ResponseEntity<String> saveCommits(String start, String end, String token_git) {
        try{
            LocalDateTime started = LocalDateTime.parse(start + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
            LocalDateTime ended = LocalDateTime.parse(end + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
            // fetch Commit details
            List<CommitEntity> commitEntityList = githubService.fetchCommits(started, ended, token_git);
            if(commitEntityList == null || commitEntityList.isEmpty()) {
                logger.error("final Commit Entity list is null and saving failed");
                return ResponseEntity.ok("Commits list is empty, nothing saved");
            }
            for(CommitEntity commit: commitEntityList) {
                if(commit.getCommitted_date().isAfter(started) || commit.getCommitted_date().isEqual(started) &&
                        commit.getCommitted_date().isBefore(ended) || commit.getCommitted_date().isEqual(ended)){
                    commitRepository.save(commit);
                    continue;
                }
                logger.info("Commit with id {} committed by {} is not within the specified time range", commit.getCommit_id(),
                        commit.getCommitter_name());
            }
            return ResponseEntity.ok("Commits Saved Successfully");

        } catch(RuntimeException e){
            logger.error("commits saved failed ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("commits saved failed");

        }
    }

    @Override
    public ResponseEntity<String> saveIssues(String start, String end, String token_git) {
        try{
            LocalDateTime started = LocalDateTime.parse(start + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
            LocalDateTime ended = LocalDateTime.parse(end + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));

            // fetch Open Issues details
            List<IssueEntity> openIssueEntityList = githubService.fetchOpenIssues(started, ended, token_git);
            // fetch Closed Issues details
            List<IssueEntity> closeIssueEntityList = githubService.fetchCloseIssues(started, ended, token_git);

            List<IssueEntity> allIssues = new ArrayList<>();

            if(openIssueEntityList != null && !openIssueEntityList.isEmpty()) {
                if(closeIssueEntityList != null && !closeIssueEntityList.isEmpty()) {
                    allIssues.addAll(openIssueEntityList);
                    allIssues.addAll(closeIssueEntityList);
                } else {
                    allIssues.addAll(openIssueEntityList);
                }
            } else {
                if(closeIssueEntityList != null && !closeIssueEntityList.isEmpty()) {
                    allIssues.addAll(closeIssueEntityList);
                }
            }

            if(allIssues.isEmpty()) {
                logger.error("final Issue Entity list is null and saving failed");
                return ResponseEntity.ok("Issue list is empty, nothing saved");
            }
            for(IssueEntity issue: allIssues) {
                if(issue.getCreated_date().isAfter(started) || issue.getCreated_date().isEqual(started) &&
                        issue.getCreated_date().isBefore(ended) || issue.getCreated_date().isEqual(ended)){
                    issueRepository.save(issue);
                    continue;
                }
                logger.info("Issue with id {} created by {} is not within the specified time range", issue.getIssue_id(),
                        issue.getUsername());
            }
            return ResponseEntity.ok("Issues Saved Successfully");

        } catch(RuntimeException e){
            logger.error("issue saving failed ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("issue saving failed ");

        }
    }

    @Override
    public PreviousProductivity fetchPrevious(PreviousRequestBody previousRequestBody) {
        try{
            // fetch details of previous sprint
            String param1 = previousRequestBody.getStartedDate();
            String param2 = previousRequestBody.getEndedDate();
            String complete_url = productivity_service_url + PRODUCTIVITY_SERVICE_PREVIOUS_SPRINT_URL + "?start=" + param1 + "&end=" + param2;
            return restTemplate.getForObject(complete_url, PreviousProductivity.class);

        } catch(RuntimeException e){
            logger.error("issue saving failed ", e);
            throw new RuntimeException();
        }
    }

}
