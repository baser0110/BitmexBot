package model;


import lombok.Data;
import model.order_parameters.*;

@Data
public class OrderPost {
    private String symbol;
    private String side;
    private Double orderQty;
    private Double price;
    private String ordType;
    private Double stopPx;
    private Double displayQty;
    private String clOrdID;
    private String clOrdLinkID;
    private Double pegOffsetValue;
    private String pegPriceType;
    private String timeInForce;
    private String execInst;
    private String contingencyType;
    private String text;
    public OrderPost(Symbol symbol, Side side, Double orderQty, Double price, Double displayQty, Double stopPx, String clOrdID, String clOrdLinkID,
                     Double pegOffsetValue, PegPriceType pegPriceType, OrdType ordType, TimeInForce timeInForce,
                     ExecInst execInst, ContingencyType contingencyType, String text) {

        if (symbol == null) throw new IllegalArgumentException("Parameter symbol is required");
        else this.symbol = symbol.toString();

        if (orderQty == null) throw new IllegalArgumentException("Parameter orderQty is required");
        else this.orderQty = orderQty;

        if (side == null) this.side = orderQty > 0 ? Side.Buy.toString() : Side.Sell.toString();
        else this.side = side.toString();

        if (ordType == null) this.ordType = OrdType.Market.toString();
        else this.ordType = ordType.toString();

        this.displayQty = displayQty;

        if (price == null && (ordType == OrdType.Limit || ordType == OrdType.LimitIfTouched || ordType == OrdType.StopLimit))
            throw new IllegalArgumentException("Parameter price is required if ordType one of: Limit, LimitIfTouched, StopLimit");
        else this.price = price;

        if (ordType == OrdType.Stop || ordType == OrdType.LimitIfTouched || ordType == OrdType.StopLimit
                || ordType == OrdType.MarketIfTouched) this.stopPx = stopPx;
        else this.stopPx = null;

        if (pegPriceType == null) this.pegPriceType = "";
        else this.pegPriceType = pegPriceType.toString();

        if (clOrdID == null) this.clOrdID = "";
        else this.clOrdID = clOrdID;

        if (clOrdLinkID == null) this.clOrdLinkID = "";
        else this.clOrdLinkID = clOrdLinkID;

        if (ordType == OrdType.Pegged || ordType == OrdType.Stop || ordType == OrdType.LimitIfTouched || ordType == OrdType.StopLimit
                || ordType == OrdType.MarketIfTouched) this.pegOffsetValue = pegOffsetValue;
        else this.pegOffsetValue = null;

        if (timeInForce == null) this.timeInForce = "";
        else this.timeInForce = timeInForce.toString();

        if (execInst == null) this.execInst = "";
        else this.execInst = execInst.toString();

        if (contingencyType == null) this.contingencyType = "";
        else this.contingencyType = contingencyType.toString();

        this.text = text;
    }
}
