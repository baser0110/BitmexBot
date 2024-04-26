package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.*;
import model.order_parameters.OrdType;
import model.order_parameters.Side;
import model.order_parameters.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class FibonacciOrderService implements BotLogic {
    private final static Logger logger = LoggerFactory.getLogger(FibonacciOrderService.class);
    private final Double startPrice;
    private final FibonacciOrderSet orderStartSet;
    private final String apiKey;
    private final String secretKey;
    private Double lastBoughtPrice;
    private String aggregatedPriceBuyOrderId = null;
    private Double aggregatedPrice = 0.0;
    private int levelCountDown;
    private List<OrderResponse> currentBuyOrders = new ArrayList<>();
    private List<OrderResponse> currentSellOrders = new ArrayList<>();

    public FibonacciOrderService(FibonacciOrderSet orderStartSet, String secretKey, String apiKey) {
        try {
            this.startPrice = getMarkPrice();
            logger.debug("MarkPrice is " + startPrice);
        } catch (IOException | InterruptedException e) {
            logger.error("getMarkPrice error, BotLogic hasn't been created >> ");
            throw new RuntimeException(e);
        }
        this.orderStartSet = orderStartSet;
        this.apiKey = apiKey;
        this.secretKey = secretKey;
        levelCountDown = orderStartSet.getLevel();
    }

    @Override
    public void start() {
        currentBuyOrders.addAll(sendFibOrders(orderStartSet, Side.Buy, startPrice));
    }

    @Override
    public void update(String info) {
        List<OrderResponse> orders = infoToOrder(info);
        if (orders != null) {
            logger.debug("Response list >> " + orders);
            for (OrderResponse order: orders
                 ) {
                switch (order.ordStatus) {
                    case ("New") : break;
                    case ("Canceled") :
                    case ("Filled") :
                        try {
                            whenFilled(order);
                        } catch (Exception e) {
                            logger.error("Sell distribution is failed >> " + e);
                        }  break;
                }
            }
        }
    }

    @Override
    public synchronized void stopAndClear() {
        if (!currentBuyOrders.isEmpty()) {
            clearDeleting(currentBuyOrders);
        }
        if (!currentSellOrders.isEmpty()) {
            clearDeleting(currentSellOrders);
        }
        if (aggregatedPriceBuyOrderId != null) {
            OrderDeleteTest toD = new OrderDeleteTest(aggregatedPriceBuyOrderId);
            toD.setText("has been canceled with service manually shutdown");
            List<OrderResponse> response = OrderHttpService.send(toD, secretKey, apiKey);
            logger.debug("cleared with shutdown >> " + response);
        }
    }

    private List<OrderResponse> infoToOrder(String info){
        ObjectMapper objectMapper = new ObjectMapper();
        WebSocketResponse orders = null;
        try {
            orders = objectMapper.readValue(info, WebSocketResponse.class);
        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
        }
        if (orders == null) {
            logger.debug("Not a order info >> " + info);
            return null;
        }
        return orders.data;
    }

    private void whenFilled(OrderResponse order) throws Exception {
        logger.debug("whenFilled method is started >> ");

        if (order.text.contains("has been canceled with service manually shutdown")) return;

        if (order.side.equals(Side.Buy.toString())) {
            logger.debug("Buy order is filled >> ");
            // sell order deleting
            List<OrderResponse> iterationsList = new ArrayList<>(currentSellOrders);
            for (OrderResponse o: iterationsList
            ) {
                List<OrderResponse> response = OrderHttpService.send(new OrderDeleteTest(o.orderID), secretKey, apiKey);
                logger.debug("order deleted >> " + response);
                currentSellOrders.remove(o);
                logger.debug("order deleted from currentSellOrders list >> " + o.orderID);
            }
            // if filled aggregated buy order
            if (order.orderID.equals(aggregatedPriceBuyOrderId)) {
                // reset checked parameters
                aggregatedPriceBuyOrderId = null;
                aggregatedPrice = 0.0;
                logger.debug("aggregatedPriceBuyOrderId set in null >> " + order.orderID);
            } else {
                // delete buy order
                lastBoughtPrice = order.price;
                currentBuyOrders.remove(order);
                logger.debug("order deleted from currentBuyOrders list >> " + order.orderID);
                levelCountDown--;
                logger.debug("levelCountDown decrement to >> " + levelCountDown);
            }
            // redistribution sell orders
            FibonacciOrderSet sellOrdersSet = new FibonacciOrderSet(orderStartSet.getLevel() - levelCountDown, orderStartSet.getStep(), orderStartSet.getSize());
            logger.debug("FibonacciOrderSet created with level >> " + sellOrdersSet.getLevel());
            currentSellOrders = sendFibOrders(sellOrdersSet, Side.Sell, lastBoughtPrice);
            logger.debug("New sell orders have been set >> ");
        }

        if (order.side.equals(Side.Sell.toString())) {
            if (order.text.contains("has been canceled for redistribution")) return;
            logger.debug("Sell order is filled >> " + order);
            currentSellOrders.remove(order);
            logger.debug("order deleted from currentSellOrders list >> " + order.orderID);
            // aggregated order is exist?
            if (aggregatedPriceBuyOrderId == null) {
                // no >> create aggregated order
                List<OrderResponse> response = OrderHttpService
                        .send(new OrderPostTest(Symbol.XBTUSD, Side.Buy, order.orderQty, lastBoughtPrice, OrdType.Limit), secretKey, apiKey);
                logger.debug("Aggregated order has been created >> " + response);
                // set checked parameters
                aggregatedPriceBuyOrderId = response.get(0).orderID;
                aggregatedPrice = response.get(0).orderQty;
                logger.debug("Checked parameters set to >> " + aggregatedPriceBuyOrderId + " " + aggregatedPrice);
            } else {
                // yes >> update checked parameters
                aggregatedPrice += order.orderQty;
                List<OrderResponse> response = OrderHttpService
                        .send(new OrderPutTest(aggregatedPriceBuyOrderId, aggregatedPrice), secretKey, apiKey);
                logger.debug("Aggregated order has been updated >> " + response);
            }
        }
    }

    private List<OrderResponse> sendFibOrders(FibonacciOrderSet set, Side side, Double price) {
        List<OrderResponse> responses = new ArrayList<>();
        List<BasicOrder> toSend = set.setFibOrders(side, price);
        for (BasicOrder order: toSend
        ) {
            responses.addAll(OrderHttpService.send(order, secretKey, apiKey));
        }
        logger.debug("sendFibOrders >> " + responses);
        return responses;
    }

    private void clearDeleting (List<OrderResponse> toDelete) {
        for (OrderResponse o: toDelete
             ) {
            OrderDeleteTest toD = new OrderDeleteTest(o.orderID);
            toD.setText("has been canceled with service manually shutdown");
            List<OrderResponse> response = OrderHttpService.send(toD, secretKey, apiKey);
            logger.debug("cleared with shutdown >> " + response);
        }
    }

    private static Double getMarkPrice() throws IOException, InterruptedException {
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
        return result;

//        ObjectMapper objectMapper = new ObjectMapper();
//        String jsonText = httpResponse.body();
//        System.out.println(jsonText);
//        jsonText = jsonText.replace("[","");
//        jsonText = jsonText.replace("]","");
//        System.out.println(jsonText);
//        MarkPriceResponse markPrice = objectMapper.readValue(jsonText, MarkPriceResponse.class);
    }

//    public static class MarkPriceResponse {
//        public String symbol;
//        public String timestamp;
//        public Double markPrice;
//
//        public MarkPriceResponse() {
//        }
//    }
}
