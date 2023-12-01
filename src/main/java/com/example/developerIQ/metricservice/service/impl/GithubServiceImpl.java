package com.example.developerIQ.metricservice.service.impl;

import com.example.developerIQ.metricservice.dto.IssueDTO;
import com.example.developerIQ.metricservice.dto.PullRequestsDTO;
import com.example.developerIQ.metricservice.dto.commits.CommitDTO;
import com.example.developerIQ.metricservice.entity.CommitEntity;
import com.example.developerIQ.metricservice.entity.IssueEntity;
import com.example.developerIQ.metricservice.entity.PullRequestEntity;
import com.example.developerIQ.metricservice.service.GithubService;
import com.example.developerIQ.metricservice.utils.GithubServiceHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.developerIQ.metricservice.utils.constants.Constants.*;

@Service
public class GithubServiceImpl implements GithubService {

    private static final Logger logger = LoggerFactory.getLogger(GithubServiceImpl.class);

    @Autowired
    private final RestTemplate restTemplate;

    @Autowired
    private final GithubServiceHelper githubServiceHelper;

    public GithubServiceImpl(RestTemplate restTemplate, GithubServiceHelper githubServiceHelper) {
        this.restTemplate = restTemplate;
        this.githubServiceHelper = githubServiceHelper;
    }

