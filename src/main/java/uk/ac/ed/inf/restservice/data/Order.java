package uk.ac.ed.inf.restservice.data;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.CreditCardInformation;
import uk.ac.ed.inf.ilp.data.Pizza;

import java.time.LocalDate;

public record Order (String orderNo, LocalDate orderDate, OrderStatus orderStatus, OrderValidationCode orderValidationCode,
                     int priceTotalInPence, Pizza[] pizzasInOrder, CreditCardInformation creditCardInformation){
}
