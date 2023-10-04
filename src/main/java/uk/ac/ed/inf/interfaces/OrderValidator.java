package uk.ac.ed.inf.interfaces;

import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Pizza;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.interfaces.OrderValidation;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

/**
 * interface to validate an order
 */
public class OrderValidator implements OrderValidation{
    /**
     * validate an order and deliver a validated version where the
     * OrderStatus and OrderValidationCode are set accordingly.
     *
     * The order validation code is defined in the enum @link uk.ac.ed.inf.ilp.constant.OrderValidationStatus
     *
     * @param orderToValidate    is the order which needs validation
     * @param definedRestaurants is the vector of defined restaurants with their according menu structure
     * @return the validated order
     */
    public Order validateOrder(Order orderToValidate, Restaurant[] definedRestaurants){

        String number = orderToValidate.getCreditCardInformation().getCreditCardNumber();
        String cvv = orderToValidate.getCreditCardInformation().getCvv();
        String exp_day = orderToValidate.getCreditCardInformation().getCreditCardExpiry();
        LocalDate orderDate = orderToValidate.getOrderDate();
        int totalCost = orderToValidate.getPriceTotalInPence();
        Pizza[] pizzas = orderToValidate.getPizzasInOrder();

        if ( number.length()!=16 ){//check card number (16 digit numeric)
            orderToValidate.setOrderValidationCode( OrderValidationCode.CARD_NUMBER_INVALID );
        } else if ( cvv.length() != 3) {//check CVV is 3 digital
            orderToValidate.setOrderValidationCode( OrderValidationCode.CVV_INVALID);
        } else if (!CardDateLegal( exp_day, orderDate) ) { //check card expiration date legal
            orderToValidate.setOrderValidationCode( OrderValidationCode.EXPIRY_DATE_INVALID);
        } else if ( pizzas.length == 0 ) { //if there is no pizza
            orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_NOT_DEFINED);
        } else if ( pizzas.length >4 ) {//check pizza maximum count is exceeded
            orderToValidate.setOrderValidationCode(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED);
        } else if ( cost(pizzas) != totalCost) {//if the order cost is valid
            orderToValidate.setOrderValidationCode(OrderValidationCode.TOTAL_INCORRECT);
        } else if (!pizzasFromMenu(pizzas, definedRestaurants)) {
            //check if the menu items selected legal in the order
            orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS);
        } else if (!restaurantOpen(Objects.requireNonNull(getRestaurant(pizzas, definedRestaurants)),orderDate)) {
            //if the order is valid on the given date for the involved restaurants (opening days)
            orderToValidate.setOrderValidationCode(OrderValidationCode.RESTAURANT_CLOSED);
        } else {
            orderToValidate.setOrderValidationCode(OrderValidationCode.NO_ERROR);
        }

        return orderToValidate;
    }

    //Functions deal with card
    private boolean CardDateLegal(String exp_day,LocalDate orderdate){
        int orderMonth = orderdate.getMonthValue();
        int orderYear = orderdate.getYear();
        if (exp_day == null || exp_day.isEmpty() ){
            return false;// check if card expiry date exist
        }else {
            String[] parts = exp_day.split("/");
            int month = Integer.parseInt(parts[0]);//get month
            int year = Integer.parseInt(parts[1]);// get year
            if (year>76){//last century card
                return false;
            }else if ( year+2000 >= orderYear) {//check year
                return month >=orderMonth;//check month
            } else {
                return false;
            }
        }
    }

    //Functions deal with pizzas
    private int cost(Pizza[] pizzas){
        //return total cost include service fee
        int total=0;
        for (Pizza p :pizzas) {
            total += p.priceInPence();
        }
        total += 100;
        return total;
    }

    private List<String> pizzaName(Pizza[] pizzas){
    //get pizza names list from pizza object list
        List<String> names = new ArrayList<String>();
        for (Pizza p :pizzas) {
            names.add(p.name());
        }
        return names;
    }

    private boolean pizzasFromMenu(Pizza[] pizzas, Restaurant[] definedRestaurants) {
    //check whether pizzas from one restaurant
        List<String> pizzaNames = pizzaName(pizzas);
        for (Restaurant r : definedRestaurants) {
            Pizza[] menu = r.menu();
            List<String> menuPizzas = pizzaName(menu);
            if (new HashSet<>(menuPizzas).containsAll(pizzaNames)) {
                return true; // all pizza in one menu
            }
        }
        return false; // pizzas in multi restaurants
    }

    //Functions deal with restaurants
    private Restaurant getRestaurant(Pizza[] pizzas, Restaurant[] definedRestaurants){
    //get which restaurant the order from
        List<String> pizzaNames = pizzaName(pizzas);
        Restaurant bestRestaurant = null;
        for (Restaurant r : definedRestaurants) {
            Pizza[] menu = r.menu();
            List<String> menuPizzas = pizzaName(menu);
            if (new HashSet<>(menuPizzas).containsAll(pizzaNames)) {
                bestRestaurant = r; // find restaurant
            }
        }
        return bestRestaurant; // not legal
    }
    private boolean restaurantOpen(Restaurant orderRestaurant, LocalDate orderDate ){
    //return whether the restaurant open at the order day
        DayOfWeek dates = orderDate.getDayOfWeek();
        DayOfWeek[] opendates = orderRestaurant.openingDays();
        for (DayOfWeek d:opendates) {
            if (d.equals(dates))return true;
        }
        return false;
    }

}