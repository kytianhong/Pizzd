package uk.ac.ed.inf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import uk.ac.ed.inf.ilp.data.NamedRegion;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;

public class GetFlightPath {
    public static final String NO_FLY_ZONE_URL = "noFlyZones";
    public static final String CENTRAL_AREA_URL = "centralArea";
    public static void main(String[] args) {
        if (args.length < 1){
            System.err.println("the base URL must be provided");
            System.exit(1);
        }

        var baseUrl = args[1];
        LocalDate date = LocalDate.parse(args[0]);
        if (baseUrl.endsWith("/") == false){
            baseUrl += "/";
        }

        try {
            var temp = new URL(baseUrl);
        } catch (Exception x) {
            System.err.println("The URL is invalid: " + x);
            System.exit(2);
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            NamedRegion central = mapper.readValue(new URL(baseUrl + CENTRAL_AREA_URL), NamedRegion.class);
            System.out.println("read all central area");
            System.out.println(central.vertices()[1]);

            NamedRegion[] nonFlyZones = mapper.readValue(new URL(baseUrl + NO_FLY_ZONE_URL), NamedRegion[].class);
            System.out.println("read all NO FLY ZONE");
            System.out.println(nonFlyZones[0].vertices()[2]);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
