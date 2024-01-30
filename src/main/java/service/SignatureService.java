package service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class SignatureService {
    private final static Logger logger = LoggerFactory.getLogger(SignatureService.class);
    private SignatureService() {}
    public static String getSignature(String secretKey, String message)  {

        Mac hmacSHA256 = null;
        StringBuilder hex = new StringBuilder();
        try {
            hmacSHA256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmacSHA256.init(secretKeySpec);
            byte[] signatureBytes = hmacSHA256.doFinal(message.getBytes(StandardCharsets.UTF_8));
            for (byte b: signatureBytes
            ) {
                hex.append(String.format("%02x",b));
            }
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            logger.error("error in signature creation process >> ");
        }

            logger.debug("signature is successfully created >> ");

        return hex.toString();
    }
}
