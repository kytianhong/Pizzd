package uk.ac.ed.inf.Algorithm;

import uk.ac.ed.inf.Interfaces.LngLatHandler;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.restservice.data.FlightPath;

import java.awt.geom.Line2D;
import java.util.*;

public class Astar {
    /*find current node's neighbours
     */
    private Map<LngLat, Double> neighbors(LngLat position, LngLat destination, NamedRegion central, NamedRegion[] nonFlyZones) {
        //create an empty hashmap to contain next position and their corresponding angle
        Map<LngLat, Double> neighbors = new HashMap<>();
        double angleIncrement = 22.5;// define angle incremental
        double angle = 0;// define initial start angle
        // find whether current position is in central area
        boolean isCentral = new LngLatHandler().isInCentralArea(position,central);
        // find whether destination is in central area
        boolean destInCentral = new LngLatHandler().isInCentralArea(destination,central);
        //if current position and destination are both in central area
        if (isCentral && destInCentral){
            while ( angle < 360) {
                // calculate next position
                LngLat exposition = new LngLatHandler().nextPosition(position, angle);
                boolean nextInCentral = new LngLatHandler().isInCentralArea(exposition,central);
                //make sure next position does not leave central area
                if(nextInCentral){
                    //make sure next position is not in none fly region, if not, add it into neighbour list
                    if (!(isNonFly(exposition,nonFlyZones,position))){neighbors.put(exposition, angle);}
                }
                angle += angleIncrement;// angle incremental
            }
        }//if both current position and destination are not in central area
        else{
            while ( angle < 360) {
                // calculate next position
                LngLat exposition = new LngLatHandler().nextPosition(position, angle);
                //make sure next position is not in none fly region, if not, add it into neighbour list
                if (!(isNonFly(exposition,nonFlyZones,position))){neighbors.put(exposition, angle);}
                angle += angleIncrement;// angle incremental
            }
        }
        return neighbors;// return neighbour HashMap
    }
    /*find whether next node's in none fly region or next step intersect with none fly region
     */
    private boolean isNonFly(LngLat exposition, NamedRegion[]nonFlyZones,LngLat position){
        List<Boolean>isInNonFlyRegion = new ArrayList<>();
        //search all none fly regions
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
    /*find whether next step intersect with none fly region
     */
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
    /*calculate heuristic distance
     */
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
    /*
     build A* research from start position to destination
     */
    public List<FlightPath> aStarSearch(LngLat start, LngLat destination, NamedRegion central, NamedRegion[] nonFlyZones) {
        // create a HashMap to contain current position and its cost so far
        Map<LngLat, Double> costSoFar = new HashMap<>();
        // create a HashMap to contain nextPosition and its corresponding parents
        Map<LngLat, LngLat> cameFrom = new HashMap<>();
        // create a HashMap to contain nextPosition and its corresponding angle move from parent
        Map<LngLat, Double> Angle =new HashMap<>();
        costSoFar.put(start, 0.0);//set up start point
        cameFrom.put(start, start);//set up start point's current position and next position
        Angle.put(start,999.0);//set up start point's position and come from angle
        //build priority queue to get the best next position by costsofar and heuristic distance
        PriorityQueue<LngLat> frontier = new PriorityQueue<>((loc1, loc2) -> {
            double priority1 = costSoFar.get(loc1) + heuristic(loc1, destination);
            double priority2 = costSoFar.get(loc2) + heuristic(loc2, destination);
            return Double.compare(priority1, priority2);
        });
        frontier.add(start);// add start into frontier priority queue
        //build new flightpath object
        while (!frontier.isEmpty()) {
            // poll the current point which with the highest priority
            LngLat current = frontier.poll();
            //if current position is close to destination, return the shortest path and exit loop
            if (new LngLatHandler().isCloseTo(current, destination)) {
                return getShortestPath(start,current,cameFrom,Angle);
            }
            // if not close to destination, build next neighbour list
            Map<LngLat, Double> neighbors = neighbors(current,destination, central, nonFlyZones);
            for (LngLat next : neighbors.keySet()) {
                // for each neighbour, calculate the newest cost so far
                double newCost = costSoFar.get(current) + SystemConstants.DRONE_MOVE_DISTANCE;
                // if this new neighbour not in costsofar list or the cost smaller than current cost
                if (!costSoFar.containsKey(next) || newCost < costSoFar.get(next)) {
                    costSoFar.put(next, newCost); // add this new cost into cost list or update
                    frontier.add(next);// add this next position into frontier priority queue
                    double angle =  neighbors.get(next);
                    Angle.put(next,angle);// add this next position and its corresponding angle into Angle HashMap
                    cameFrom.put(next,current);// add this next position and its corresponding parent into camefrom HashMap
                }
            }
        }
       return null;
    }
    /*method to get the shortest path from A* algorithm
     */
    private List<FlightPath> getShortestPath(LngLat start,LngLat destination, Map<LngLat, LngLat> cameFrom ,Map<LngLat, Double> Angle){
        LngLat currentPathTile = destination;// set the current to destination first
        List<LngLat> path = new ArrayList<>();// set up an empty array list to contain the shortest path
        while (currentPathTile!=start){// loop stop until it met the start position
            path.add(currentPathTile);// add the current path tile to the shortest path array
            currentPathTile = cameFrom.get(currentPathTile);// get the current path tile's parent from cameFrom hashmap
            // Check for null to avoid potential NullPointerException
            if (currentPathTile == null) {
                // Handle the case where there's no valid path
                return Collections.emptyList();
            }
        }
        path.add(start);// add start position to the shortest path list
        Collections.reverse(path);// reverse the list to make sure it is from start to destination
        List<FlightPath>  flightPaths = new ArrayList<>();
        // for each position in this list, reform it into a  legal flight path with: from position, angle, to position
        for (int i = 0; i+1 < path.size(); i++) {
            FlightPath f = new FlightPath(
                    path.get(i).lng(), path.get(i).lat(),
                    Angle.get(path.get(i+1)), path.get(i+1).lng(), path.get(i+1).lat()
            );
            flightPaths.add(f);
        }
        // add the hover at the end of single flight path
        FlightPath hover = new FlightPath(destination.lng(),destination.lat(),999.0,destination.lng(),destination.lat());
        flightPaths.add(hover);
        return flightPaths;
    }
}
