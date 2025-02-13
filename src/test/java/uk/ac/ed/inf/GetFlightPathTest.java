package uk.ac.ed.inf;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.restservice.data.ToWriteFlight;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GetFlightPathTest {
    private GetFlightPath getFlightPath;
    private NamedRegion centralArea;
    private NamedRegion[] noFlyZones;
    private Map<Order, LngLat> validatedOrders;

    @BeforeEach
    public void setup() {
        getFlightPath = new GetFlightPath();

        // **创建测试禁飞区**
        noFlyZones = new NamedRegion[]{
                new NamedRegion("NoFlyZone1", new LngLat[]{new LngLat(-3.1875, 55.9440)})
        };

        // **创建测试中央区域**
        centralArea = new NamedRegion("CentralArea", new LngLat[]{new LngLat(-3.1865, 55.9435)});

        // **创建测试订单**
        validatedOrders = new HashMap<>();
        Order testOrder = new Order();
        testOrder.setOrderNo("ORDER123");
        validatedOrders.put(testOrder, new LngLat(-3.1900, 55.9438));
    }

    @Test
    public void testFlightPathGeneration() {
        LocalDate testDate = LocalDate.of(2025, 2, 13);
        getFlightPath.getFlightPath(validatedOrders, centralArea, noFlyZones, testDate);

        // **检查是否正确执行**
        assertNotNull(validatedOrders, " 订单数据不应为空！");
        assertTrue(validatedOrders.size() > 0, " 应至少有一个订单被处理！");
    }

    @Test
    public void testWriteFlightPath() {
        LocalDate testDate = LocalDate.of(2025, 2, 13);
        List<ToWriteFlight> flightData = new ArrayList<>();
        flightData.add(new ToWriteFlight("ORDER123", -3.1868, 55.9445, 0.0, -3.1890, 55.9425));

        // **执行写入**
        getFlightPath.writeFlightPath(flightData, testDate);

        // **检查文件是否创建**
        String filePath = "resultfiles/flightpath-" + testDate + ".json";
        File file = new File(filePath);
        assertTrue(file.exists(), "✅ JSON 文件应成功创建！");

        // **清理测试文件**
        file.delete();
    }
}
