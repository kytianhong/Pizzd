package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

public class CheckOrderValid {
    public static void main(String[] args) {
        System.out.println("ILP Test Application using the IlpDataObjects.jar file");

        var order = new Order();
        order.setOrderNo(String.format("%08X", ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE)));
        order.setOrderDate(LocalDate.of(2023, 9, 1));

        order.setCreditCardInformation(
                new CreditCardInformation(
                        "1212121212121212",
                        String.format("%02d/%02d", ThreadLocalRandom.current().nextInt(1, 12), ThreadLocalRandom.current().nextInt(24, 29)),
                        "222"
                )
        );

        // every order has the defined outcome
        order.setOrderStatus(OrderStatus.UNDEFINED);
        order.setOrderValidationCode(OrderValidationCode.UNDEFINED);

        // get a random restaurant

        // and load the order items plus the price
        order.setPizzasInOrder(new Pizza[]{new Pizza("A", 1212) , new Pizza("Pizza A", 2300)});
        order.setPriceTotalInPence(3512 + SystemConstants.ORDER_CHARGE_IN_PENCE);

        var validatedOrder =
                new OrderValidator().validateOrder(order,
                        new Restaurant[]{new Restaurant("myRestaurant",
                                new LngLat(55.945535152517735, -3.1912869215011597),
                                new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.FRIDAY},
                                new Pizza[]{new Pizza("Pizza A", 2300), new Pizza("A", 1212)}),

                                new Restaurant("2rdRestaurant",
                                        new LngLat(55.945535152517735, -3.1912869215011597),
                                        new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.FRIDAY},
                                        new Pizza[]{new Pizza("Pizza B", 2300), new Pizza("B", 1212)})
                        });

        System.out.println("order validation resulted in status: " +
                validatedOrder.getOrderStatus() +
                " and validation code: " +
                validatedOrder.getOrderValidationCode());
    }
}
//System.out.println("ILP Test Application using the IlpDataObjects.jar file");
//        LngLat startPosition = new LngLat(4,56);
//        LngLat Lefttop = new LngLat(-3.192473,55.946233);
//        LngLat Leftbot = new LngLat(-3.192473,55.942617);
//        LngLat Rightbot = new LngLat(-3.184319,55.942617);
//        LngLat Righttop = new LngLat(-3.184319,55.946233);
////        LngLat []CentralArea = [Lefttop,Leftbot];
//        NamedRegion central = new NamedRegion("central area",new LngLat[]{new LngLat(1,55),
//                new LngLat(1,60),new LngLat(3,60),new LngLat(3,55)});
//        if (new LngLatHandler().isInRegion(startPosition,central)){
//            System.out.println("in region");
//        }else {
//            System.out.println("not in region");
//        }