package com.example.developerIQ.metricservice.integration;

import com.example.developerIQ.metricservice.common.PreviousProductivity;
import com.example.developerIQ.metricservice.common.SprintDate;
import com.example.developerIQ.metricservice.common.productivityRequest.PreviousRequestBody;
import com.example.developerIQ.metricservice.entity.CommitEntity;
import com.example.developerIQ.metricservice.entity.IssueEntity;
import com.example.developerIQ.metricservice.entity.PullRequestEntity;
import com.example.developerIQ.metricservice.repository.CommitRepository;
import com.example.developerIQ.metricservice.repository.IssueRepository;
import com.example.developerIQ.metricservice.repository.PullRequestRepository;
import com.example.developerIQ.metricservice.service.impl.GithubServiceImpl;
import com.example.developerIQ.metricservice.service.impl.MetricServiceImpl;
import com.example.developerIQ.metricservice.service.impl.ValidateServiceImpl;
import com.example.developerIQ.metricservice.utils.AuthenticateUser;
import com.example.developerIQ.metricservice.utils.GithubServiceHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.ResponseActions;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.example.developerIQ.metricservice.constants.TestConstants.MOCK_TOKEN;
import static com.example.developerIQ.metricservice.constants.TestConstants.PULL_REQUESTS;
import static com.example.developerIQ.metricservice.utils.constants.Constants.HEADERS;
import static com.example.developerIQ.metricservice.utils.constants.Constants.VERSION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestPropertySource(locations = "classpath:application-test.properties")
public class MetricControllerTests {

    @Container
    @ServiceConnection
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:5.0");

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    private CommitRepository commitRepository;

    @Autowired
    private PullRequestRepository pullRequestRepository;

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private ValidateServiceImpl validateService;

    @Autowired
    private AuthenticateUser authenticateUser;

    private MockRestServiceServer mockServer;
    @Mock
    private RestTemplate restTemplate1;

    @InjectMocks
    private GithubServiceImpl githubService;

    @InjectMocks
    private MetricServiceImpl metricService;

    @Mock
    private GithubServiceHelper githubServiceHelper;

    @Autowired
    private Environment environment;

    @Test
    public void testPropertyValues() {
        String propertyValue = environment.getProperty("test.property.name");
        assertNotNull(propertyValue);
        assertEquals("test", propertyValue);
    }



