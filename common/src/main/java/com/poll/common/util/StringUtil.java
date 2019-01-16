package com.poll.common.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;

import com.poll.common.Constants;
import org.apache.commons.codec.binary.Hex;

import static org.springframework.util.StringUtils.isEmpty;


/**
 * 字符串操作类
 */
public class StringUtil {

	public static final char[] hexCharArr = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
	
	public static final String[] starArr = {"*"};
	
	/**
	 * 将传入对象转化为字符串，并去除两端空格，若传入对象为空，则返回空字符串
	 * @param obj
	 * @return
	 */
	public static String trimStr(Object obj) {
		if(obj == null) {
			return Constants.STR_BLANK;
		}
		return obj.toString().trim();
	}
	
	/**
     * 将写成unicode的文本转化为可阅读的正常文本
     * 
     * 例如文件文件中存储的字符串为：\\u6d4b\\u8bd5
     * 			转化后的文本为：测试
     * 
     * @param originStr
     * @return
     */
    public static String cvtUnicode2Normal(String originStr) {
    	
    	if (originStr == null) {
			return null;
		}
    	
    	Matcher matcher = RegularUtil.unicodePattern.matcher(originStr);
    	
    	StringBuffer sb = new StringBuffer();
    	
    	while (matcher.find()) {
			
    		String group = matcher.group(1);
    		
    		try {
    			char ch = (char)Integer.parseInt(group, 16);
    			
    			matcher.appendReplacement(sb, String.valueOf(ch));
    			
			} catch (Exception e) {
			}
		}
    	matcher.appendTail(sb);
    	
    	return sb.toString();
    } 
    
    /**
	 * 替换utf8Mb4编码字符为utf8，一般用于过滤富文本编辑器中的无法被解析的特殊字符
	 * 
	 * utf8为三字节编码 utf8Mb4为四字节编码
	 * 
	 * @param originStr
	 * @return
	 */
	static public String filterOffUtf8Mb4(String originStr) {  
	    
		byte[] bytes = null;
		
	    try {
			bytes = originStr.getBytes(Constants.CHARSET_UTF8);
			
			ByteBuffer buffer = ByteBuffer.allocate(bytes.length);  
			int i = 0;  
			while (i < bytes.length) {  
				short b = bytes[i];  
				if (b > 0) {  
					buffer.put(bytes[i++]);  
					continue;  
				}  
				b += 256;  
				if ((b ^ 0xC0) >> 4 == 0) {  
					buffer.put(bytes, i, 2);  
					i += 2;  
				}  
				else if ((b ^ 0xE0) >> 4 == 0) {  
					buffer.put(bytes, i, 3);  
					i += 3;  
				}  
				else if ((b ^ 0xF0) >> 4 == 0) {  
					i += 4;  
				}  
			}  
			buffer.flip();
			
			byte[] dst = new byte[buffer.limit()];
			buffer.get(dst);
			
			return new String(dst, Constants.CHARSET_UTF8);
			
		} catch (UnsupportedEncodingException e) {
		}  
	    
	    //出现异常将返回原文
	    return originStr;
	}
	 /**
     * 将出现mb4编码的字符，使用replaceStr代替, 并由posStrMap返回替换位置
     * 
     * 
     * @param originStr
     * @param posStrMap
     * @return
     */
    public static String filterOffUtf8Mb4ReturnPos(String originStr, Map<Integer, String> posStrMap, String replaceStr) {  
    	
    	byte[] bytes = null;
    	
    	if (replaceStr == null) {
			replaceStr = "*";
		}
    	
    	try {
    		bytes = originStr.getBytes(Constants.CHARSET_UTF8);
    		
    		ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
    		StringBuilder sb = new StringBuilder();
    		int i = 0;  
    		while (i < bytes.length) {  
    			short b = bytes[i];  
    			if (b > 0) {  
    				buffer.put(bytes[i++]);  
    				continue;  
    			}  
    			b += 256;  
    			if ((b ^ 0xC0) >> 4 == 0) {  
    				buffer.put(bytes, i, 2);  
    				i += 2;  
    			}  
    			else if ((b ^ 0xE0) >> 4 == 0) {  
    				buffer.put(bytes, i, 3);  
    				i += 3;  
    			}  
    			else if ((b ^ 0xF0) >> 4 == 0) {  
    				buffer.flip();
    				byte[] btArr = new byte[buffer.limit()];
    				buffer.get(btArr);
    				
    				sb.append(new String(btArr, Constants.CHARSET_UTF8)).append(replaceStr);
    				if (posStrMap != null) {
						posStrMap.put(sb.length() - 1, new String(Arrays.copyOfRange(bytes, i, i + 4), Constants.CHARSET_UTF8));
					}
    				i += 4;  
    				
    				buffer.clear();
    			}  
    		}  
    		buffer.flip();
			byte[] btArr = new byte[buffer.limit()];
			buffer.get(btArr);
    		return sb.append(new String(btArr, Constants.CHARSET_UTF8)).toString();
    		
    	} catch (UnsupportedEncodingException e) {
    	}  
    	
    	//出现异常将返回原文
    	return originStr;
    }
	
