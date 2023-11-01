package uk.ac.ed.inf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;

public class GetFlightPath {
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

        List<Order>validatedOrder = new OrderProcess().getValidOrder(baseUrl,date);
        //call write deliveries method
        OrderProcess.writeDeliveries(validatedOrder,date);
        //call get nonFlyZone and central method
        NamedRegion[] nonFlyZones = getNonFlyZones(baseUrl);
        NamedRegion central = getCentralArea(baseUrl);

    }
}
