package uk.ac.ed.inf.restservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import uk.ac.ed.inf.restservice.data.CentralArea;

import java.io.IOException;
import java.net.URL;
public class GetCentral {
    public static final String CENTRAL_AREA_URL = "centralArea";
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
            var central = mapper.readValue(new URL(baseUrl + CENTRAL_AREA_URL), CentralArea.class);
            System.out.println("read all central area");
//            System.out.println(central.vertices()[1]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
