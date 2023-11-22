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

        // new a feature collector
        FeatureCollection featureCollection = new FeatureCollection();
        //set coordinate by stream and collector
        List<Double[]> coordinates = new ArrayList<>();
        for (FlightPath flightPath : flightPaths) {
            coordinates.add(new Double[]{flightPath.fromLongitude(), flightPath.fromLatitude()}) ;
        }
        // new a Geometry to contain coordinates
        Geometry geometry = new Geometry(coordinates);
        // initialize the type to line string
        geometry.setType("LineString");
        //new a new feature
        GeoJSONFeature feature = new GeoJSONFeature();
        // set feature type -> Feature
        feature.setType("Feature");
        // set geometry into feature
        feature.setGeometry(geometry);
        feature.setProperties(new Property());
        // set this feature to feature collector
        featureCollection.addFeature(feature);
        // Add properties if needed
        // feature.setProperties(properties);

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
