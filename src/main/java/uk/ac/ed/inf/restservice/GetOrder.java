package uk.ac.ed.inf.restservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import uk.ac.ed.inf.restservice.data.Order;
import uk.ac.ed.inf.restservice.data.Restaurant;

import java.io.IOException;
import java.net.URL;
public class GetOrder {
    public static final String ORDER_URL = "orders";

    public static void main(String[] args) {
//        if (args.length < 1){
//            System.err.println("the base URL must be provided");
//            System.exit(1);
//        }
        var baseUrl = "https://ilp-rest.azurewebsites.net";
//        var baseUrl = args[0];
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
            var orders = mapper.readValue(new URL(baseUrl + ORDER_URL), Order[].class);
            System.out.println("read all orders");
//            System.out.println(orders[13].priceTotalInPence());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
