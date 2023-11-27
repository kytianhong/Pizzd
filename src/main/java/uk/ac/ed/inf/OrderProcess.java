package uk.ac.ed.inf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import uk.ac.ed.inf.Interfaces.OrderValidator;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Pizza;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.restservice.data.Deliveries;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class OrderProcess {
    private static final String ORDER_URL = "orders";
    private static final String RESTAURANT_URL = "restaurants";
    public Map<Order, LngLat> getValidOrder(String baseUrl, LocalDate date) {

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            Order[] orders = mapper.readValue(new URL(baseUrl + ORDER_URL), Order[].class);
            System.out.println("read all " + orders.length + " orders");
            Restaurant[] restaurants = mapper.readValue(new URL(baseUrl + RESTAURANT_URL), Restaurant[].class);
            System.out.println("read all " + restaurants.length + " restaurants");

            //validate orders
            Map<Order,LngLat> extractedOrders = new HashMap<>();
            for (Order i : orders) {
                if (i.getOrderDate().equals(date)) {
                    Order toBeAdd =  new OrderValidator().validateOrder(i,restaurants);
                    if (toBeAdd.getOrderValidationCode().equals(OrderValidationCode.NO_ERROR)){
                        //get destination
                        Restaurant restaurant = getRestaurant(i.getPizzasInOrder(),restaurants);
                        toBeAdd.setOrderStatus(OrderStatus.DELIVERED);
                        extractedOrders.put(toBeAdd,restaurant.location());
                    }
                }
            }
            System.out.println("validated "+extractedOrders.size()+" orders in "+ date.toString());
            return extractedOrders;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public Restaurant getRestaurant(Pizza[] pizzas, Restaurant[] definedRestaurants){
        //get which restaurant the order from
        List<Pizza> pizzaSet = Arrays.asList(pizzas);
        Restaurant bestRestaurant = null;
        for (Restaurant r : definedRestaurants) {
            Pizza[] menu = r.menu();
            List<Pizza> menuSet = Arrays.asList(menu);
            if (new HashSet<>(menuSet).containsAll(pizzaSet)) {
                bestRestaurant = r; // find restaurant
            }
        }
        return bestRestaurant; // not legal
    }

    public void writeDeliveries(Set<Order> validatedOrder, LocalDate date){
        // Use Java streams transform Order to Deliveries
        List<Deliveries> deliveriesList = validatedOrder.stream()
                .map(order -> new Deliveries(
                        order.getOrderNo(),
                        order.getOrderStatus(),
                        order.getOrderValidationCode(),
                        order.getPriceTotalInPence()
                )).collect(Collectors.toList());// Collect results into List<Deliveries>
        // Serialize the datedOrders list to a JSON file
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            String formattedDate = date.toString();
            String fileName = "deliveries-" + formattedDate + ".json";
            objectMapper.writeValue(new File("resultfiles/"+fileName), deliveriesList);
            System.out.println("Dated orders saved to resultfiles/"+fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
