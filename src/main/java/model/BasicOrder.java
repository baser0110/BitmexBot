package model;

import com.google.gson.Gson;
import util.Endpoints;
import util.Verb;

public class BasicOrder {
    protected BasicOrder() {}
    protected Verb verb;
    protected String path = Endpoints.ORDER_PATH;
    protected String extendURL = "";
    protected String orderToJson() {
        return verb == Verb.GET ? "" : new Gson().toJson(this);
    }
    protected String getVerb() {
        return verb.toString();
    }
    protected String getPath() {
        return path;
    }
    protected String getExtendURL() {
        return extendURL;
    }
}
