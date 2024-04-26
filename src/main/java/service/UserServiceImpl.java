package service;

import dao.UserDAO;
import entity.User;

public class UserServiceImpl implements UserService{
    private final UserDAO userDAO = new UserDAO();

    public boolean setUser(String username, String password) {
        return userDAO.setUser(username, password);
    }
    public boolean setKeysForUser(String username, String secretKey, String apiKey) {
        return userDAO.setKeysForUser(username, secretKey, apiKey);
    }
    public User getUser(String username) {
        return userDAO.getUser(username);
    }

}
