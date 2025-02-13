package uk.ac.ed.inf.Interfaces;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Pizza;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.interfaces.OrderValidation;

import java.time.LocalDate;

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
//        LocalDate today = LocalDate.now();
        int totalCost = orderToValidate.getPriceTotalInPence();
        Pizza[] pizzas = orderToValidate.getPizzasInOrder();
        orderToValidate.setOrderStatus(OrderStatus.INVALID);

        if ( ! CardNumberLegal(number)){//check card number (16 digit numeric)
            orderToValidate.setOrderValidationCode( OrderValidationCode.CARD_NUMBER_INVALID );
        } else if ( ! CardCvvLegal(cvv)) {//check CVV is 3 digital
            orderToValidate.setOrderValidationCode( OrderValidationCode.CVV_INVALID);
        } else if ( ! CardDateLegal( exp_day, orderDate ) ) { //check card expiration date legal
            orderToValidate.setOrderValidationCode( OrderValidationCode.EXPIRY_DATE_INVALID);
//        } else if ( ! pizzasUnDefined(pizzas,definedRestaurants)) { //if pizza out of range
//            orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_NOT_DEFINED);
//        } else if (  pizzaMaxExceed(pizzas) ) {//check pizza maximum count is exceeded
//            orderToValidate.setOrderValidationCode(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED);
//        } else if ( ! costValid(pizzas,totalCost) ) {//if the order cost is valid
//            orderToValidate.setOrderValidationCode(OrderValidationCode.TOTAL_INCORRECT);
//        } else if (!pizzasFromMenu(pizzas, definedRestaurants)) {
//            //check if the menu items selected legal in the order
//            orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS);
//        } else if (!restaurantOpen(Objects.requireNonNull(getRestaurant(pizzas, definedRestaurants)),orderDate)) {
//            //if the order is valid on the given date for the involved restaurants (opening day)
//            orderToValidate.setOrderValidationCode(OrderValidationCode.RESTAURANT_CLOSED);
//        } else {//no error, change order status
//            orderToValidate.setOrderValidationCode(OrderValidationCode.NO_ERROR);
//            orderToValidate.setOrderStatus(OrderStatus.VALID_BUT_NOT_DELIVERED);
        }

        return orderToValidate;
    }

    //Functions deal with card

    /**check card number (16 digit numeric)*/
    private boolean CardNumberLegal(String number){
        return number.length() == 16;
    }
    /**check CVV is 3 digital*/
    private boolean CardCvvLegal(String cvv){
        return cvv.length() == 3;
    }
    /**check card expiration date legal*/
    private boolean CardDateLegal(String exp_day,LocalDate date){
        int orderMonth = date.getMonthValue();
        int orderYear = date.getYear();
        if (exp_day == null || exp_day.isEmpty() ){
            return false;// check if card expiry date exist
        }else {
            String[] parts = exp_day.split("/");
            int month = Integer.parseInt(parts[0]);//get month
            int year = Integer.parseInt(parts[1]);// get year
//            if (year>76){//last century card
//                return false;
//            }else
                if ( year+2000 > orderYear) {//check year
                return true;
//            }else if ( year+2000 == orderYear && month >=orderMonth) {
//                return true;//check month
            } else {
                return false;
            }
        }
    }

    //Functions deal with pizzas
    /**if the order cost is valid*/
//    private boolean costValid(Pizza[] pizzas, int Total){
//        //return total cost include service fee
//        int total=0;
//        for (Pizza p :pizzas) {
//            total += p.priceInPence();
//        }
//        total += SystemConstants.ORDER_CHARGE_IN_PENCE;
//        return total == Total;
//    }
//    private List<Pizza> pizzaName(Pizza[] pizzas){
//    //get pizza names list from pizza object list
//        List<Pizza> names = new ArrayList<Pizza>();
//        for (Pizza p :pizzas) {
//            names.add(p);
//        }
//        return names;
//    }
    /**check pizza maximum count is exceeded*/
//    private boolean pizzaMaxExceed(Pizza[] pizzas){
//        return pizzas.length > SystemConstants.MAX_PIZZAS_PER_ORDER;
//    }
//    private boolean pizzasUnDefined(Pizza[] pizzas, Restaurant[] definedRestaurants) {
//        List<Pizza> pizzaSet = Arrays.asList(pizzas);
//        List<Pizza> Menu = new ArrayList<Pizza>();
//        for (Restaurant r : definedRestaurants) {
//            Pizza[] menu = r.menu();
//            for (Pizza p : menu) {
//                Menu.add(p);
//            }
//        }
//        if (new HashSet<>(Menu).containsAll(pizzaSet)) {
//            return true; // all pizza in menu
//        }else return false;
//    }
//    /**check if the menu items selected legal in the order*/
//    private boolean pizzasFromMenu(Pizza[] pizzas, Restaurant[] definedRestaurants) {
//    //check whether pizzas from one restaurant
//        List<Pizza> pizzaSet = Arrays.asList(pizzas);
//        for (Restaurant r : definedRestaurants) {
//            Pizza[] menu = r.menu();
//            List<Pizza> menuSet = Arrays.asList(menu);
//            if (new HashSet<>(menuSet).containsAll(pizzaSet)) {
//                return true; // all pizza in one menu
//            }
//        }
//        return false; // pizzas in multi restaurants
//    }
//
//    //Functions deal with restaurants
//    public Restaurant getRestaurant(Pizza[] pizzas, Restaurant[] definedRestaurants){
//    //get which restaurant the order from
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
//    /**if the order is valid on the given date for the involved restaurants (opening days)*/
//    private boolean restaurantOpen(Restaurant orderRestaurant, LocalDate orderDate ){
//    //return whether the restaurant open at the order day
//        DayOfWeek dates = orderDate.getDayOfWeek();
//        DayOfWeek[] opendates = orderRestaurant.openingDays();
//        for (DayOfWeek d:opendates) {
//            if (d.equals(dates))return true;
//        }
//        return false;
//    }

}