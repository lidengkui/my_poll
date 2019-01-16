package com.poll.common.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import static com.poll.common.util.DateUtil.convertDate2Str;
import static com.poll.common.util.ObjectId.createProcessIdentifier;

/**
 * orderNo生成器
 * <p>简单实现：支持分布式环境，单机秒级并发1000
 */
public class Ids {

    public static final char PADCHAR = '0';
    private static final short PROCESS_IDENTIFIER;

    static {
        try {
            PROCESS_IDENTIFIER = createProcessIdentifier();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 获取Did
     *
     * @return 默认20字符长度的数字不重复编码
     */
    public static String getDid() {
        return Did.getInstance().getId();
    }

    /**
     * @param size 位数需大于20
     */
    public static String getDid(int size) {
        return Did.getInstance().getId(size);
    }


    /**
     * ID生成器
     * <p>20字符长度 yyMMddHHmmss+3位本机IP末三位+5位随机数字
     *
     * @author zhangpu
     */
    public static class Did {
        private static final Logger logger = LoggerFactory.getLogger(Ids.class);
        private static final int MIN_LENGTH = 20;
        private static final int SEQU_LENGTH = 4;
        private static final int SEQU_MAX = 9999;
        private static Did did = new Did();
        private static String pidStr = null;
        private AtomicLong sequence = new AtomicLong(1);
        private String nodeFlag;
        private Object nodeFlagLock = new Object();

        private Did() {
            super();
        }

        public static Did getInstance() {
            return did;
        }

        private static short short2(final short x) {
            short b = (short) (x % (short) 100);
            return b <= 10 ? 10 : b;
        }

        /**
         * 生产新Id(20位)
         *
         * @return
         */
        public String getId() {
            return getId(MIN_LENGTH);
        }

        /**
         * @param size 位数需大于20
         */
        public String getId(int size) {
          try{
              if (size < MIN_LENGTH) {
                  throw new Exception("did最小长度为" + MIN_LENGTH);
              }
          }catch (Exception e){
              e.printStackTrace();
          }
            StringBuilder sb = new StringBuilder();
            // 当前时间(14位)
            sb.append(convertDate2Str(new Date(), "yyMMddHHmmssSSS")); //yyMMddHHmmssSSS
            // 随机数字(size-18位)
            sb.append(RandomUtil.genNumberStr((size - 17)));
            // 进程id(2位)
            sb.append(getPid());
            //序号
            sb.append(getSequ());
            return sb.toString();
        }
        /**
         * 获取两位pid
         */
        private String getPid() {
            if (pidStr == null) {
                pidStr = String.valueOf(Math.abs(short2(PROCESS_IDENTIFIER)));
            }
            return pidStr;
        }

        public String getSequ() {
            long timeCount = 0;
            while (true) {
                timeCount = sequence.get();
                if (sequence.compareAndSet(SEQU_MAX, 1)) {
                    timeCount = 1;
                    break;
                }
                if (sequence.compareAndSet(timeCount, timeCount + 1)) {
                    break;
                }
            }
            return StringUtils.leftPad(String.valueOf(timeCount), SEQU_LENGTH, '0');
        }
    }


}
