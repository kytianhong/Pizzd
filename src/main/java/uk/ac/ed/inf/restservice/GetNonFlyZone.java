package uk.ac.ed.inf.restservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import uk.ac.ed.inf.restservice.data.NonFlyZone;

import java.io.IOException;
import java.net.URL;
public class GetNonFlyZone {
    public static final String NO_FLY_ZONE_URL = "noFlyZones";
    public static void main(String[] args) {
        var baseUrl = "https://ilp-rest.azurewebsites.net";

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
            var nonFlyZones = mapper.readValue(new URL(baseUrl + NO_FLY_ZONE_URL), NonFlyZone[].class);
            System.out.println("read all NO FLY ZONE");
            System.out.println(nonFlyZones[0].vertices()[2]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
