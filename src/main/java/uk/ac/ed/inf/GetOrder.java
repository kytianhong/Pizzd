package uk.ac.ed.inf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import uk.ac.ed.inf.Interfaces.OrderValidator;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.restservice.data.Deliveries;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GetOrder {
    public static final String ORDER_URL = "orders";
    public static final String RESTAURANT_URL = "restaurants";
    public static void main(String[] args) {
        if (args.length < 1){
            System.err.println("the base URL must be provided");
            System.exit(1);
        }
//        var baseUrl = "https://ilp-rest.azurewebsites.net";
//        LocalDate date = LocalDate.of(2023, 9, 1);
        var baseUrl = args[1];
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


        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            Order[] orders = mapper.readValue(new URL(baseUrl + ORDER_URL), Order[].class);
            System.out.println("read all orders");
            System.out.println(orders.length);
            Restaurant [] restaurants = mapper.readValue(new URL(baseUrl + RESTAURANT_URL), Restaurant[].class);
            System.out.println("read all restaurants");

            List<Order> extractedOrders = new ArrayList<>();
            for (Order i : orders) {
                if (i.getOrderDate().equals(date)) {
//                   System.out.println(i.orderNo());
                   Order tobeadd =  new OrderValidator().validateOrder(i,restaurants);
//                   extractedOrders.add(i);
                    extractedOrders.add(tobeadd);
                }
            }
//            System.out.println(extractedOrders.size());
            // Use Java streams transform Order to Deliveries
            List<Deliveries> deliveriesList = extractedOrders.stream()
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

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
