package uk.ac.ed.inf.GeoJsonFeatures;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GeoJSONFeature {
    private String type;
    private Geometry geometry;
    private Property properties;

    // Constructors, getters, and setters
    public GeoJSONFeature() {
        this.type = "Feature";
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public Object getProperties() {
        return properties;
    }

    public void setProperties(Property properties) {
        this.properties = properties;
    }
}

