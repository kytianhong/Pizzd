package uk.ac.ed.inf.restservice.data;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;

public record Deliveries (String orderNo, OrderStatus orderStatus, OrderValidationCode orderValidationCode, int costInPence){
}
