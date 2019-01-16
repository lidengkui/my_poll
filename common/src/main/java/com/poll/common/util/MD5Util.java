package com.poll.common.util;

import java.security.MessageDigest;

public class MD5Util {

    public MD5Util() {
    }

    public static final String encode(String s) {
        char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        try {
            if (s != null && !"".equals(s)) {
                byte[] strTemp = s.getBytes("UTF-8");
                MessageDigest mdTemp = MessageDigest.getInstance("MD5");
                mdTemp.update(strTemp);
                byte[] md = mdTemp.digest();
                int j = md.length;
                char[] str = new char[j * 2];
                int k = 0;

                for(int i = 0; i < j; ++i) {
                    byte b = md[i];
                    str[k++] = hexDigits[b >> 4 & 15];
                    str[k++] = hexDigits[b & 15];
                }

                return new String(str);
            } else {
                return null;
            }
        } catch (Exception var10) {
            var10.printStackTrace();
            return null;
        }
    }

    public static final String encode(String s, String charset) {
        char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        try {
            if (s != null && !"".equals(s)) {
                byte[] strTemp = s.getBytes(charset);
                MessageDigest mdTemp = MessageDigest.getInstance("MD5");
                mdTemp.update(strTemp);
                byte[] md = mdTemp.digest();
                int j = md.length;
                char[] str = new char[j * 2];
                int k = 0;

                for(int i = 0; i < j; ++i) {
                    byte b = md[i];
                    str[k++] = hexDigits[b >> 4 & 15];
                    str[k++] = hexDigits[b & 15];
                }

                return new String(str);
            } else {
                return null;
            }
        } catch (Exception var11) {
            var11.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        String str = "英雄联盟";
        System.out.println(encode(str));
        System.err.println(encode(str, "GBK"));
        System.out.println(encode(""));
    }
}
