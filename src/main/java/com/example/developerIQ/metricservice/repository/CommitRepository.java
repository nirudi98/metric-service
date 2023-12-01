package com.example.developerIQ.metricservice.repository;

import com.example.developerIQ.metricservice.entity.CommitEntity;
import com.example.developerIQ.metricservice.entity.PullRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommitRepository extends MongoRepository<CommitEntity, Integer> {
}
