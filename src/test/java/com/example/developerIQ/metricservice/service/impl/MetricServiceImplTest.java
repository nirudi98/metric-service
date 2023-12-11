package com.example.developerIQ.metricservice.service.impl;

import com.example.developerIQ.metricservice.common.PreviousProductivity;
import com.example.developerIQ.metricservice.common.productivityRequest.PreviousRequestBody;
import com.example.developerIQ.metricservice.configure.RestTemplateConfig;
import com.example.developerIQ.metricservice.entity.CommitEntity;
import com.example.developerIQ.metricservice.entity.IssueEntity;
import com.example.developerIQ.metricservice.entity.PullRequestEntity;
import com.example.developerIQ.metricservice.repository.CommitRepository;
import com.example.developerIQ.metricservice.repository.IssueRepository;
import com.example.developerIQ.metricservice.repository.PullRequestRepository;
import com.example.developerIQ.metricservice.utils.AuthenticateUser;
import com.example.developerIQ.metricservice.utils.SecretManagerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.example.developerIQ.metricservice.constants.TestConstants.MOCK_TOKEN;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Import({RestTemplateConfig.class})
@ExtendWith(MockitoExtension.class)
class MetricServiceImplTest {

    @Mock
    private GithubServiceImpl githubService;

    @Mock
    private PullRequestRepository pullRequestRepository;

    @Mock
    private CommitRepository commitRepository;

    @Mock
    private IssueRepository issueRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private MetricServiceImpl metricService;

    @Mock
    private AuthenticateUser authenticateUser;

    @Test
    void saveAllMetrics() {
        String start = "2023-12-01";
        String end = "2023-12-12";

        List<PullRequestEntity> mockPullRequestEntityList = new ArrayList<>();
        List<CommitEntity> mockCommitEntityList = new ArrayList<>();
        List<IssueEntity> mockOpenIssueEntityList = new ArrayList<>();
        List<IssueEntity> mockCloseIssueEntityList = new ArrayList<>();

        PullRequestEntity pr1 = generatePREntityMock();
        CommitEntity commit1 = generateCommitEntityMock();
        IssueEntity openIssue1 = generateIssueEntityMock();
        IssueEntity closeIssue1 = generateCloseIssueEntityMock();

        mockPullRequestEntityList.add(pr1);
        mockCommitEntityList.add(commit1);
        mockOpenIssueEntityList.add(openIssue1);
        mockCloseIssueEntityList.add(closeIssue1);

        when(githubService.fetchPullRequests(any(), any(), anyString())).thenReturn(mockPullRequestEntityList);
        when(githubService.fetchCommits(any(), any(), anyString())).thenReturn(mockCommitEntityList);
        when(githubService.fetchOpenIssues(any(), any(), anyString())).thenReturn(mockOpenIssueEntityList);
        when(githubService.fetchCloseIssues(any(), any(), anyString())).thenReturn(mockCloseIssueEntityList);
        when(authenticateUser.decodeString()).thenReturn(MOCK_TOKEN);

        ResponseEntity<String> result = metricService.saveAllMetrics(start, end);

        assertEquals("Pull Requests/ Commits/ Issues Saved Successfully", result.getBody());

        // Verify that save method is called for each entity in the lists
        for (PullRequestEntity mockPullRequestEntity : mockPullRequestEntityList) {
            verify(pullRequestRepository).save(mockPullRequestEntity);
        }

        for (CommitEntity mockCommitEntity : mockCommitEntityList) {
            verify(commitRepository).save(mockCommitEntity);
        }

        for (IssueEntity mockOpenIssueEntity : mockOpenIssueEntityList) {
            verify(issueRepository).save(mockOpenIssueEntity);
        }

        for (IssueEntity mockCloseIssueEntity : mockCloseIssueEntityList) {
            verify(issueRepository).save(mockCloseIssueEntity);
        }
    }

    @Test
    void savePullRequests() {
        PullRequestEntity one = generatePREntityMock();
        PullRequestEntity two = generatePREntityMock();

        String start = "2023-12-01";
        String end = "2023-12-12";

        List<PullRequestEntity> mockPullRequests = new ArrayList<>();
        mockPullRequests.add(one);
        mockPullRequests.add(two);

        when(githubService.fetchPullRequests(any(), any(), anyString())).thenReturn(mockPullRequests);
        when(authenticateUser.decodeString()).thenReturn(MOCK_TOKEN);

        ResponseEntity<String> result = metricService.savePullRequests(start, end);

        assertEquals("Pull Requests Saved Successfully", result.getBody());
        verify(pullRequestRepository, Mockito.times(mockPullRequests.size())).save(any());
    }

