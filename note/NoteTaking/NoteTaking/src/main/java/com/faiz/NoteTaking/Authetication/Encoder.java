package com.faiz.NoteTaking.Authetication;

import java.security.MessageDigest;
import java.util.Base64;

import org.springframework.stereotype.Component;

@Component
public class Encoder {
    public String Enocode(String rawString) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(rawString.getBytes());
            return Base64.getEncoder().encodeToString((hash));
        } catch (Exception e) {
            throw new RuntimeException("Error while encoding the password");
        }
    }

    public boolean match(String rawString, String encodedString) {
        try {
            return Enocode(rawString).equals(encodedString);
        } catch (Exception e) {
            throw new RuntimeException("Error while matching the password");
        }
    }
}
