package com.example.developerIQ.metricservice.repository;

import com.example.developerIQ.metricservice.entity.PullRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PullRequestRepository extends MongoRepository<PullRequestEntity, String> {
}
