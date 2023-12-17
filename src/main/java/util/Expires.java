package util;

public class Expires {
    private static final long EXPIRES_DELAY = 5;
    public static String createExpires() {
        return Long.toString(System.currentTimeMillis() / 1000 + EXPIRES_DELAY);
    }
}
