package uk.ac.ed.inf.interfaces;

import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.CreditCardInformation;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Pizza;
import uk.ac.ed.inf.ilp.data.Restaurant;

import uk.ac.ed.inf.ilp.interfaces.OrderValidation;

import java.time.LocalDate;
import java.util.Scanner;
import java.util.List;


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
     * <p>
     * Fields to validate include (among others - for details please see the OrderValidationStatus):
     * <p>
     * number (16 digit numeric)
     * CVV
     * expiration date
     * the menu items selected in the order
     * the involved restaurants
     * if the maximum count is exceeded
     * if the order is valid on the given date for the involved restaurants (opening days)
     *
     * @param orderToValidate    is the order which needs validation
     * @param definedRestaurants is the vector of defined restaurants with their according menu structure
     * @return the validated order
     */
    public Order validateOrder(Order orderToValidate, Restaurant[] definedRestaurants){

        if (orderToValidate == null) {
            orderToValidate.setOrderValidationCode(OrderValidationCode.TOTAL_INCORRECT);
            return orderToValidate;
        }

        String number = orderToValidate.getCreditCardInformation().getCreditCardNumber();
        String cvv = orderToValidate.getCreditCardInformation().getCvv();

        String exp_day = orderToValidate.getCreditCardInformation().getCreditCardExpiry();
        LocalDate date = orderToValidate.getOrderDate();

        Pizza[] pizzas = orderToValidate.getPizzasInOrder();

        if ( number.length()!=16 ){
            //check card number (16 digit numeric)
            orderToValidate.setOrderValidationCode( OrderValidationCode.CARD_NUMBER_INVALID );
        } else if ( cvv.length() != 3) {
            //check CVV
            orderToValidate.setOrderValidationCode( OrderValidationCode.CVV_INVALID);
        } else if (CardDateLegal( exp_day, date) ) {
            //check card expiration date
            orderToValidate.setOrderValidationCode( OrderValidationCode.EXPIRY_DATE_INVALID);
        } else if ( pizzas.length >4 ) {
            //if the maximum count is exceeded
            orderToValidate.setOrderValidationCode(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED);
        } else if ( pizzas.length == 0) {
            //if there is no pizza
            orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_NOT_DEFINED);
        }else {
            orderToValidate.setOrderValidationCode(OrderValidationCode.NO_ERROR);
        }
            //check if the menu items selected legal in the order
            //orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS);

            //if the order is valid on the given date for the involved restaurants (opening days)
            //orderToValidate.setOrderValidationCode(OrderValidationCode.RESTAURANT_CLOSED);

        return orderToValidate;
    }

    private boolean PizzaFrom(Pizza[] pizzas, Restaurant[] definedRestaurants) {
        return false;
    }
    private boolean CardDateLegal(String exp_day,LocalDate date){
        if (exp_day == null || exp_day.isEmpty() ){
            return false;
        }else {
            String[] parts = exp_day.split("/");
            int month = Integer.parseInt(parts[0]);
            int year = Integer.parseInt(parts[1]);

            if (year >= date.getYear()) {
                if (month >= date.getMonthValue()) return true;
                else return false;
            } else {
                return false;
            }
        }
    }

}