package service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BotService {
    private static ExecutorService service = Executors.newCachedThreadPool();
    private WebSocketService ws;


    public BotService(BotLogic logic, String secretKey, String apiKey) {
        ws = new WebSocketService(logic, secretKey, apiKey);
    }

    public void run() {
        service = Executors.newCachedThreadPool();
        service.submit(() -> {
            ws.connect();
        });
    }

    public void stop() {
        ws.stopConnection();
        service.shutdownNow();
    }

}