    @BeforeEach
    void setUp() {
        LocalDateTime started = LocalDateTime.parse("2023-10-01" +
                " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
        LocalDateTime ended = LocalDateTime.parse("2023-10-13" +
                " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
    }

    @Test
    public void testSaveAllMetrics() {
        String validToken = authenticateUser.validateTokenAuthService();
        String validGitToken = authenticateUser.decodeString();

        HttpHeaders headers = new HttpHeaders();
        String finalToken = "Bearer " + validToken;
        headers.set("Authorization", finalToken);

        SprintDate sprintDate = new SprintDate();
        sprintDate.setGivenStartDate("2023-01-01");
        sprintDate.setGivenEndDate("2023-01-10");
        sprintDate.setToken(validGitToken);

        HttpEntity<SprintDate> requestEntity = new HttpEntity<>(sprintDate,headers);


        ResponseEntity<String> response = restTemplate.exchange("/metrics/fetch/all-metrics",
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testSavePRMetrics() {
        String validToken = authenticateUser.validateTokenAuthService();
        String validGitToken = authenticateUser.decodeString();

        HttpHeaders headers = new HttpHeaders();
        String finalToken = "Bearer " + validToken;
        headers.set("Authorization", finalToken);

        SprintDate sprintDate = new SprintDate();
        sprintDate.setGivenStartDate("2023-01-10");
        sprintDate.setGivenEndDate("2023-01-15");
        sprintDate.setToken(validGitToken);

        HttpEntity<SprintDate> requestEntity = new HttpEntity<>(sprintDate,headers);


        ResponseEntity<String> response = restTemplate.exchange("/metrics/pulls",
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testSaveCommitsMetrics() {
        String validToken = authenticateUser.validateTokenAuthService();
        String validGitToken = authenticateUser.decodeString();

        HttpHeaders headers = new HttpHeaders();
        String finalToken = "Bearer " + validToken;
        headers.set("Authorization", finalToken);

        SprintDate sprintDate = new SprintDate();
        sprintDate.setGivenStartDate("2023-01-10");
        sprintDate.setGivenEndDate("2023-01-15");
        sprintDate.setToken(validGitToken);

        HttpEntity<SprintDate> requestEntity = new HttpEntity<>(sprintDate,headers);


        ResponseEntity<String> response = restTemplate.exchange("/metrics/commits",
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testSaveIssueMetrics() {
        String validToken = authenticateUser.validateTokenAuthService();
        String validGitToken = authenticateUser.decodeString();

        HttpHeaders headers = new HttpHeaders();
        String finalToken = "Bearer " + validToken;
        headers.set("Authorization", finalToken);

        SprintDate sprintDate = new SprintDate();
        sprintDate.setGivenStartDate("2023-01-10");
        sprintDate.setGivenEndDate("2023-01-15");
        sprintDate.setToken(validGitToken);

        HttpEntity<SprintDate> requestEntity = new HttpEntity<>(sprintDate,headers);

        ResponseEntity<String> response = restTemplate.exchange("/metrics/issues",
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testFetchPreviousSprintStats() {
        String validToken = authenticateUser.validateTokenAuthService();

        HttpHeaders headers = new HttpHeaders();
        String finalToken = "Bearer " + validToken;
        headers.set("Authorization", finalToken);

        PreviousRequestBody previousRequestBody = new PreviousRequestBody();
        previousRequestBody.setStartedDate("2023-01-10");
        previousRequestBody.setEndedDate("2023-01-15");

        HttpEntity<PreviousRequestBody> requestEntity = new HttpEntity<>(previousRequestBody,headers);


        ResponseEntity<PreviousProductivity> response = restTemplate.exchange("/metrics/fetch/previous-sprint-stats",
                HttpMethod.POST,
                requestEntity,
                PreviousProductivity.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    private HttpHeaders generateHTTPHeaders(String auth) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", auth);
        httpHeaders.set("Accept", HEADERS);
        httpHeaders.set("X-GitHub-Api-Version", VERSION);
        return httpHeaders;
    }

    private PullRequestEntity generatePREntityMock() {
        String created_date = "2023-11-01";
        LocalDateTime dateTime = LocalDateTime.of(2023, 12, 1, 12, 0);
        PullRequestEntity pr = new PullRequestEntity();
        pr.setId(UUID.randomUUID().toString());
        pr.setUrl("test.com");
        pr.setMerged_date(null);
        pr.setClosed_date(null);
        pr.setCreated_date(dateTime);
        pr.setUsername("test user");
        pr.setTitle("test pr");

        return pr;
    }

    private CommitEntity generateCommitEntityMock() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 12, 1, 12, 0);
        CommitEntity entity = new CommitEntity();
        entity.setCommit_id(UUID.randomUUID().toString());
        entity.setAuthor_name("test name");
        entity.setAuthored_date(dateTime);

        entity.setCommitter_name("test one");
        entity.setCommitted_date(dateTime);
        entity.setUrl("test.com");
        entity.setMessage("test msg");

        return entity;
    }

    private IssueEntity generateIssueEntityMock() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 12, 1, 12, 0);
        IssueEntity entity = new IssueEntity();
        entity.setIssue_id(UUID.randomUUID().toString());
        entity.setTitle("test title");
        entity.setState("open");

        entity.setDescription("test desc");
        entity.setUsername("test");
        entity.setCreated_date(dateTime);
        entity.setClosed_date(dateTime);

        return entity;
    }

    private IssueEntity generateCloseIssueEntityMock() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 12, 1, 12, 0);
        IssueEntity entity = new IssueEntity();
        entity.setIssue_id(UUID.randomUUID().toString());
        entity.setTitle("test title");
        entity.setState("closed");

        entity.setDescription("test desc");
        entity.setUsername("test");
        entity.setCreated_date(dateTime);
        entity.setClosed_date(dateTime);

        return entity;
    }

    private PreviousProductivity generatePrevious() {
        PreviousProductivity previous = new PreviousProductivity();
        previous.setPrevious_pr_count(10);
        previous.setPrevious_commit_count(20);
        previous.setPrevious_open_issue_count(3);
        previous.setPrevious_closed_issue_count(2);

        return previous;
    }

    private String generateMockResponsePR() {
        return "[{\"url\": \"https://api.github.com/repos/octocat/Hello-World/pulls/2846\", \"id\": 1578671897, \"number\": 2846," +
                " \"state\": \"open\", \"title\": \"Create blank.yml\",\"user\": {\"login\": \"CW3-Root\",\"type\": \"User\"," +
                "\"site_admin\": false},\"created_at\": \"2023-10-30T09:35:16Z\",\"closed_at\": null,\"merged_at\": null}]";
    }


//    @Test
//    public void testRetrieveProductivityOverview_exception() {
//
//        HttpHeaders headers = new HttpHeaders();
//        String finalToken = "1234";
//        headers.set("Authorization", finalToken);
//
//        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
//
//        ResponseEntity<Productivity> response = restTemplate.exchange("/productivity/display/overview?username=test&startDate=2023-01-01&endDate=2023-01-31",
//                HttpMethod.GET,
//                requestEntity,
//                Productivity.class
//        );
//
//        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
//    }
//
//    @Test
//    public void testRetrievePROverview() {
//        String validToken = authenticateUser.validateTokenAuthService();
//
//        HttpHeaders headers = new HttpHeaders();
//        String finalToken = "Bearer " + validToken;
//        headers.set("Authorization", finalToken);
//
//        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
//
//        ResponseEntity<PullRequests> response = restTemplate.exchange("/productivity/display/pull-request-productivity?username=test&startDate=2023-01-01&endDate=2023-01-31",
//                HttpMethod.GET,
//                requestEntity,
//                PullRequests.class
//        );
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//    }
//
//    @Test
//    public void testRetrievePROverview_exception() {
//
//        HttpHeaders headers = new HttpHeaders();
//        String finalToken = "invalid_token";
//        headers.set("Authorization", finalToken);
//
//        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
//
//        ResponseEntity<PullRequests> response = restTemplate.exchange("/productivity/display/pull-request-productivity?username=test&startDate=2023-01-01&endDate=2023-01-31",
//                HttpMethod.GET,
//                requestEntity,
//                PullRequests.class
//        );
//
//        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
//    }
//
//    @Test
//    public void testRetrievePRStats() {
//        String validToken = authenticateUser.validateTokenAuthService();
//
//        HttpHeaders headers = new HttpHeaders();
//        String finalToken = "Bearer " + validToken;
//        headers.set("Authorization", finalToken);
//
//        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
//
//        ResponseEntity<PullRequests> response = restTemplate.exchange("/productivity/display/pull-request-productivity/stats" +
//                        "?username=test&startDate=2023-01-01&endDate=2023-01-31",
//                HttpMethod.GET,
//                requestEntity,
//                PullRequests.class
//        );
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//    }
//
//    @Test
//    public void testRetrievePRStats_exception() {
//
//        HttpHeaders headers = new HttpHeaders();
//        String finalToken = "invalid_token";
//        headers.set("Authorization", finalToken);
//
//        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
//
//        ResponseEntity<PullRequests> response = restTemplate.exchange("/productivity/display/pull-request-productivity/stats" +
//                        "?username=test&startDate=2023-01-01&endDate=2023-01-31",
//                HttpMethod.GET,
//                requestEntity,
//                PullRequests.class
//        );
//
//        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
//    }
//
//    @Test
//    public void testRetrieveIssuesOverview() {
//        String validToken = authenticateUser.validateTokenAuthService();
//
//        HttpHeaders headers = new HttpHeaders();
//        String finalToken = "Bearer " + validToken;
//        headers.set("Authorization", finalToken);
//
//        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
//
//        ResponseEntity<IssuesSprint> response = restTemplate.exchange("/productivity/display/issues-productivity" +
//                        "?username=test&startDate=2023-01-01&endDate=2023-01-31",
//                HttpMethod.GET,
//                requestEntity,
//                IssuesSprint.class
//        );
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals("issue one", Objects.requireNonNull(response.getBody()).getIssueInformationList().stream().toList().get(0).getTitle());
//
//    }
//
//    @Test
//    public void testRetrieveIssuesOverview_exception() {
//
//        HttpHeaders headers = new HttpHeaders();
//        String finalToken = "invalid_token";
//        headers.set("Authorization", finalToken);
//
//        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
//
//        ResponseEntity<IssuesSprint> response = restTemplate.exchange("/productivity/display/issues-productivity" +
//                        "?username=test&startDate=2023-01-01&endDate=2023-01-31",
//                HttpMethod.GET,
//                requestEntity,
//                IssuesSprint.class
//        );
//
//        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
//    }
//
//    @Test
//    public void testRetrieveIssuesStats() {
//        String validToken = authenticateUser.validateTokenAuthService();
//
//        HttpHeaders headers = new HttpHeaders();
//        String finalToken = "Bearer " + validToken;
//        headers.set("Authorization", finalToken);
//
//        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
//
//        ResponseEntity<IssueStatics> response = restTemplate.exchange("/productivity/display/issues-productivity/stats" +
//                        "?username=test&startDate=2023-01-01&endDate=2023-01-31",
//                HttpMethod.GET,
//                requestEntity,
//                IssueStatics.class
//        );
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals(1, Objects.requireNonNull(response.getBody()).getAll_issue_count());
//    }
//
//    @Test
//    public void testRetrieveIssuesStats_exception() {
//
//        HttpHeaders headers = new HttpHeaders();
//        String finalToken = "invalid_token";
//        headers.set("Authorization", finalToken);
//
//        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
//
//        ResponseEntity<IssueStatics> response = restTemplate.exchange("/productivity/display/issues-productivity/stats" +
//                        "?username=test&startDate=2023-01-01&endDate=2023-01-31",
//                HttpMethod.GET,
//                requestEntity,
//                IssueStatics.class
//        );
//
//        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
//    }
//
//    @Test
//    public void testRetrieveCommitsOverview() {
//        String validToken = authenticateUser.validateTokenAuthService();
//
//        HttpHeaders headers = new HttpHeaders();
//        String finalToken = "Bearer " + validToken;
//        headers.set("Authorization", finalToken);
//
//        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
//
//        ResponseEntity<Commits> response = restTemplate.exchange("/productivity/display/commits-productivity" +
//                        "?username=test&startDate=2023-01-01&endDate=2023-01-31",
//                HttpMethod.GET,
//                requestEntity,
//                Commits.class
//        );
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//    }
//
//    @Test
//    public void testRetrieveCommitsOverview_exception() {
//
//        HttpHeaders headers = new HttpHeaders();
//        String finalToken = "invalid_token";
//        headers.set("Authorization", finalToken);
//
//        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
//
//        ResponseEntity<Commits> response = restTemplate.exchange("/productivity/display/commits-productivity" +
//                        "?username=test&startDate=2023-01-01&endDate=2023-01-31",
//                HttpMethod.GET,
//                requestEntity,
//                Commits.class
//        );
//
//        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
//    }

}
