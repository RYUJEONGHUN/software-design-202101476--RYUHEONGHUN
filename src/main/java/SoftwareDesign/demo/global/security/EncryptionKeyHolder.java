package SoftwareDesign.demo.global.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class EncryptionKeyHolder {

    private static String encryptionKey;

    public EncryptionKeyHolder(@Value("${data.encryption.key}") String key) {
        if (!StringUtils.hasText(key)) {
            throw new IllegalStateException("data.encryption.key must not be empty.");
        }
        encryptionKey = key;
    }

    public static String getEncryptionKey() {
        if (!StringUtils.hasText(encryptionKey)) {
            throw new IllegalStateException("Encryption key has not been initialized.");
        }
        return encryptionKey;
    }
}
