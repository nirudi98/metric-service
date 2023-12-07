package com.example.developerIQ.metricservice.utils;

import com.example.developerIQ.metricservice.common.AuthenticationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

import static com.example.developerIQ.metricservice.utils.constants.Constants.GENERATE_TOKEN_REQUEST;


@Service
public class AuthenticateUser {

    @Value("${auth-service.url}")
    private String auth_service_url;

    @Value("${github.secret}")
    private String git_secret;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticateUser.class);

    @Autowired
    private RestTemplate restTemplate;

    public String validateTokenAuthService() {
        try{
            AuthenticationRequest authenticationRequest = new AuthenticationRequest();
            authenticationRequest.setUsername("Sula");
            authenticationRequest.setPassword("sulas");

            String complete_url = auth_service_url + GENERATE_TOKEN_REQUEST;

            ResponseEntity<String> responseToken = restTemplate.postForEntity(complete_url, authenticationRequest, String.class);

            return responseToken.getBody();

        } catch(RuntimeException e){
            logger.error("calling auth service failed ", e);
            throw new RuntimeException();
        }
    }

    public String decodeString() {
        // Base64 encoded string
        String encodedString = git_secret;

        // Decoding the Base64 string
        String decodedString = decodeBase64(encodedString);

        // Displaying the decoded string
        System.out.println("Decoded String: " + decodedString);
        return decodedString;
    }

    public String decodeBase64(String encodedString) {
        // Decoding the Base64 string
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);

        // Converting the decoded bytes to a string
        return new String(decodedBytes);
    }
}
