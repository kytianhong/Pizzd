package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Stresstest {
    public static void main(String[] args) {
        System.out.println("Running Stress Test...");

        GetFlightPath flightPathGenerator = new GetFlightPath();
        NamedRegion central = new NamedRegion("CentralArea", new LngLat[]{new LngLat(-3.1865, 55.9435)});
        NamedRegion[] noFlyZones = new NamedRegion[]{new NamedRegion("NoFlyZone1", new LngLat[]{new LngLat(-3.1875, 55.9440)})};
        LocalDate testDate = LocalDate.of(2023, 4, 15);

        // Generate 50 test orders
        Map<Order, LngLat> orders = new HashMap<>();
        for (int i = 0; i < 50; i++) {
            orders.put(new Order(), new LngLat(-3.1970, 55.9485));
        }

        try {
            flightPathGenerator.getFlightPath(orders, central, noFlyZones, testDate);
            System.out.println(" Stress test with 50 orders completed successfully.");
        } catch (Exception e) {
            System.out.println(" Stress test failed: " + e.getMessage());
        }
    }
}
