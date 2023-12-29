package com.bigtree.auth.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.util.Base64;

@Component
@Slf4j
public class CryptoHelper {

    @Value( "${crypto.key}" )
    private String privateKey;

    private char[] PASSWORD = "EF737CC29DAE7C80644A5B01544CBA61".toCharArray();
    private String SALT = "12345";
    private String IV = "79994A6EF73DA76C";


    public String encryptAndEncode(String raw)
    {
        try
        {
            Cipher c = getCipher(1,SALT);
            byte[] encryptedVal = c.doFinal(getBytes(raw));
            return Base64.getEncoder().encodeToString(encryptedVal);
        }
        catch (Throwable t)
        {
            throw new RuntimeException(t);
        }
    }

    public String decodeAndDecrypt(String encrypted) throws Exception
    {
        byte[] decodedValue = Base64.getDecoder().decode(encrypted);
        Cipher c = getCipher(2,SALT);
        byte[] decValue = c.doFinal(decodedValue);
        return new String(decValue);
    }

    private byte[] getBytes(String str) throws UnsupportedEncodingException
    {
        return str.getBytes("UTF-8");
    }

    private Cipher getCipher(int mode,String salt)throws Exception
    {

        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(mode, generateKey(salt), new IvParameterSpec(getIV()));
        return c;
    }

    private byte[] getIV() {
        try {
            return getBytes(IV);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private Key generateKey(String salt) throws Exception
    {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

        byte[] saltb = getBytes(salt);

        KeySpec spec = new PBEKeySpec(PASSWORD, saltb, 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    public String encryptUrl(String value){
        String initVector = "uPfVxw5nykjNf9hF";
        String key = "uPfVxw5nykjNf9hF";

        IvParameterSpec iv = null;
        try {
            iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

//            String encodedData = new String(Base64.getEncoder().encode(value.getBytes()));
            byte[] encrypted = cipher.doFinal(value.getBytes());
            String encodedData = new String(Base64.getEncoder().encode(encrypted));
            return encodedData;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String decryptUrl(String value){
        String initVector = "uPfVxw5nykjNf9hF";
        String key = "uPfVxw5nykjNf9hF";

        IvParameterSpec iv = null;
        try {
            iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] decrypted = cipher.doFinal(value.getBytes());
            byte[] decodedValue = Base64.getDecoder().decode(decrypted);
            return new String(decodedValue);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

}
