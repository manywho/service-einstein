package com.manywho.services.einstein.vision;

import com.google.inject.Provider;
import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.api.security.AuthenticatedWho;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;
import com.manywho.services.einstein.ApplicationConfiguration;
import com.manywho.services.einstein.einstein.EinsteinClient;
import com.manywho.services.einstein.vision.PredictionWithImageUrl.Inputs;
import com.manywho.services.einstein.vision.PredictionWithImageUrl.Outputs;
import lombok.experimental.var;

import javax.inject.Inject;
import java.util.HashMap;

public class PredictionWithImageUrlCommand implements ActionCommand<ApplicationConfiguration, PredictionWithImageUrl, Inputs, Outputs> {
    private final Provider<AuthenticatedWho> authenticatedWhoProvider;
    private final EinsteinClient einsteinClient;

    @Inject
    public PredictionWithImageUrlCommand(Provider<AuthenticatedWho> authenticatedWhoProvider, EinsteinClient einsteinClient) {
        this.authenticatedWhoProvider = authenticatedWhoProvider;
        this.einsteinClient = einsteinClient;
    }

    @Override
    public ActionResponse<Outputs> execute(ApplicationConfiguration configuration, ServiceRequest request, Inputs inputs) {
        var token = authenticatedWhoProvider.get().getToken();

        // If the user is running in public mode, we need to get the token using the configuration information
        if (token != null && token.equalsIgnoreCase("none")) {
            token = einsteinClient.getToken(null, configuration);
        }

        var parameters = new HashMap<String, String>();
        parameters.put("modelId", inputs.getModel());
        parameters.put("sampleLocation", inputs.getUrl());

        var probabilities = einsteinClient.fetchPredictionResult(token, "/vision/predict", parameters);

        return new ActionResponse<>(new Outputs(probabilities));
    }


}