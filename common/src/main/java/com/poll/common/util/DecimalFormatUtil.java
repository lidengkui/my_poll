package com.poll.common.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class DecimalFormatUtil {

	public static final DecimalFormat DF1 = new DecimalFormat("0.0"); 
	public static final DecimalFormat DF2 = new DecimalFormat("0.00");
	public static final DecimalFormat DF4 = new DecimalFormat("0.0000");
	
	
	/**
	 * 将输入数字格式化为目标格式
	 * @param inNum
	 * @param df
	 * @return
	 */
	public static BigDecimal format2BigDecimal(Object inNum, DecimalFormat df) {
		try {
			return new BigDecimal(df.format(inNum));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static BigDecimal format2BigDecimal2(Object inNum) {
		return format2BigDecimal(inNum, DF2);
	}
	
	/**
	 * 将输入数字格式化为目标格式
	 * @param inNum
	 * @return
	 */
	public static String format2String(Object inNum, DecimalFormat df) {
		try {
			return df.format(inNum);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String format2String2(Object inNum) {
		return format2String(inNum, DF2);
	}
}
