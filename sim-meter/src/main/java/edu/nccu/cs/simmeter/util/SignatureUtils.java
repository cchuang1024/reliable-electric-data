package edu.nccu.cs.simmeter.util;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Signature;
import java.security.SignatureException;

public class SignatureUtils {
    public static byte[] sign(KeyPair keyPair, byte[] bytes)
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
        Signature sign = Signature.getInstance("SHA1withDSA", "BC");
        sign.initSign(keyPair.getPrivate());
        sign.update(bytes);

        return sign.sign();
    }

    public static boolean verify(KeyPair keyPair, byte[] bytes, byte[] signature)
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
        Signature sign = Signature.getInstance("SHA1withDSA", "BC");
        sign.initVerify(keyPair.getPublic());
        sign.update(bytes);

        return sign.verify(signature);
    }
}
