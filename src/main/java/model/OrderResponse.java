package model;

import lombok.Data;

@Data
public class OrderResponse {
    public String orderID;
    public String clOrdID;
    public String clOrdLinkID;
    public Double account;
    public String symbol;
    public String side;
    public Double orderQty;
    public Double price;
    public Double displayQty;
    public Double stopPx;
    public Double pegOffsetValue;
    public String pegPriceType;
    public String currency;
    public String settlCurrency;
    public String ordType;
    public String timeInForce;
    public String execInst;
    public String contingencyType;
    public String ordStatus;
    public String triggered;
    public Boolean workingIndicator;
    public String ordRejReason;
    public Double leavesQty;
    public Double cumQty;
    public Double avgPx;
    public String text;
    public String transactTime;
    public String timestamp;

    public OrderResponse() {}

    @Override
    public String toString() {
        return "OrderResponse{" +
                "orderID='" + orderID + '\'' +
                ", side='" + side + '\'' +
                ", orderQty=" + orderQty +
                ", price=" + price +
                ", ordStatus='" + ordStatus + '\'' +
                '}';
    }
}
