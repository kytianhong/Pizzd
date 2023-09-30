package uk.ac.ed.inf.interfaces;

import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.interfaces.OrderValidation;


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
//        number (16 digit numeric)
//        CVV
//        expiration date
//        the menu items selected in the order
//        the involved restaurants
//        if the maximum count is exceeded
//        if the order is valid on the given date for the involved restaurants (opening days)
        return null;
    }
}