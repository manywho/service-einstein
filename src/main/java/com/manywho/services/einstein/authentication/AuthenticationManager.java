package com.manywho.services.einstein.authentication;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Sets;
import com.manywho.sdk.api.jackson.ObjectMapperFactory;
import com.manywho.sdk.api.security.AuthenticatedWhoResult;
import com.manywho.sdk.api.security.AuthenticationCredentials;
import com.manywho.sdk.services.configuration.ConfigurationParser;
import com.manywho.services.einstein.ApplicationConfiguration;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.experimental.var;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.bouncycastle.util.io.pem.PemReader;

import javax.inject.Inject;
import java.io.IOException;
import java.io.StringReader;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class AuthenticationManager {
    private final ConfigurationParser configurationParser;

    @Inject
    public AuthenticationManager(ConfigurationParser configurationParser) {
        this.configurationParser = configurationParser;
    }

    public AuthenticatedWhoResult authenticate(AuthenticationCredentials credentials) {
        ApplicationConfiguration configuration = configurationParser.from(credentials);

        // Get the token for this user
        String token = getToken(credentials.getUsername(), configuration);

        // If we have a token, we're logged in
        if (token != null &&
            token.trim().length() > 0) {
            AuthenticatedWhoResult authenticatedWhoResult = new AuthenticatedWhoResult();
            authenticatedWhoResult.setDirectoryId("einstein");
            authenticatedWhoResult.setDirectoryName("Einstein");
            authenticatedWhoResult.setEmail(credentials.getUsername());
            authenticatedWhoResult.setFirstName("Einstein");
            authenticatedWhoResult.setIdentityProvider("?");
            authenticatedWhoResult.setLastName("User");
            authenticatedWhoResult.setStatus(AuthenticatedWhoResult.AuthenticationStatus.Authenticated);
            authenticatedWhoResult.setTenantName("?");
            authenticatedWhoResult.setToken(token);
            authenticatedWhoResult.setUserId(credentials.getUsername());
            authenticatedWhoResult.setUsername(credentials.getUsername());

            return authenticatedWhoResult;
        }

        return AuthenticatedWhoResult.createDeniedResult();
    }

    public static String getToken(String username, ApplicationConfiguration configuration) {
        // Try to parse and load the given private key
        Key key;
        try {
            var pemReader = new PemReader(new StringReader(configuration.getPrivateKey()));

            key = KeyFactory.getInstance("RSA", "BC")
                    .generatePrivate(new PKCS8EncodedKeySpec(pemReader.readPemObject().getContent()));
        } catch (Exception e) {
            throw new RuntimeException("Unable to load the private key", e);
        }

        // If the user has provided an account id in the configuration, we use that as a stored credential
        if (username == null ||
            username.trim().length() == 0) {
            username = configuration.getAccountId();
        }

        // Generate a JWT assertion
        var assertion = Jwts.builder()
                .setSubject(username)
                .setAudience("https://api.einstein.ai/v2/oauth2/token")
                .setExpiration(Date.from(LocalDateTime.now().plusDays(2).toInstant(ZoneOffset.UTC)))
                .signWith(SignatureAlgorithm.RS256, key)
                .compact();

        // Send the JWT assertion to the API and get a token back
        var post = new HttpPost("https://api.einstein.ai/v2/oauth2/token");
        post.setEntity(new UrlEncodedFormEntity(Sets.newHashSet(
                new BasicNameValuePair("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer"),
                new BasicNameValuePair("assertion", assertion)
        )));

        JsonNode result;

        try (var response = HttpClients.createDefault().execute(post)) {
            result = ObjectMapperFactory.create().readTree(response.getEntity().getContent());
        } catch (IOException e) {
            throw new RuntimeException("Unable to successfully get an access token", e);
        }

        return result.get("access_token").asText();
    }
}
