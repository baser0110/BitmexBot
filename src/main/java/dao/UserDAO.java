package dao;


import entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.SessionFactoryUtil;
public class UserDAO {
    private final static Logger logger = LoggerFactory.getLogger(UserDAO.class);
    private final static SessionFactory sessionFactory = SessionFactoryUtil.getSessionFactory();

    public boolean setUser(String username, String password) {
        try {
            if (username == null || password == null || password.isEmpty() || username.isEmpty())
                throw new NullPointerException();
        } catch (NullPointerException e) {
            logger.error("username & password can not be empty");
            return false;
        }

        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {

//            Query<User> query = session.createQuery("select u from User u where u.username = :username", User.class);
//            query.setParameter("username", username);
//            if (!query.list().isEmpty()) throw new IllegalArgumentException("username is already used");

            transaction = session.beginTransaction();
            session.persist(User.builder().username(username).password(password).build());
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error in persisting new User " + username + " " +  e.getMessage());
            return false;
        }
        return true;
    }

    public boolean setKeysForUser(String username, String secretKey, String apiKey) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {

            Query<User> query = session.createQuery("from User where username = :username", User.class);
            query.setParameter("username", username);

            User user = query.getSingleResult();
            user.setApiKey(apiKey);
            user.setSecretKey(secretKey);

            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error in persisting new User " + username + e.getMessage());
            return false;
        }
        return true;
    }

    public User getUser(String username) {
        User user = null;
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User where username = :username", User.class);
            query.setParameter("username", username);
            user = query.uniqueResult();
        } catch (Exception e) {
                logger.error(username + " User not found " + e.getMessage());
        }
            return user;
    }
}
