package model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import util.Verb;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrderGetTest extends BasicOrder {
    private boolean reverse = true;
    private Double count;
    public OrderGetTest(Double count) {

        verb = Verb.GET;

        this.count = count;

        extendURL();
        path += extendURL;
    }
    private void extendURL() {
        extendURL = "?reverse=" + reverse;
        if (count != null) extendURL += "&count=" + getCount();
    }
}
