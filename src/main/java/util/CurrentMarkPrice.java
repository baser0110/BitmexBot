package util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CurrentMarkPrice {

    private CurrentMarkPrice() {
    }

    public static Double getMarkPrice() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://testnet.bitmex.com/api/v1/instrument?symbol=XBTUSD&columns=markPrice&count=1&reverse=false"))
                .header("accept", "application/json")
                .build();
        HttpResponse<String> httpResponse = HttpClient.newBuilder().build().send(httpRequest, HttpResponse.BodyHandlers.ofString());

        String jsonString = httpResponse.body();
        double result = 0.;
        String[] rows = jsonString.trim().split(",");
        for (String json: rows
        ) {
            if (json.contains("\"markPrice\":")) {
                json = json.replace("\"markPrice\":","");
                result = Double.parseDouble(json);
            }
        }

//        ObjectMapper objectMapper = new ObjectMapper();
//        String jsonText = httpResponse.body();
//        System.out.println(jsonText);
//        jsonText = jsonText.replace("[","");
//        jsonText = jsonText.replace("]","");
//        System.out.println(jsonText);
//        MarkPriceResponse markPrice = objectMapper.readValue(jsonText, MarkPriceResponse.class);

        return result;
    }

    public static class MarkPriceResponse {
        public String symbol;
        public String timestamp;
        public Double markPrice;

        public MarkPriceResponse() {
        }
    }
}
