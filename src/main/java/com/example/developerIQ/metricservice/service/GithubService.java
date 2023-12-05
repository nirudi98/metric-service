package com.example.developerIQ.metricservice.service;

import com.example.developerIQ.metricservice.entity.CommitEntity;
import com.example.developerIQ.metricservice.entity.IssueEntity;
import com.example.developerIQ.metricservice.entity.PullRequestEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface GithubService {

    public List<PullRequestEntity> fetchPullRequests(LocalDateTime start, LocalDateTime end, String token_git);
    public List<CommitEntity> fetchCommits(LocalDateTime start, LocalDateTime end, String token_git);
    public List<IssueEntity> fetchOpenIssues(LocalDateTime start, LocalDateTime end, String token_git);
    public List<IssueEntity> fetchCloseIssues(LocalDateTime start, LocalDateTime end, String token_git);
}
