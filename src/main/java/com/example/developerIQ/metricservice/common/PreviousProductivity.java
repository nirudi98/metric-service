package com.example.developerIQ.metricservice.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PreviousProductivity {

    private int previous_pr_count;
    private int previous_open_issue_count;
    private int previous_closed_issue_count;
    private int previous_commit_count;
}
