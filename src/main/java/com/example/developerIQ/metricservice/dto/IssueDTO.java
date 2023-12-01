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
public class IssueDTO {
    @JsonProperty("id")
    private String id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("state")
    private String state;

    @JsonProperty("user")
    private RepoUsers users;

    @JsonProperty("created_at")
    private String createdDate;

    @JsonProperty("closed_at")
    private String closedDate;

    @JsonProperty("body")
    private String description;

}
