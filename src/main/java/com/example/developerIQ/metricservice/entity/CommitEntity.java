package com.example.developerIQ.metricservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

@Document(collection = "commits")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommitEntity implements Serializable {

    @Id
    private String commit_id;

    private String author_name;
    private String committer_name;
    private String url;
    private String message;
    private LocalDateTime authored_date;
    private LocalDateTime committed_date;
}
