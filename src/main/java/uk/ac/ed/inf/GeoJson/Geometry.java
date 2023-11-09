package uk.ac.ed.inf.GeoJson;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Geometry {
    private String type;
    private List<Double[]> coordinates;

    // Default constructor
    public Geometry(List<Double[]> coordinates) {
        this.type = "LineString";
        this.coordinates = coordinates;
    }

    // Constructors, getters, and setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Double[]> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Double[]> coordinates) {
        this.coordinates = coordinates;
    }

    public void addCoordinate(Double[] coordinate) {
        this.coordinates.add(coordinate);
    }
}
