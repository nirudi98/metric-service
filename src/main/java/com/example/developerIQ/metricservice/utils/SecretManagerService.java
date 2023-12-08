package com.example.developerIQ.metricservice.utils;

import com.google.cloud.secretmanager.v1.AccessSecretVersionRequest;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class SecretManagerService {

    private final String projectId;
    private final String secretId;
    @Autowired
    public SecretManagerService(@Value("${gcp.project-id}") String projectId,
                                @Value("${gcp.secret-id}") String secretId) {
        this.projectId = projectId;
        this.secretId = secretId;
    }

    public String getSecret() {
        System.out.println("inside get secret ");
        try (SecretManagerServiceClient client = SecretManagerServiceClient.create()) {
            String secretName = String.format("projects/%s/secrets/%s/versions/latest", projectId, secretId);
            System.out.println("inside try " + projectId + secretId);

            AccessSecretVersionRequest request =
                    AccessSecretVersionRequest.newBuilder().setName(secretName).build();

            SecretPayload payload = client.accessSecretVersion(request).getPayload();
            return new String(payload.getData().toByteArray(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to access secret from Secret Manager", e);
        }
    }
}
