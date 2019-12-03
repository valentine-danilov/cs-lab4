package by.danilov.cs.lab2.service;

import by.danilov.cs.lab2.domain.Token;
import by.danilov.cs.lab2.security.store.KeyStore;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;

@Service
public class RSATokenService {

    private KeyStore keyStore;

    public RSATokenService(KeyStore keyStore) {
        this.keyStore = keyStore;
    }

    public Token generateSessionToken() throws NoSuchAlgorithmException {

        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        SecretKey secretKey = keyGen.generateKey();
        byte[] token = secretKey.getEncoded();

        int ivSize = 16;
        byte[] iv = new byte[ivSize];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        System.out.println(Arrays.toString(token));
        System.out.println(Arrays.toString(iv));

        Token generatedToken = new Token(Base64.getEncoder().encodeToString(token), Base64.getEncoder().encodeToString(iv));

        keyStore.setToken(generatedToken.getToken());
        keyStore.setIv(generatedToken.getIv());

        return generatedToken;
    }

    public Token getEncryptedToken(String publicKey, Token token) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        byte[] tokenBytes = token.getToken().getBytes(StandardCharsets.UTF_8);
        byte[] ivBytes = token.getIv().getBytes(StandardCharsets.UTF_8);

        Key key = getPublicKey(publicKey);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encryptedTokenBytes = cipher.doFinal(tokenBytes);
        byte[] encryptedIVBytes = cipher.doFinal(ivBytes);

        String encryptedToken = Base64.getEncoder().encodeToString(encryptedTokenBytes);
        String encryptedIV = Base64.getEncoder().encodeToString(encryptedIVBytes);

        return new Token(encryptedToken, encryptedIV);
    }

    private Key getPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        publicKey = publicKey
                .replaceAll("\\n", "")
                .replace("-----BEGIN RSA PUBLIC KEY-----", "")
                .replace("-----END RSA PUBLIC KEY-----", "");

        byte[] bytes = Base64.getDecoder().decode((publicKey.getBytes()));

        return PKCS1PublicKeyReader.decodePKCS1PublicKey(bytes);
    }

    /*public static void main(String[] args) throws NoSuchAlgorithmException {
        RSATokenService service = new RSATokenService();
        Token token = service.generateSessionToken();
        System.out.println(token.getToken());
        System.out.println(token.getIv());
    }*/
}
