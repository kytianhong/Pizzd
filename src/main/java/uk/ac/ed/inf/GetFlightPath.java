package uk.ac.ed.inf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import uk.ac.ed.inf.Algorithm.Astar;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.restservice.data.Feature;
import uk.ac.ed.inf.restservice.data.FlightPath;
import uk.ac.ed.inf.restservice.data.Geometry;
import uk.ac.ed.inf.restservice.data.ToWriteFlight;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GetFlightPath {
    private static final LngLat APPLETON =new LngLat(-3.186874, 55.944494);
    private static final String NO_FLY_ZONE_URL = "noFlyZones";
    private static final String CENTRAL_AREA_URL = "centralArea";
    public static NamedRegion[] getNonFlyZones(String baseUrl){
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            NamedRegion[] nonFlyZones = mapper.readValue(new URL(baseUrl + NO_FLY_ZONE_URL), NamedRegion[].class);
            System.out.println("read all NO FLY ZONE");
            System.out.println(nonFlyZones[0].vertices()[2]);
            return nonFlyZones;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public static NamedRegion getCentralArea(String baseUrl){
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            NamedRegion central = mapper.readValue(new URL(baseUrl + CENTRAL_AREA_URL), NamedRegion.class);
            System.out.println("read all central area");
            System.out.println(central.vertices()[1]);
            return central;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<ToWriteFlight> getFlightPath(String baseUrl, LocalDate date){
        // call order process to get the day's order and its corresponding destination
        Map<Order, LngLat> validatedOrder = new OrderProcess().getValidOrder(baseUrl,date);
        //call get nonFlyZone and central method
        NamedRegion[] nonFlyZones = getNonFlyZones(baseUrl);
        NamedRegion central = getCentralArea(baseUrl);
        //new a list which contain the final flight path list of all orders
        List<FlightPath> flightList=new ArrayList<>();
        List<ToWriteFlight> toWriteFlights = new ArrayList<>();
        for (Order i :validatedOrder.keySet()) {
            // get the corresponding destination
            LngLat destination = validatedOrder.get(i);
            //get the flight path of order i
            List<FlightPath> flightPaths = Astar.aStarSearch(APPLETON,destination,central,nonFlyZones);
            // rebuild the write_flight_path
            for (int j = 0;j<flightPaths.size()-1;j++){
                ToWriteFlight f = new ToWriteFlight(i.getOrderNo(),
                        flightPaths.get(j).fromLongitude(),
                        flightPaths.get(j).fromLatitude(),
                        flightPaths.get(j+1).angle(),
                        flightPaths.get(j+1).fromLongitude(),
                        flightPaths.get(j+1).fromLatitude());
                toWriteFlights.add(f);
            }
            //add all flight path of current order into final flight path list
            flightList.addAll(flightPaths);
        }
        writeFlightPath( toWriteFlights, date, validatedOrder );

        return toWriteFlights;
    }
    public static void writeFlightPath(List<ToWriteFlight> flightList, LocalDate date,Map<Order, LngLat> validatedOrder ){
        //write file
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            String formattedDate = date.toString();
            String fileName = "flightpath-" + formattedDate + ".json";
            objectMapper.writeValue(new File("resultfiles/"+fileName), flightList);
            System.out.println("FlightPath saved to flightpath-date.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // write deliver
        OrderProcess.writeDeliveries(validatedOrder.keySet(), date);
    }
    public static void writeDronePath(List<FlightPath> flightList, LocalDate date,Map<Order, LngLat> validatedOrder ){

        // Create an ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();
        // Disable pretty-printing
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, false);

        List<Double[]> coordinate = flightList.stream()
                .map(ToWriteFlight -> new Double[]{ToWriteFlight.fromLongitude(), ToWriteFlight.fromLatitude()}
                ).collect(Collectors.toList());

        Geometry geometry = new Geometry(" LineString",coordinate);
        // Create a GeoJSON Feature of type LineString
        Feature feature = new Feature("FeatureCollection",geometry);

        try {
            String formattedDate = date.toString();
            String fileName = "drone-" + formattedDate + ".geojson";
            objectMapper.writeValue(new File("resultfiles/"+fileName), feature);
            System.out.println("Drone saved to drone-date.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public static void main(String[] args) {
        if (args.length < 1){
            System.err.println("the base URL must be provided");
            System.exit(1);
        }

        String baseUrl = args[1];
        LocalDate date = LocalDate.parse(args[0]);
        if (baseUrl.endsWith("/") == false){
            baseUrl += "/";
        }

        try {
            var temp = new URL(baseUrl);
        } catch (Exception x) {
            System.err.println("The URL is invalid: " + x);
            System.exit(2);
        }

        List<ToWriteFlight> flightList=getFlightPath(baseUrl, date);
        System.out.println(flightList.size());
    }
}
