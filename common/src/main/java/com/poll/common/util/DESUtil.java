package com.poll.common.util;

import com.poll.common.Constants;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class DESUtil {

	private static final String DEFAULT_KEY = "#Ed55n@?";
	private static final String DEFAULT_TRANSFORMAT = "DES/CBC/PKCS5Padding";

	// key需要为8位
	private String key;

	//"算法/模式/补码方式"
	private String transformat;

	private IvParameterSpec iv = null;

	private DESUtil(){
	    this(null);
	}

    private DESUtil(String key){
        this(key, null);
    }

	private DESUtil(String key, String transformat){

        if (key == null) {
            key = DEFAULT_KEY;
        }
        this.key = key;

        if (transformat == null) {
            transformat = DEFAULT_TRANSFORMAT;
        }
        this.transformat = transformat;
        if (transformat.contains("/CBC/")) {
            iv = new IvParameterSpec("01234567".getBytes());
        }
	}

	public static DESUtil getInstance(){
		return new DESUtil();
	}

	public static DESUtil getInstance(String key){
		return new DESUtil(key);
	}

	public static DESUtil getInstance(String key, String transformat){
		return new DESUtil(key, transformat);
	}

	// 加密
	public String encrypt(String sSrc) throws Exception {

		if(sSrc == null || sSrc.length() == 0){
			return Constants.STR_BLANK;
		}

		SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), Constants.ENC_NAME_DES);
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

		SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), Constants.ENC_NAME_DES);
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

		String enString = DESUtil.getInstance().encrypt("\ndfa\n中文");
		System.out.println("加密后的字串是：" + enString);

		String deString = DESUtil.getInstance().decrypt(enString);
		System.out.println("解密密后的字串是：" + deString);
	}
}
