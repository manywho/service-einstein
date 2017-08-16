package com.manywho.services.einstein.language;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.actions.Action;

@Action.Metadata(name = "Prediction for Intent (Simple)", summary = "Get an intent prediction for a given string", uri = "language/prediction/intentsimple")
public class PredictionForIntentSimple implements Action {
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
        @Action.Output(name = "Intent", contentType = ContentType.String)
        private String intent;

        public Outputs(String intent) {
            this.intent = intent;
        }
    }
}