	/**
	 * 使用传入的字符库从指定的起始位置依次替换原始字符串的字符
	 * 
	 * 例如 传入字符串18030540444，起始位置为3，替换长度为4，字符库为["*"],			则替换结果为180****0444
	 * 										         字符库为["测"，"试"]		则替换结果为180测试测试0444
	 * 										         字符库为["测试"]			则替换结果为180测试测试测试测试0444
	 * 为空则默认替换为空字符串
	 * 
	 * @param originStr
	 * @param startIndex 			起始替换位置
	 * @param replaceLen			替换的长度
	 * @param replaceStrArr         替换字符库
	 * @return
	 */
	public static String replaceStringByStrArr(String originStr, int startIndex, int replaceLen, String[] replaceStrArr) {
		
		if(originStr == null || replaceLen <= 0 || startIndex >= originStr.length() || replaceStrArr == null || replaceStrArr.length < 1) {
			return originStr;
		}
		
		//替换起始位置小于0则设置为0
		if(startIndex < 0 ) {
			startIndex = 0;
		}

		//实际替换长度由替换起始位置与字符串长度确定
		int maxLen = originStr.length() - startIndex;
		if (replaceLen > maxLen) {
			replaceLen = maxLen;
		}
		
		//组织替换字符串
		StringBuilder sb = new StringBuilder();
		for (int i = 0, index = i; i < replaceLen; i++, index ++) {
			
			//控制序号循环取字符库的字符
			if (index >= replaceStrArr.length) {
				index -= replaceStrArr.length;
			}
			
			String replaceStr = replaceStrArr[index];
			if (replaceStr == null) {
				replaceStr = Constants.STR_BLANK;
			}
			
			sb.append(replaceStr);
		}
		
		//返回   起始位置前字符串 + 替换字符串 + 后半部分字符串  
		return originStr.substring(0, startIndex) + sb.toString() + originStr.substring(startIndex + replaceLen);
	}
	
	/**
	 * 从指定的序号开始，使用新字符串替换原始字符串
	 * 
	 * 例如 传入字符串18030540444，起始位置为3， 替换字符串为vvvv,			则替换结果为180vvvv0444
	 * 								       替换字符串为vvvvvvvvvv	则替换结果为180vvvvvvvvvv
	 * @param originStr
	 * @param startIndex
	 * @param replaceStr
	 * @return
	 */
	public static String replaceStringByStr(String originStr, int startIndex, String replaceStr) {
		
		//此处允许替换为空字符串
		if(originStr == null || startIndex >= originStr.length()) {
			return originStr;
		}
		
		//替换起始位置小于0则设置为0
		if(startIndex < 0) {
			startIndex = 0;
		}
		
		//为空则默认替换掉起始位置的字符
		if (replaceStr == null) {
			replaceStr = Constants.STR_BLANK;
		}

		int replaceLen = replaceStr.length();
		if (replaceLen < 1) {
			replaceLen = 1;
		}
		
		//尾部字符串的起始截取位置
		int tailIndex = startIndex + replaceLen;
		if (tailIndex > originStr.length()) {
			tailIndex = originStr.length();
		}
		
		//返回   起始位置前字符串 + 替换字符串 + 后半部分字符串  
		return originStr.substring(0, startIndex) + replaceStr + originStr.substring(tailIndex);
	}
	
