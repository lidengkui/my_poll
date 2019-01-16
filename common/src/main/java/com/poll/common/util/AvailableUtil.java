package com.poll.common.util;

import com.poll.common.CodeContants;
import com.poll.common.Constants;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AvailableUtil {

    /**
     * 检查状态 时间 是否可用
     * @param sysDate
     * @param status
     * @param startTime
     * @param endTime
     * @param throwsError   判断失败时是否抛出异常
     * @param errorMsg      抛出异常描述
     */
    public static boolean checkStatusAndTime(Byte status, Date startTime, Date endTime, Date sysDate, boolean throwsError, String errorMsg) {

        if (sysDate == null) {
            sysDate = new Date();
        }
        return checkStatus(status, throwsError, errorMsg) &&
                checkTime(startTime, endTime, sysDate, throwsError, errorMsg);
    }
    public static boolean checkStatusAndTime(Byte status, Date startTime, Date endTime, boolean throwsError, String errorMsg) {

        return checkStatusAndTime(status, startTime, endTime, new Date(), throwsError, errorMsg);
    }

    /**
     * 校验时间
     * @param startTime
     * @param endTime
     * @param sysDate
     * @param throwsError
     * @param errorMsg
     * @return
     */
    public static boolean checkTime(Date startTime, Date endTime, Date sysDate, boolean throwsError, String errorMsg) {

        if (sysDate == null) {
            sysDate = new Date();
        }
        return  checkStartTime(startTime, sysDate, throwsError, errorMsg) &&
                checkEndTime(endTime, sysDate, throwsError, errorMsg);
    }
    public static boolean checkTime(Date startTime, Date endTime, boolean throwsError, String errorMsg) {
        return checkTime(startTime, endTime, new Date(), throwsError, errorMsg);
    }

    /**
     * 检查状态是否可用，只有状态值为1时才认定为可用
     * @param status
     * @param throwsError
     * @param errorMsg
     * @return
     */
    public static boolean checkStatus(Byte status, boolean throwsError, String errorMsg) {

        if (!Constants.FLAG_YES_BYTE.equals(status)) {
            return handleRetuenFalse(throwsError, errorMsg);
        }
        return true;
    }

    /**
     * 检查是否已到可用开始时间
     * @param startTime
     * @param sysDate
     * @param throwsError
     * @param errorMsg
     * @return
     */
    public static boolean checkStartTime(Date startTime, Date sysDate, boolean throwsError, String errorMsg) {

        if (sysDate == null) {
            sysDate = new Date();
        }
        if (sysDate.compareTo(startTime) < 0) {
            return handleRetuenFalse(throwsError, errorMsg);
        }
        return true;
    }

    /**
     * 检查是否未到可用结束时间
     * @param endTime
     * @param sysDate
     * @param throwsError
     * @param errorMsg
     * @return
     */
    public static boolean checkEndTime(Date endTime, Date sysDate, boolean throwsError, String errorMsg) {

        if (sysDate == null) {
            sysDate = new Date();
        }
        if (sysDate.compareTo(endTime) >= 0) {
            return handleRetuenFalse(throwsError, errorMsg);
        }
        return true;
    }

    /**
     * 检查传入字符是否命中规则字符串
     *
     * 规则字符串定义： *全部 逗号分割数组正向包含 -开头逗号分割数组反向包含
     *
     * 例：str=test
     *    ruleStr=*                     返回true
     *    ruleStr=test,test1,test2      返回true
     *    ruleStr=-test,test1,test2     返回false
     *
     * @param str
     * @param ruleStr
     * @param throwsError
     * @param errorMsg
     * @return
     */
    public static boolean checkStrMatchRule(String str, String ruleStr, boolean throwsError, String errorMsg) {

        if (str == null || ruleStr == null) {
            return handleRetuenFalse(throwsError, errorMsg);
        }

        //*匹配所有 -反向包含 逗号分割正向包含
        if (!Constants.STR_STAR.equals(ruleStr)) {
            //反向包含
            if (ruleStr.startsWith(Constants.STR_MINUS)) {
                String substring = ruleStr.substring(1);
                if (Arrays.asList(substring.split(Constants.STR_COMMA)).contains(str)) {
                    return handleRetuenFalse(throwsError, errorMsg);
                }
            } else {//正向包含
                if (!Arrays.asList(ruleStr.split(Constants.STR_COMMA)).contains(str)) {
                    return handleRetuenFalse(throwsError, errorMsg);
                }
            }
        }
        return true;
    }

    /**
     * 检查周期性是否可用
     * @param sysDate           校验时间，可为空，为空则
     * @param ruleStr           周期规则字符串，多组规则用;分割
     *                          *一直可用
     *                          -开头反向可用 每组可
     *                          HOD("HOD", "日中时"),
                                DOW("DOW", "周中日"),
                                DOM("DOM", "月中日"),
                                DOY("DOY", "年中日"),
                                WOM("WOM", "月中周"),
                                WOY("WOY", "年中周"),
                                MOY("MOY", "年中月"),
     *                          例HOD:8,9,10指定每天8,9,10点可用，HOD:8,9,10;DOW:1指定周一的8,9,10点可用
     * @param throwsError
     * @param errorMsg
     * @return
     */
    public static boolean checkCycleRule(Date sysDate, String ruleStr, boolean throwsError, String errorMsg) {

        if (ruleStr == null) {
            return handleRetuenFalse(throwsError, errorMsg);
        }

        //*匹配所有 -反向包含
        if (!Constants.STR_STAR.equals(ruleStr)) {

            //英文分号分割规则组
            List<String> ruleList = Arrays.asList(ruleStr.split(Constants.STR_SEMICOLON));

            if (sysDate == null) {
                sysDate = new Date();
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sysDate);

            //当前小时
            String hod = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
            //转换周中天同数值相同
            int dowNum = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            if (dowNum == 0) {
                dowNum = 7;
            }
            String dow = String.valueOf(dowNum);
            String dom = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            String doy = String.valueOf(calendar.get(Calendar.DAY_OF_YEAR));
            String wom = String.valueOf(calendar.get(Calendar.WEEK_OF_MONTH));
            String woy = String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR));
            String moy = String.valueOf(calendar.get(Calendar.MONTH) + 1);

            //遍历规则组
            for (String rule : ruleList) {

                boolean reverse = false;
                //反向包含
                if (rule.startsWith(Constants.STR_MINUS)) {
                    //去除前置符号
                    rule = rule.substring(1);
                    //标记反向
                    reverse = true;
                }

                String[] ruleArr = rule.split(Constants.STR_COLON);
                if (ruleArr.length != 2) {
                    continue;
                }

                String checkValue = null;

                if (CodeContants.CYCLE_AVAILABLE_TYPE.HOD.getValue().equalsIgnoreCase(ruleArr[0])) {
                    checkValue = hod;
                } else if (CodeContants.CYCLE_AVAILABLE_TYPE.DOW.getValue().equalsIgnoreCase(ruleArr[0])) {
                    checkValue = dow;
                } else if (CodeContants.CYCLE_AVAILABLE_TYPE.DOM.getValue().equalsIgnoreCase(ruleArr[0])) {
                    checkValue = dom;
                } else if (CodeContants.CYCLE_AVAILABLE_TYPE.DOY.getValue().equalsIgnoreCase(ruleArr[0])) {
                    checkValue = doy;
                } else if (CodeContants.CYCLE_AVAILABLE_TYPE.WOM.getValue().equalsIgnoreCase(ruleArr[0])) {
                    checkValue = wom;
                } else if (CodeContants.CYCLE_AVAILABLE_TYPE.WOY.getValue().equalsIgnoreCase(ruleArr[0])) {
                    checkValue = woy;
                } else if (CodeContants.CYCLE_AVAILABLE_TYPE.MOY.getValue().equalsIgnoreCase(ruleArr[0])) {
                    checkValue = moy;
                } else {
                    continue;
                }

                if (!ifValueMatchInList(checkValue,  Arrays.asList(ruleArr[1].split(Constants.STR_COMMA)), reverse)) {
                    return handleRetuenFalse(throwsError, errorMsg);
                }
            }
        }

        return true;
    }

    /**
     * 检查指定值是否符合list包含规则
     * @param str
     * @param identfList
     * @param reverse           标记是否反向包含，即list中包含指定值时，reverse为true，则返回不包含
     * @return
     */
    public static boolean ifValueMatchInList(String str, List<String> identfList, boolean reverse) {

        if (identfList.contains(str)) {
            if (reverse) {
                return false;
            }
        } else {
            if (!reverse) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查传入字符串是否命中正则规则
     * 
     * 若正则以-开头，意为反向
     * 
     * @param str
     * @param regStr
     * @param throwsError
     * @param errorMsg
     * @return
     */
    public static boolean checkStrMatchReg(String str, String regStr, boolean throwsError, String errorMsg) {

        if (str == null) {
            return handleRetuenFalse(throwsError, errorMsg);
        }

        regStr = StringUtil.trimStr(regStr);
        if (regStr.equals(Constants.STR_BLANK)) {
            return handleRetuenFalse(throwsError, errorMsg);
        }

        //反向正则，当正则匹配时，授权失败
        if (regStr.startsWith(Constants.STR_MINUS)) {
            regStr = regStr.substring(1);
            if (str.matches(regStr)) {
                return handleRetuenFalse(throwsError, errorMsg);
            }
        } else if (!str.matches(regStr)) {//正向正则
            return handleRetuenFalse(throwsError, errorMsg);
        }
        
        return true;
    }

    /**
     * 解析库存限制字符串后的限制数字，如D:-1,解析得到-1
     * @param stockStr
     * @return
     */
    public static int parseStockLimitNum(String stockStr) {
        try {
            String[] split = stockStr.split(Constants.STR_COLON);
            String numStr = split[split.length - 1];
            return Integer.parseInt(numStr);
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * 处理返回是抛出异常还是返回false
     * @param throwsError
     * @param errorMsg
     * @return
     */
    private static boolean handleRetuenFalse(boolean throwsError, String errorMsg) {
        if (throwsError) {
            throw new RuntimeException(errorMsg);
        }
        return false;
    }

}
