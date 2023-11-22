package uk.ac.ed.inf.GeoJsonFeatures;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Property {
    private String name;
    public Property(){
        this.name = "FlightPath";
    }
    public void setName(String name) {
        this.name = name;
    }
}
