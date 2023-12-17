package model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import model.order_parameters.*;
import util.Verb;


@EqualsAndHashCode(callSuper = true)
@Data
public class OrderPostTest extends BasicOrder {

    private String symbol;
    private String side;
    private Double orderQty;
    private Double price;
    private String ordType;
//    private String text;

    public OrderPostTest(Symbol symbol, Side side, Double orderQty, Double price, OrdType ordType) {

        verb = Verb.POST;

        if (symbol == null) throw new IllegalArgumentException("Parameter symbol is required");
        else this.symbol = symbol.toString();

        if (orderQty == null) throw new IllegalArgumentException("Parameter orderQty is required");
        else this.orderQty = orderQty;

        if (side == null) this.side = orderQty > 0 ? Side.Buy.toString() : Side.Sell.toString();
        else this.side = side.toString();

        if (ordType == null) this.ordType = OrdType.Market.toString();
        else this.ordType = ordType.toString();

        if (price == null && (ordType == OrdType.Limit || ordType == OrdType.LimitIfTouched || ordType == OrdType.StopLimit))
            throw new IllegalArgumentException("Parameter price is required if ordType one of: Limit, LimitIfTouched, StopLimit");
        else this.price = price;

//        this.text = "My crazy test :)";
    }
}
