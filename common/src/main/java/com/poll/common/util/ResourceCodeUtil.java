package com.poll.common.util;

import com.poll.common.CodeContants;
import com.poll.common.Constants;
import com.poll.common.dto.ResourceCode;
import java.util.Calendar;
import java.util.Date;


public class ResourceCodeUtil {

    /**
     * 生成资源编码
     *
     * @param rt                    资源类型
     * @param resourceId            资源id
     * @param limitMax              限量
     * @param startTime             资源可用开始时间 可为空
     * @param endTime               资源可用结束时间 可为空
     * @param endTimeDelayMills     资源可用结束延迟毫秒数
     * @param sysDate               系统当前时间
     * @return
     */
    public static ResourceCode genResourceCode(CodeContants.RESOURCE_TYPE rt, String resourceId, long limitMax, Date startTime, Date endTime, Long endTimeDelayMills, Date sysDate) {

        if (sysDate == null) {
            sysDate = new Date();
        }

        String infix = null;
        Date endDate = null;

        if (rt.equals(CodeContants.RESOURCE_TYPE.T)) {

            endDate = endTime;

        } else if (rt.equals(CodeContants.RESOURCE_TYPE.H)) {

            infix = DateUtil.convertDate2Str(sysDate, DateUtil.FORMATE_YYYYMMDDHH);
            endDate = DateUtil.getNextHourBegin(sysDate);

        } else if (rt.equals(CodeContants.RESOURCE_TYPE.D)) {

            infix = DateUtil.convertDate2Str(sysDate, DateUtil.FORMATE_YYYYMMDD);
            endDate = DateUtil.getNextDayBegin(sysDate);

        } else if (rt.equals(CodeContants.RESOURCE_TYPE.W)) {

            Date[] weekFirstLastDay = DateUtil.getWeekFirstLastDay(sysDate, Calendar.MONDAY);
            infix = DateUtil.convertDate2Str(weekFirstLastDay[0], DateUtil.FORMATE_YYYYMMDD);
            endDate = weekFirstLastDay[1];

        } else if (rt.equals(CodeContants.RESOURCE_TYPE.M)) {

            Date[] monthBeginEnd = DateUtil.getMonthBeginEnd(sysDate);

            infix = DateUtil.convertDate2Str(monthBeginEnd[0], DateUtil.FORMATE_YYYYMM);
            endDate = monthBeginEnd[1];
        }

        Date delay = endDate == null ? null : new Date(endDate.getTime() + endTimeDelayMills);

        String code = null;
        if (infix == null) {
            code = String.format("%s:%s", rt.getValue(), resourceId);
        } else {
            code = String.format("%s:%s:%s", rt.getValue(), infix, resourceId);
        }

        return new ResourceCode(code, limitMax, startTime, endDate, delay);
    }

    public static void main(String[] args) {

        String resouceId = "1";
        long limitMax = -1;
        Date startTime = DateUtil.convertStr2Date("20180817000000");
        Date endTime = DateUtil.convertStr2Date("20180818000000");
        long endTimeDelayMills = Constants.MILLS_DAY1;
        Date sysDate = new Date();

        System.out.println(genResourceCode(CodeContants.RESOURCE_TYPE.H, resouceId, limitMax, startTime, endTime, endTimeDelayMills, sysDate));
        System.out.println(genResourceCode(CodeContants.RESOURCE_TYPE.D, resouceId, limitMax, startTime, endTime, endTimeDelayMills, sysDate));
        System.out.println(genResourceCode(CodeContants.RESOURCE_TYPE.W, resouceId, limitMax, startTime, endTime, endTimeDelayMills, sysDate));
        System.out.println(genResourceCode(CodeContants.RESOURCE_TYPE.M, resouceId, limitMax, startTime, endTime, endTimeDelayMills, sysDate));
        System.out.println(genResourceCode(CodeContants.RESOURCE_TYPE.T, resouceId, limitMax, startTime, endTime, endTimeDelayMills, sysDate));

    }

}
