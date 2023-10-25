package uk.ac.ed.inf;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.util.EntityUtils;
import uk.ac.ed.inf.ilp.constant.OrderStatus;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

/**
 * Main function to get data from Rest service
 *
 */
public class App 
{
    public static void main( String[] args ) throws ParseException, IOException {
        if (args.length != 2) {
            System.err.println("Usage: java -jar target/PizzaDronz-1.0-SNAPSHOT.jar <date> <baseURL>");
            System.exit(1);
        }
        String dateString = args[0];
        String baseUrl = args[1];

        // Validate the date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date date = dateFormat.parse(dateString);

        // Make an HTTP request to the REST service
        HttpClient httpClient = HttpClients.createDefault();
        String requestUrl = baseUrl + "/api/orders?date=" + dateString;
        HttpGet httpGet = new HttpGet(requestUrl);

        String response = EntityUtils.toString(httpClient.execute(httpGet).getEntity());

        // Parse the JSON response
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(response);

        // Process the JSON data
        // You can access the data in the JSON response using rootNode.get("key") or other methods
        // Example: JsonNode orders = rootNode.get("orders");

        // Store the data in a JSON file
        String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(date);
        saveJsonToFile(rootNode, "flightpath-" + dateStr + ".json");
        saveGeoJsonToFile(rootNode, "drone-" + dateStr + ".geojson");
        saveJsonToFile(rootNode, "deliveries-" + dateStr + ".json");
//        System.out.println("Data saved to: " + filename);
    }

    private static void saveJsonToFile(JsonNode data, String filename) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(new File(filename), data);
        System.out.println("Data saved to: " + filename);
    }

    private static void saveGeoJsonToFile(JsonNode data, String filename) throws IOException {
        // Implement the logic to save data in GeoJSON format to the specified filename
        // Example: use a GeoJSON library to create the GeoJSON file
        // Replace this with your actual code to save GeoJSON data
        System.out.println("GeoJSON data saved to: " + filename);
    }
}
