package com.example.developerIQ.metricservice.integration.repository;

import com.example.developerIQ.metricservice.repository.CommitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@DataMongoTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CommitRepositoryTest {

    @Container
    @ServiceConnection
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:5.0");

    @Autowired
    CommitRepository commitRepository;

    @BeforeEach
    void setUp() {
        LocalDateTime started = LocalDateTime.parse("2023-10-01" +
                " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
        LocalDateTime ended = LocalDateTime.parse("2023-10-13" +
                " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));

//        List<CommitEntity> commitEntityList = List.of(new CommitEntity
//                ("123456","author", "committer",
//                        "commit@gmail.com","this is test commit",started, ended));
//        commitRepository.saveAll(commitEntityList);
    }

    @Test
    void connectionEstablished() {
        assertThat(mongoDBContainer.isCreated()).isTrue();
        assertThat(mongoDBContainer.isRunning()).isTrue();
    }

//    @Test
//    void shouldReturnUserByUsername() {
//        CommitEntity commitEntity = commitRepository.findCommitByName("committer").stream()
//                .findFirst().orElseThrow(() -> new NoSuchElementException("No CommitEntity found in the list"));
//        assertEquals("commit@gmail.com", commitEntity.getUrl(), "User url" +
//                "should be 'commit@gmail.com'");
//    }

}
