package service;

import entity.User;

public interface UserService {
    boolean setUser(String username, String password);
    boolean setKeysForUser(String username, String secretKey, String apiKey);
    User getUser(String username);

}
