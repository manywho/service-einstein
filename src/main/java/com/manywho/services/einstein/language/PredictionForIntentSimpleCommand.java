package com.manywho.services.einstein.language;

import com.google.inject.Provider;
import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.api.security.AuthenticatedWho;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;
import com.manywho.services.einstein.ApplicationConfiguration;
import com.manywho.services.einstein.einstein.EinsteinClient;
import com.manywho.services.einstein.language.PredictionForIntentSimple.Inputs;
import com.manywho.services.einstein.language.PredictionForIntentSimple.Outputs;
import lombok.experimental.var;

import javax.inject.Inject;
import java.util.HashMap;

public class PredictionForIntentSimpleCommand implements ActionCommand<ApplicationConfiguration, PredictionForIntentSimple, Inputs, Outputs> {
    private final Provider<AuthenticatedWho> authenticatedWhoProvider;
    private final EinsteinClient einsteinClient;

    @Inject
    public PredictionForIntentSimpleCommand(Provider<AuthenticatedWho> authenticatedWhoProvider, EinsteinClient einsteinClient) {
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

        var probabilities = einsteinClient.fetchPredictionResult(token, "/language/intent", parameters);

        String intent;

        // Get the top probability from the list, otherwise reply with a blank
        if (probabilities.isEmpty()) {
            intent = "";
        } else {
            intent = probabilities.get(0).getLabel();
        }

        return new ActionResponse<>(new Outputs(intent));
    }
}
