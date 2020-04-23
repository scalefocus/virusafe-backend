package io.virusafe.configuration;

import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtConfiguration {

    private String bearerPrefix;
    private String authorizationHeaderName;
    @Getter(AccessLevel.NONE)
    private String secretKey;
    private String algorithm;
    private String numberClaim;
    private String createdClaim;
    private String userGuidClaim;
    private List<String> clientIds = new ArrayList<>();
    private String clientIdHeaderName;
    private Long tokenValidity;
    private Long refreshValidity;
    private String secretClaim;
    private String hashAlgorithm;

    public SecretKey getSecretKey() {
        byte[] secretKeyBytes = Base64.getDecoder().decode(secretKey);
        return new SecretKeySpec(secretKeyBytes, 0, secretKeyBytes.length,
                SignatureAlgorithm.forName(algorithm).getJcaName());
    }
}
