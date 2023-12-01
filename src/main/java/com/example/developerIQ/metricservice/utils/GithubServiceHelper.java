package com.example.developerIQ.metricservice.utils;

import com.example.developerIQ.metricservice.dto.IssueDTO;
import com.example.developerIQ.metricservice.dto.PullRequestsDTO;
import com.example.developerIQ.metricservice.dto.commits.CommitDTO;
import com.example.developerIQ.metricservice.entity.CommitEntity;
import com.example.developerIQ.metricservice.entity.IssueEntity;
import com.example.developerIQ.metricservice.entity.PullRequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.example.developerIQ.metricservice.utils.constants.Constants.*;

@Service
public class GithubServiceHelper {
    private static final Logger logger = LoggerFactory.getLogger(GithubServiceHelper.class);

    @Autowired
    private RestTemplate restTemplate;

    public List<String> fetchAdditionalPageURLList(Map<Integer, String> pageURLMap) {
        PullRequestsDTO pullRequestDTO = new PullRequestsDTO();
        String url = "";
        Integer pageAmount = 0;
        List<String> additionalURL = new ArrayList<>();

        // since the map should only contain the last page details
        // only one entry should be there
        if(pageURLMap == null || pageURLMap.isEmpty()) {
            logger.info("There are no additional pages. Only one page result is available");
            return additionalURL;
        }
        for (Map.Entry<Integer, String> entry : pageURLMap.entrySet()) {
            pageAmount = entry.getKey();
            String[] parts = entry.getValue().split("\\?");
            url = parts.length > 0 ? parts[0] : null;
        }

        if(pageAmount > 0 && url != null) {
            for (int i = 2; i <= pageAmount; i++) {
                String urlWithNumber = url + "?page=" + i;
                // create a list of each url
                additionalURL.add(urlWithNumber);
            }
        }
    return additionalURL;

    }

    public List<PullRequestEntity> formatDatesFetched(List<PullRequestsDTO> initialList) {
        logger.info("Convert dates fetched to date type");

        LocalDateTime created_date = null; LocalDateTime closed_date = null; LocalDateTime merged_date = null;
        List<PullRequestEntity> pullRequestEntityList = new ArrayList<>();
        Boolean isMerged = false;

        try{
            if(initialList == null || initialList.isEmpty()){
                logger.info("No pull requests available");
                throw new NullPointerException();
            }
            for(PullRequestsDTO pr: initialList) {
                if(pr.getCreated_at() != null){
                    created_date = getDateFromString(pr.getCreated_at());
                    closed_date = getDateFromString(pr.getClosed_at());
                    merged_date = getDateFromString(pr.getMerged_at());

                    // Extract the date only
//                    String dateOnly = dateTime.toLocalDate().toString();
                }
                // check if PR has been merged or not
                if(pr.getNumber() != null) {
                    isMerged = checkIsMerged(pr.getNumber());
                }
                // map values to entity and add to final list
                pullRequestEntityList.add(generatePREntity(pr, created_date, closed_date, merged_date, isMerged));
            }

            logger.info("PR Entity list size {}", pullRequestEntityList.size());
            return pullRequestEntityList;

        } catch (NullPointerException e) {
            logger.error("Pull request processing failed");
            throw new RuntimeException();
        }

    }

    public List<CommitEntity> formatDatesFetchedForCommits(List<CommitDTO> initialList) {
        logger.info("Convert dates fetched to date type");

        LocalDateTime committed_date = null; LocalDateTime authored_date = null;
        List<CommitEntity> commitEntityList = new ArrayList<>();

        try{
            if(initialList == null || initialList.isEmpty()){
                logger.info("No pull requests available");
                throw new NullPointerException();
            }
            for(CommitDTO commit: initialList) {
                if(commit.getCommit().getCommit_committer().getDate() != null || commit.getCommit().getCommit_author().getDate() != null){
                    committed_date = getDateFromString(commit.getCommit().getCommit_committer().getDate());
                    authored_date = getDateFromString(commit.getCommit().getCommit_author().getDate());

                }
                // map values to entity and add to final list
                commitEntityList.add(generateCommitEntity(commit, committed_date, authored_date));
            }

            logger.info("Commits Entity list size {}", commitEntityList.size());
            return commitEntityList;

        } catch (NullPointerException e) {
            logger.error("Commits processing failed");
            throw new RuntimeException();
        }

    }

