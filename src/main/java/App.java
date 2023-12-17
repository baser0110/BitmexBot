import model.*;
import model.order_parameters.OrdType;
import model.order_parameters.Side;
import model.order_parameters.Symbol;
import service.OrderHttpService;
import service.WebSocketService;

import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception {
//        OrderPostTest post = new OrderPostTest(Symbol.XBTUSD, Side.Buy,100.,30000., OrdType.Limit);
//        System.out.println(OrderHttpService.send(post));
//        OrderDeleteTest delete = new OrderDeleteTest("c3f39d5b-0440-4dc8-ba8d-08dfaca048b3","78a9df49-b115-4bd2-98b0-dcd842a07ce6");
//        OrderGetTest get = new OrderGetTest(10.);
//        System.out.println(OrderHttpService.send(get));
        WebSocketService wss = new WebSocketService();
        wss.run();
    }
}
