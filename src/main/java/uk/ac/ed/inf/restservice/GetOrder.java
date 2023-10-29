package uk.ac.ed.inf.restservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import uk.ac.ed.inf.restservice.data.Order;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GetOrder {
    public static final String ORDER_URL = "orders";
    public static Order[] orders;
    public static void getOrders(String[] args) {
//        if (args.length < 1){
//            System.err.println("the base URL must be provided");
//            System.exit(1);
//        }
        var baseUrl = "https://ilp-rest.azurewebsites.net";
        LocalDate date = LocalDate.of(2023, 9, 1);
//        var baseUrl = args[1];
//        var date = args[0];
//        if (baseUrl.endsWith("/") == false){
//            baseUrl += "/";
//        }
//
//        try {
//            var temp = new URL(baseUrl);
//        } catch (Exception x) {
//            System.err.println("The URL is invalid: " + x);
//            System.exit(2);
//        }


        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            var orders = mapper.readValue(new URL(baseUrl + ORDER_URL), Order[].class);
            System.out.println("read all orders");
            System.out.println(orders[13].priceTotalInPence());
//            return orders;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<Order> extractedOrders = new ArrayList<>();
        for (Order i:orders) {
            if(i.orderDate() == date){
                extractedOrders.add(i);
            }
        }
        Order[] datedOrders = (Order[]) extractedOrders.toArray();
//        return datedOrders;
    }

}
