package service;

import model.OrderWebSocket;

import java.util.Scanner;

public class WebSocketService {
    private boolean isManualStopped = false;
    private OrderWebSocket ws;

    public WebSocketService() {
        ws = new OrderWebSocket();
    }

    public void run() {
        ws.connect();
        Scanner scanner = new Scanner(System.in);
        while (!isManualStopped) {
            if (scanner.nextLine().trim().equalsIgnoreCase("stop")) isManualStopped = true;
            if (!ws.isConnected() && !isManualStopped) {
                reconnect();
            }
        }
        ws.stopConnection();
    }

    private void reconnect() {
        ws = new OrderWebSocket();
        ws.connect();
    }

}