	/**
	 * 替换指定序号位置的字符为新的字符串
	 * 
	 * 例如 传入字符串18030540444，指定序号为3， 替换字符串为v,			则替换结果为180v0540444
	 * 								       替换字符串为vvvvvvvvvv	则替换结果为180vvvvvvvvvv0540444
	 * @param originStr
	 * @param replaceIndex
	 * @param replaceStr
	 * @return
	 */
	public static String replaceCharAtIndexByStr(String originStr, int replaceIndex, String replaceStr) {
		
		//此处允许替换为空字符串
		if(originStr == null || replaceIndex < 0 || replaceIndex >= originStr.length()) {
			return originStr;
		}
		
		//为空则默认替换掉起始位置的字符
		if (replaceStr == null) {
			replaceStr = Constants.STR_BLANK;
		}

		//返回   起始位置前字符串 + 替换字符串 + 后半部分字符串  
		return originStr.substring(0, replaceIndex) + replaceStr + originStr.substring(replaceIndex + 1);
	}
	
	/**
	 * 根据字符长度自动打星号替换
	 * @param originStr
	 * @param replaceStrArr
	 * @return
	 */
	public static String replaceCharAuto(String originStr, String[] replaceStrArr) {
		
		int length = originStr.length();
		
		int replaceLen = length / 3 + 1; 
		
		int startIndex = (length - replaceLen) / 2;
		
		if (startIndex <= 0) {
			startIndex = 1;
		}
		
		return StringUtil.replaceStringByStrArr(originStr, startIndex, replaceLen, replaceStrArr);
	}
	public static String replaceCharAuto(String originStr) {
		return replaceCharAuto(originStr, starArr);
	}
	
	/**
	 * 替换之前，先将mb4格式的字符串替换
	 * @param originStr
	 * @param replaceStrArr
	 * @return
	 */
	public static String replaceCharAutoMb4(String originStr, String[] replaceStrArr) {
		
		//替换位置
		Map<Integer, String> posStrMap = new HashMap<Integer, String>();
		
		//过滤后字符串
		String filteredStr = filterOffUtf8Mb4ReturnPos(originStr, posStrMap, null);
		
		//替换后字符串
		StringBuilder sb = new StringBuilder(replaceCharAuto(filteredStr, replaceStrArr));
		
		if (!posStrMap.isEmpty()) {
			int deviation = 0;
			Iterator<Entry<Integer, String>> iterator = posStrMap.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<Integer, String> next = iterator.next();
				int from = next.getKey() + deviation;
				sb.replace(from, from + 1, next.getValue());
				deviation += next.getValue().length() - 1;
			}
		}
		
