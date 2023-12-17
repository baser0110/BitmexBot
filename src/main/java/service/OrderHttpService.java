package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.BasicOrder;
import model.OrderHttpRequest;
import model.OrderResponse;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class OrderHttpService {

    private OrderHttpService() {
    }

    public static List<OrderResponse> send(BasicOrder order) throws Exception {
        HttpRequest httpRequest = new OrderHttpRequest(order).getHttpRequest();
        HttpResponse<String> httpResponse = HttpClient.newBuilder().build().send(httpRequest, HttpResponse.BodyHandlers.ofString());

        boolean isPostType = httpRequest.method().equals("POST");
        List<OrderResponse> responseOrders = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonText = httpResponse.body();

        if (isPostType) responseOrders.add(objectMapper.readValue(jsonText, OrderResponse.class));
            else responseOrders.addAll(objectMapper.readValue(jsonText, List.class));

        return responseOrders;
    }
}
