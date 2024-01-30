package model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.BotLogic;
import service.FibonacciOrderService;
import service.SignatureService;
import util.Endpoints;
import util.Expires;
import util.WeirdKeyStorage;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@ClientEndpoint
public class OrderWebSocket {
    private final static Logger logger = LoggerFactory.getLogger(OrderWebSocket.class);
    private final long MAX_DELAY = 5000;
    private BotLogic logic = new FibonacciOrderService(new FibonacciOrderSet(6,100,100));
    public OrderWebSocket() {
        apiExpires = Expires.createExpires();
        message = getMessage();
        apiSignature = SignatureService.getSignature(WeirdKeyStorage.SECRET_KEY, message);
    }
    private String apiExpires;
    private String message;
    private String apiSignature;
    @Getter
    private boolean isConnected = false;
    private Session session;
    private AtomicLong lastMessageTime = new AtomicLong();

    public void connect() {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        try {
            session = container.connectToServer(this, URI.create(Endpoints.WS_ENDPOINT));
            session.setMaxIdleTimeout(TimeUnit.MINUTES.toMillis(60));
            if (session.isOpen()) {
                isConnected = true;
                logger.debug("session has been established >> ");
                session.getBasicRemote().sendText(getAuthentication());
                session.getBasicRemote().sendText(getOrderSubscribe());
                logic.start();
            }
        } catch (DeploymentException | IOException e) {
            System.out.println(e.getMessage());
        }
        heartBitTimer();
    }

    @OnMessage
    public void orderInformation(String info) {
        lastMessageTime.set(System.currentTimeMillis());
        if (info.equals("pong")) {
            logger.debug("Last time updated by pong >> " + lastMessageTime.toString());
            return;
        }
        logger.debug("Last time updated by info >> " + lastMessageTime.toString());
//        System.out.println(info);
        logic.update(info);
    }

    public void heartBitTimer() {
        ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
        timer.scheduleWithFixedDelay(() -> {
            if (System.currentTimeMillis() - lastMessageTime.get() > MAX_DELAY)
                sendPing();
            if (System.currentTimeMillis() - lastMessageTime.get() > MAX_DELAY * 5) {
                isConnected = false;
                stopConnection();
            }
            if (!isConnected) {
                timer.shutdownNow();
                if (timer.isShutdown()) logger.debug("HeatBitTimer is stopped >> ");
            }
            }, 0, MAX_DELAY, TimeUnit.MILLISECONDS);
    }

    public void stopConnection() {
//        WebSocketContainer container = session.getContainer();
//        if (container != null) container.stop();
        if (session != null & session.isOpen()) {
            try {
                session.close();
                isConnected = false;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            logger.debug("Session is closed >> ");
        }
    }

    private void sendPing() {
        try {
            if (session.isOpen()) session.getBasicRemote().sendText("ping");
            logger.debug("Ping message is sent >> ");
        } catch (IOException e) {
            logger.error("Ping sending error >> " + e.getMessage());
//            throw new RuntimeException(e);
        }
    }

    private String getMessage() {
        return Endpoints.WS_VERB_AND_PATH + apiExpires;
    }

    private String getAuthentication() {
        return "{\"op\": \"authKeyExpires\", \"args\": [\"" + WeirdKeyStorage.API_KEY + "\", " + apiExpires + ", \"" + apiSignature + "\"]}";
    }

    private String getOrderSubscribe() {
        return "{\"op\": \"subscribe\", \"args\": [\"order\"]}";
    }
}
