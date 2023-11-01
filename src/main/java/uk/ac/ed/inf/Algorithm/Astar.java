package uk.ac.ed.inf.Algorithm;

import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.Interfaces.LngLatHandler;

import java.util.*;

public class Astar {
    public static List<LngLat> neighbors(LngLat position) {
        List<LngLat> neighbors = new ArrayList<>();
        for (double angle=0; angle<360;){
            LngLat exposition = new LngLatHandler().nextPosition(position, angle);
            neighbors.add(exposition);
            angle+=22.5;
        }
        return neighbors;
    }
    public static float cost(LngLat fromId, LngLat toId) {

        return 0.0f;
    }
    public static double heuristic(LngLat a, LngLat b) {
        double x1 = a.lng();
        double y1 = a.lat();
        double x2 = b.lng();
        double y2 = b.lat();
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    public static Map<LngLat, LngLat> aStarSearch( LngLat start, LngLat destination) {
        Map<LngLat, Double> costSoFar = new HashMap<>();
        Map<LngLat, LngLat> cameFrom = new HashMap<>();
        costSoFar.put(start, 0.0);
        cameFrom.put(start, null);
        PriorityQueue<LngLat> frontier = new PriorityQueue<>((loc1, loc2) -> {
            double priority1 = costSoFar.get(loc1) + heuristic(loc1, destination);
            double priority2 = costSoFar.get(loc2) + heuristic(loc2, destination);
            return Double.compare(priority1, priority2);
        });
//        PriorityQueue<LngLat> frontier = new PriorityQueue<>();
        frontier.add(start);

        while (!frontier.isEmpty()) {
            LngLat current = frontier.poll();

            if (current.equals(destination)) {
                break;
            }

            for (LngLat next : neighbors(current)) {
                double newCost = costSoFar.get(current) + SystemConstants.DRONE_MOVE_DISTANCE;
                if (!costSoFar.containsKey(next) || newCost < costSoFar.get(next)) {
                    costSoFar.put(next, newCost);
//                    double priority = newCost + heuristic(next, destination);
                    frontier.add(next);
                    cameFrom.put(next, current);
                }
            }
        }

        return cameFrom;
    }

    public static void main(String[] args) {
        // You can test your A* search algorithm here
    }
}
