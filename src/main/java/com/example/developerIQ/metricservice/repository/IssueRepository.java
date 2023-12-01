package com.example.developerIQ.metricservice.repository;

import com.example.developerIQ.metricservice.entity.IssueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IssueRepository extends MongoRepository<IssueEntity, Integer> {
}
