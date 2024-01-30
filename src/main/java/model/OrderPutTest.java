package model;

import lombok.Data;
import util.Verb;

@Data
public class OrderPutTest extends BasicOrder {
    private String orderID;
    private Double orderQty;
    public OrderPutTest(String orderId, Double newOrderQty) {
        verb = Verb.PUT;
        this.orderID = orderId;
        this.orderQty = newOrderQty;
    }
}
