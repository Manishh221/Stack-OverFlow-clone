package io.mountblue.StackOverflow.utils;

import lombok.experimental.UtilityClass;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class GravatarUtil {

    public static String getGravatar160pxUrl(String email) {
        return getGravatarUrl(email, 160);
    }

    public static String getGravatar80pxUrl(String email) {
        return getGravatarUrl(email, 80);
    }

    private static String getGravatarUrl(String email, int size) {
        String hash = md5Hex(email.trim().toLowerCase());
        return "https://www.gravatar.com/avatar/" + hash + "?s=" + size + "&d=identicon&r=g";
    }

    private static String md5Hex(String message) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(message.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
