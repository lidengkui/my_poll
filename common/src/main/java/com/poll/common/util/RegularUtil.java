package com.poll.common.util;

import java.util.regex.Pattern;

/**
 * @description: 正则表达式工具
 */
public class RegularUtil {

		//手机号码正则
		public static String phoneNoReg = "^1\\d{10}$";

		//用户名正则
		public static String userNameReg = "^[a-zA-Z]\\w{4,19}$";

		//纯数字正则
		public static String pureNumReg = "^\\d+$";

		//数字正则
		public static String numReg = "^-?\\d+(\\.\\d+)?$";

		//http正则
		public static String httpReg = "^(http|https)://\\S+$";

		//http request contentType正则
		public static String contentTypeReg = "^(\\S*/\\S+);?";

		//content type 中 filename正则
		public static String filenameReg = "filename=\\S*?(\\w+(\\.\\w+))\\S*?;?";

		//content type 中 filename中的类型正则 xxx.jpg,取.jpg,防止类型取错
		public static String fileTypeReg = "^(.\\w+).*$";

		//抓取html的<img标签
		public static String imgReg = "<img[\\s]+[^>]*?/?>";

		//抓取html的 src 属性                前可有空格1个或多个  src  后可有空格任意个
		public static String srcReg = "\\s+src\\s*=\\s*[\"\']([^>\"\']*?)[\"\']";

		//抓取html的 title 属性
		public static String titleReg = "\\s+title\\s*=\\s*[\"\'][^>\"\']*?[\"\']";

		//抓取微信图片地址中的tp=webp
		public static String tpReg = "=webp";
		
		public static final String paramValueReg = "={1}(\\w+=*)&?";
		
		//unicode正则,从字符串中抓取\u0000的unicode编码
		public static String unicodeReg = "\\\\u([a-fA-F0-9]{4})";
        public static Pattern unicodePattern = Pattern.compile(unicodeReg);

		//中文正则
		public static String chineseReg = "[\u4e00-\u9fa5]";
		//数字字母正则
		public  static String numRegAndChinese ="[A-Za-z0-9]+";

		//空白字符
		public static String whiteCharReg = "\\s";

		public static String nonWhiteCharReg = "\\S";

		//单词字符
		public static String wordReg = "\\w+";

		//非单词字符
		public static String nonWordReg = "\\W";

		//匹配大写字母
		public static String upperLetterReg = "([A-Z])";
        public static Pattern upperLetterPattern = Pattern.compile(upperLetterReg);

		//匹配_a
		public static String underlineLetterReg = "_+([a-zA-Z])";
        public static Pattern underlineLetterPattern = Pattern.compile(underlineLetterReg);

	
	
	
}
