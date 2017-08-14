package com.manywho.services.einstein.einstein;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manywho.sdk.services.utils.Streams;
import com.manywho.services.einstein.language.Probability;
import lombok.experimental.var;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;

import javax.inject.Inject;
import java.io.IOException;
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
}