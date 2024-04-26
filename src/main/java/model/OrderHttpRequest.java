package model;

import lombok.Data;
import util.Endpoints;
import util.Expires;
import service.SignatureService;
import util.WeirdKeyStorage;

import java.net.URI;
import java.net.http.HttpRequest;

@Data
public class OrderHttpRequest {
    private HttpRequest httpRequest;
    private BasicOrder order;
    private String apiExpires;
    private String message;
    private String data;
    private String apiSignature;
    private String apiKey;

    public OrderHttpRequest(BasicOrder order, String secretKey, String apiKey) throws Exception {
        this.order = order;
        this.apiKey = apiKey;
        this.data = order.orderToJson();
        this.apiExpires = Expires.createExpires();
        this.message = getMessage();
        this.apiSignature = SignatureService.getSignature(secretKey, message);
        this.httpRequest = createHttpRequest();
//        System.out.println(order.getExtendURL());
    }

    private HttpRequest createHttpRequest() {
        return HttpRequest.newBuilder()
                .method(order.getVerb(), HttpRequest.BodyPublishers.ofString(data))
                .uri(URI.create(Endpoints.TEST_BASE_URL + Endpoints.ORDER + order.getExtendURL()))
                .header("Content-Type","application/json")
                .header("api-key", apiKey)
                .header("api-expires", apiExpires)
                .header("api-signature", apiSignature)
                .build();
    }

    private String getMessage() {
//        System.out.println(data);
        return order.getVerb() + order.getPath() + apiExpires + data;
    }
}
