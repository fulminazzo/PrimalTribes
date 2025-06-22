package it.fulminazzo.primaltribes.Utils;

import it.fulminazzo.primaltribes.PrimalTribes;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class Base64Util {
    public static String encodeBase64(byte[] bytesArray) {
        return Base64.getEncoder().encodeToString(bytesArray);
    }

    public static String encrypt(String clearText, PublicKey publicKey) {
        if (clearText == null || publicKey == null) return null;
        if (clearText.replace(" ", "").equalsIgnoreCase("")) return "";
        try {
            Cipher encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] secretMessageBytes = clearText.getBytes(StandardCharsets.UTF_8);
            byte[] encryptedMessageBytes = encryptCipher.doFinal(secretMessageBytes);
            return encodeBase64(encryptedMessageBytes);
        }
        catch (Exception e) {
            PrimalTribes.logError(String.format("There was an error encrypting a string: %s", e.getMessage()));
            return null;
        }
    }

    public static byte[] decodeBase64(String encryptedString) {
        return Base64.getDecoder().decode(encryptedString);
    }

    public static String decrypt(String encryptedText, PrivateKey privateKey) {
        if (encryptedText == null || privateKey == null) return null;
        if (encryptedText.replace(" ", "").equalsIgnoreCase("")) return "";
        try {
            Cipher decryptCipher = Cipher.getInstance("RSA");
            decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] strEncryptedBytes = decodeBase64(encryptedText);
            byte[] decryptedMessageBytes = decryptCipher.doFinal(strEncryptedBytes);
            return new String(decryptedMessageBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            PrimalTribes.logError(String.format("There was an error decrypting a string: %s", e.getMessage()));
            return null;
        }
    }
}
