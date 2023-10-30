package uk.ac.ed.inf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.restservice.data.Deliveries;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class GetFlightPath {
    private static final String NO_FLY_ZONE_URL = "noFlyZones";
    private static final String CENTRAL_AREA_URL = "centralArea";
    public static void getFlightPath(String baseUrl, List<Order> validatedOrder){
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        NamedRegion central;
        NamedRegion[] nonFlyZones;
        try {
            central = mapper.readValue(new URL(baseUrl + CENTRAL_AREA_URL), NamedRegion.class);
            System.out.println("read all central area");
            System.out.println(central.vertices()[1]);

            nonFlyZones = mapper.readValue(new URL(baseUrl + NO_FLY_ZONE_URL), NamedRegion[].class);
            System.out.println("read all NO FLY ZONE");
            System.out.println(nonFlyZones[0].vertices()[2]);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public static void writeDeliveries(List<Order>validatedOrder,LocalDate date){
        // Use Java streams transform Order to Deliveries
        List<Deliveries> deliveriesList = validatedOrder.stream()
                .map(order -> new Deliveries(
                        order.getOrderNo(),
                        order.getOrderStatus(),
                        order.getOrderValidationCode(),
                        order.getPriceTotalInPence()
                ))
                .collect(Collectors.toList());// Collect results into List<Deliveries>
        // Serialize the datedOrders list to a JSON file
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            String formattedDate = date.toString();
            String fileName = "deliveries-" + formattedDate + ".json";
            objectMapper.writeValue(new File("resultfiles/"+fileName), deliveriesList);
            System.out.println("Dated orders saved to datedOrders.json");
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

        List<Order>validatedOrder = new GetValidOrder().getValidOrder(baseUrl,date);
        //call write deliveries method
        writeDeliveries(validatedOrder,date);
        //call get flight path method
        getFlightPath(baseUrl,validatedOrder);
    }
}
