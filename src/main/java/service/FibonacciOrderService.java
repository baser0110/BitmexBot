package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.*;
import model.order_parameters.OrdType;
import model.order_parameters.Side;
import model.order_parameters.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.CurrentMarkPrice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FibonacciOrderService implements BotLogic {

    private final static Logger logger = LoggerFactory.getLogger(FibonacciOrderService.class);
    private Double startPrice;
    private Double lastBoughtPrice;
    private String accumulatedPriceBuyOrderId = null;
    private Double accumulatedPrice = 0.0;
    private final FibonacciOrderSet orderStartSet;
    private int levelCountDown;
    private List<OrderResponse> currentOrders = new ArrayList<>();
    private List<OrderResponse> canceledIgnoreList = new ArrayList<>();

    public FibonacciOrderService(FibonacciOrderSet orderStartSet) {
        try {
            this.startPrice = CurrentMarkPrice.getMarkPrice();
        } catch (IOException | InterruptedException e) {

            logger.error("getMarkPrice error, BotLogic hasn't been created >> ");

            throw new RuntimeException(e);
        }
        this.orderStartSet = orderStartSet;
        levelCountDown = orderStartSet.getLevel();
    }

    @Override
    public void start() {
        sendFibOrders(orderStartSet, Side.Buy, startPrice);
    }

    @Override
    public void update(String info) {

        List<OrderResponse> orders = infoToOrder(info);

        if (orders != null) {

            logger.debug("Response list >> " + orders);

            for (OrderResponse order: orders
                 ) {
                switch (order.ordStatus) {
                    case ("New") : currentOrders.add(order); break;
//                    case ("Canceled") :
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

        if (order.side.equals(Side.Buy.toString())) {

            logger.debug("Buy order is filled >> ");

            lastBoughtPrice = order.price;

            List<OrderResponse> iterationsList = new ArrayList<>(currentOrders);

            for (OrderResponse o: iterationsList
                 ) {
                // sell order deleting
                if (o.side.equals("Sell")) {

                    List<OrderResponse> response = OrderHttpService.send(new OrderDeleteTest(o.orderID));

//                    canceledIgnoreList.addAll(response);

                    logger.debug("order deleted >> " + response);

                    currentOrders.remove(o);

                    logger.debug("order deleted from currentOrders list >> " + o.orderID);
                }
                //
                if (o.orderID.equals(order.orderID)) {
                    // if filled accumulated buy order
                    if (o.orderID.equals(accumulatedPriceBuyOrderId)) {
                        // reset checked parameters
                        accumulatedPriceBuyOrderId = null;
                        accumulatedPrice = 0.0;

                        currentOrders.remove(o);

                        logger.debug("order deleted from currentOrders list >> " + o.orderID);

                        break;
                    }
                    currentOrders.remove(o);

                    logger.debug("order deleted from currentOrders list >> " + o.orderID);

                    levelCountDown--;

                    logger.debug("levelCountDown decrement to >> " + levelCountDown);
                }
            }
            // new sell orders set creating
            FibonacciOrderSet sellOrdersSet = new FibonacciOrderSet(orderStartSet.getLevel() - levelCountDown, orderStartSet.getStep(), orderStartSet.getSize());

            logger.debug("FibonacciOrderSet created with level >> " + sellOrdersSet.getLevel());

            sendFibOrders(sellOrdersSet, Side.Sell, lastBoughtPrice);

            logger.debug("New sell orders have been set >> ");
        }

        if (order.side.equals(Side.Sell.toString())) {

            logger.debug("Sell order is filled >> ");

            List<OrderResponse> iterationsList = new ArrayList<>(currentOrders);

            for (OrderResponse o: iterationsList
            ) {

//                if (canceledIgnoreList.contains(o)) continue;

                if (o.orderID.equals(order.orderID)) {
                    // accumulated order is exist?
                    if (accumulatedPriceBuyOrderId == null) {
                        // no >> create accumulated order
                        List<OrderResponse> response = OrderHttpService.send(new OrderPostTest(Symbol.XBTUSD, Side.Buy, o.orderQty, lastBoughtPrice, OrdType.Limit));

                        logger.debug("Accumulated order has been created >> " + response);
                        // set checked parameters
                        accumulatedPriceBuyOrderId = response.get(0).orderID;
                        accumulatedPrice = response.get(0).orderQty;

                        logger.debug("Checked parameters set to >> " + accumulatedPriceBuyOrderId + " " + accumulatedPrice);

                    } else {
                        // yes >> update checked parameters
                        accumulatedPrice += o.orderQty;

                        List<OrderResponse> response = OrderHttpService.send(new OrderPutTest(accumulatedPriceBuyOrderId, accumulatedPrice));

                        logger.debug("Accumulated order has been updated >> " + response);
                    }

                    currentOrders.remove(o);

                    logger.debug("order deleted from currentOrders list >> " + o.orderID);
                }
            }
        }
    }

    private void sendFibOrders(FibonacciOrderSet set, Side side, Double price) {

        List<BasicOrder> toSend = set.setFibOrders(side, price);

        for (BasicOrder order: toSend
        ) {
            List<OrderResponse> response = OrderHttpService.send(order);
        }
    }

}
