package model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import util.Verb;
@EqualsAndHashCode(callSuper = true)
@Data
public class OrderDeleteTest extends BasicOrder{

    private String[] orderID;
//    @till fixed
//    private String[] clOrdID;
    private String text;

    public OrderDeleteTest(String... orderID) {

        verb = Verb.DELETE;

        this.orderID = orderID;
        this.text = "has been canceled";
        }
}
