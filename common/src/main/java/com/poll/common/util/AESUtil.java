package com.poll.common.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.poll.common.Constants;
import org.apache.commons.codec.binary.Base64;


public class AESUtil {

	private static final String DEFAULT_KEY = "#@d47n@?eHlAH@5A";
	private static final String DEFAULT_TRANSFORMAT = "AES/ECB/PKCS5Padding";

	// key需要为16位
	private String key;

	//"算法/模式/补码方式"
	private String transformat;

	private IvParameterSpec iv = null;

	private AESUtil(){
		this(null);
	}

	private AESUtil(String key){
		this(key, null);
	}

	private AESUtil(String key, String transformat){

		if (key == null) {
			key = DEFAULT_KEY;
		}
		this.key = key;

		if (transformat == null) {
			transformat = DEFAULT_TRANSFORMAT;
		}
		this.transformat = transformat;
		if (transformat.contains("/CBC/")) {
			iv = new IvParameterSpec("0123456789123456".getBytes());
		}
	}

	public static AESUtil getInstance(){
		return new AESUtil();
	}

	public static AESUtil getInstance(String key){
		return new AESUtil(key);
	}

	public static AESUtil getInstance(String key, String transformat){
		return new AESUtil(key, transformat);
	}

	// 加密
	public String encrypt(String sSrc) throws Exception {

		if(sSrc == null || sSrc.length() == 0){
			return Constants.STR_BLANK;
		}

		SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), Constants.ENC_NAME_AES);
		Cipher cipher = Cipher.getInstance(transformat);
		if (this.transformat != null && this.transformat.contains("/CBC/")) {
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
		} else {
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		}
		byte[] encrypted = cipher.doFinal(sSrc.getBytes(Constants.CHARSET_UTF8));

		return new String(Base64.encodeBase64URLSafe(encrypted), Constants.CHARSET_UTF8);
	}

	// 解密
	public String decrypt(String sSrc) throws Exception {

		if(sSrc == null || sSrc.length() == 0){
			return Constants.STR_BLANK;
		}

		SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), Constants.ENC_NAME_AES);
		Cipher cipher = Cipher.getInstance(transformat);
		if (this.transformat != null && this.transformat.contains("/CBC/")) {
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
		} else {
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		}
		byte[] encrypted = Base64.decodeBase64(sSrc);
		byte[] original = cipher.doFinal(encrypted);

		return new String(original, Constants.CHARSET_UTF8);
	}

	public static void main(String[] args) throws Exception {

		String enString = AESUtil.getInstance().encrypt("\ndfa\n中文");
		System.out.println("加密后的字串是：" + enString);

		String deString = AESUtil.getInstance().decrypt(enString);
		System.out.println("解密密后的字串是：" + deString);
	}
}
