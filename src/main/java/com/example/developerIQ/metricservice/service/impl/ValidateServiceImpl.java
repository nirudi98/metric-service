package com.example.developerIQ.metricservice.service.impl;

import com.example.developerIQ.metricservice.service.ValidateService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;

@Service
public class ValidateServiceImpl implements ValidateService {

    private static final Logger logger = LoggerFactory.getLogger(ValidateServiceImpl.class);

    @Value("${jwt.secret.key}")
    private String SECRET_KEY;

    @Override
    public Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        }
        catch(JwtException e){
           logger.error("WT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.");
           return false;
        }
    }

    public Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
