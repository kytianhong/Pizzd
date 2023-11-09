package uk.ac.ed.inf.Interfaces;

import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.interfaces.LngLatHandling;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.lang.Math;

/**
 * implement the needed computations for a LngLat
 */
public class LngLatHandler implements LngLatHandling {
    /**
     * get the distance between two positions
     * @param startPosition is where the start is
     * @param endPosition is where the end is
     * @return the Euclidean distance between the positions
     */
    public double distanceTo(LngLat startPosition, LngLat endPosition){
        double x1_x2 = startPosition.lng() - endPosition.lng();
        double y1_y2 = startPosition.lat() - endPosition.lat();
        double distance_sq = (x1_x2 * x1_x2) +  (y1_y2 * y1_y2);
        return Math.sqrt(distance_sq);
    }

    /**
     * check if two positions are close (<= than SystemConstants.DRONE_IS_CLOSE_DISTANCE)
     * @param startPosition is the starting position
     * @param otherPosition is the position to check
     * @return if the positions are close
     */
    public boolean isCloseTo(LngLat startPosition, LngLat otherPosition){
        if(startPosition == null  || otherPosition == null){
             return true;
        }
        double distance = distanceTo(startPosition,otherPosition);
        return distance < SystemConstants.DRONE_IS_CLOSE_DISTANCE;
    }

    /**
     * special handling shortcut for the central area.
     * Here an implementation might add special improved processing
     * as the central region is always rectangular
     * @param point to be checked
     * @param centralArea the central area
     * @return if the point is in the central area
     */
    public boolean isInCentralArea(LngLat point, NamedRegion centralArea) {
        if (centralArea == null){
            throw new IllegalArgumentException("the named region is null");
        }
        if (centralArea.name().equals(SystemConstants.CENTRAL_REGION_NAME) == false) {
            throw new IllegalArgumentException("the named region: " + centralArea.name() +
                    " is not valid - must be: " + SystemConstants.CENTRAL_REGION_NAME);
        }
        return isInRegion(point, centralArea);
    }

    /**
     * check if the @position is in the @region (includes the border)
     * @param position to check
     * @param region as a closed polygon
     * @return if the position is inside the region (including the border)
     */
    public boolean isInRegion(LngLat position, NamedRegion region){
        Path2D.Double area = new Path2D.Double();
        for (LngLat vertex : region.vertices()) {
            Point2D.Double point = new Point2D.Double(vertex.lng(), vertex.lat());
            if (area.getCurrentPoint() == null) {
                area.moveTo(point.getX(), point.getY());
            } else {
                area.lineTo(point.getX(), point.getY());
            }
        }
        area.closePath();
        Point2D.Double point = new Point2D.Double(position.lng(), position.lat());
        return area.contains(point);
    }

    /**
     * find the next position if an @angle is applied to a @startPosition
     * @param startPosition is where the start is
     * @param angle is the angle to use in degrees
     * @return the new position after the angle is used
     */
    public LngLat nextPosition(LngLat startPosition, double angle){
        double radians = Math.toRadians(angle);
        double a = SystemConstants.DRONE_MOVE_DISTANCE * Math.cos(radians);
        double b = SystemConstants.DRONE_MOVE_DISTANCE * Math.sin(radians);
        double x = startPosition.lng() + a;
        double y = startPosition.lat() + b;
        if(angle>=0 && angle<360){
            return new LngLat(x,y);
        }else{
            return startPosition;
        }
    }
}