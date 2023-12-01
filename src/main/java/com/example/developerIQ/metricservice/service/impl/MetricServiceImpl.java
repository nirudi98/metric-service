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

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
    public String saveAllMetrics() {
        try{
            // fetch PR details
            List<PullRequestEntity> pullRequestEntityList = githubService.fetchPullRequests();
            // fetch commit details
            List<CommitEntity> commitEntityList = githubService.fetchCommits();
            // fetch open and closed issue details
            List<IssueEntity> openIssueEntityList = githubService.fetchOpenIssues();
            List<IssueEntity> closeIssueEntityList = githubService.fetchCloseIssues();

            // saving PR details
            if(pullRequestEntityList == null || pullRequestEntityList.isEmpty()) {
                throw new NullPointerException("final PR Entity list is null and saving failed");
            }
            for(PullRequestEntity pr: pullRequestEntityList) {
                pullRequestRepository.save(pr);
            }

            // saving commit details
            if(commitEntityList == null || commitEntityList.isEmpty()) {
                throw new NullPointerException("final Commit Entity list is null and saving failed");
            }
            for(CommitEntity commit: commitEntityList) {
                commitRepository.save(commit);
            }

            // saving issues details
            List<IssueEntity> allIssues = new ArrayList<>();
            allIssues.addAll(openIssueEntityList);
            allIssues.addAll(closeIssueEntityList);

            if(allIssues.isEmpty()) {
                throw new NullPointerException("final Issue Entity list is null and saving failed");
            }
            for(IssueEntity issue: allIssues) {
                issueRepository.save(issue);
            }

            return "Pull Requests/ Commits/ Issues Saved Successfully";

        } catch(RuntimeException e){
            logger.error("pull requests/ commits/ issues save failed ", e);
            throw new RuntimeException();
        }
    }

    @Override
    public String savePullRequests() {
        try{
            // fetch PR details
            List<PullRequestEntity> pullRequestEntityList = githubService.fetchPullRequests();
            if(pullRequestEntityList == null || pullRequestEntityList.isEmpty()) {
                throw new NullPointerException("final PR Entity list is null and saving failed");
            }
            for(PullRequestEntity pr: pullRequestEntityList) {
                pullRequestRepository.save(pr);
            }
            return "Pull Requests Saved Successfully";

        } catch(RuntimeException e){
            logger.error("pull requests save failed ", e);
            throw new RuntimeException();
        }
    }

    @Override
    public String saveCommits() {
        try{
            // fetch Commit details
            List<CommitEntity> commitEntityList = githubService.fetchCommits();
            if(commitEntityList == null || commitEntityList.isEmpty()) {
                throw new NullPointerException("final Commit Entity list is null and saving failed");
            }
            for(CommitEntity commit: commitEntityList) {
                commitRepository.save(commit);
            }
            return "Commits Saved Successfully";

        } catch(RuntimeException e){
            logger.error("commits saved failed ", e);
            throw new RuntimeException();
        }
    }

    @Override
    public String saveIssues() {
        try{
            // fetch Open Issues details
            List<IssueEntity> openIssueEntityList = githubService.fetchOpenIssues();
            // fetch Closed Issues details
            List<IssueEntity> closeIssueEntityList = githubService.fetchCloseIssues();

            List<IssueEntity> allIssues = new ArrayList<>();
            allIssues.addAll(openIssueEntityList);
            allIssues.addAll(closeIssueEntityList);

            if(allIssues.isEmpty()) {
                throw new NullPointerException("final Issue Entity list is null and saving failed");
            }
            for(IssueEntity issue: allIssues) {
                issueRepository.save(issue);
            }
            return "Issues Saved Successfully";

        } catch(RuntimeException e){
            logger.error("issue saving failed ", e);
            throw new RuntimeException();
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
