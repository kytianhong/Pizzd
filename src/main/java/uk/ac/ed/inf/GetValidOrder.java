package uk.ac.ed.inf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import uk.ac.ed.inf.Interfaces.OrderValidator;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GetValidOrder {
    public static final String ORDER_URL = "orders";
    public static final String RESTAURANT_URL = "restaurants";
    public static List<Order> main(String[] args) {
        if (args.length < 1){
            System.err.println("the base URL must be provided");
            System.exit(1);
        }

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
            Restaurant[] restaurants = mapper.readValue(new URL(baseUrl + RESTAURANT_URL), Restaurant[].class);
            System.out.println("read all restaurants");

            //validate orders
            List<Order> extractedOrders = new ArrayList<>();
            for (Order i : orders) {
                if (i.getOrderDate().equals(date)) {
                    Order tobeadd =  new OrderValidator().validateOrder(i,restaurants);
                    if (tobeadd.getOrderValidationCode().equals(OrderValidationCode.NO_ERROR)){
                        extractedOrders.add(tobeadd);
                    }
                }
            }
            System.out.println(extractedOrders.size());
            return extractedOrders;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
