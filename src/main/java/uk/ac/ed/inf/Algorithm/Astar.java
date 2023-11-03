package uk.ac.ed.inf.Algorithm;

import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.Interfaces.LngLatHandler;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.restservice.data.FlightPath;

import java.util.*;

public class Astar {
    public static Map<LngLat, Double> neighbors(LngLat position, NamedRegion central, NamedRegion[] nonFlyZones) {
        Map<LngLat, Double> neighbors = new HashMap<>();
        double angleIncrement = 22.5;
        for (double angle = 0; angle < 360; angle += angleIncrement) {

            LngLat exposition = new LngLatHandler().nextPosition(position, angle);
            boolean isCentral = new LngLatHandler().isInCentralArea(position,central);
            if (isCentral){
                for (NamedRegion r:nonFlyZones) {
                    boolean isNextNonFly = new LngLatHandler().isInRegion(exposition,r);
                    if(!isNextNonFly){
                        neighbors.put(exposition,angle);
                    }
                }
            }else neighbors.put(exposition,angle);
        }
        return neighbors;
    }
//    public static boolean isNeighborsNonFly(LngLat position, NamedRegion central,NamedRegion[] nonFlyZones) {
//
//    }
    public static double heuristic(LngLat a, LngLat b) {
        double x1 = a.lng();
        double y1 = a.lat();
        double x2 = b.lng();
        double y2 = b.lat();
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    public static List<FlightPath> aStarSearch( LngLat start, LngLat destination,NamedRegion central,NamedRegion[] nonFlyZones) {
        Map<LngLat, Double> costSoFar = new HashMap<>();
        Map<LngLat, Double> cameFrom = new HashMap<>();//(LngLat startPosition, double angle)
        //build new flightpath object
        List<FlightPath>  flightPaths = new ArrayList<>();
        //set start point
        costSoFar.put(start, 0.0);
        cameFrom.put(start, 999.0);//(current position, angle)
        //build priority queue to get the best next position
        PriorityQueue<LngLat> frontier = new PriorityQueue<>((loc1, loc2) -> {
            double priority1 = costSoFar.get(loc1) + heuristic(loc1, destination);
            double priority2 = costSoFar.get(loc2) + heuristic(loc2, destination);
            return Double.compare(priority1, priority2);
        });

        frontier.add(start);

        while (!frontier.isEmpty()) {
            LngLat current = frontier.poll();

            if (current.equals(destination)) {
                break;
            }
            Map<LngLat, Double> neighbors=neighbors(current, central, nonFlyZones);
            for (LngLat next : neighbors.keySet()) {

                double newCost = costSoFar.get(current) + SystemConstants.DRONE_MOVE_DISTANCE;
                if (!costSoFar.containsKey(next) || newCost < costSoFar.get(next)) {
                    costSoFar.put(next, newCost);
                    frontier.add(next);
                    double angle =  neighbors.get(next);
                    cameFrom.put(next,angle);
                    //add new flight path point
                    flightPaths.add(new FlightPath(current.lng(), current.lat(),angle,next.lng(),next.lat()));
                }
            }
        }

        return flightPaths;
    }

    public static void main(String[] args) {
        // You can test your A* search algorithm here
    }
}
