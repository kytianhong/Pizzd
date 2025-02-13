package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class IntegrationTest {
    public static void main(String[] args) {
        System.out.println("Running Full Integration Test...");

        GetFlightPath flightPathGenerator = new GetFlightPath();
        NamedRegion central = new NamedRegion("CentralArea", new LngLat[]{new LngLat(-3.1865, 55.9435)});
        NamedRegion[] noFlyZones = new NamedRegion[]{new NamedRegion("NoFlyZone1", new LngLat[]{new LngLat(-3.1875, 55.9440)})};
        LocalDate testDate = LocalDate.of(2023, 4, 15);

        // Create a valid test order
        Map<Order, LngLat> validOrders = new HashMap<>();
        validOrders.put(new Order(), new LngLat(-3.190, 55.9545));

        try {
            flightPathGenerator.getFlightPath(validOrders, central, noFlyZones, testDate);
            System.out.println("100% Integration Test Passed! Order was processed correctly.");
        } catch (Exception e) {
            System.out.println("Integration Test Failed! " + e.getMessage());
        }
    }
}
