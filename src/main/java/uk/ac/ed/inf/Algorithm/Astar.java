package uk.ac.ed.inf.Algorithm;

import uk.ac.ed.inf.Interfaces.LngLatHandler;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.restservice.data.FlightPath;

import java.awt.geom.Line2D;
import java.util.*;

public class Astar {
    private Map<LngLat, Double> neighbors(LngLat position, LngLat destination, NamedRegion central, NamedRegion[] nonFlyZones) {
        Map<LngLat, Double> neighbors = new HashMap<>();
        double angleIncrement = 22.5;
        double angle = 0;
        boolean isCentral = new LngLatHandler().isInCentralArea(position,central);
        boolean destInCentral = new LngLatHandler().isInCentralArea(destination,central);
        if (isCentral && destInCentral){
            while ( angle < 360) {
                LngLat exposition = new LngLatHandler().nextPosition(position, angle);
                boolean nextInCentral = new LngLatHandler().isInCentralArea(exposition,central);
                if(nextInCentral){
                    if (!(isNonFly(exposition,nonFlyZones,position))){neighbors.put(exposition, angle);}
                }
                angle += angleIncrement;
            }
        }
        else{
            while ( angle < 360) {
                LngLat exposition = new LngLatHandler().nextPosition(position, angle);
                if (!(isNonFly(exposition,nonFlyZones,position))){neighbors.put(exposition, angle);}
                angle += angleIncrement;
            }
        }
        return neighbors;
    }
    private boolean isNonFly(LngLat exposition, NamedRegion[]nonFlyZones,LngLat position){
        List<Boolean>isInNonFlyRegion = new ArrayList<>();
        for (NamedRegion r:nonFlyZones) {
            boolean isNextNonFly = new LngLatHandler().isInRegion(exposition,r);
            boolean isIntersect = intersects(r, exposition,position);
            if(isNextNonFly){
                return true;
            }
            isInNonFlyRegion.add(isIntersect);
        }
        //if exposition in one or more fly region
        return isInNonFlyRegion.contains(Boolean.TRUE);
    }
    private boolean intersects(NamedRegion region, LngLat exposition, LngLat position ) {
        Line2D.Double line1 = new Line2D.Double(position.lng(), position.lat(), exposition.lng(), exposition.lat());

        for (int i = 0; i < region.vertices().length - 1; i++) {
            Line2D.Double line2 = new Line2D.Double(region.vertices()[i].lng(), region.vertices()[i].lat()
                    , region.vertices()[i+1].lng(), region.vertices()[i+1].lat());
            if(line1.intersectsLine(line2)){
//                System.out.println("相交，返回true");
                return true;//相交，返回true
            }
        }
        return false;
    }
    private static double heuristic(LngLat a, LngLat b) {
        //Manhattan distance
//        double x1 = a.lng();
//        double y1 = a.lat();
//        double x2 = b.lng();
//        double y2 = b.lat();
//        return Math.abs(x1 - x2) + Math.abs(y1 - y2);

        //Euclidean Distance
        double x1 = a.lng();
        double y1 = a.lat();
        double x2 = b.lng();
        double y2 = b.lat();

        double deltaX = x2 - x1;
        double deltaY = y2 - y1;

        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    public List<FlightPath> aStarSearch(LngLat start, LngLat destination, NamedRegion central, NamedRegion[] nonFlyZones) {
        Map<LngLat, Double> costSoFar = new HashMap<>();
        Map<LngLat, LngLat> cameFrom = new HashMap<>();//(LngLat nextPosition, LngLat parent)
        Map<LngLat, Double> Angle =new HashMap<>();// current position, angle

        costSoFar.put(start, 0.0);//set start point
        cameFrom.put(start, start);//(current position,next position)
        Angle.put(start,999.0);
        //build priority queue to get the best next position
        PriorityQueue<LngLat> frontier = new PriorityQueue<>((loc1, loc2) -> {
            double priority1 = costSoFar.get(loc1) + heuristic(loc1, destination);
            double priority2 = costSoFar.get(loc2) + heuristic(loc2, destination);
            return Double.compare(priority1, priority2);
        });

        frontier.add(start);
        //build new flightpath object
        while (!frontier.isEmpty()) {
            LngLat current = frontier.poll();

            if (new LngLatHandler().isCloseTo(current, destination)) {
                return getShortestPath(start,current,cameFrom,Angle);
            }
            Map<LngLat, Double> neighbors = neighbors(current,destination, central, nonFlyZones);
            for (LngLat next : neighbors.keySet()) {
                double newCost = costSoFar.get(current) + SystemConstants.DRONE_MOVE_DISTANCE;
                if (!costSoFar.containsKey(next) || newCost < costSoFar.get(next)) {
                    costSoFar.put(next, newCost);
                    frontier.add(next);
                    double angle =  neighbors.get(next);
                    Angle.put(next,angle);
                    cameFrom.put(next,current);
                }
            }
        }
       return null;
    }
    private List<FlightPath> getShortestPath(LngLat start,LngLat destination, Map<LngLat, LngLat> cameFrom ,Map<LngLat, Double> Angle){
        LngLat currentPathTile = destination;
        List<LngLat> path = new ArrayList<>();
        while (currentPathTile!=start){
            path.add(currentPathTile);
            currentPathTile = cameFrom.get(currentPathTile);
            // Check for null to avoid potential NullPointerException
            if (currentPathTile == null) {
                // Handle the case where there's no valid path
                return Collections.emptyList();
            }

        }
        path.add(start);// add start position
        Collections.reverse(path);
        List<FlightPath>  flightPaths = new ArrayList<>();
        for (int i = 0; i+1 < path.size(); i++) {
            FlightPath f = new FlightPath(
                    path.get(i).lng(),
                    path.get(i).lat(),
                    Angle.get(path.get(i+1)),
                    path.get(i+1).lng(),
                    path.get(i+1).lat()
            );
            flightPaths.add(f);
        }
        FlightPath hover = new FlightPath(destination.lng(),destination.lat(),999.0,destination.lng(),destination.lat());
        flightPaths.add(hover);
        System.out.println("path size is: "+flightPaths.size());
        return flightPaths;
    }
}
