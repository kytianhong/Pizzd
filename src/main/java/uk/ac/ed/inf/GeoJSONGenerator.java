package uk.ac.ed.inf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import uk.ac.ed.inf.GeoJsonFeatures.FeatureCollection;
import uk.ac.ed.inf.GeoJsonFeatures.GeoJSONFeature;
import uk.ac.ed.inf.GeoJsonFeatures.Geometry;
import uk.ac.ed.inf.GeoJsonFeatures.Property;
import uk.ac.ed.inf.restservice.data.FlightPath;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GeoJSONGenerator {
    public void generatorGeoJSON(List<FlightPath> flightPaths, LocalDate date) {
        List<Double[]> coordinates = new ArrayList<>();//set coordinate by stream and collector
        for (FlightPath flightPath : flightPaths) {
            //add coordinate of each position to coordinate list
            coordinates.add(new Double[]{flightPath.fromLongitude(), flightPath.fromLatitude()}) ;
        }
        // new a Geometry to contain coordinates
        Geometry geometry = new Geometry(coordinates);
        geometry.setType("LineString");// initialize the type to line string
        // new a new feature to contain geometry and property
        GeoJSONFeature feature = new GeoJSONFeature();
        feature.setType("Feature");// set feature type -> Feature
        feature.setGeometry(geometry); // set geometry into feature
        feature.setProperties(new Property());// set property into feature
        // new a feature collector
        FeatureCollection featureCollection = new FeatureCollection();
        featureCollection.addFeature(feature);// set this feature to FeatureCollector

        try {
            String formattedDate = date.toString();
            String fileName = "drone-" + formattedDate + ".geojson";
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            // Serialize the GeoJSON feature to a file
            mapper.writeValue(new File("resultfiles/"+fileName), featureCollection);
            System.out.println("Drone routine saved to resultfiles/"+fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
