package uk.ac.ed.inf;

import uk.ac.ed.inf.Interfaces.OrderValidator;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.CreditCardInformation;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

public class SecurityTest {
    public static void main(String[] args) {
        System.out.println("Running Security Test...");

        Order fakeOrder = createFakeOrder();
        Order validatedOrder = new OrderValidator().validateOrder(fakeOrder, new Restaurant[]{});

        if (validatedOrder.getOrderValidationCode() == OrderValidationCode.CARD_NUMBER_INVALID) {
            System.out.println(" Test Passed! Fake credit card was detected.");
        } else {
            System.out.println(" Test Failed! Security vulnerability detected.");
        }
    }

    private static Order createFakeOrder() {
        Order order = new Order();
        order.setCreditCardInformation(new CreditCardInformation("0000000000000000", "12/50", "000"));
        return order;
    }
}
