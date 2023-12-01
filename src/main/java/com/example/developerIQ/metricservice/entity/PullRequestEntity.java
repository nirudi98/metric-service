package com.example.developerIQ.metricservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

@Document(collection = "pull_requests")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PullRequestEntity implements Serializable {

    @Id
    private String id;
    private String url;
    private String username;
    private String title;
    private String pull_number;
    private Boolean is_merged;
    private String user_type;
    private String status;
    private LocalDateTime created_date;
    private LocalDateTime closed_date;
    private LocalDateTime merged_date;
}
