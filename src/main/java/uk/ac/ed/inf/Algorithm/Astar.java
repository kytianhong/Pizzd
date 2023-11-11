package uk.ac.ed.inf.Algorithm;

import uk.ac.ed.inf.Interfaces.LngLatHandler;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.restservice.data.FlightPath;

import java.util.*;
import java.util.stream.Collectors;

public class Astar {
    public static Map<LngLat, Double> neighbors(LngLat position, NamedRegion central, NamedRegion[] nonFlyZones) {
        Map<LngLat, Double> neighbors = new HashMap<>();
        double angleIncrement = 22.5;
        double angle = 0;
        boolean isCentral = new LngLatHandler().isInCentralArea(position,central);
        if (isCentral){
            while ( angle < 360) {
                LngLat exposition = new LngLatHandler().nextPosition(position, angle);
                if (!isNonFly(exposition,nonFlyZones)){neighbors.put(exposition, angle);}
                angle += angleIncrement;
                }
        }
        else{
            while ( angle < 360) {
                LngLat exposition = new LngLatHandler().nextPosition(position, angle);
                neighbors.put(exposition, angle);
                angle += angleIncrement;
            }
        }
//        System.out.println(neighbors.size());
        return neighbors;
    }
    public static boolean isNonFly(LngLat exposition, NamedRegion[]nonFlyZones){
        List<Boolean>isInNonFlyRegion = new ArrayList<>();
        for (NamedRegion r:nonFlyZones) {
            boolean isNextNonFly = new LngLatHandler().isInRegion(exposition,r);
            isInNonFlyRegion.add(isNextNonFly);
        }
        if(isInNonFlyRegion.contains(Boolean.TRUE)){//if exposition in one or more fly region
            return true;
        }else {
            return false;
        }
    }
    public static double heuristic(LngLat a, LngLat b) {
        double x1 = a.lng();
        double y1 = a.lat();
        double x2 = b.lng();
        double y2 = b.lat();
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }
    public static double euclideanDistance(LngLat a, LngLat b) {
        double x1 = a.lng();
        double y1 = a.lat();
        double x2 = b.lng();
        double y2 = b.lat();

        double deltaX = x2 - x1;
        double deltaY = y2 - y1;

        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }
    public static List<FlightPath> aStarSearch(LngLat start, LngLat destination, NamedRegion central, NamedRegion[] nonFlyZones) {
        Map<LngLat, Double> costSoFar = new HashMap<>();
//        List<Node> cameFrom = new ArrayList<>();
        Map<LngLat, LngLat> cameFrom = new HashMap<>();//(LngLat nextPosition, LngLat parent)
        Map<LngLat, Double> Angle =new HashMap<>();// current position, angle

        costSoFar.put(start, 0.0);//set start point
//        cameFrom.add(new Node(start));
        cameFrom.put(start, start);//(current position,next position)
        Angle.put(start,999.0);
        //build priority queue to get the best next position
        PriorityQueue<LngLat> frontier = new PriorityQueue<>((loc1, loc2) -> {
//            double priority1 = costSoFar.get(loc1) + heuristic(loc1, destination);
//            double priority2 = costSoFar.get(loc2) + heuristic(loc2, destination);
            double priority1 = costSoFar.get(loc1) + euclideanDistance(loc1, destination);
            double priority2 = costSoFar.get(loc2) + euclideanDistance(loc2, destination);
            return Double.compare(priority1, priority2);
        });

        frontier.add(start);
        //build new flightpath object
        while (!frontier.isEmpty()) {
            LngLat current = frontier.poll();

            if (new LngLatHandler().isCloseTo(current, destination)) {
//                System.out.println("camefrom size is "+ cameFrom.size());
                List<FlightPath> flightPaths = getShortestPath(start,current,cameFrom,Angle);
//                System.out.println("flightPath size is "+ flightPaths.size());
                return flightPaths;
            }
            Map<LngLat, Double> neighbors=neighbors(current, central, nonFlyZones);
            for (LngLat next : neighbors.keySet()) {
                double newCost = costSoFar.get(current) + SystemConstants.DRONE_MOVE_DISTANCE;
                if (!costSoFar.containsKey(next) || newCost < costSoFar.get(next)) {
                    costSoFar.put(next, newCost);
                    frontier.add(next);
                    double angle =  neighbors.get(next);
                    Angle.put(current,angle);
                    cameFrom.put(next,current);
                }
            }
        }
       return null;
    }
    public static List<FlightPath> getShortestPath(LngLat start,LngLat destination, Map<LngLat, LngLat> cameFrom ,Map<LngLat, Double> Angle){
        LngLat currentPathTile = destination;
        List<LngLat> path = new ArrayList<>();
        while (currentPathTile!=start){
            path.add(currentPathTile);
            currentPathTile = cameFrom.get(currentPathTile);
//            System.out.println("camefrom size is "+ cameFrom.size());
            // Check for null to avoid potential NullPointerException
            if (currentPathTile == null) {
                // Handle the case where there's no valid path
//                System.out.println("currentPathTile is null");
                return Collections.emptyList();
            }else {
//                System.out.println("currentPathTile is "+ currentPathTile);
            }
        }
        path.add(start);// add start position
//        System.out.println(start);
        Collections.reverse(path);
//        System.out.println("path size is "+ path.size());
//        List<FlightPath>  flightPaths = cameFrom.keySet().stream()
        List<FlightPath>  flightPaths = path.stream()
                .map(p -> new FlightPath(
                        p.lng(),
                        p.lat(),
                        Angle.get(p),
                        cameFrom.get(p).lng(),
                        cameFrom.get(p).lat()
                )).collect(Collectors.toList());// Collect results into List<Deliveries>
        return flightPaths;
    }
//    public static void main(String[] args) {
////        // test A* search algorithm here
//        LngLat APPLETON =new LngLat(-3.186874, 55.944494);
//////        LngLat RESTAURANT = new LngLat(-3.1912869215011597, 55.945535152517735);//rest 1
//        LngLat RESTAURANT1 = new LngLat(-3.202541470527649, 55.943284737579376);//rest 2
//        LngLat RESTAURANT2 = new LngLat(-3.1920251175592607,55.943292317760935);//rest 3
//        NamedRegion CentralArea= new NamedRegion("central",
//                                new LngLat[]{new LngLat(-3.192473, 55.946233),
//                                             new LngLat(-3.192473,55.942617),
//                                             new LngLat(-3.184319,55.942617),
//                                             new LngLat(-3.184319,55.946233)});
////
//        NamedRegion[] NonFlightZone = new NamedRegion[]{
//                                new NamedRegion("George Square Area",
//                                new LngLat[]{new LngLat(-3.190578818321228,55.94402412577528),
//                                        new LngLat(-3.1899887323379517,55.94284650540911),
//                                        new LngLat(-3.187097311019897,55.94328811724263),
//                                        new LngLat(-3.187682032585144,55.944477740393744),
//                                        new LngLat(-3.190578818321228,55.94402412577528)})
//                                };
////
//    List<FlightPath> AppleToRest1 = new Astar().aStarSearch(APPLETON,RESTAURANT1,CentralArea,NonFlightZone);
//    List<FlightPath> RestToApple1 = new Astar().aStarSearch(RESTAURANT1,APPLETON,CentralArea,NonFlightZone);
//        List<FlightPath> AppleToRest2 = new Astar().aStarSearch(APPLETON,RESTAURANT2,CentralArea,NonFlightZone);
//        List<FlightPath> RestToApple2 = new Astar().aStarSearch(RESTAURANT2,APPLETON,CentralArea,NonFlightZone);
//        LocalDate date = LocalDate.of(2022, 1, 27);
//        List<FlightPath> droneMoveList=new ArrayList<>();
//        droneMoveList.addAll(AppleToRest1);
//        droneMoveList.addAll(RestToApple1);
//        droneMoveList.addAll(AppleToRest2);
//        droneMoveList.addAll(RestToApple2);
//        new GeoJSONGenerator().generatorGeoJSON(droneMoveList,date);
//    }
}
