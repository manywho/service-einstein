package com.manywho.services.einstein.vision;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.actions.Action;
import com.manywho.services.einstein.language.Probability;

import java.util.List;

@Action.Metadata(name = "Prediction with Image URL", summary = "Get a prediction for an image specified by URL", uri = "vision/prediction/image/url")
public class PredictionWithImageUrl implements Action {
    public static class Inputs {
        @Action.Input(name = "URL", contentType = ContentType.String)
        private String url;

        @Action.Input(name = "Model ID", contentType = ContentType.String)
        private String model;

        public String getUrl() {
            return url;
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