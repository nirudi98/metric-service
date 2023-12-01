package com.example.developerIQ.metricservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

@Document(collection = "issues")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class IssueEntity implements Serializable {

    @Id
    private String issue_id;

    private String title;
    private String state;
    private String username;
    private String description;
    private LocalDateTime created_date;
    private LocalDateTime closed_date;
}
