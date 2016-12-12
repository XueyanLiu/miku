package com.biubiu.miku.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
    /**
     * 进行md5加密
     *
     * @param text 源字符
     * @return 加密后字符
     */
    public static String md5(String text) {
        String result = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(text.getBytes());
            byte[] b = md5.digest();
            result = byte16to32(b);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 16位转换为32位
     *
     * @param byte16
     * @return
     */
    public static String byte16to32(byte[] byte16) {
        int i;
        StringBuffer buf = new StringBuffer("");
        for (byte b : byte16) {
            i = b;
            if (i < 0) {
                i += 256;
            }
            if (i < 16) {
                buf.append("0");
            }
            buf.append(Integer.toHexString(i));
        }
        return buf.toString();
    }
}
