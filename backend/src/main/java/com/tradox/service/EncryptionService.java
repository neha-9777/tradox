package com.tradox.service;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

@Service
public class EncryptionService {

    private static final String ALGORITHM = "AES";
    private static final String DEFAULT_SECRET_KEY = "TradoX_Secure_AES256_Encryption_Key_2026";

    private final SecretKey secretKey;

    public EncryptionService() {
        this(null);
    }

    public EncryptionService(@org.springframework.beans.factory.annotation.Value("${app.encryption.secret-key:${ENCRYPTION_SECRET:}}") String secret) {
        String keyToUse = (secret != null && !secret.isBlank()) ? secret : DEFAULT_SECRET_KEY;
        this.secretKey = generateKey(keyToUse);
    }

    public String encrypt(String plainText) {
        if (plainText == null || plainText.isBlank()) {
            return plainText;
        }
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting secret: " + e.getMessage(), e);
        }
    }

    public String decrypt(String cipherText) {
        if (cipherText == null || cipherText.isBlank()) {
            return cipherText;
        }
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodedBytes = Base64.getDecoder().decode(cipherText);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            // Return raw string if not encrypted
            return cipherText;
        }
    }

    private SecretKey generateKey(String secret) {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] key = sha.digest(secret.getBytes(StandardCharsets.UTF_8));
            return new SecretKeySpec(key, ALGORITHM);
        } catch (Exception e) {
            throw new RuntimeException("Error initializing encryption key", e);
        }
    }
}
