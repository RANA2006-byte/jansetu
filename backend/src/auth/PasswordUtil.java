
package auth;

import java.security.SecureRandom;
import java.security.MessageDigest;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordUtil {

    private static final int ITERATIONS = 210000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;

    // Generate a unique random salt for every password
    public static String generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];

        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);

        return Base64.getEncoder().encodeToString(salt);
    }

    // Convert password into a secure hash
    public static String hashPassword(String password, String salt) {

        try {
            byte[] saltBytes = Base64.getDecoder().decode(salt);

            KeySpec spec = new PBEKeySpec(
                    password.toCharArray(),
                    saltBytes,
                    ITERATIONS,
                    KEY_LENGTH);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            byte[] hash = factory.generateSecret(spec).getEncoded();

            return Base64.getEncoder().encodeToString(hash);

        } catch (Exception e) {
            throw new RuntimeException("Password hashing failed", e);
        }
    }

    // Verify entered password against stored hash
    public static boolean verifyPassword(
            String password, String salt, String storedHash) {

        String generatedHash = hashPassword(password, salt);

        byte[] generatedBytes = Base64.getDecoder().decode(generatedHash);

        byte[] storedBytes = Base64.getDecoder().decode(storedHash);

        return MessageDigest.isEqual(generatedBytes, storedBytes);
    }
}
