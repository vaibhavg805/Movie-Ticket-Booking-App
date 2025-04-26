package com.vaibhav.booking.system.serviceimpl;

import com.vaibhav.booking.system.util.QRCodeUtil;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class MfaService {

    private final GoogleAuthenticator gAuth = new GoogleAuthenticator();


    private final Map<String, VerificationAttempt> verificationAttempts = new ConcurrentHashMap<>();

    @Value("${app.mfa.issuer:MFA Application}")
    private String issuer;

    @Value("${app.mfa.encryption.key}")
    private String encryptionKey;

    // Store encrypted secrets with their salts
    private static class SecretKeyData {
        String encryptedKey;
        String salt;
    }

    private static class VerificationAttempt {
        int count;
        long lastAttemptTime;
    }

    public Map<String, String> generateSecretKey(String username) {
        GoogleAuthenticatorKey key = gAuth.createCredentials();
        String secretKey = key.getKey();

        // Generate a random salt
        String salt = KeyGenerators.string().generateKey();

        // Create an encryptor with the password and salt
        TextEncryptor encryptor = Encryptors.text(encryptionKey, salt);

        // Encrypt the secret key
        String encryptedKey = encryptor.encrypt(secretKey);

        // Generate QR code URL using username
        String base64QrCode = QRCodeUtil.generateBase64QRCode(username, secretKey);

        Map<String, String> result = new HashMap<>();
        result.put("secretKey", secretKey); // Plain text for initial setup
        result.put("encryptedKey", encryptedKey); // For storage
        result.put("salt", salt); // For decryption
       result.put("qrCodeBase64", base64QrCode);
        return result;
    }

    public String decryptSecretKey(String encryptedKey, String salt) {
        TextEncryptor encryptor = Encryptors.text(encryptionKey, salt);
        return encryptor.decrypt(encryptedKey);
    }

    public boolean verifyCode(String username,String secretKey,String code) {
        // Check for brute force attempts
        VerificationAttempt attempt = verificationAttempts.computeIfAbsent(username,
                k -> new VerificationAttempt());

        long currentTime = System.currentTimeMillis();
        if (currentTime - attempt.lastAttemptTime < 30000) { // 30 seconds window
            if (attempt.count >= 3) {
                return false; // Too many attempts
            }
        } else {
            // Reset counter
            attempt.count = 0;
        }

        attempt.lastAttemptTime = currentTime;
        attempt.count++;

        try {
            int verificationCode = Integer.parseInt(code);
            boolean isValid = gAuth.authorize(secretKey, verificationCode);

            if (isValid) {
                // Reset attempts on success
                verificationAttempts.remove(username);
            }

            return isValid;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public String generateQrCodeUrl(String username, String secretKey) {
        return GoogleAuthenticatorQRGenerator.getOtpAuthURL(issuer, username,
                new GoogleAuthenticatorKey.Builder(secretKey).build());
    }
}
