package uk.ac.ed.inf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.restservice.data.Deliveries;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class OrderProcess {
    private static final String ORDER_URL = "orders";
    private static final String RESTAURANT_URL = "restaurants";
//    public Map<Order, LngLat> getValidOrder(String baseUrl) {
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.registerModule(new JavaTimeModule());
//        try {
//            Order[] orders = mapper.readValue(new URL(baseUrl + ORDER_URL ), Order[].class);
////            Order[] orders = mapper.readValue(new URL(baseUrl + ORDER_URL + "/" + date.toString()), Order[].class);
//            Restaurant[] restaurants = mapper.readValue(new URL(baseUrl + RESTAURANT_URL), Restaurant[].class);
//            System.out.println("Read all " + orders.length + " orders in ");
//            System.out.println("Read all " + restaurants.length + " restaurants");
//
//            //check whether there is at least one valid order
//            if (orders.length<1){
//                System.err.println("The date is invalid, no order in this day");
//                System.exit(1);
//            }
//            //validate orders
//            System.out.println("Validating orders:");
//            Map<Order,LngLat> extractedOrders = new HashMap<>();
//            for (Order i : orders) {
//                Order toBeAdd =  new OrderValidator().validateOrder(i,restaurants);
//                if (toBeAdd.getOrderValidationCode().equals(OrderValidationCode.NO_ERROR)){
//                    //get destination
//                    Restaurant restaurant = getRestaurant(i.getPizzasInOrder(),restaurants);
//                    toBeAdd.setOrderStatus(OrderStatus.DELIVERED);
//                    extractedOrders.put(toBeAdd,restaurant.location());
//                }else {
//                    System.out.println("Order " +toBeAdd.getOrderNo() + " is invalid with validation code: " + toBeAdd.getOrderValidationCode());
//                }
//            }
//            System.out.println(extractedOrders.size()+" valid orders in ");
//            return extractedOrders;
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

//    }
//    public Restaurant getRestaurant(Pizza[] pizzas, Restaurant[] definedRestaurants){
//        //get which restaurant the order from
//        List<Pizza> pizzaSet = Arrays.asList(pizzas);
//        Restaurant bestRestaurant = null;
//        for (Restaurant r : definedRestaurants) {
//            Pizza[] menu = r.menu();
//            List<Pizza> menuSet = Arrays.asList(menu);
//            if (new HashSet<>(menuSet).containsAll(pizzaSet)) {
//                bestRestaurant = r; // find restaurant
//            }
//        }
//        return bestRestaurant; // not legal
//    }

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
            System.out.println("Deliveries saved to resultfiles/"+fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
