package com.manywho.services.einstein.language;

import com.google.inject.Provider;
import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.api.security.AuthenticatedWho;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;
import com.manywho.services.einstein.ApplicationConfiguration;
import com.manywho.services.einstein.einstein.EinsteinClient;
import com.manywho.services.einstein.language.PredictionForIntent.Inputs;
import com.manywho.services.einstein.language.PredictionForIntent.Outputs;
import lombok.experimental.var;

import javax.inject.Inject;
import java.util.HashMap;

public class PredictionForSentimentCommand implements ActionCommand<ApplicationConfiguration, PredictionForSentiment, Inputs, Outputs> {
    private final Provider<AuthenticatedWho> authenticatedWhoProvider;
    private final EinsteinClient einsteinClient;

    @Inject
    public PredictionForSentimentCommand(Provider<AuthenticatedWho> authenticatedWhoProvider, EinsteinClient einsteinClient) {
        this.authenticatedWhoProvider = authenticatedWhoProvider;
        this.einsteinClient = einsteinClient;
    }

    @Override
    public ActionResponse<Outputs> execute(ApplicationConfiguration configuration, ServiceRequest serviceRequest, Inputs inputs) {
        var token = authenticatedWhoProvider.get().getToken();

        // If the user is running in public mode, we need to get the token using the configuration information
        if (token != null && token.equalsIgnoreCase("none")) {
            token = einsteinClient.getToken(null, configuration);
        }

        var parameters = new HashMap<String, String>();
        parameters.put("document", inputs.getDocument());
        parameters.put("modelId", inputs.getModel());

        var probabilities = einsteinClient.fetchPredictionResult(token, "/language/sentiment", parameters);

        return new ActionResponse<>(new Outputs(probabilities));
    }
}
