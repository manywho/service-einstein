package com.manywho.services.einstein.language;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.actions.Action;

import java.util.List;

@Action.Metadata(name = "Prediction for Intent", summary = "Create an intent prediction for a given string", uri = "language/prediction/intent")
public class PredictionForIntent implements Action {
    public static class Inputs {
        @Action.Input(name = "Document", contentType = ContentType.String)
        private String document;

        @Action.Input(name = "Model ID", contentType = ContentType.String)
        private String model;

        public String getDocument() {
            return document;
        }

        public String getModel() {
            return model;
        }
    }

    public static class Outputs {
        @Action.Output(name = "Probabilities", contentType = ContentType.List)
        private List<Probability> probabilities;

        public Outputs(List<Probability> probabilities) {
            this.probabilities = probabilities;
        }
    }
}
