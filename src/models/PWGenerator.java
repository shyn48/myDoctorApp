package models;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class PWGenerator {
    public static String getSHA512Password(String passwordToEncrypt, byte[] salt){
        String generatedPassword = null;
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);

            byte[] bytes = md.digest(passwordToEncrypt.getBytes());

            StringBuilder sb = new StringBuilder();

            for (byte aByte : bytes) {
                //sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1)); Elaborate yet obfuscated way to convert a byte to a hexadecimal string
                sb.append(String.format("%02x", aByte)); //Better way of displaying a number as hex string
            }

            generatedPassword = sb.toString();

        } catch (NoSuchAlgorithmException e) {
            System.err.println(e.getMessage());
        }

        return generatedPassword;

    }

    public static byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }
}
