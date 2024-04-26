import model.*;
import service.*;
import util.WeirdKeyStorage;

public class App {
    public static void main( String[] args ) throws Exception {
//        OrderPostTest post = new OrderPostTest(Symbol.XBTUSD, Side.Buy,100.,30000., OrdType.Limit);
//        System.out.println(OrderHttpService.send(post));
//        OrderDeleteTest delete = new OrderDeleteTest("c3f39d5b-0440-4dc8-ba8d-08dfaca048b3","78a9df49-b115-4bd2-98b0-dcd842a07ce6");
        OrderGetTest get = new OrderGetTest(1.);
        System.out.println(OrderHttpService.send(get,"iuQ5LYS","0bXQOdMv95zWL29Sn0oID-gx"));

//        BotService wss = new BotService(
//                new FibonacciOrderService(new FibonacciOrderSet(6,100,100), WeirdKeyStorage.SECRET_KEY , WeirdKeyStorage.API_KEY)
//                , WeirdKeyStorage.SECRET_KEY, WeirdKeyStorage.API_KEY);
//        wss.run();

//        System.out.println(CurrentMarkPrice.getMarkPrice());

//        FibonacciOrderService.(new FibonacciOrderSet(6,100,100));

//        UserDAO.setUser(null,null);
//        UserDAO.setKeyForUser("root","123","123");
//        System.out.println(new UserDAO().getUser("root"));
//        System.out.println(new UserDAO1().validate("root","default"));
    }
}
