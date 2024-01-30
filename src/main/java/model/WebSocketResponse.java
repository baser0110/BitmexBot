package model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

public class WebSocketResponse {
    public String table;
    public String action;
    public ArrayList<OrderResponse> data;
    public WebSocketResponse() {}
}
