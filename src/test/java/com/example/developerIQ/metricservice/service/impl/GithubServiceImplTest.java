package com.example.developerIQ.metricservice.service.impl;

import com.example.developerIQ.metricservice.entity.CommitEntity;
import com.example.developerIQ.metricservice.entity.IssueEntity;
import com.example.developerIQ.metricservice.entity.PullRequestEntity;
import com.example.developerIQ.metricservice.repository.PullRequestRepository;
import com.example.developerIQ.metricservice.utils.GithubServiceHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.example.developerIQ.metricservice.constants.TestConstants.MOCK_TOKEN;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GithubServiceImplTest {

    @InjectMocks
    private GithubServiceImpl githubService;

    @Mock
    private PullRequestRepository pullRequestRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private MetricServiceImpl metricService;

    @Mock
    private GithubServiceHelper githubServiceHelper;


    @Test
    void fetchPullRequests() {
        LocalDateTime start = LocalDateTime.of(2023, 12, 1, 12, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 3, 12, 0);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add("Custom-Header", "Custom-Value");
        headers.add("Link", "Custom-Value");

        ResponseEntity<String> mockResponseEntity = new ResponseEntity<>(generateMockResponsePR(), headers, HttpStatus.OK);
        githubService = new GithubServiceImpl(restTemplate, githubServiceHelper);

        Mockito.when(restTemplate.exchange(
                Mockito.anyString(),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(HttpEntity.class),
                Mockito.eq(String.class),
                Mockito.anyMap()
        )).thenReturn(mockResponseEntity);

        when(githubServiceHelper.fetchAdditionalPageURLList(Mockito.anyMap()))
                .thenReturn(Collections.emptyList());
        Mockito.when(githubServiceHelper.formatDatesFetched(Mockito.anyList()))
                .thenReturn(Collections.singletonList(generatePREntityMock()));

        List<PullRequestEntity> result = githubService.fetchPullRequests(start, end, MOCK_TOKEN);
        assertEquals(1, result.size());
    }

    @Test
    public void testFetchPullRequests_EmptyList() {
        LocalDateTime start = LocalDateTime.of(2023, 12, 1, 12, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 3, 12, 0);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add("Custom-Header", "Custom-Value");
        headers.add("Link", "Custom-Value");

        ResponseEntity<String> mockResponseEntity = new ResponseEntity<>("[]", headers, HttpStatus.OK);
        githubService = new GithubServiceImpl(restTemplate, githubServiceHelper);

        Mockito.when(restTemplate.exchange(
                Mockito.anyString(),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(HttpEntity.class),
                Mockito.eq(String.class)
        )).thenReturn(mockResponseEntity);

        assertThrows(RuntimeException.class, () -> githubService.fetchPullRequests(start, end, MOCK_TOKEN));
    }

    @Test
    public void testFetchPullRequests_Error() {
        LocalDateTime start = LocalDateTime.of(2023, 12, 1, 12, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 3, 12, 0);

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenThrow(new RuntimeException("Simulated error"));

        assertThrows(RuntimeException.class, () -> githubService.fetchPullRequests(start, end, MOCK_TOKEN));
    }

    @Test
    void fetchCommits() {
        LocalDateTime start = LocalDateTime.of(2023, 12, 1, 12, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 3, 12, 0);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add("Custom-Header", "Custom-Value");
        headers.add("Link", "Custom-Value");

        ResponseEntity<String> mockResponseEntity = new ResponseEntity<>(generateMockResponseCommit(), headers, HttpStatus.OK);
        githubService = new GithubServiceImpl(restTemplate, githubServiceHelper);

        Mockito.when(restTemplate.exchange(
                Mockito.anyString(),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(HttpEntity.class),
                Mockito.eq(String.class),
                Mockito.anyMap()
        )).thenReturn(mockResponseEntity);

        when(githubServiceHelper.fetchAdditionalPageURLList(Mockito.anyMap()))
                .thenReturn(Collections.emptyList());
        Mockito.when(githubServiceHelper.formatDatesFetchedForCommits(Mockito.anyList()))
                .thenReturn(Collections.singletonList(generateCommitEntityMock()));

        List<CommitEntity> result = githubService.fetchCommits(start, end, MOCK_TOKEN);
        assertEquals(1, result.size());
    }

    @Test
    public void testFetchCommits_EmptyList() {
        LocalDateTime start = LocalDateTime.of(2023, 12, 1, 12, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 3, 12, 0);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add("Custom-Header", "Custom-Value");
        headers.add("Link", "Custom-Value");

        ResponseEntity<String> mockResponseEntity = new ResponseEntity<>("[]", headers, HttpStatus.OK);
        githubService = new GithubServiceImpl(restTemplate, githubServiceHelper);

        Mockito.when(restTemplate.exchange(
                Mockito.anyString(),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(HttpEntity.class),
                Mockito.eq(String.class),
                Mockito.anyMap()
        )).thenReturn(mockResponseEntity);

        List<CommitEntity> commitList = githubService.fetchCommits(start, end, MOCK_TOKEN);
        assertEquals(0,commitList.size());
    }

    @Test
    public void testFetchCommits_Error() {
        LocalDateTime start = LocalDateTime.of(2023, 12, 1, 12, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 3, 12, 0);

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenThrow(new RuntimeException("Simulated error"));

        assertThrows(RuntimeException.class, () -> githubService.fetchCommits(start, end, MOCK_TOKEN));
    }

    @Test
    void fetchOpenIssues() {
        LocalDateTime start = LocalDateTime.of(2023, 12, 1, 12, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 3, 12, 0);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add("Custom-Header", "Custom-Value");
        headers.add("Link", "Custom-Value");

        ResponseEntity<String> mockResponseEntity = new ResponseEntity<>(generateMockResponseOpenIssues(), headers, HttpStatus.OK);
        githubService = new GithubServiceImpl(restTemplate, githubServiceHelper);

        Mockito.when(restTemplate.exchange(
                Mockito.anyString(),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(HttpEntity.class),
                Mockito.eq(String.class),
                Mockito.anyMap()
        )).thenReturn(mockResponseEntity);

        when(githubServiceHelper.fetchAdditionalPageURLList(Mockito.anyMap()))
                .thenReturn(Collections.emptyList());
        Mockito.when(githubServiceHelper.formatDatesFetchedForIssues(Mockito.anyList()))
                .thenReturn(Collections.singletonList((generateIssueEntityMock())));

        List<IssueEntity> result = githubService.fetchOpenIssues(start, end, MOCK_TOKEN);
        assertEquals(1, result.size());
    }

    @Test
    public void testFetchOpenIssue_EmptyList() {
        LocalDateTime start = LocalDateTime.of(2023, 12, 1, 12, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 3, 12, 0);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add("Custom-Header", "Custom-Value");
        headers.add("Link", "Custom-Value");

        ResponseEntity<String> mockResponseEntity = new ResponseEntity<>("[]", headers, HttpStatus.OK);
        githubService = new GithubServiceImpl(restTemplate, githubServiceHelper);

        Mockito.when(restTemplate.exchange(
                Mockito.anyString(),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(HttpEntity.class),
                Mockito.eq(String.class),
                Mockito.anyMap()
        )).thenReturn(mockResponseEntity);

        List<IssueEntity> issueList = githubService.fetchOpenIssues(start, end, MOCK_TOKEN);
        assertEquals(0,issueList.size());
    }

    @Test
    public void testFetchOpenIssues_Error() {
        LocalDateTime start = LocalDateTime.of(2023, 12, 1, 12, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 3, 12, 0);

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class), Mockito.anyMap()))
                .thenThrow(new RuntimeException("Simulated error"));

        assertThrows(RuntimeException.class, () -> githubService.fetchOpenIssues(start, end, MOCK_TOKEN));
    }

    @Test
    void fetchCloseIssues() {
        LocalDateTime start = LocalDateTime.of(2023, 12, 1, 12, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 3, 12, 0);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add("Custom-Header", "Custom-Value");
        headers.add("Link", "Custom-Value");

        ResponseEntity<String> mockResponseEntity = new ResponseEntity<>(generateMockResponseCloseIssues(), headers, HttpStatus.OK);
        githubService = new GithubServiceImpl(restTemplate, githubServiceHelper);

        Mockito.when(restTemplate.exchange(
                Mockito.anyString(),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(HttpEntity.class),
                Mockito.eq(String.class),
                Mockito.anyMap()
        )).thenReturn(mockResponseEntity);

        when(githubServiceHelper.fetchAdditionalPageURLList(Mockito.anyMap()))
                .thenReturn(Collections.emptyList());
        Mockito.when(githubServiceHelper.formatDatesFetchedForIssues(Mockito.anyList()))
                .thenReturn(Collections.singletonList(generateIssueEntityMock()));

        List<IssueEntity> result = githubService.fetchCloseIssues(start, end, MOCK_TOKEN);
        assertEquals(1, result.size());
    }

    @Test
    public void testFetchCloseIssue_EmptyList() {
        LocalDateTime start = LocalDateTime.of(2023, 12, 1, 12, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 3, 12, 0);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add("Custom-Header", "Custom-Value");
        headers.add("Link", "Custom-Value");

        ResponseEntity<String> mockResponseEntity = new ResponseEntity<>("[]", headers, HttpStatus.OK);
        githubService = new GithubServiceImpl(restTemplate, githubServiceHelper);

        Mockito.when(restTemplate.exchange(
                Mockito.anyString(),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(HttpEntity.class),
                Mockito.eq(String.class),
                Mockito.anyMap()
        )).thenReturn(mockResponseEntity);

        List<IssueEntity> issueList = githubService.fetchCloseIssues(start, end, MOCK_TOKEN);
        assertEquals(0,issueList.size());
    }

    @Test
    public void testFetchCloseIssue_Error() {
        LocalDateTime start = LocalDateTime.of(2023, 12, 1, 12, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 3, 12, 0);

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class), Mockito.anyMap()))
                .thenThrow(new RuntimeException("Simulated error"));

        assertThrows(RuntimeException.class, () -> githubService.fetchCloseIssues(start, end, MOCK_TOKEN));
    }

    private String generateMockResponsePR() {
        return "[{\"url\": \"https://api.github.com/repos/octocat/Hello-World/pulls/2846\", \"id\": 1578671897, \"number\": 2846," +
                " \"state\": \"open\", \"title\": \"Create blank.yml\",\"user\": {\"login\": \"CW3-Root\",\"type\": \"User\"," +
                "\"site_admin\": false},\"created_at\": \"2023-10-30T09:35:16Z\",\"closed_at\": null,\"merged_at\": null}]";
    }

    private String generateMockResponseCommit() {
        return "[{\"commit\": {\"author\": {\"name\": \"Andrew Chow\",\"email\": \"github@achow101.com\",\"date\": \"2023-11-30T19:17:29Z\"}," +
                "\"committer\": {\"name\": \"Andrew Chow\",\"email\": \"github@achow101.com\",\"date\": \"2023-11-30T19:28:46Z\" }}," +
                "\"message\": \"Merge bitcoin/bitcoin#26762: bugfix: Make `CCheckQueue`\"," +
                " \"url\": \"https://api.github.com/repos/bitcoin/bitcoin/git/commits/498994b6f55d04a7940f832e7fbd17e5acdaff15\"}]";
    }

    private String generateMockResponseOpenIssues() {
        return "[{\"url\": \"https://api.github.com/repos/bitcoin/bitcoin/issues/28983\",\"id\": 2020711772,\"number\": 28983," +
                "\"title\": \"Stratum v2 Template Provider (take 2)\"," +
                "\"user\": {\"login\": \"Majors\",\"type\": \"User\",\"site_admin\": false},\"state\": \"open\"," +
                "\"created_at\": \"2023-12-01T11:39:27Z\",\"closed_at\": null," +
                "\"body\": \"Based on on  I rebased it and re-wrote the commit history. See the original branch for the evolution of the spec.\"}]";
    }

    private String generateMockResponseCloseIssues() {
        return "[{\"url\": \"https://api.github.com/repos/bitcoin/bitcoin/issues/28983\",\"id\": 2020711772,\"number\": 28983," +
                "\"title\": \"Stratum v2 Template Provider (take 2)\"," +
                "\"user\": {\"login\": \"Majors\",\"type\": \"User\",\"site_admin\": false},\"state\": \"closed\"," +
                "\"created_at\": \"2023-12-01T11:39:27Z\",\"closed_at\": \"2023-12-20T11:39:27Z\"," +
                "\"body\": \"Based on on  I rebased it and re-wrote the commit history. See the original branch for the evolution of the spec.\"}]";
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
}
