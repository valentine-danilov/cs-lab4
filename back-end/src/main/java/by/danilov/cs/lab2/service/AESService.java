package by.danilov.cs.lab2.service;

import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static by.danilov.cs.lab2.util.EncodingUtil.convertToBytes;
import static by.danilov.cs.lab2.util.EncodingUtil.convertToString;

@Service
public class AESService {

    public String encryptAES_CBC(String source, String key, String iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        byte[] keyBytes = convertToBytes(key);
        byte[] ivBytes = convertToBytes(iv);

        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

        byte[] decryptedSourceBytes = cipher.doFinal(source.getBytes());

        String decryptedSource = convertToString(decryptedSourceBytes);

        System.out.println(decryptedSource);

        return decryptedSource;

    }

    public String decryptAES_CBC(String encrypted, String key, String iv) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {

        byte[] keyBytes = convertToBytes(key);
        byte[] ivBytes = convertToBytes(iv);

        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

        byte[] decryptedSourceBytes = cipher.doFinal(convertToBytes(encrypted));

        String decryptedSource = new String(decryptedSourceBytes);

        System.out.println(decryptedSource);

        return decryptedSource;
    }
}
