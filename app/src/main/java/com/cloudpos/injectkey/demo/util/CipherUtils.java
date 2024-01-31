package com.cloudpos.injectkey.demo.util;


import com.cloudpos.injectkey.demo.key.PINPadException;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.X509Certificate;

import javax.crypto.Cipher;

/**
 * Created by lizhou on 18-3-5.
 */

public class CipherUtils {

    public static byte[] encrypt(X509Certificate cert, byte[] data) throws PINPadException {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, cert.getPublicKey());
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new PINPadException("Encrypt data error", e);
        }
    }

    public static byte[] sig(PrivateKey priKey, byte[] data) throws PINPadException {
        try {
            Signature signature = Signature.getInstance("SHA256WithRSA");
            signature.initSign(priKey);
            signature.update(data);
            return signature.sign();
        } catch (Exception e) {
            throw new PINPadException("Signature data error", e);
        }
    }

    public static boolean verifySig(PublicKey pubKey, byte[] data, byte[] sig) throws PINPadException {
        try {
            Signature signature = Signature.getInstance("SHA256WithRSA");
            signature.initVerify(pubKey);
            signature.update(data);
            return signature.verify(sig);
        } catch (Exception e) {
            throw new PINPadException("Verify signature data error", e);
        }
    }
}
