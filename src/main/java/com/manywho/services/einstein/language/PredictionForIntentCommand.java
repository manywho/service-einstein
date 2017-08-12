package com.manywho.services.einstein.language;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.manywho.sdk.api.jackson.ObjectMapperFactory;
import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.api.security.AuthenticatedWho;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;
import com.manywho.sdk.services.utils.Streams;
import com.manywho.services.einstein.ApplicationConfiguration;
import com.manywho.services.einstein.language.PredictionForIntent.Inputs;
import com.manywho.services.einstein.language.PredictionForIntent.Outputs;
import lombok.experimental.var;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.stream.Collectors;

public class PredictionForIntentCommand implements ActionCommand<ApplicationConfiguration, PredictionForIntent, Inputs, Outputs> {
    private final Provider<AuthenticatedWho> authenticatedWhoProvider;

    @Inject
    public PredictionForIntentCommand(Provider<AuthenticatedWho> authenticatedWhoProvider) {
        this.authenticatedWhoProvider = authenticatedWhoProvider;
    }

    @Override
    public ActionResponse<Outputs> execute(ApplicationConfiguration configuration, ServiceRequest serviceRequest, Inputs inputs) {
        var entity = MultipartEntityBuilder.create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .addTextBody("document", inputs.getDocument())
                .addTextBody("modelId", inputs.getModel())
                .build();

        var post = new HttpPost("https://api.einstein.ai/v2/language/intent");
        post.addHeader("Authorization", "Bearer " + authenticatedWhoProvider.get().getToken());
        post.setEntity(entity);

        JsonNode result;

        try (var response = HttpClients.createDefault().execute(post)) {
            result = ObjectMapperFactory.create().readTree(response.getEntity().getContent());
        } catch (IOException e) {
            throw new RuntimeException("Unable to load the intent", e);
        }

        var probabilities = Streams.asStream(result.get("probabilities"))
                .map(probability -> new Probability(probability.get("label").asText(), probability.get("probability").asDouble()))
                .collect(Collectors.toList());

        return new ActionResponse<>(new Outputs(probabilities));
    }
}
