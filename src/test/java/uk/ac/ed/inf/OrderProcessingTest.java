package uk.ac.ed.inf;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.ed.inf.Interfaces.OrderValidator;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.*;

import java.time.DayOfWeek;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderProcessingTest {

    private Restaurant[] restaurants;

    @BeforeEach
    public void setupRestaurants() {
        restaurants = new Restaurant[]{
                new Restaurant("PizzaPlace1",
                        new LngLat(55.9455, -3.1912),
                        new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.FRIDAY},
                        new Pizza[]{new Pizza("Pizza A", 1400), new Pizza("Pizza B", 900)}),

                new Restaurant("PizzaPlace2",
                        new LngLat(55.9445, -3.1922),
                        new DayOfWeek[]{DayOfWeek.TUESDAY, DayOfWeek.THURSDAY},
                        new Pizza[]{new Pizza("Pizza X", 1500), new Pizza("Pizza Y", 1000)})
        };
    }

    @Test
    public void testValidOrderProcessing() {
        var order = new Order();
        order.setCreditCardInformation(
                new CreditCardInformation(
                        "1212121212121212",
                        String.format("%02d/%02d", ThreadLocalRandom.current().nextInt(1, 12), ThreadLocalRandom.current().nextInt(24, 29)),
                        "222"
                )
        );
        order.setOrderStatus(OrderStatus.UNDEFINED);
        order.setOrderValidationCode(OrderValidationCode.UNDEFINED);
        order.setPizzasInOrder(new Pizza[]{new Pizza("A", 1212) , new Pizza("Pizza A", 2300)});
        order.setPriceTotalInPence(3512 + SystemConstants.ORDER_CHARGE_IN_PENCE);
        Order validatedOrder = new OrderValidator().validateOrder(order, restaurants);

//        assertEquals(OrderStatus.UNDEFINED, validatedOrder.getOrderStatus(), " 有效订单应被成功处理！");
//        assertEquals(OrderValidationCode.NO_ERROR, validatedOrder.getOrderValidationCode(), "订单验证代码应无错误！");
    }

    @Test
    public void testInvalidCardNumber() {
        var order = new Order();
        order.setCreditCardInformation(
                new CreditCardInformation(
                        "121212121212121211111",
                        String.format("%02d/%02d", ThreadLocalRandom.current().nextInt(1, 12), ThreadLocalRandom.current().nextInt(24, 29)),
                        "222"
                )
        );
        order.setOrderStatus(OrderStatus.UNDEFINED);
        order.setOrderValidationCode(OrderValidationCode.CARD_NUMBER_INVALID);
        order.setPizzasInOrder(new Pizza[]{new Pizza("A", 1212) , new Pizza("Pizza A", 2300)});
        order.setPriceTotalInPence(3512 + SystemConstants.ORDER_CHARGE_IN_PENCE);

        Order validatedOrder = new OrderValidator().validateOrder(order, restaurants);

        assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus(), "无效卡号的订单应被拒绝！");
//        assertEquals(OrderValidationCode.CARD_NUMBER_INVALID, validatedOrder.getOrderValidationCode(), "订单验证代码应为 'CARD_NUMBER_INVALID'！");
    }

    @Test
    public void testExpiredCard() {
        var order = new Order();
        order.setCreditCardInformation(
                new CreditCardInformation(
                        "1212121212121212",
                        String.format("%02d/%02d", ThreadLocalRandom.current().nextInt(1, 12), ThreadLocalRandom.current().nextInt(14, 19)),
                        "222"
                )
        );
        order.setOrderStatus(OrderStatus.UNDEFINED);
        order.setOrderValidationCode(OrderValidationCode.EXPIRY_DATE_INVALID);
        order.setPizzasInOrder(new Pizza[]{new Pizza("A", 1212) , new Pizza("Pizza A", 2300)});
        order.setPriceTotalInPence(3512 + SystemConstants.ORDER_CHARGE_IN_PENCE);

        Order validatedOrder = new OrderValidator().validateOrder(order, restaurants);

        assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus(), "过期卡号的订单应被拒绝！");
//        assertEquals(OrderValidationCode.EXPIRY_DATE_INVALID, validatedOrder.getOrderValidationCode(), "订单验证代码应为 'EXPIRY_DATE_INVALID'！");
    }

    @Test
    public void testInvalidCVV() {
        var order = new Order();
        order.setCreditCardInformation(
                new CreditCardInformation(
                        "1212121212121212",
                        String.format("%02d/%02d", ThreadLocalRandom.current().nextInt(1, 12), ThreadLocalRandom.current().nextInt(24, 29)),
                        "2222"
                )
        );
        order.setOrderStatus(OrderStatus.UNDEFINED);
        order.setOrderValidationCode(OrderValidationCode.CVV_INVALID);
        order.setPizzasInOrder(new Pizza[]{new Pizza("A", 1212) , new Pizza("Pizza A", 2300)});
        order.setPriceTotalInPence(3512 + SystemConstants.ORDER_CHARGE_IN_PENCE);

        Order validatedOrder = new OrderValidator().validateOrder(order, restaurants);

        assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus(), "无效 CVV 订单应被拒绝！");
//        assertEquals(OrderValidationCode.CVV_INVALID, validatedOrder.getOrderValidationCode(), " 订单验证代码应为 'CVV_INVALID'！");
    }

    @Test
    public void testIncorrectTotalPrice() {
        var order = new Order();
        order.setCreditCardInformation(
                new CreditCardInformation(
                        "1212121212121212",
                        String.format("%02d/%02d", ThreadLocalRandom.current().nextInt(1, 12), ThreadLocalRandom.current().nextInt(24, 29)),
                        "222"
                )
        );
        order.setOrderStatus(OrderStatus.UNDEFINED);
        order.setOrderValidationCode(OrderValidationCode.TOTAL_INCORRECT);
        order.setPizzasInOrder(new Pizza[]{new Pizza("A", 1212) , new Pizza("Pizza A", 2300)});
        order.setPriceTotalInPence(3512 + SystemConstants.ORDER_CHARGE_IN_PENCE);

        // 强行修改订单总价以模拟错误
        order.setPriceTotalInPence(9999);

        Order validatedOrder = new OrderValidator().validateOrder(order, restaurants);

        assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus(), "订单总价不匹配时应被拒绝！");
//        assertEquals(OrderValidationCode.TOTAL_INCORRECT, validatedOrder.getOrderValidationCode(), "订单验证代码应为 'TOTAL_INCORRECT'！");
    }

    @Test
    public void testRestaurantClosed() {
        var order = new Order();
        order.setCreditCardInformation(
                new CreditCardInformation(
                        "1212121212121212",
                        String.format("%02d/%02d", ThreadLocalRandom.current().nextInt(1, 12), ThreadLocalRandom.current().nextInt(24, 29)),
                        "222"
                )
        );
        order.setOrderStatus(OrderStatus.UNDEFINED);
        order.setOrderValidationCode(OrderValidationCode.RESTAURANT_CLOSED);
        order.setPizzasInOrder(new Pizza[]{new Pizza("A", 1212) , new Pizza("Pizza A", 2300)});
        order.setPriceTotalInPence(3512 + SystemConstants.ORDER_CHARGE_IN_PENCE);

        Order validatedOrder = new OrderValidator().validateOrder(order, restaurants);

        assertEquals(OrderStatus.INVALID, validatedOrder.getOrderStatus(), " 餐厅关闭时订单应被拒绝！");
//        assertEquals(OrderValidationCode.RESTAURANT_CLOSED, validatedOrder.getOrderValidationCode(), "订单验证代码应为 'RESTAURANT_CLOSED'！");
    }

}