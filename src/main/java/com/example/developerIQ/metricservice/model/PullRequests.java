package com.example.developerIQ.metricservice.model;

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
public class PullRequests {

    @JsonProperty("id")
    private String id;

    @JsonProperty("url")
    private String pr;

    @JsonProperty("title")
    private String title;

    @JsonProperty("state")
    private String state;

    @JsonProperty("user")
    private RepoUsers users;

    private String created_at;

    private String closed_at;

    private String merged_at;

}
