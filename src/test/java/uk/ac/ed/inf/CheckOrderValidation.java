package uk.ac.ed.inf;

import uk.ac.ed.inf.Interfaces.OrderValidator;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.*;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class CheckOrderValidation {
    public static void main(String[] args) {
        System.out.println("PizzaDrone Order Validation Test");

        // Create test orders
        Order[] testOrders = generateTestOrders();

        // Define test restaurants
        Restaurant[] restaurants = new Restaurant[]{
                new Restaurant("PizzaPlace1",
                        new LngLat(55.9455, -3.1912),
                        new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.FRIDAY},
                        new Pizza[]{new Pizza("Pizza A", 1400), new Pizza("Pizza B", 900)}),

                new Restaurant("PizzaPlace2",
                        new LngLat(55.9445, -3.1922),
                        new DayOfWeek[]{DayOfWeek.TUESDAY, DayOfWeek.THURSDAY},
                        new Pizza[]{new Pizza("Pizza X", 1500), new Pizza("Pizza Y", 1000)})
        };

        // Validate orders
        for (Order order : testOrders) {
            Order validatedOrder = new OrderValidator().validateOrder(order, restaurants);
            System.out.println("\nOrder " + order.getOrderNo() +
                    " validation result: " + validatedOrder.getOrderStatus() +
                    " (" + validatedOrder.getOrderValidationCode() + ")");
        }
    }

    /**
     * Generate multiple test orders with various validation failures.
     */
    private static Order[] generateTestOrders() {
        return new Order[]{
                createOrder("12345678", "INVALID_CARD", "1111111111111111", "05/26", "123", 1400, 900, 2300),
                createOrder("87654321", "EXPIRED_CARD", "5555444433332222", "02/20", "456", 1500, 1000, 2500),
                createOrder("11223344", "INVALID_CVV", "4024007131862720", "07/25", "12", 1400, 900, 2300),
                createOrder("55667788", "INCORRECT_TOTAL", "6011000990139424", "08/26", "789", 1400, 900, 2400),
                createOrder("99887766", "TOO_MANY_PIZZAS", "378282246310005", "12/27", "321",
                        1400, 900, 1400, 900, 1400, 900, 1400, 900), // More than allowed pizzas
                createOrder("11220099", "RESTAURANT_CLOSED", "5105105105105100", "11/24", "987", 1400, 900, 2300)
        };
    }

    /**
     * Create an order with specific test parameters.
     */
    private static Order createOrder(String orderNo, String validationType,
                                     String cardNumber, String expiry, String cvv,
                                     int... pizzaPrices) {
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setOrderDate(LocalDate.now());

        // Set credit card details
        order.setCreditCardInformation(new CreditCardInformation(cardNumber, expiry, cvv));

        // Set pizzas
        Pizza[] pizzas = new Pizza[pizzaPrices.length];
        int totalPrice = 0;
        for (int i = 0; i < pizzaPrices.length; i++) {
            pizzas[i] = new Pizza("Test Pizza " + (i + 1), pizzaPrices[i]);
            totalPrice += pizzaPrices[i];
        }
        order.setPizzasInOrder(pizzas);

        // Set total price (intentionally incorrect for certain test cases)
        if (validationType.equals("INCORRECT_TOTAL")) {
            totalPrice += 100; // Make the total incorrect
        }
        order.setPriceTotalInPence(totalPrice + SystemConstants.ORDER_CHARGE_IN_PENCE);

        // Default status
        order.setOrderStatus(OrderStatus.UNDEFINED);
        order.setOrderValidationCode(OrderValidationCode.UNDEFINED);

        return order;
    }
}
