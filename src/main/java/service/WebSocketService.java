package service;

import lombok.Getter;
import org.glassfish.tyrus.spi.ClientContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Endpoints;
import util.Expires;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@ClientEndpoint
public class WebSocketService {
    private final static Logger logger = LoggerFactory.getLogger(WebSocketService.class);
    private final long MAX_DELAY = 5000;
    private final String apiKey;
    private final String apiExpires;
    private final String apiSignature;
    private final BotLogic logic;
    @Getter
    private boolean isConnected = false;
    private Session session;
    private final AtomicLong lastMessageTime = new AtomicLong();
    public WebSocketService(BotLogic logic, String secretKey, String apiKey) {
        this.apiExpires = Expires.createExpires();
        this.apiSignature = SignatureService.getSignature(secretKey, getMessage());
        this.apiKey = apiKey;
        this.logic = logic;
    }

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
        if (session != null & session.isOpen()) {
            try {
                logic.stopAndClear();
                session.close();
                isConnected = false;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            logger.debug("Session has been manually closed >> ");
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
        return "{\"op\": \"authKeyExpires\", \"args\": [\"" + apiKey + "\", " + apiExpires + ", \"" + apiSignature + "\"]}";
    }

    private String getOrderSubscribe() {
        return "{\"op\": \"subscribe\", \"args\": [\"order\"]}";
    }
}


//        try{
//        if(container!=null&&container instanceof ClientContainer clientContainer){
//        clientContainer.getClient().stop();
//        Log.debug("Container is stopped");
//        }
//
//        if(session!=null&&session.isOpen()){
//        session.close();
//        Log.debug("Session is closed");
//        }
//        }catch(Exception e){
//        Log.error("Error during cleanup websocket threads: "+e.getMessage());
//        }
