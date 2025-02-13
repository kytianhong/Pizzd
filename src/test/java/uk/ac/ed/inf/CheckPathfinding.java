package uk.ac.ed.inf;

import uk.ac.ed.inf.Algorithm.Astar;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.restservice.data.FlightPath;

import java.util.List;

public class CheckPathfinding {
    public static void main(String[] args) {
        System.out.println("PizzaDrone Pathfinding Algorithm Test");

        // Define central delivery region
        NamedRegion centralArea = new NamedRegion("Central Area",
                new LngLat[]{new LngLat(-3.192473, 55.946233),
                        new LngLat(-3.192473, 55.942617),
                        new LngLat(-3.184319, 55.942617),
                        new LngLat(-3.192473, 55.946233),
                        new LngLat(-3.192473, 55.946233)
                } );


        // Define no-fly zones
        NamedRegion[] noFlyZones = new NamedRegion[]{
                new NamedRegion("NoFlyZone1", new LngLat[]{
                        new LngLat(-3.1880, 55.9440),
                        new LngLat(-3.1890, 55.9445)
                }),
                new NamedRegion("NoFlyZone2", new LngLat[]{
                        new LngLat(-3.1865, 55.9450),
                        new LngLat(-3.1890, 55.9445)
                })
        };

        // Define test paths
        LngLat[] testCases = {
                new LngLat(-3.1868, 55.9445), // Case 1: Standard path
//                new LngLat(-3.1900, 55.9438), // Case 2: Avoids No-Fly Zone
//                new LngLat(-3.1885, 55.9442)  // Case 3: No possible path
        };

        // Run tests
        for (int i = 0; i < testCases.length; i++) {
            System.out.println("\nTest Case " + (i + 1) + " - Destination: " + testCases[i]);

            long startTime = System.nanoTime();
            List<FlightPath> path = new Astar().aStarSearch(
                    new LngLat(-3.1868, 55.9445), // Start at Appleton Tower
                    testCases[i],
                    centralArea,
                    noFlyZones
            );
            long endTime = System.nanoTime();
            double executionTime = (endTime - startTime) / 1e9;

            if (path.isEmpty()) {
                System.out.println(" No valid path found.");
            } else {
                System.out.println("Path found with " + path.size() + " steps.");
            }

            System.out.println("Execution Time: " + executionTime + "s");
        }
    }
}
