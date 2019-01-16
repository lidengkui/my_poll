package com.poll.service.business;

import com.poll.common.util.MD5Util;
import com.poll.common.util.RandomUtil;
import org.junit.Test;

public class UserService_V1Test {

    @Test
    public void genInserUserSql() {

        String userName = "gaoyuan";
        String mobile = "18030540425";

        String pwd = RandomUtil.genNumberStr(6);
        System.out.println("pwd->" + pwd);

        String salt = RandomUtil.genLetterNumStr(10);
        String rdmFlag = RandomUtil.genLetterNumStr(6);

        String pwdMd5 = MD5Util.encode(pwd).toLowerCase();
        System.out.println("pwdMd5->" + pwdMd5);

        String pwdInDb = MD5Util.encode(pwdMd5 + salt).toLowerCase();
        System.out.println("pwdInDb->" + pwdInDb);


    }

}