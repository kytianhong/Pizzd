package uk.ac.ed.inf;

import org.junit.jupiter.api.Test;
import uk.ac.ed.inf.Algorithm.Astar;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.restservice.data.FlightPath;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PathfindingTest {

    @Test
    public void testPathfinding() {
        Astar pathfinder = new Astar();
        NamedRegion central = new NamedRegion("CentralArea", new LngLat[]{new LngLat(-3.1865, 55.9435)});
        NamedRegion[] noFlyZones = new NamedRegion[]{new NamedRegion("NoFlyZone1", new LngLat[]{new LngLat(-3.1875, 55.9440)})};

        LngLat start = new LngLat(-3.1868, 55.9445);
        LngLat destination = new LngLat(-3.1900, 55.9438);

        List<FlightPath> path = pathfinder.aStarSearch(start, destination, central, noFlyZones);

        assertNotNull(path, "✅ 路径应成功生成！");
        assertFalse(path.isEmpty(), "✅ 生成的路径不应为空！");
    }
}