    @Override
    public List<PullRequestEntity> fetchPullRequests(){
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(PULL_REQUESTS, HttpMethod.GET,
                    new HttpEntity<>(generateHTTPHeaders()), String.class);
            String responseHeaders = Objects.requireNonNull(responseEntity.getHeaders().get("Link")).toString();

            // fetch the number of available pages and the link to use
            Map<Integer, String> pagesAndURL = getTotalPagesAndURL(responseHeaders);

            ObjectMapper objectMapper = new ObjectMapper();
            // first page results
            List<PullRequestsDTO> pullRequestsList = objectMapper.readValue(responseEntity.getBody(), new TypeReference<List<PullRequestsDTO>>() {});

            if(pullRequestsList == null || pullRequestsList.isEmpty()){
                logger.info("Pull Request list is empty");
                throw new RuntimeException();
            }
            logger.info("Number of pull requests fetched from first page " + pullRequestsList.size());

            List<PullRequestsDTO> temporaryPRList = new ArrayList<>();
            List<PullRequestsDTO> finalPRList = new ArrayList<>(pullRequestsList);

            List<String> url = githubServiceHelper.fetchAdditionalPageURLList(pagesAndURL);
            if(!url.isEmpty()) {
                int count = 2;
                for (String page : url) {
                    ResponseEntity<String> responseEntityPerPage = restTemplate.exchange(page, HttpMethod.GET,
                            new HttpEntity<>(generateHTTPHeaders()), String.class);
                    temporaryPRList = objectMapper.readValue(responseEntityPerPage.getBody(), new TypeReference<List<PullRequestsDTO>>() {});

                    logger.info("Temporary PR list size {} in page number {}", temporaryPRList.size(), count);

                    finalPRList.addAll(temporaryPRList);
                    count++;
                }

            }

            logger.info("Total pull requests fetched " + finalPRList.size());
            logger.info("Successfully fetched PR from Github REPO {} ", pullRequestsList.size());

            return githubServiceHelper.formatDatesFetched(finalPRList);

        } catch (JsonProcessingException | RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CommitEntity> fetchCommits(){
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(COMMITS, HttpMethod.GET,
                    new HttpEntity<>(generateHTTPHeaders()), String.class);
            String responseHeaders = Objects.requireNonNull(responseEntity.getHeaders().get("Link")).toString();

            // fetch the number of available pages and the link to use
            Map<Integer, String> pagesAndURL = getTotalPagesAndURL(responseHeaders);

            ObjectMapper objectMapper = new ObjectMapper();
            // first page results
            List<CommitDTO> commitList = objectMapper.readValue(responseEntity.getBody(), new TypeReference<List<CommitDTO>>() {});

            if(commitList == null || commitList.isEmpty()){
                logger.info("Commit list is empty");
                throw new RuntimeException();
            }
            logger.info("Number of commits fetched from first page " + commitList.size());

            List<CommitDTO> temporaryCommitList = new ArrayList<>();
            List<CommitDTO> finalCommitList = new ArrayList<>(commitList);

            List<String> url = githubServiceHelper.fetchAdditionalPageURLList(pagesAndURL);
            if(!url.isEmpty()) {
                int count = 2;
                for (String page : url) {
                    ResponseEntity<String> responseEntityPerPage = restTemplate.exchange(page, HttpMethod.GET,
                            new HttpEntity<>(generateHTTPHeaders()), String.class);
                    temporaryCommitList = objectMapper.readValue(responseEntityPerPage.getBody(), new TypeReference<List<CommitDTO>>() {});

                    logger.info("Temporary commit list size {} in page number {}", temporaryCommitList.size(), count);

                    finalCommitList.addAll(temporaryCommitList);
                    count++;
                }

            }

            logger.info("Total commits fetched " + finalCommitList.size());
            logger.info("Successfully fetched Commits from Github REPO {} ", commitList.size());

            return githubServiceHelper.formatDatesFetchedForCommits(finalCommitList);

        } catch (JsonProcessingException | RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<IssueEntity> fetchOpenIssues(){
        try {
            Map<String, String> uriVariables = new HashMap<>();
            uriVariables.put("state", "open");

            ResponseEntity<String> responseEntity = restTemplate.exchange(ISSUES, HttpMethod.GET,
                    new HttpEntity<>(generateHTTPHeaders()), String.class, uriVariables);
            String responseHeaders = Objects.requireNonNull(responseEntity.getHeaders().get("Link")).toString();

            // fetch the number of available pages and the link to use
            Map<Integer, String> pagesAndURL = getTotalPagesAndURL(responseHeaders);

            ObjectMapper objectMapper = new ObjectMapper();
            // first page results
            List<IssueDTO> openIssueList = objectMapper.readValue(responseEntity.getBody(), new TypeReference<List<IssueDTO>>() {});

            if(openIssueList == null || openIssueList.isEmpty()){
                logger.info("Open Issue list is empty");
                throw new RuntimeException();
            }
            logger.info("Number of open issues fetched from first page " + openIssueList.size());

            List<IssueDTO> temporaryOpenIssueList = new ArrayList<>();
            List<IssueDTO> finalOpenIssueList = new ArrayList<>(openIssueList);

            List<String> url = githubServiceHelper.fetchAdditionalPageURLList(pagesAndURL);
            if(!url.isEmpty()) {
                int count = 2;
                for (String page : url) {
                    ResponseEntity<String> responseEntityPerPage = restTemplate.exchange(page, HttpMethod.GET,
                            new HttpEntity<>(generateHTTPHeaders()), String.class);
                    temporaryOpenIssueList = objectMapper.readValue(responseEntityPerPage.getBody(), new TypeReference<List<IssueDTO>>() {});

                    logger.info("Temporary Open issue list size {} in page number {}", temporaryOpenIssueList.size(), count);

                    finalOpenIssueList.addAll(temporaryOpenIssueList);
                    count++;
                }

            }

            logger.info("Total open issues fetched " + finalOpenIssueList.size());
            logger.info("Successfully fetched Open Issues from Github REPO {} ", finalOpenIssueList.size());

            return githubServiceHelper.formatDatesFetchedForIssues(finalOpenIssueList);

        } catch (JsonProcessingException | RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<IssueEntity> fetchCloseIssues(){
        try {
            Map<String, String> uriVariables = new HashMap<>();
            uriVariables.put("state", "closed");

            ResponseEntity<String> responseEntity = restTemplate.exchange(ISSUES, HttpMethod.GET,
                    new HttpEntity<>(generateHTTPHeaders()), String.class, uriVariables);
            String responseHeaders = Objects.requireNonNull(responseEntity.getHeaders().get("Link")).toString();

            // fetch the number of available pages and the link to use
            Map<Integer, String> pagesAndURL = getTotalPagesAndURL(responseHeaders);

            ObjectMapper objectMapper = new ObjectMapper();
            // first page results
            List<IssueDTO> closedIssueList = objectMapper.readValue(responseEntity.getBody(), new TypeReference<List<IssueDTO>>() {});

            if(closedIssueList == null || closedIssueList.isEmpty()){
                logger.info("Closed Issue list is empty");
                throw new RuntimeException();
            }
            logger.info("Number of closed issues fetched from first page " + closedIssueList.size());

            List<IssueDTO> temporaryClosedIssueList = new ArrayList<>();
            List<IssueDTO> finalClosedIssueList = new ArrayList<>(closedIssueList);

            List<String> url = githubServiceHelper.fetchAdditionalPageURLList(pagesAndURL);
            if(!url.isEmpty()) {
                int count = 2;
                for (String page : url) {
                    ResponseEntity<String> responseEntityPerPage = restTemplate.exchange(page, HttpMethod.GET,
                            new HttpEntity<>(generateHTTPHeaders()), String.class);
                    temporaryClosedIssueList = objectMapper.readValue(responseEntityPerPage.getBody(), new TypeReference<List<IssueDTO>>() {});

                    logger.info("Temporary Closed issue list size {} in page number {}", temporaryClosedIssueList.size(), count);

                    finalClosedIssueList.addAll(temporaryClosedIssueList);
                    count++;
                }

            }

            logger.info("Total closed issues fetched " + finalClosedIssueList.size());
            logger.info("Successfully fetched Closed Issues from Github REPO {} ", finalClosedIssueList.size());

            return githubServiceHelper.formatDatesFetchedForIssues(finalClosedIssueList);

        } catch (JsonProcessingException | RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpHeaders generateHTTPHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", AUTHORIZATION);
        httpHeaders.set("Accept", HEADERS);
        httpHeaders.set("X-GitHub-Api-Version", VERSION);
        return httpHeaders;
    }

    private Map<Integer, String> getTotalPagesAndURL(String link) {
        // Remove square brackets from the input
        String content = link.substring(1, link.length() - 1);

        // Split the string using comma and trim the elements
        String[] elements = content.split("\\s*,\\s*");

        // Extract strings within <>
        Pattern url_pattern = Pattern.compile("<(.*?)>");


        Optional<String> url = Arrays.stream(elements).filter(element -> element.contains("last"))
                .map(filteredURL -> {
                    Matcher matcher = url_pattern.matcher(filteredURL);
                    return matcher.find() ? matcher.group(1) : null;
                })
                .filter(Objects::nonNull).findFirst();


        Optional<Integer> firstPageValue = Arrays.stream(elements)
                // Filter out elements containing "last"
                .filter(element -> element.contains("last"))
                // Define a regular expression pattern to extract page values
                .map(element -> {
                    Pattern pattern = Pattern.compile("page=(\\d+)");
                    Matcher matcher = pattern.matcher(element);
                    return matcher.find() ? Integer.parseInt(matcher.group(1)) : null;
                })
                // Filter out null values and find the first page value
                .filter(Objects::nonNull)
                .findFirst();

        // Assign the filtered page value to an integer variable
        // Default value if not found
        int filteredPageValue = firstPageValue.orElse(-1);

        HashMap<Integer, String> pageURLMap = new HashMap<>();
        pageURLMap.put(filteredPageValue, url.orElse(null));
        return pageURLMap;
    }

}
