package util;

import model.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import java.util.Properties;

public class SessionFactoryService {
    private static SessionFactoryService instance;
    private final SessionFactory sessionFactory;

    private SessionFactoryService() {
        Properties properties = new Properties();
        properties.put(Environment.DRIVER, "com.mysql.jdbc.Driver");
        properties.put(Environment.URL, "jdbc:mysql://localhost:3306/bitmexbot/user");
        properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQLDialect");
        properties.put("hibernate.connection.useUnicode", true);
        properties.put("hibernate.connection.characterEncoding", "UTF-8");
        properties.put("hibernate.connection.charSet", "UTF-8");
        properties.put(Environment.USER, "root");
        properties.put(Environment.PASS, "default");

        sessionFactory = new Configuration()
                .setProperties(properties)
                .addAnnotatedClass(User.class)
                .buildSessionFactory();
    }

    public static SessionFactory getSessionFactory() {
        if (instance == null) {
            instance = new SessionFactoryService();
        }
        return instance.sessionFactory;
    }
}
