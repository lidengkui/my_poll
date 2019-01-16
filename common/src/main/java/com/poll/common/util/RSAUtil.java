package com.poll.common.util;

import com.poll.common.Constants;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSAUtil {

	//"算法/模式/补码方式"
	private String transformat = Constants.ENC_NAME_RSA + "/" + Constants.ENC_MODEL_ECB + "/" + Constants.ENC_FILL_PKCS1PADDING;

	//私钥字符串
	private String publicKeyStr = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCCWAPsTr6LoN3gq3nhX1J9NP8TAeAg/4vVo2UXA/t3WkF3xUYiF/lvdW2f6Fpx7VQY38v+l4oFAtSua0Z4cMjdpy0rsCyM42z8QSirgYaHBh5USxlffV2Ot2aDV0wCbZH11itHN7z9VaaZbjBmQUAqxAeX282ZgwM0i1rn+nHYzwIDAQAB";
	//私钥字符串
	private String privateKeyStr = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIJYA+xOvoug3eCreeFfUn00/xMB4CD/i9WjZRcD+3daQXfFRiIX+W91bZ/oWnHtVBjfy/6XigUC1K5rRnhwyN2nLSuwLIzjbPxBKKuBhocGHlRLGV99XY63ZoNXTAJtkfXWK0c3vP1VppluMGZBQCrEB5fbzZmDAzSLWuf6cdjPAgMBAAECgYApgu586/czcOA5FWOTq1ASIDa8TvSajchzGb6wPcxwjoYbJvDXGtuN69AmBVIIxpTp9xx40LpcLjKd+fxPj1vpA1fCGr8m9/0rjHFX/TFqGHoA+N3jmwng1/aWjmRy0i318jnB3U4BqBlIjNsKBJ0fM3lKzT9+D8z8zlzQgWZegQJBAO2VTvzpZP+lX+5TIAIB54vpaXDve7MXh5envzhurU/gRPz3J/0WZ6ZN4qsg+yl/ixF7iKaUWLj8S3K/B/eUaXkCQQCMcptrIJiLUjamRqAGBqSnljyNcj+4DcGhkUF1kUXshoQBJJGNd5jZHENLmhQQHAbQ9XalcmrkYmbY6GyLK4qHAkEA7GEyjpXUpDow4RkZboXgj6by/qd3Zq+Re8UtjwMnMqLLYPizc663/5HcZTFSU26Puhwz0LEmOR7kHk2Mqrqh2QJAXTPVBbN857/oOlHV8gnIjNo7VtaBiH/AhpqhaQa982eVBDkjSlEaosGJuwsF02b18wdh9AtI+kR+4eTj2ztK0wJAUv/HaHbu69vtv9i4dljgaNENoAgHp7XInp1Gz8GPwEgVgoUPqM/nI7/Pj4F52TyfASv5kseIcvU0vBWxf+Svlw==";
	
	//公钥
	private RSAPublicKey publicKey;
	//私钥
	private RSAPrivateKey privateKey;

	
	/**
	 * @param transformat
	 * @param privateKeyStr
	 * @param publicKeyStr
	 */
	public RSAUtil(String transformat, String privateKeyStr, String publicKeyStr) {
		super();
		this.transformat = transformat;
		setPublicKeyStr(publicKeyStr);
		setPrivateKeyStr(privateKeyStr);
	}

	public RSAUtil() {
		setPublicKey();
		setPrivateKey();
	}

	public String getTransformat() {
		return transformat;
	}

	public void setTransformat(String transformat) {
		this.transformat = transformat;
	}

	public String getPublicKeyStr() {
		return publicKeyStr;
	}
	
	public void setPublicKeyStr(String publicKeyStr) {
		
		this.publicKeyStr = publicKeyStr;
		
		setPublicKey();
	}
	
	public String getPrivateKeyStr() {
		return privateKeyStr;
	}

	public void setPrivateKeyStr(String privateKeyStr) {
		
		this.privateKeyStr = privateKeyStr;
		
		setPrivateKey();
	}


	public RSAPrivateKey getPrivateKey() {
		return privateKey;
	}

	private void setPrivateKey() {
		try {
			byte[] buffer = Base64Util.decode(this.privateKeyStr.getBytes(Constants.CHARSET_UTF8));
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
			KeyFactory keyFactory = KeyFactory.getInstance(Constants.ENC_NAME_RSA);
			this.privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public RSAPublicKey getPublicKey() {
		return publicKey;
	}

	private void setPublicKey() {
		try {
			byte[] buffer = Base64Util.decode(this.publicKeyStr.getBytes(Constants.CHARSET_UTF8));
			KeyFactory keyFactory = KeyFactory.getInstance(Constants.ENC_NAME_RSA);
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
			this.publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 随机生成密钥对 0号位公钥 1号位私钥
	 * 
	 * 生成公私钥字符串需要将生成的byteArr用base64编码
	 * 
	 * @throws Exception 
	 */
	public static String[] genKeyPair() throws Exception {
		
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(Constants.ENC_NAME_RSA);
		
		keyPairGen.initialize(1024, new SecureRandom());
		KeyPair keyPair = keyPairGen.generateKeyPair();
		
		RSAPrivateKey privateKey = (RSAPrivateKey)keyPair.getPrivate();
		RSAPublicKey publicKey = (RSAPublicKey)keyPair.getPublic();
		
		String privateKeyStr = new String(Base64Util.encode(privateKey.getEncoded()), Constants.CHARSET_UTF8);
		String publicKeyStr = new String(Base64Util.encode(publicKey.getEncoded()), Constants.CHARSET_UTF8);
		
		String[] pairArr = {publicKeyStr, privateKeyStr};
		
		return pairArr;
	}
	
	/**
	 * 签名(私钥加密)
	 * 
	 * @param plainStr 明文数据
	 * @return
	 * @throws Exception 加密过程中的异常信息
	 */
	public String sign(String plainStr) throws Exception {
		
		Cipher cipher = Cipher.getInstance(this.transformat);
		
		cipher.init(Cipher.ENCRYPT_MODE, privateKey);
		
		byte[] output = cipher.doFinal(plainStr.getBytes(Constants.CHARSET_UTF8));
		
		return new String(Base64Util.encode(output), Constants.CHARSET_UTF8);
	}
	
	/**
	 * 验证签名(公钥解密)
	 * 
	 * @param encStr 密文数据
	 * @return
	 * @throws Exception 加密过程中的异常信息
	 */
	public String verifySign(String encStr) throws Exception {
		
		Cipher cipher = Cipher.getInstance(this.transformat);
		
		cipher.init(Cipher.DECRYPT_MODE, publicKey);
		
		byte[] output = cipher.doFinal(Base64Util.decode(encStr.getBytes(Constants.CHARSET_UTF8)));
		
		return new String(output, Constants.CHARSET_UTF8);
	}
	
	
	/**
	 * 公钥加密
	 * 
	 * @param plainStr 明文数据
	 * @return
	 * @throws Exception 加密过程中的异常信息
	 */
	public String encrypt(String plainStr) throws Exception {
		
		Cipher cipher = Cipher.getInstance(this.transformat);
		
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		
		byte[] output = cipher.doFinal(plainStr.getBytes(Constants.CHARSET_UTF8));
		
		return new String(Base64Util.encode(output), Constants.CHARSET_UTF8);
	}

	/**
	 * 私钥解密
	 * 
	 * @param encStr 密文数据
	 * @return 
	 * @throws Exception 解密过程中的异常信息
	 */
	public String decrypt(String encStr) throws Exception {
		
		Cipher cipher = Cipher.getInstance(this.transformat);
		
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		
		byte[] output = cipher.doFinal(Base64Util.decode(encStr.getBytes(Constants.CHARSET_UTF8)));
		
		return new String(output, Constants.CHARSET_UTF8);
	}
}
