package uk.ac.ed.inf;

import uk.ac.ed.inf.Algorithm.Astar;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.restservice.data.FlightPath;
import uk.ac.ed.inf.restservice.data.ToWriteFlight;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Functional {
    public static void main(String[] args) {
        System.out.println("Small test for ILPDATAobject");
        var orderStatus = OrderStatus.DELIVERED;
        System.out.println("ILPDATAobject.jar was used");

        NamedRegion[] NonFlightZone = new NamedRegion[]{
                                new NamedRegion("Dr Elsie Inglis Quadrangle",
                                new LngLat[]{new LngLat(-3.1907182931900024,55.94519570234043),
                                        new LngLat(-3.1906163692474365,
                                                 55.94498241796357),
                                        new LngLat(-3.1900262832641597,
                                                 55.94507554227258),
                                        new LngLat(-3.190133571624756,
                                                55.94529783810495),
                                        new LngLat(-3.1907182931900024,
                                                 55.94519570234043)})
                                };

        LngLat APPLETON =new LngLat(-3.186874, 55.944494);
        LngLat RESTAURANT = new LngLat(-3.1912869215011597,55.945535152517735);


        NamedRegion central = new NamedRegion("central",
                                new LngLat[]{new LngLat(-3.192473, 55.946233),
                                             new LngLat(-3.192473,55.942617),
                                             new LngLat(-3.184319,55.942617),
                                             new LngLat(-3.184319,55.946233)});
        //new a list which contain the final flight path list of all orders
        List<FlightPath> droneMoveList=new ArrayList<>();
//        List<ToWriteFlight> toWriteFlights = new ArrayList<>();

            //get the flight path of order i
            List<FlightPath> AppleToRest = new Astar().aStarSearch(RESTAURANT,APPLETON,central,NonFlightZone);

            System.out.println("path size is:"+AppleToRest.size());
            //add all drone move path of current order into final flight path list
            droneMoveList.addAll(AppleToRest);
//         rebuild the write_flight_path
            List<ToWriteFlight> aTr = AppleToRest.stream()
                    .map(p -> new ToWriteFlight(
                            "12345",
                            p.fromLongitude(),
                            p.fromLatitude(),
                            p.angle(),
                            p.toLongitude(),
                            p.toLatitude()
                    )).collect(Collectors.toList());

//        add all flight path of current order into final flight path list
//            toWriteFlights.addAll(aTr);
//
//        LocalDate date = LocalDate.of(2023, 1, 1);
////        new GetFlightPath().writeFlightPath( toWriteFlights, date, validatedOrder );
//        new GeoJSONGenerator().generatorGeoJSON(droneMoveList,date);
//        LngLat exposition = new LngLat(-3.186874,
//                55.944494);
//        LngLat position = new LngLat(-3.187174,
//                55.944494);

//        LngLat exposition = new LngLat(-3.1906387214561915,55.94392871386398);
//        LngLat position = new LngLat(-3.1905326554390134,55.94403477988116);

//        if (intersects(NonFlightZone[0],exposition,position)){
//            System.out.println("不相交");
//        }else {
//            System.out.println("相交");
//        }
    }
}