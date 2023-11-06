package uk.ac.ed.inf.restservice.data;

import java.util.List;

public record Geometry(String type, List<Double[]> coordinates) {
//    private String type;
//    private List<Double[]> coordinates;
//    public Geometry(){
//        type = "LineString";
//    }
//    public void setCoordinates(List<Double[]> coordinates) {
//        this.coordinates = coordinates;
//    }
}
