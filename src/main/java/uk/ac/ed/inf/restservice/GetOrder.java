package uk.ac.ed.inf.restservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import uk.ac.ed.inf.restservice.data.Order;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Arrays;

public class GetOrder {
    public static final String ORDER_URL = "orders";
    public static void main(String[] args) {
//        if (args.length < 1){
//            System.err.println("the base URL must be provided");
//            System.exit(1);
//        }
        var baseUrl = "https://ilp-rest.azurewebsites.net";
        LocalDate date = LocalDate.of(2023, 9, 1);
//        var baseUrl = args[1];
//        var date = args[0];
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
            System.out.println(orders[13].priceTotalInPence());
            Order[] extractedOrders = new Order[orders.length]; // Create an array

            int count = 0;
            for (Order i : orders) {
                if (i.orderDate().equals(date)) {
                    extractedOrders[count++] = i;
                }
            }
            // Resize the array if necessary
            if (count < extractedOrders.length) {
                extractedOrders = Arrays.copyOf(extractedOrders, count);
            }
            // Serialize the datedOrders list to a JSON file
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            try {
                objectMapper.writeValue(new File("datedOrders.json"), extractedOrders);
                System.out.println("Dated orders saved to datedOrders.json");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
//            return orders;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


//        return datedOrders;
    }

}
