package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import exception.WrongKeyException;
import model.BasicOrder;
import model.OrderHttpRequest;
import model.OrderResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class OrderHttpService {
    private final static Logger logger = LoggerFactory.getLogger(OrderHttpService.class);
    private OrderHttpService() {
    }

    public static List<OrderResponse> send(BasicOrder order, String secretKey, String apiKey) {

        HttpRequest httpRequest = null;
        HttpResponse<String> httpResponse = null;

        try {
            httpRequest = new OrderHttpRequest(order, secretKey, apiKey).getHttpRequest();
            httpResponse = HttpClient.newBuilder().build().send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            logger.error("Error in sending http request >> ");
        }

        boolean isPostType = httpRequest.method().equals("POST") || httpRequest.method().equals("PUT");
        List<OrderResponse> responseOrders = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonText = httpResponse.body();
            if (jsonText.contains("Invalid API Key") || jsonText.contains("Signature not valid"))
                throw new WrongKeyException(jsonText.contains("Invalid API Key") ? "Invalid API Key" : "Invalid Secret Key");
            logger.debug("order is sent {jsonText} >> " + jsonText);

        try {
            if (isPostType) responseOrders.add(objectMapper.readValue(jsonText, OrderResponse.class));
            else responseOrders.addAll(objectMapper.readValue(jsonText, List.class));
        } catch (JsonProcessingException e) {
            logger.error("JSON parsing error >> ");
        }

            logger.debug("order is sent {responseOrders} >> " + responseOrders);

        return responseOrders;
    }
}
