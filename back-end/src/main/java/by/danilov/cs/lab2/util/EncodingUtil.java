package by.danilov.cs.lab2.util;

import java.util.Base64;

public class EncodingUtil {

    public static byte[] convertToBytes(String str) {
        return Base64.getDecoder().decode(str);
    }

    public static String convertToString(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

}
