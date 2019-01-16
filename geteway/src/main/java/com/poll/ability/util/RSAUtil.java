package com.poll.ability.util;

import com.poll.common.util.MD5Util;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSAUtil {

    private static String TRANSFORMAT = "RSA/ECB/PKCS1Padding";
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public RSAUtil() {
    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    public void loadPublicKeyStr(String publicKeyStr) throws Exception {
        try {
            byte[] buffer = Base64.decodeBase64(publicKeyStr);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            this.publicKey = (RSAPublicKey)keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException var5) {
            throw new Exception("无此算法", var5);
        } catch (InvalidKeySpecException var6) {
            throw new Exception("公钥非法", var6);
        } catch (NullPointerException var7) {
            throw new Exception("公钥数据为空", var7);
        }
    }

    public void loadPrivateKeyStr(String privateKeyStr) throws Exception {
        try {
            byte[] buffer = Base64.decodeBase64(privateKeyStr);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            this.privateKey = (RSAPrivateKey)keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException var5) {
            throw new Exception("无此算法", var5);
        } catch (InvalidKeySpecException var6) {
            var6.printStackTrace();
            throw new Exception("私钥非法", var6);
        } catch (NullPointerException var7) {
            throw new Exception("私钥数据为空", var7);
        }
    }

    public String encryptStr(String plainTextData) throws Exception {
        if (this.publicKey == null) {
            throw new Exception("加密公钥为空, 请设置");
        } else {
            Cipher cipher = null;

            try {
                cipher = Cipher.getInstance(TRANSFORMAT);
                cipher.init(1, this.publicKey);
                byte[] output = cipher.doFinal(plainTextData.getBytes("UTF-8"));
                return new String(Base64.encodeBase64(output), "UTF-8");
            } catch (NoSuchAlgorithmException var4) {
                throw new Exception("无此加密算法", var4);
            } catch (NoSuchPaddingException var5) {
                var5.printStackTrace();
                return null;
            } catch (InvalidKeyException var6) {
                throw new Exception("加密公钥非法,请检查", var6);
            } catch (IllegalBlockSizeException var7) {
                throw new Exception("明文长度非法", var7);
            } catch (BadPaddingException var8) {
                throw new Exception("明文数据已损坏", var8);
            }
        }
    }

    public String decryptStr(String cipherData) throws Exception {
        if (this.privateKey == null) {
            throw new Exception("解密私钥为空, 请设置");
        } else {
            Cipher cipher = null;

            try {
                cipher = Cipher.getInstance(TRANSFORMAT);
                cipher.init(2, this.privateKey);
                byte[] output = cipher.doFinal(Base64.decodeBase64(cipherData.getBytes("UTF-8")));
                return new String(output, "UTF-8");
            } catch (NoSuchAlgorithmException var4) {
                throw new Exception("无此解密算法", var4);
            } catch (NoSuchPaddingException var5) {
                var5.printStackTrace();
                return null;
            } catch (InvalidKeyException var6) {
                throw new Exception("解密私钥非法,请检查", var6);
            } catch (IllegalBlockSizeException var7) {
                throw new Exception("密文长度非法", var7);
            } catch (BadPaddingException var8) {
                throw new Exception("密文数据已损坏", var8);
            }
        }
    }

    public String makeSign(String rawText) throws Exception {
        if (this.privateKey == null) {
            throw new Exception("制作签名私钥加载失败");
        } else {
            String signHex = null;

            try {
                rawText = MD5Util.encode(rawText);
                byte[] signTarget = rawText.getBytes("UTF-8");
                Signature signature = Signature.getInstance("SHA256withRSA");
                signature.initSign(this.privateKey);
                signature.update(signTarget);
                byte[] signBytes = signature.sign();
                signHex = new String(Base64.encodeBase64(signBytes), "UTF-8");
                return signHex;
            } catch (InvalidKeyException var6) {
                throw new Exception("签名私钥非法,请检查", var6);
            }
        }
    }

    public boolean verifySign(String rawText, String signHex) throws Exception {
        if (this.publicKey == null) {
            throw new Exception("验证签名公钥加载失败");
        } else {
            boolean flg = false;

            try {
                rawText = MD5Util.encode(rawText);
                byte[] signTarget = rawText.getBytes("UTF-8");
                byte[] signBytes = Base64.decodeBase64(signHex.getBytes("UTF-8"));
                Signature signature = Signature.getInstance("SHA256withRSA");
                signature.initVerify(this.publicKey);
                signature.update(signTarget);
                flg = signature.verify(signBytes);
            } catch (Exception var7) {
                var7.printStackTrace();
            }

            return flg;
        }
    }
}
