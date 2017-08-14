package com.manywho.services.einstein.language;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.actions.Action;

import java.util.List;

@Action.Metadata(name = "Prediction for Sentiment", summary = "Get a sentiment prediction for a given string", uri = "language/prediction/sentiment")
public class PredictionForSentiment implements Action {
    public static class Inputs {
        @Input(name = "Document", contentType = ContentType.String)
        private String document;

        @Input(name = "Model ID", contentType = ContentType.String)
        private String model;

        public String getDocument() {
            return document;
        }

        public String getModel() {
            return model;
        }
    }

    public static class Outputs {
        @Output(name = "Probabilities", contentType = ContentType.List)
        private List<Probability> probabilities;

        public Outputs(List<Probability> probabilities) {
            this.probabilities = probabilities;
        }
    }
}
