package com.manywho.services.einstein.einstein;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.manywho.sdk.api.jackson.ObjectMapperFactory;
import com.manywho.sdk.services.utils.Streams;
import com.manywho.services.einstein.ApplicationConfiguration;
import com.manywho.services.einstein.language.Probability;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.experimental.var;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EinsteinClient {
    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Inject
    public EinsteinClient(CloseableHttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public List<Probability> fetchPredictionResult(String token, String path, Map<String, String> parameters) {
        var entityBuilder = MultipartEntityBuilder.create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            entityBuilder.addTextBody(entry.getKey(), entry.getValue());
        }

        var post = new HttpPost("https://api.einstein.ai/v2" + path);
        post.addHeader("Authorization", "Bearer " + token);
        post.setEntity(entityBuilder.build());

        try (var response = httpClient.execute(post)) {
            var result = objectMapper.readTree(response.getEntity().getContent());

            // If the prediction was successful, then we return it
            if (response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() <= 299) {
                return Streams.asStream(result.get("probabilities"))
                        .map(probability -> new Probability(probability.get("label").asText(), probability.get("probability").asDouble()))
                        .collect(Collectors.toList());
            }

            // Otherwise throw an error
            throw new RuntimeException("There was an error retrieving the prediction: " + result.get("message").asText());
        } catch (IOException e) {
            throw new RuntimeException("Unable to load the prediction", e);
        }
    }

    public String getToken(String username, ApplicationConfiguration configuration) {
        // Try to parse and load the given private key
        Key key;
        try {
            var pemReader = new PemReader(new StringReader(configuration.getPrivateKey()));

            key = KeyFactory.getInstance("RSA", "BC")
                    .generatePrivate(new PKCS8EncodedKeySpec(pemReader.readPemObject().getContent()));
        } catch (Exception e) {
            throw new RuntimeException("Unable to load the private key", e);
        }

        // If the user has provided an account ID in the configuration, we use that as a stored credential
        if (Strings.isNullOrEmpty(username)) {
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

        try (var response = httpClient.execute(post)) {
            result = ObjectMapperFactory.create().readTree(response.getEntity().getContent());
        } catch (IOException e) {
            throw new RuntimeException("Unable to successfully get an access token", e);
        }

        if (result == null || result.hasNonNull("access_token") == false) {
            throw new RuntimeException("No token was returned from Einstein");
        }

        return result.get("access_token").asText();
    }
}