		return sb.toString();
	}
	public static String replaceCharAutoMb4(String originStr) {
		
		return replaceCharAutoMb4(originStr, starArr);
	}
	
	/**
	 * 将字符串首字母替换为小写
	 * @param originStr
	 * @return
	 */
    public static String replaceFirstChar2Lower(String originStr) {

    	if (originStr != null && originStr.length() > 0) {
			originStr = originStr.substring(0, 1).toLowerCase() + originStr.substring(1);
		}
    	return originStr;
    }
    /**
	 * 将字符串首字母替换为大写
	 * @param originStr
	 * @return
	 */
    public static String replaceFirstChar2Upper(String originStr) {

    	if (originStr != null && originStr.length() > 0) {
			originStr = originStr.substring(0, 1).toUpperCase() + originStr.substring(1);
		}
    	return originStr;
    }
    
    /**
	 * 将字符串转换为ASCII码 16进制
	 * 
	 * @param originStr
	 * @return
	 */
	public static String cvtStr2ASCII_Hex(String originStr) {
		
		StringBuilder sb = new StringBuilder();
		
		byte[] bGBK = originStr.getBytes();
		for (int i = 0; i < bGBK.length; i++) {
			sb.append(Integer.toHexString(bGBK[i] & 0xff));
		}
		
		return sb.toString();
	}
	/**
	 * 将字符串转换为ASCII码 10进制
	 * 
	 * @param originStr
	 * @return
	 */
	public static String cvtStr2ASCII_Dec(String originStr) {
		
		StringBuilder sb = new StringBuilder();
		
		byte[] bGBK = originStr.getBytes();
		for (int i = 0; i < bGBK.length; i++) {
			sb.append(Integer.toString(bGBK[i] & 0xff));
		}
		
		return sb.toString();
	}
	/**
	 * 将字符串转换为ASCII码 8进制
	 * 
	 * @param originStr
	 * @return
	 */
	public static String cvtStr2ASCII_Oct(String originStr) {
		
		StringBuilder sb = new StringBuilder();
		
		byte[] bGBK = originStr.getBytes();
		for (int i = 0; i < bGBK.length; i++) {
			sb.append(Integer.toOctalString(bGBK[i] & 0xff));
		}
		
		return sb.toString();
	}
	/**
	 * 将字符串转换为ASCII码 2进制
	 * 
	 * @param originStr
	 * @return
	 */
	public static String cvtStr2ASCII_Bin(String originStr) {
		
		StringBuilder sb = new StringBuilder();
		
		byte[] bGBK = originStr.getBytes();
		for (int i = 0; i < bGBK.length; i++) {
			sb.append(Integer.toBinaryString(bGBK[i] & 0xff));
		}
		
		return sb.toString();
	}
	
	/**
	 * 将字符串转换为unicode编码 16
	 * 
	 * @param originStr
	 * @return
	 */
	public static String cvtStr2Unicode(String originStr) {
		
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < originStr.length(); i++) {
	        char c = originStr.charAt(i);
	        sb.append("\\u" + appendHead2Len(Integer.toHexString(c), '0', 4));
	    }
		
		return sb.toString();
	}
    
	/**
	 * 为指定字符串在头部追加指定字符至目标长度
	 * @param originStr				null将视为空字符串
	 * @param appendChar			追加的指定字符
	 * @param targetLen				将字符串追加至指定长度
	 * @return
	 */
	public static String appendHead2Len(String originStr, char appendChar, int targetLen) {
		
		if (originStr == null) {
			originStr = Constants.STR_BLANK;
		}
		
		StringBuilder sb = new StringBuilder(originStr);
		
		while (sb.length() < targetLen) {
			sb.insert(0, appendChar);
		}
		
		return sb.toString();
	}
	/**
	 * 为指定字符串在尾部追加指定字符至目标长度
	 * @param originStr				null将视为空字符串
	 * @param appendChar			追加的指定字符
	 * @param targetLen				将字符串追加至指定长度
	 * @return
	 */
	public static String appendTail2Len(String originStr, char appendChar, int targetLen) {
		
		if (originStr == null) {
			originStr = Constants.STR_BLANK;
		}
		
		StringBuilder sb = new StringBuilder(originStr);
		
		while (sb.toString().length() < targetLen) {
			sb.append(appendChar);
		}
		
		return sb.toString();
	}
	
	/**
     * 判断字符是否是中文
     *
     * @param c 字符
     * @return 是否是中文
     */
    public static boolean isChinese(char c) {
    	return String.valueOf(c).matches(RegularUtil.chineseReg);
    }
	
    
    /**
     * 根据指定的char生成指定长度的字符串
     * 
     * 输入   'v' 5
     * 返回   vvvvv
     * 
     * @param c
     * @param length
     * @return
     */
    public static String genFixLenStrByChar(char c, int length) {
    	
    	
    	if (length < 1) {
			return Constants.STR_BLANK;
		}
    	
    	StringBuilder sb = new StringBuilder();
    	while (sb.length() < length) {
			sb.append(c);
		}
    	
    	return sb.toString();
    }
	
    /**
     * 将指定的字符串扩展times倍
     * 
     * 输入 "test" 3
     * 返回 "testtesttest"
     * 
     * @param str
     * @param times
     * @return
     */
    public static String genTimesStrByStr(String str, int times) {
    	
    	if (str == null) {
			return null;
		}
    	
    	StringBuilder sb = new StringBuilder();
    	for (int i = 0; i < times; i++) {
    		sb.append(str);
		}
    	
    	return sb.toString();
    }
    
    /**
     * 将驼峰命名转化为下划线命名
     * 
     * 例：userLogin -> user_login 
     * 
     * @param str
     * @return
     */
    public static String cvtHump2Underline(String str) {
    	
    	if (str == null) {
    		return null;
		}
    	
    	Matcher matcher = RegularUtil.upperLetterPattern.matcher(str);
		
		StringBuffer sb = new StringBuffer();
		
		while (matcher.find()) {
			String group = matcher.group(1);
			
			matcher.appendReplacement(sb, "_" + group.toLowerCase());
		}
		
		matcher.appendTail(sb);
    	
		if ("_".equals(sb.substring(0, 1))) {
			return sb.substring(1);
		}
		
    	return sb.toString();
    }
    
    /**
     * 将下划线命名转化为驼峰命名
     * @param str
     * @return
     */
    public static String cvtUnderline2Hump(String str) {
    	
    	if (str == null) {
    		return null;
		}
    	
    	Matcher matcher = RegularUtil.underlineLetterPattern.matcher(str);
		
		StringBuffer sb = new StringBuffer();
		
		while (matcher.find()) {
			String group = matcher.group(1);
			matcher.appendReplacement(sb, group.toUpperCase());
		}
		
		matcher.appendTail(sb);
    	
		sb.replace(0, 1, sb.substring(0, 1).toLowerCase());
		
    	return sb.toString(); 
    }
    
    /**
     * 普通字符串转16进制字符串
     * @param byteArr
     * @return
     * @throws UnsupportedEncodingException 
     */
    public static String cvtByteArr2HexStr(byte[] byteArr) {
    	
    	if (byteArr == null) {
			return null;
		}
    	return Hex.encodeHexString(byteArr);
    }
    
    public static String cvtByteArr2HexStr(byte[] byteArr, boolean toLowerCase) {
    	
    	if (byteArr == null) {
			return null;
		}
    	
    	String encodeHexString = Hex.encodeHexString(byteArr);
    	
    	return encodeHexString.toLowerCase();
    }
    
    /**
     * 16进制字符串转普通字符串
     * @param hexStr
     * @param charsetName
     * @return
     * @throws Exception 
     */
    public static String cvtHexStr2NormalStr(String hexStr, String charsetName) throws Exception {
    	
    	if (hexStr == null) {
			return null;
		}
    	
    	byte[] decodeHex = Hex.decodeHex(hexStr.toCharArray());
    	
    	return new String(decodeHex, charsetName);
    }
    public static String cvtHexStr2NormalStr(String hexStr) throws Exception {
    	
    	return cvtHexStr2NormalStr(hexStr, Constants.CHARSET_UTF8);
    }
    
    public static int[] cvtStringList2Int(List<String> strList) {
		
		if (strList == null) {
			return null;
		}
		
		int[] cvtArr = new int[strList.size()];
		
		for (int i = 0; i < strList.size(); i++) {
			cvtArr[i] = Integer.valueOf(strList.get(i));
		}
		
		return cvtArr;
	}
	public static long[] cvtStringList2Long(List<String> strList) {
		
		if (strList == null) {
			return null;
		}
		
		long[] cvtArr = new long[strList.size()];
		
		for (int i = 0; i < strList.size(); i++) {
			cvtArr[i] = Long.valueOf(strList.get(i));
		}
		
		return cvtArr;
	}
	public static byte[] cvtStringList2Byte(List<String> strList) {
		
		if (strList == null) {
			return null;
		}
		
		byte[] cvtArr = new byte[strList.size()];
		
		for (int i = 0; i < strList.size(); i++) {
			cvtArr[i] = Byte.valueOf(strList.get(i));
		}
		
		return cvtArr;
	}
	public static boolean[] cvtStringList2Boolean(List<String> strList) {
		
		if (strList == null) {
			return null;
		}
		
		boolean[] cvtArr = new boolean[strList.size()];
		
		for (int i = 0; i < strList.size(); i++) {
			cvtArr[i] = Boolean.parseBoolean(strList.get(i));
		}
		
		return cvtArr;
	}



	public  static boolean equalsIgnoreCase(String firstStr,String secondStr){
		boolean isEquals ;
		if (firstStr.equalsIgnoreCase(secondStr)){
			isEquals= true;
		}else {
			isEquals= false;
		}
		return isEquals;
	}


	public static String substringAfter(String str, String separator) {
		if (isEmpty(str)) {
			return str;
		} else if (separator == null) {
			return "";
		} else {
			int pos = str.indexOf(separator);
			return pos == -1 ? "" : str.substring(pos + separator.length());
		}
	}
}
