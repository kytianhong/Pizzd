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
            System.out.println(nonFlyZones.length);
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
            System.out.println(central.name());
            return central;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
//List<ToWriteFlight>
    public static  void getFlightPath(String baseUrl, LocalDate date){
        // call order process to get the day's order and its corresponding destination
        Map<Order, LngLat> validatedOrder = new OrderProcess().getValidOrder(baseUrl,date);
        //call get nonFlyZone and central method
        NamedRegion[] nonFlyZones = getNonFlyZones(baseUrl);
        NamedRegion central = getCentralArea(baseUrl);
        //new a list which contain the final flight path list of all orders
        List<FlightPath> droneMoveList=new ArrayList<>();
        List<ToWriteFlight> toWriteFlights = new ArrayList<>();
        for (Order i :validatedOrder.keySet()) {
            // get the corresponding destination
            LngLat destination = validatedOrder.get(i);
            //get the flight path of order i
            List<FlightPath> AppleToRest = new Astar().aStarSearch(APPLETON,destination,central,nonFlyZones);
            List<FlightPath> RestToApple = new Astar().aStarSearch(destination,APPLETON,central,nonFlyZones);
            //add all drone move path of current order into final flight path list
            droneMoveList.addAll(AppleToRest);
            droneMoveList.addAll(RestToApple);
            // rebuild the write_flight_path
            List<ToWriteFlight> aTr = AppleToRest.stream()
                    .map(p -> new ToWriteFlight(
                            i.getOrderNo(),
                            p.fromLongitude(),
                            p.fromLatitude(),
                            p.angle(),
                            p.toLongitude(),
                            p.toLatitude()
                    )).collect(Collectors.toList());
            List<ToWriteFlight> rTa = RestToApple.stream()
                    .map(p -> new ToWriteFlight(
                            i.getOrderNo(),
                            p.fromLongitude(),
                            p.fromLatitude(),
                            p.angle(),
                            p.toLongitude(),
                            p.toLatitude()
                    )).collect(Collectors.toList());
            //add all flight path of current order into final flight path list
            toWriteFlights.addAll(aTr);
            toWriteFlights.addAll(rTa);
        }
        writeFlightPath( toWriteFlights, date, validatedOrder );
        new GeoJSONGenerator().generatorGeoJSON(droneMoveList,date);
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

//        List<ToWriteFlight> flightList=
        getFlightPath(baseUrl, date);
    }
}

//new a list which contain the final flight path list of all orders

//        List<FlightPath> flightPaths = new Astar().aStarSearch(APPLETON,RESTAURANT,central,nonFlyZones);
// rebuild the write_flight_path

//        System.out.println(flightList.size());

//        new GeoJSONGenerator().generatorGeoJSON(flightList,date);

//            for (int j = 0;j<flightPaths.size()-1;j++){
//    ToWriteFlight f = new ToWriteFlight(i.getOrderNo(),
//                        flightPaths.get(j).fromLongitude(),
//                        flightPaths.get(j).fromLatitude(),
//                        flightPaths.get(j).angle(),
//                        flightPaths.get(j).toLongitude(),
//                        flightPaths.get(j).toLatitude());

//        getCentralArea(baseUrl);
//        getNonFlyZones(baseUrl);

//        LngLat APPLETON =new LngLat(-3.186874, 55.944494);
//        LngLat RESTAURANT = new LngLat(-3.202541470527649, 55.943284737579376);
//        LngLat APPLETON =new LngLat(-3.1888, 55.94488);
//        LngLat RESTAURANT = new LngLat(-3.1900, 55.94500);

//Map<Order, LngLat> validatedOrder = new OrderProcess().getValidOrder(baseUrl,date);
//        //call get nonFlyZone and central method
//        NamedRegion[] nonFlyZones = getNonFlyZones(baseUrl);
//        NamedRegion central = getCentralArea(baseUrl);
