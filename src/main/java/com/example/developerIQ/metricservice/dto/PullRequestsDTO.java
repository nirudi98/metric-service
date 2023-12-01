package com.example.developerIQ.metricservice.dto;

import com.example.developerIQ.metricservice.model.RepoUsers;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PullRequestsDTO {

    private String id;
    private String url;
    private String title;
    private String state;
    private String number;
    private RepoUsers user;
    private String created_at;
    private String closed_at;
    private String merged_at;
}
