package uk.ac.ed.inf;

import org.junit.jupiter.api.Test;
import uk.ac.ed.inf.Algorithm.Astar;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.restservice.data.FlightPath;

import java.util.List;

public class PerformanceTest {

    @Test
    public void testPathfindingPerformance() {
        long startTime = System.nanoTime();

        Astar pathfinder = new Astar();
        NamedRegion central = new NamedRegion("CentralArea", new LngLat[]{new LngLat(-3.1865, 55.9435)});
        NamedRegion[] noFlyZones = new NamedRegion[]{new NamedRegion("NoFlyZone1", new LngLat[]{new LngLat(-3.1875, 55.9440)})};

        LngLat start = new LngLat(-3.193, 55.9445);
        LngLat destination = new LngLat(-3.1900, 55.9445);
        List<FlightPath> path = pathfinder.aStarSearch(start, destination, central, noFlyZones);

        long endTime = System.nanoTime();
        double executionTime = (endTime - startTime) / 1e6; // 转换为毫秒

        System.out.println("路径计算时间: " + executionTime + " ms");
//        assertTrue(executionTime < 25, " 路径计算应在 25ms 内完成！");
    }
}
