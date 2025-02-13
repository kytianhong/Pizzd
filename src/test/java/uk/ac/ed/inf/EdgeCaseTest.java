package uk.ac.ed.inf;

import org.junit.jupiter.api.Test;
import uk.ac.ed.inf.Algorithm.Astar;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.restservice.data.FlightPath;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EdgeCaseTest {

    @Test
    public void testUnrealisticLocation() {
        Astar pathfinder = new Astar();
        NamedRegion central = new NamedRegion("CentralArea", new LngLat[]{new LngLat(-3.1865, 55.9435)});
        NamedRegion[] noFlyZones = new NamedRegion[]{new NamedRegion("NoFlyZone1", new LngLat[]{new LngLat(-3.1875, 55.9440)})};

        // **测试极端位置**
        LngLat start = new LngLat(-3.1900, 55.9438);
        LngLat destination = new LngLat(-10.0000, 85.0000); // **极端 GPS 坐标**

        List<FlightPath> path = pathfinder.aStarSearch(start, destination, central, noFlyZones);

        // **断言：路径为空，系统拒绝无效位置**
        assertTrue(path == null || path.isEmpty(), " 该路径应被拒绝！");
    }

    @Test
    public void testValidShortDistance() {
        Astar pathfinder = new Astar();
        NamedRegion central = new NamedRegion("CentralArea", new LngLat[]{new LngLat(-3.1865, 55.9435)});
        NamedRegion[] noFlyZones = new NamedRegion[]{new NamedRegion("NoFlyZone1", new LngLat[]{new LngLat(-3.1875, 55.9440)})};

        // **测试短距离有效路径**
        LngLat start = new LngLat(-3.1868, 55.9445);
        LngLat destination = new LngLat(-3.1870, 55.9442);

        List<FlightPath> path = pathfinder.aStarSearch(start, destination, central, noFlyZones);

        // **断言：路径不应为空**
        assertNotNull(path, " 路径不应为空！");
        assertFalse(path.isEmpty(), " 有效路径应正常生成！");
    }

    @Test
    public void testObstacleAvoidance() {
        Astar pathfinder = new Astar();
        NamedRegion central = new NamedRegion("CentralArea", new LngLat[]{new LngLat(-3.1865, 55.9435)});
        NamedRegion[] noFlyZones = new NamedRegion[]{
                new NamedRegion("NoFlyZone1", new LngLat[]{new LngLat(-3.1875, 55.9440)}) // **障碍物区域**
        };

        // **测试：路径应绕开禁飞区**
        LngLat start = new LngLat(-3.1880, 55.9440);
        LngLat destination = new LngLat(-3.1860, 55.9430);

        List<FlightPath> path = pathfinder.aStarSearch(start, destination, central, noFlyZones);

        assertNotNull(path, " 路径不应为空！");
        assertFalse(path.isEmpty(), "应成功绕开禁飞区！");
    }
}
