package com.example.developerIQ.metricservice.dto.commits;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NestedCommits {

    @JsonProperty("committer")
    private Committer commit_committer;

    @JsonProperty("author")
    private Committer commit_author;

    @JsonProperty("message")
    private String message;
}
