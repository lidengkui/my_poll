package com.poll.common.util;

import com.poll.common.Constants;
import org.apache.commons.codec.binary.Base64;


public class Base64Util {
	
	/**
	 * 加密
	 * @param bytesArr
	 * @return
	 */
	public static byte[] encode(byte[] bytesArr) {
		try {
			return Base64.encodeBase64URLSafe(bytesArr);
		} catch (Exception e) {
		}
		return null;
	}
	public static String encode(String str, String charsetName) {
		try {
			byte[] bytesArr = str.getBytes(charsetName);
			return Base64.encodeBase64URLSafeString(bytesArr);
		} catch (Exception e) {
		}
		return null;
	}
	public static String encode(String str) {
		return encode(str, Constants.CHARSET_UTF8);
	}

	
	/**
	 * 解密 
	 * @param byteArr
	 * @return
	 */
	public static byte[] decode(byte[] byteArr) {
		try {
			return Base64.decodeBase64(byteArr);
		} catch (Exception e) {
		}
		return null;
	}
	public static String decode(String str, String charsetName) {
		
		try {
			byte[] decodeBuffer = Base64.decodeBase64(str.getBytes(charsetName));
			return new String(decodeBuffer, charsetName);
		} catch (Exception e) {
		}
		return null;
	}
	public static String decode(String str) {
		return decode(str, Constants.CHARSET_UTF8);
	}
}