    @Test
    public void testSavePullRequests_EmptyList() {
        String start = "2023-12-01";
        String end = "2023-12-12";

        List<PullRequestEntity> mockList = null;
        when(githubService.fetchPullRequests(any(), any(), anyString())).thenReturn(mockList);
        when(authenticateUser.decodeString()).thenReturn(MOCK_TOKEN);

        ResponseEntity<String> response = metricService.savePullRequests(start, end);
        assertEquals("PR list is empty, nothing saved", response.getBody());
        verify(pullRequestRepository, Mockito.never()).save(any());
    }

    @Test
    void saveCommits() {
        String start = "2023-12-01";
        String end = "2023-12-12";

        CommitEntity one = generateCommitEntityMock();
        CommitEntity two = generateCommitEntityMock();

        List<CommitEntity> mockCommits = new ArrayList<>();
        mockCommits.add(one);
        mockCommits.add(two);

        when(githubService.fetchCommits(any(), any(), anyString())).thenReturn(mockCommits);
        when(authenticateUser.decodeString()).thenReturn(MOCK_TOKEN);

        ResponseEntity<String> result = metricService.saveCommits(start, end);

        assertEquals("Commits Saved Successfully", result.getBody());
        verify(commitRepository, Mockito.times(mockCommits.size())).save(any());
    }

    @Test
    public void testSaveCommits_EmptyList() {
        String start = "2023-12-01";
        String end = "2023-12-12";

        List<CommitEntity> mockList = null;
        when(githubService.fetchCommits(any(), any(), anyString())).thenReturn(mockList);
        when(authenticateUser.decodeString()).thenReturn(MOCK_TOKEN);

        ResponseEntity<String> response = metricService.saveCommits(start, end);
        assertEquals("Commits list is empty, nothing saved", response.getBody());
        verify(commitRepository, Mockito.never()).save(any());
    }

    @Test
    void saveIssues() {
        String start = "2023-12-01";
        String end = "2023-12-12";

        IssueEntity one = generateIssueEntityMock();
        IssueEntity two = generateCloseIssueEntityMock();

        List<IssueEntity> mockCommitsOpen = new ArrayList<>();
        mockCommitsOpen.add(one);

        List<IssueEntity> mockCommitsClose = new ArrayList<>();
        mockCommitsClose.add(two);

        List<IssueEntity> all = new ArrayList<>();
        all.addAll(mockCommitsOpen);
        all.addAll(mockCommitsClose);

        when(githubService.fetchOpenIssues(any(), any(), anyString())).thenReturn(mockCommitsOpen);
        when(githubService.fetchCloseIssues(any(), any(), anyString())).thenReturn(mockCommitsClose);
        when(authenticateUser.decodeString()).thenReturn(MOCK_TOKEN);

        ResponseEntity<String> result = metricService.saveIssues(start, end);

        assertEquals("Issues Saved Successfully", result.getBody());
        verify(issueRepository, Mockito.times(all.size())).save(any());
    }

    @Test
    public void testSaveIssues_EmptyList() {
        String start = "2023-12-01";
        String end = "2023-12-12";

        List<IssueEntity> mockListOpen = null;
        List<IssueEntity> mockListClose = null;
        when(githubService.fetchOpenIssues(any(), any(), anyString())).thenReturn(mockListOpen);
        when(githubService.fetchCloseIssues(any(), any(), anyString())).thenReturn(mockListClose);
        when(authenticateUser.decodeString()).thenReturn(MOCK_TOKEN);

        ResponseEntity<String> response = metricService.saveIssues(start, end);
        assertEquals("Issue list is empty, nothing saved", response.getBody());
        verify(issueRepository, Mockito.never()).save(any());
    }

    @Test
    void fetchPrevious() {
        String startedDate = "2023-01-01";
        String endedDate = "2023-01-10";

        PreviousProductivity mockPreviousProductivity = generatePrevious();
        when(restTemplate.getForObject(Mockito.anyString(), Mockito.eq(PreviousProductivity.class)))
                .thenReturn(mockPreviousProductivity);

        PreviousRequestBody previousRequestBody = new PreviousRequestBody(startedDate, endedDate);
        PreviousProductivity result = metricService.fetchPrevious(previousRequestBody);

        assertEquals(mockPreviousProductivity, result);
    }

    @Test
    public void testFetchPreviousWithRestTemplateError() {
        String startedDate = "2023-01-01";
        String endedDate = "2023-01-10";

        when(restTemplate.getForObject(Mockito.anyString(), Mockito.eq(PreviousProductivity.class)))
                .thenThrow(new RuntimeException("Simulated error"));

        assertThrows(RuntimeException.class, () -> metricService.fetchPrevious(new PreviousRequestBody(startedDate, endedDate)));
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

}