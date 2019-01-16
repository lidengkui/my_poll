package com.poll.common.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	public static final String FORMATE_YYYYMMDDHHMMSSSSS = "yyyyMMddHHmmssSSS";
	public static final String FORMATE_YYYY_MM_DD_HH_MM_SS_MINUS = "yyyy-MM-dd HH:mm:ss";
	public static final String FORMATE_YYYY_MM_DD_HH_MM_SS_SLASH = "yyyy/MM/dd HH:mm:ss";
	public static final String FORMATE_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
	public static final String FORMATE_YYYYMMDDHH = "yyyyMMddHH";
	public static final String FORMATE_YYYYMMDD = "yyyyMMdd";
	public static final String FORMATE_YYYYMM = "yyyyMM";
	public static final String FORMATE_HHMMSS = "HHmmss";
	public static final String FORMATE_HHMM= "HHmm";
	public static final String FORMATE_HHMM_= "HH:mm";

	public static final String FORMATE_YYYY_MM_DD_MINUS = "yyyy-MM-dd";
	public static final String FORMATE_YYYY_MM_DD_SLASH = "yyyy/MM/dd";
	public static final String FORMATE_YYYY_MM_DD_CHN = "yyyy年MM月dd日";

	public static final String WEEKDAYNAMEARR[] = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };

	/**
	 * 将字符串装换成日期类型
	 * @param str 需要转换的字符串
	 * @return Date 转换后的日期
	 */
	public static Date convertStr2Date(String str, String format) {
		
		try {
			return new SimpleDateFormat(format).parse(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 校验字符是否是指定格式
	 * @param str
	 * @param format
	 * @return
	 */
	public static boolean validateStr(String str, String format) {
		try {
			new SimpleDateFormat(format).parse(str);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	
	/**
	 * 将字符串装换成日期类型yyyyMMddHHmmss
	 * @param str 需要转换的字符串
	 * @return Date 转换后的日期
	 */
	public static Date convertStr2Date(String str) {
		
		return convertStr2Date(str, FORMATE_YYYYMMDDHHMMSS);
	}
	
	
	/**
	 * 日期格式化
	 * @param date
	 * @param format
	 * @return
	 */
	public static String convertDate2Str(Date date, String format){
		
		try {
			return new SimpleDateFormat(format).format(date);
		} catch (Exception e) {
		}
		
		return "";
	}

	public static String getCurrentDateStr(String format) {
		
		return convertDate2Str(new Date(), format);
	}
	
	/**
	 * 获得当前时间yyyyMMddHHmmss
	 * @return String 系统当前时间X
	 */
	public static String getCurrentTimeSecds() {
		
		return getCurrentDateStr(FORMATE_YYYYMMDDHHMMSS);
	}
	
	/**
	 * 得到系统当前时间。(精确到毫秒)yyyyMMddHHmmssSSS
	 * @return String 系统当前时间
	 */
	public static String getCurrentTimeMills() {
		
		return getCurrentDateStr(FORMATE_YYYYMMDDHHMMSSSSS);
	}
	
	/**
	 * 取得系统时间戳 getTime数值
	 * @return
	 */
	public static long getCurrentTimeStampMills() {
		
		return new Date().getTime();
	}

	/**
	 * 获取当前日期,格式：YYYYMMDD
	 * @return String 系统当前日期
	 */
	public static String getCurrentDateYMD() {
		
		return getCurrentDateStr(FORMATE_YYYYMMDD);
	}
	
	/**
	 * 获取当前日期。格式：YYYY-MM-DD
	 * @return String 系统当前日期
	 */
	public static String getCurrentDateY_M_D() {
		
		return getCurrentDateStr(FORMATE_YYYY_MM_DD_MINUS);
	}
	
	/**
	 * 获取系统当前日期（YYYY年MM月DD日）
	 * @return
	 */
	public static String getCurrentDateYMDChn() {
		
		return getCurrentDateStr(FORMATE_YYYY_MM_DD_CHN);
	}
	
	/**
	 * 获取当前时间 HHmmss
	 * @return String 系统当前时间
	 */
	public static String getCurrentDateHMS() {
		
		return getCurrentDateStr(FORMATE_HHMMSS);
	}
	
	/**
	 * 获取当前系统时间和星期几
	 * @return YYYY年MM月DD日 星期几
	 */
	public static String getCurrentDateAndWeekDay(String dateFormat) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		
		String week_day = WEEKDAYNAMEARR[day - 1];
		
		return convertDate2Str(calendar.getTime(), dateFormat) + " "+ week_day;
	}
	
	/**
	 * 获取当前系统时间的星期几
	 * @return 星期几
	 */
	public static String getCurrentWeekDayName() {
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		
		int day = cal.get(Calendar.DAY_OF_WEEK);
		
		return WEEKDAYNAMEARR[day - 1];
	}
	
	public static String getWeekDayName(Date date) {
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		
		int day = cal.get(Calendar.DAY_OF_WEEK);
		
		return WEEKDAYNAMEARR[day - 1];
	}
	
	/**
	 * 取当前系统时间的年月日，时分秒，星期
	 * @return
	 */
	public static String[] getCurrentDateArray(){
		
		Date now = new Date();
		String day = convertDate2Str(now, FORMATE_YYYYMMDD);
		String time = convertDate2Str(now, FORMATE_HHMMSS);
		String weekDay = getWeekDayName(now);
		
		String[] nowArray = {day, time, weekDay};
		
		return nowArray;
	}
	
	/**
	 * 增加毫秒，计算时间
	 * @param date
	 * @param milliSecond
	 * @return
	 */
	public static Date addMilliSecond(Date date, int milliSecond){
		
		return calcTime(date, Calendar.MILLISECOND, milliSecond);
	}
	
	/**
	 * 增加秒，计算时间
	 * @param date
	 * @param second
	 * @return
	 */
	public static Date addSecond(Date date, int second){
		
		return calcTime(date, Calendar.SECOND, second);
	}
	
	/**
	 * 增加分钟，计算时间
	 * @param date
	 * @param minute
	 * @return
	 */
	public static Date addMinute(Date date, int minute){
		
		return calcTime(date, Calendar.MINUTE, minute);
	}
	
	/**
	 * 增加小时，计算时间
	 * @param date
	 * @param hour
	 * @return
	 */
	public static Date addHour(Date date, int hour){
		
		return calcTime(date, Calendar.HOUR, hour);
	}
	
	/**
	 * 增加天，计算时间
	 * @param date
	 * @param day
	 * @return
	 */
	public static Date addDay(Date date, int day){
		
		return calcTime(date, Calendar.DAY_OF_YEAR, day);
	}
	
	/**
	 * 增加月数，计算时间
	 * @param date
	 * @param month
	 * @return
	 */
	public static Date addMonth(Date date, int month){
		
		return calcTime(date, Calendar.MONTH, month);
	}
	
	/**
	 * 计算时间运算
	 * @param date
	 * @param calendarField
	 * @param addAmount
	 * @return
	 */
	public static Date calcTime(Date date, int calendarField, int addAmount) {
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		calendar.add(calendarField, addAmount);
		
		return calendar.getTime();
	}
	
	/***
	 * 获取一个时间与当前时间相差多少毫秒
	 *
	 * @param date
	 * @return
	 */
	public static long getDiffMillsWithNow(Date date) {
		
		return new Date().getTime() - date.getTime();
	}
	
	
	 /**
     * 获取指定时间的那天 00:00:00 的时间
     * @param date
     * @return
     */
    public static Date getDayBegin(Date date) {
    	
         Calendar calendar = Calendar.getInstance();
         calendar.setTime(date);
         
         calendar.set(Calendar.HOUR_OF_DAY, 0);
         calendar.set(Calendar.MINUTE, 0);
         calendar.set(Calendar.SECOND, 0);
         calendar.set(Calendar.MILLISECOND, 0);
         
         return calendar.getTime();
    }
	
	
	/**
     * 获取指定时间的那天 23:59:59 的时间
     * @param date
     * @return
     */
    public static Date getDayEnd(Date date) {
    	
         Calendar calendar = Calendar.getInstance();
         calendar.setTime(date);
         
         calendar.set(Calendar.HOUR_OF_DAY, 23);
         calendar.set(Calendar.MINUTE, 59);
         calendar.set(Calendar.SECOND, 59);
         calendar.set(Calendar.MILLISECOND, 999);
         
         return calendar.getTime();
    }
    
    /**
     * 获取指定时间的当天的下一天开始时间
     * 
     * 例如 输入2016-06-06 06:06：06
     *    得到2016-06-07 00:00:00
     * 
     * @param date
     * @return
     */
    public static Date getNextDayBegin(Date date) {
    	
         Calendar calendar = Calendar.getInstance();
         calendar.setTime(date);
         
         calendar.add(Calendar.DAY_OF_YEAR, 1);
         calendar.set(Calendar.HOUR_OF_DAY, 0);
         calendar.set(Calendar.MINUTE, 0);
         calendar.set(Calendar.SECOND, 0);
         calendar.set(Calendar.MILLISECOND, 0);
         
         return calendar.getTime();
    }
   
    public static Date getMonthBegin(Date date) {
    	Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        
        return calendar.getTime();
    }
    
    public static Date getNextMonthBegin(Date date) {
    	Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        
        return calendar.getTime();
    }

    public static Date[] getMonthBeginEnd(Date date) {

        Date[] firstLastDay = new Date[2];

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        firstLastDay[0] = calendar.getTime();

        calendar.add(Calendar.MONTH, 1);
        firstLastDay[1] = calendar.getTime();

        return firstLastDay;
    }
    public static Date[] getNextMonthBeginEnd(Date date) {

        Date[] firstLastDay = new Date[2];

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.MONTH, 1);

        firstLastDay[0] = calendar.getTime();

        calendar.add(Calendar.MONTH, 1);
        firstLastDay[1] = calendar.getTime();

        return firstLastDay;
    }

	public static Date getHourBegin(Date date) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTime();
	}

	public static Date getNextHourBegin(Date date) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(Calendar.HOUR_OF_DAY, 1);

		return calendar.getTime();
	}

    
	/**
	 * 比较date2 + rangeNum天数后与date1大小
	 * @param date1
	 * @param date2
	 * @return 小于0 小于   等于0 等于  大于0 大于
	 */
	public static int compareInRangeDay(Date date1, Date date2, int rangeNum) {
		
		Calendar c2 = Calendar.getInstance();
		c2.setTime(date2);
		
		c2.add(Calendar.DAY_OF_YEAR, rangeNum);
		
		return date1.compareTo(c2.getTime());
	}
	
	/**
	 * 将日期对象转换成年月数值形式表示
	 * 如2016-09-02日期对象 转换为201609
	 * @param date
	 * @return
	 */
	public static int convertDate2IntegerYm(Date date) {
		
		String ymStr = convertDate2Str(date, FORMATE_YYYYMM);
		
		return Integer.parseInt(ymStr);
	}
	
	/**
	 * 将日期对象转换成年月日数值形式表示
	 * 如2016-09-02日期对象 转换为20160902
	 * @param date
	 * @return
	 */
	public static int convertDate2IntegerYmd(Date date) {
		
		String ymStr = convertDate2Str(date, FORMATE_YYYYMMDD);
		
		return Integer.parseInt(ymStr);
	}
	
	/**
     * 是否处于跨月临界区间
     * 
     * 例如：2017-03-31 23:59:00 与 2017-04-01 00:01:00时间之间，将被认定为1分钟误差的3月至4月的临界点
     * 
     * @param sysDate
     * @param rangeMinite
     * @return 返回时间数组{不为空则处于临界点时间点为找到的临界点， 前临界点， 后临界点}
     */
	 public static Date[] isInMonthThresholds(Date sysDate, int rangeMinite) {
	    	
    	Date[] rangeDate = new Date[3];
    	
    	Calendar c = Calendar.getInstance();
		c.setTime(sysDate);
		
		//计算前临界时间
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		c.add(Calendar.MINUTE, rangeMinite);
		rangeDate[1] = c.getTime();
		
		//计算后临界时间
		c.add(Calendar.MONTH, 1);
		c.add(Calendar.MINUTE, -rangeMinite - rangeMinite);
		rangeDate[2] = c.getTime();
		
		//判断是否处在临界区间
		if (sysDate.compareTo(rangeDate[1]) <= 0) {
			rangeDate[0] = rangeDate[1];
		} else if (sysDate.compareTo(rangeDate[2]) >= 0) {
			rangeDate[0] = rangeDate[2];
		}
		
    	return rangeDate;
    }
    
    /**
     * 是否处于跨天临界区间
     * 
     * 例如：2017-03-17 23:59:00 与 2017-03-18 00:01:00时间之间，将被认定为1分钟误差的3月17日与18日的临界点
     * 
     * @param sysDate
     * @param rangeMinite
     * @return 返回时间数组{不为空则处于临界点时间点为找到的临界点， 前临界点， 后临界点}
     */
	public static Date[] isInDayThresholds(Date sysDate, int rangeMinite) {
		
		Date[] rangeDate = new Date[3];
		
		Calendar c = Calendar.getInstance();
		c.setTime(sysDate);
	
		//计算前临界时间
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		c.add(Calendar.MINUTE, rangeMinite);
		rangeDate[1] = c.getTime();
		
		//计算后临界时间
		c.add(Calendar.DAY_OF_YEAR, 1);
		c.add(Calendar.MINUTE, -rangeMinite - rangeMinite);
		rangeDate[2] = c.getTime();
		
		//判断是否处在临界区间
		if (sysDate.compareTo(rangeDate[1]) <= 0) {
			rangeDate[0] = rangeDate[1];
		} else if (sysDate.compareTo(rangeDate[2]) >= 0) {
			rangeDate[0] = rangeDate[2];
		}
		
		return rangeDate;
	}
	
	/**
	 * 计算传入时间的开始与结束日期
	 * @param sysDate			
	 * @param firstDayOfWeek	设置周第一天
	 * @return
	 */
	public static Date[] getWeekFirstLastDay(Date sysDate, int firstDayOfWeek) {
		
		Date[] firstLastDay = new Date[2];
		
		Calendar c = Calendar.getInstance();
		c.setTime(sysDate);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		c.setFirstDayOfWeek(firstDayOfWeek);

		c.set(Calendar.DAY_OF_WEEK, firstDayOfWeek);
		firstLastDay[0] = c.getTime();
		
		c.add(Calendar.DAY_OF_WEEK, 7);
		firstLastDay[1] = c.getTime();
		
		return firstLastDay;
	}
	public static Date[] getNextWeekFirstLastDay(Date sysDate, int firstDayOfWeek) {

		Date[] firstLastDay = new Date[2];

		Calendar c = Calendar.getInstance();
		c.setTime(sysDate);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		c.setFirstDayOfWeek(firstDayOfWeek);

		c.set(Calendar.DAY_OF_WEEK, firstDayOfWeek);
		c.add(Calendar.WEEK_OF_YEAR, 1);

		firstLastDay[0] = c.getTime();

		c.add(Calendar.DAY_OF_WEEK, 7);
		firstLastDay[1] = c.getTime();

		return firstLastDay;
	}

	/**
	 * 毫秒转日期
	 * @param mils
	 * @param fromat  $D(天) $h(小时) $m(分钟) $s(秒)
	 * @return
	 */
	public static String formatMillisecond(long mils, String fromat) {
		if (fromat.contains("$D")) {
			//天
			long day = mils / (1000 * 60 * 60 * 24);
			String newStr = "";
			if (day > 0) {
				newStr = day + "天";
			}
			fromat = fromat.replaceAll("\\$D", newStr);
		}
		if (fromat.contains("$h")) {
			//小时
			long hour = (mils % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
			String newStr = "";
			if (hour > 0) {
				newStr = hour + "小时";
			}
			fromat = fromat.replaceAll("\\$h", newStr);
		}
		if (fromat.contains("$m")) {
			//分
			long minutes = (mils % (1000 * 60 * 60)) / (1000 * 60);
			String newStr = "";
			if (minutes > 0) {
				newStr = minutes + "分钟";
			}
			fromat = fromat.replaceAll("\\$m", newStr);
		}
		if (fromat.contains("$s")) {
			//秒
			long seconds = (mils % (1000 * 60)) / 1000;
			String newStr = "";
			if (seconds > 0) {
				newStr = seconds + "秒";
			}
			fromat = fromat.replaceAll("\\$s", newStr);
		}
		return fromat;
	}
}
