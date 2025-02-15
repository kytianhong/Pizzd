package uk.ac.ed.inf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import uk.ac.ed.inf.Algorithm.Astar;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.restservice.data.FlightPath;
import uk.ac.ed.inf.restservice.data.ToWriteFlight;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetFlightPath {
    private static final LngLat APPLETON =new LngLat(-3.186874, 55.944494);
    private static final String NO_FLY_ZONE_URL = "noFlyZones";
    private static final String CENTRAL_AREA_URL = "centralArea";
    public NamedRegion[] getNonFlyZones(String baseUrl){
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            //read all NONFLYING zones
            NamedRegion[] nonFlyZones = mapper.readValue(new URL(baseUrl + NO_FLY_ZONE_URL), NamedRegion[].class);
            System.out.println("Read all " + nonFlyZones.length + " NO FLY ZONEs");
            return nonFlyZones;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public NamedRegion getCentralArea(String baseUrl){
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            //read all central area
            NamedRegion central = mapper.readValue(new URL(baseUrl + CENTRAL_AREA_URL), NamedRegion.class);
            System.out.println("Read all central area");
            return central;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void getFlightPath(Map<Order, LngLat> validatedOrder,NamedRegion central,NamedRegion[] nonFlyZones,LocalDate date){
        //new a list which contain the final flight path list of all orders
        List<FlightPath> droneMoveList=new ArrayList<>();
        List<ToWriteFlight> toWriteFlights = new ArrayList<>();
        System.out.println("Generating flight path...");
        for (Order i :validatedOrder.keySet()) {
            // get the corresponding destination
            LngLat destination = validatedOrder.get(i);
            //get the flight path of order i
            List<FlightPath> AppleToRest = new Astar().aStarSearch(APPLETON,destination,central,nonFlyZones);
            LngLat restaurant = new LngLat(AppleToRest.get(AppleToRest.size()-1).toLongitude(),AppleToRest.get(AppleToRest.size()-1).toLatitude());
            List<FlightPath> RestToApple = new Astar().aStarSearch(restaurant,APPLETON,central,nonFlyZones);
            //add all drone move path of current order into final flight path list
            droneMoveList.addAll(AppleToRest);
            droneMoveList.addAll(RestToApple);
            // rebuild the write_flight_path
            List<ToWriteFlight> aTr = restoreFlightPath(i.getOrderNo(),AppleToRest);
            List<ToWriteFlight> rTa = restoreFlightPath(i.getOrderNo(),RestToApple);
            //add all flight path of current order into final flight path list
            toWriteFlights.addAll(aTr);
            toWriteFlights.addAll(rTa);
            int FlightLength =  aTr.size()+rTa.size();
            System.out.println("Order: "+i.getOrderNo() + " has totally " + FlightLength + " drone movement steps");
        }
        new OrderProcess().writeDeliveries(validatedOrder.keySet(), date);// write deliver
        new GetFlightPath().writeFlightPath( toWriteFlights, date);// write FlightPath file
        new GeoJSONGenerator().generatorGeoJSON(droneMoveList,date);// write Drone move file
        System.out.println("There are totally " + toWriteFlights.size() + " drone movement steps in " + date);
    }
    private List<ToWriteFlight> restoreFlightPath(String orderNo, List<FlightPath> flightPath){
        // rebuild the write_flight_path
        List<ToWriteFlight> f = flightPath.stream()
                .map(p -> new ToWriteFlight(
                        orderNo,
                        p.fromLongitude(), p.fromLatitude(),
                        p.angle(),
                        p.toLongitude(), p.toLatitude()
                )).toList();
        return f;
    }
    public void writeFlightPath(List<ToWriteFlight> flightList, LocalDate date ){
        //write file
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            String formattedDate = date.toString();
            String fileName = "flightpath-" + formattedDate + ".json";
            objectMapper.writeValue(new File("resultfiles/"+fileName), flightList);
            System.out.println("FlightPath saved to resultfiles/"+fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
//        long startTime = System.nanoTime();// record start time
//        if (args.length < 1){
//            System.err.println("the base URL must be provided");
//            System.exit(1);
//        }
//
//        String baseUrl = args[1];
//        LocalDate date = LocalDate.parse(args[0]);
//        if (!baseUrl.endsWith("/")){
//            baseUrl += "/";
//        }
//
//        try {
//            var temp = new URL(baseUrl);
//        } catch (Exception x) {
//            System.err.println("The URL is invalid: " + x);
//            System.exit(2);
//        }
//
//        // call order process to get the day's order and its corresponding destination
//        Map<Order, LngLat> validatedOrder = new OrderProcess().getValidOrder(baseUrl);
//        //call get nonFlyZone and central method
//        NamedRegion[] nonFlyZones = new GetFlightPath().getNonFlyZones(baseUrl);
//        NamedRegion central = new GetFlightPath().getCentralArea(baseUrl);
//
//        new GetFlightPath().getFlightPath(validatedOrder,central,nonFlyZones,date);
//
//        long endTime = System.nanoTime();// record end time
//        double executionTime = (endTime - startTime) / 1e9;// calculate running time
//        System.out.println("Running time: " + executionTime + " s");
    }
}

