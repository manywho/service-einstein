package com.manywho.services.einstein.language;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.types.Type;

@Type.Element(name = "Probability")
public class Probability implements Type {
    @Type.Property(name = "Label", contentType = ContentType.String, bound = false)
    private String label;

    @Type.Property(name = "Probability", contentType = ContentType.Number, bound = false)
    private double probability;

    public Probability(String label, double probability) {
        this.label = label;
        this.probability = probability;
    }

    public String getLabel() {
        return this.label;
    }
}
