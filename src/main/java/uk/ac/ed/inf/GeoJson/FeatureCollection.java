package uk.ac.ed.inf.GeoJson;

import java.util.ArrayList;
import java.util.List;

public class FeatureCollection {
    private String type;
    private List<GeoJSONFeature> features;

    public FeatureCollection() {
        this.type = "FeatureCollection";
        this.features = new ArrayList<>();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<GeoJSONFeature> getFeatures() {
        return features;
    }

    public void setFeatures(List<GeoJSONFeature> features) {
        this.features = features;
    }

    public void addFeature(GeoJSONFeature feature) {
        this.features.add(feature);
    }
}
