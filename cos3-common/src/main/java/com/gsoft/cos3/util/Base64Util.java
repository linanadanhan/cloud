package com.gsoft.cos3.util;

import java.io.UnsupportedEncodingException;

/**
 *  [一句话功能简述]
 *  [功能详细描述]
 * @作者 SN
 * @version [版本号, 2017年9月23日]
 * @see [相关类/方法]
 * @since [产品/模块版本] 
 */
public class Base64Util
{
    // 将 s 进行 BASE64 编码
    public static String getBASE64(String s)
    {
        return getBASE64(s, "GBK");
    }

    // 将 s 进行 BASE64 编码
    public static String getBASE64(String s, String charsetName)
    {
        if (s == null)
            return null;
        try
        {
            return new String(Base64.encode(s.getBytes(charsetName)));
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            return "";
        }
    }

    // 将 BASE64 编码的字符串 s 进行解码
    public static String getFromBASE64(String s)
    {
        return getFromBASE64(s, "GBK");
    }

    // 将 BASE64 编码的字符串 s 进行解码
    public static String getFromBASE64(String s, String charsetName)
    {
        if (s == null)
            return null;
        try
        {
            byte[] b = Base64.decode(s);
            return new String(b, charsetName);
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
