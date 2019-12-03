package by.danilov.cs.lab2.service;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

class PKCS1PublicKeyReader {

    private static final int SEQUENCE_TAG = 0x30;
    private static final int BIT_STRING_TAG = 0x03;
    private static final byte[] NO_UNUSED_BITS = new byte[]{0x00};
    private static final byte[] RSA_ALGORITHM_IDENTIFIER_SEQUENCE =
            {(byte) 0x30, (byte) 0x0d,
                    (byte) 0x06, (byte) 0x09, (byte) 0x2a, (byte) 0x86, (byte) 0x48, (byte) 0x86, (byte) 0xf7, (byte) 0x0d, (byte) 0x01, (byte) 0x01, (byte) 0x01,
                    (byte) 0x05, (byte) 0x00};


    static RSAPublicKey decodePKCS1PublicKey(byte[] pkcs1PublicKeyEncoding)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] subjectPublicKeyInfo2 = createSubjectPublicKeyInfoEncoding(pkcs1PublicKeyEncoding);
        KeyFactory rsaKeyFactory = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) rsaKeyFactory.generatePublic(new X509EncodedKeySpec(subjectPublicKeyInfo2));
    }

    private static byte[] createSubjectPublicKeyInfoEncoding(byte[] pkcs1PublicKeyEncoding) {
        byte[] subjectPublicKeyBitString = createDEREncoding(BIT_STRING_TAG, concat(NO_UNUSED_BITS, pkcs1PublicKeyEncoding));
        byte[] subjectPublicKeyInfoValue = concat(RSA_ALGORITHM_IDENTIFIER_SEQUENCE, subjectPublicKeyBitString);

        return createDEREncoding(SEQUENCE_TAG, subjectPublicKeyInfoValue);
    }

    private static byte[] concat(byte[]... bas) {
        int len = 0;
        for (byte[] ba : bas) {
            len += ba.length;
        }

        byte[] buf = new byte[len];
        int off = 0;
        for (byte[] ba : bas) {
            System.arraycopy(ba, 0, buf, off, ba.length);
            off += ba.length;
        }

        return buf;
    }

    private static byte[] createDEREncoding(int tag, byte[] value) {
        if (tag < 0 || tag >= 0xFF) {
            throw new IllegalArgumentException("Currently only single byte tags supported");
        }

        byte[] lengthEncoding = createDERLengthEncoding(value.length);

        int size = 1 + lengthEncoding.length + value.length;
        byte[] derEncodingBuf = new byte[size];

        int off = 0;
        derEncodingBuf[off++] = (byte) tag;
        System.arraycopy(lengthEncoding, 0, derEncodingBuf, off, lengthEncoding.length);
        off += lengthEncoding.length;
        System.arraycopy(value, 0, derEncodingBuf, off, value.length);

        return derEncodingBuf;
    }

    private static byte[] createDERLengthEncoding(int size) {
        if (size <= 0x7F) {
            // single byte length encoding
            return new byte[]{(byte) size};
        } else if (size <= 0xFF) {
            // double byte length encoding
            return new byte[]{(byte) 0x81, (byte) size};
        } else if (size <= 0xFFFF) {
            // triple byte length encoding
            return new byte[]{(byte) 0x82, (byte) (size >> Byte.SIZE), (byte) size};
        }

        throw new IllegalArgumentException("size too large, only up to 64KiB length encoding supported: " + size);
    }
}