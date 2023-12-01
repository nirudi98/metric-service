package com.example.developerIQ.metricservice.service;

import com.example.developerIQ.metricservice.entity.CommitEntity;
import com.example.developerIQ.metricservice.entity.IssueEntity;
import com.example.developerIQ.metricservice.entity.PullRequestEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GithubService {

    public List<PullRequestEntity> fetchPullRequests();
    public List<CommitEntity> fetchCommits();
    public List<IssueEntity> fetchOpenIssues();
    public List<IssueEntity> fetchCloseIssues();
}