    public List<IssueEntity> formatDatesFetchedForIssues(List<IssueDTO> initialList) {
        logger.info("Convert dates fetched to date type");

        LocalDateTime created_date = null; LocalDateTime closed_date = null;
        List<IssueEntity> issueEntityList = new ArrayList<>();

        try{
            if(initialList == null || initialList.isEmpty()){
                logger.info("No issues available");
                throw new NullPointerException();
            }
            for(IssueDTO issue: initialList) {
                if(issue.getCreatedDate() != null){
                    created_date = getDateFromString(issue.getCreatedDate());
                    closed_date = getDateFromString(issue.getClosedDate());

                }
                // map values to entity and add to final list
                issueEntityList.add(generateIssueEntity(issue, created_date, closed_date));
            }

            logger.info("Issue Entity list size {}", issueEntityList.size());
            return issueEntityList;

        } catch (NullPointerException e) {
            logger.error("Issue processing failed");
            throw new RuntimeException();
        }

    }

    private PullRequestEntity generatePREntity(PullRequestsDTO pr, LocalDateTime created, LocalDateTime closed, LocalDateTime merged, Boolean isMerged) {
        PullRequestEntity entity = new PullRequestEntity();
        entity.setId(pr.getId());
        entity.setTitle(pr.getTitle());
        entity.setUrl(pr.getUrl());
        entity.setStatus(pr.getState());
        entity.setUsername(pr.getUser().getLogin());
        entity.setUser_type(pr.getUser().getType());
        entity.setPull_number(pr.getNumber());
        entity.setIs_merged(isMerged);

        entity.setCreated_date(created);
        entity.setClosed_date(closed);
        entity.setMerged_date(merged);
        return entity;
    }

    private CommitEntity generateCommitEntity(CommitDTO commit, LocalDateTime commit_created, LocalDateTime author_created) {
        CommitEntity entity = new CommitEntity();
        entity.setCommit_id(UUID.randomUUID().toString());
        entity.setAuthor_name(commit.getCommit().getCommit_author().getName());
        entity.setAuthored_date(commit_created);

        entity.setCommitter_name(commit.getCommit().getCommit_committer().getName());
        entity.setCommitted_date(author_created);
        entity.setUrl(commit.getUrl());
        entity.setMessage(commit.getCommit().getMessage());

        return entity;
    }

    private IssueEntity generateIssueEntity(IssueDTO issue, LocalDateTime created, LocalDateTime closed) {
        IssueEntity entity = new IssueEntity();
        entity.setIssue_id(UUID.randomUUID().toString());
        entity.setTitle(issue.getTitle());
        entity.setState(issue.getState());

        entity.setDescription(issue.getDescription());
        entity.setUsername(issue.getUsers().getLogin());
        entity.setCreated_date(created);
        entity.setClosed_date(closed);

        return entity;
    }

    public static LocalDateTime getDateFromString(String date)
    {
        if(date == null) {
            logger.info("date is null");
            return null;
        }
        return LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME);
    }

    private Boolean checkIsMerged(String pr_number) {
        try {
            Map<String, String> uriVariables = new HashMap<>();
            uriVariables.put("pull_number", pr_number);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("Authorization", AUTHORIZATION);
            httpHeaders.set("Accept", HEADERS);
            httpHeaders.set("X-GitHub-Api-Version", VERSION);

            ResponseEntity<String> responseEntity = restTemplate.exchange(PULL_REQUESTS_MERGE_CHECK, HttpMethod.GET,
                    new HttpEntity<>(httpHeaders), String.class, uriVariables);
            int statusCode = responseEntity.getStatusCode().value();

            return (statusCode == 200 || statusCode == 204);
        } catch (HttpClientErrorException e) {
            int statusCode = e.getStatusCode().value();

            if (statusCode == 404) {
                // Handle 404 Not Found case
                logger.error("Resource not found: {}", PULL_REQUESTS_MERGE_CHECK);
            } else {
                // Handle other HTTP status codes
                logger.error("Unexpected status code: {}", statusCode);
            }
            return false;
        } catch (Exception e) {
            logger.error("An unexpected error occurred: {}", e.getMessage());
            return false;
        }
    }
}
