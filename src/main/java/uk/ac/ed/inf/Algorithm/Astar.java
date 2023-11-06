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
//            System.out.println(current);
            if (new LngLatHandler().isCloseTo(current,destination)) {
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
//                    FlightPath f = new FlightPath(current.lng(),current.lat(),angle,next.lng(),next.lat());
//                    flightPaths.add(f);
//                    System.out.println(f);
                    //add new flight path
                }
            }
            FlightPath f = new FlightPath(current.lng(),current.lat(),cameFrom.get(current));
            flightPaths.add(f);
            System.out.println(f);
        }
//        System.out.println(cameFrom);
//        System.out.println(cameFrom.size());
        return flightPaths;
    }

    public static void main(String[] args) {
        // test A* search algorithm here
        LngLat APPLETON =new LngLat(-3.186874, 55.944494);
        LngLat RESTAURANT = new LngLat(-3.1912869215011597, 55.945535152517735);
        NamedRegion CentralArea= new NamedRegion("central",
                                new LngLat[]{new LngLat(-3.192473, 55.946233),
                                             new LngLat(-3.192473,55.942617),
                                             new LngLat(-3.184319,55.942617),
                                             new LngLat(-3.184319,55.946233)});

        NamedRegion[] NonFlightZone = new NamedRegion[]{
                                new NamedRegion("George Square Area",
                                new LngLat[]{new LngLat(-3.190578818321228,55.94402412577528),
                                        new LngLat(-3.1899887323379517,55.94284650540911),
                                        new LngLat(-3.187097311019897,55.94328811724263),
                                        new LngLat(-3.187682032585144,55.944477740393744),
                                        new LngLat(-3.190578818321228,55.94402412577528)})
                                };

        List<FlightPath> flightPaths  = aStarSearch(RESTAURANT,APPLETON,CentralArea,NonFlightZone);
        System.out.println(flightPaths.size());
    }
}
