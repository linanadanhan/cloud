package com.gsoft.cos3.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * 封装了XML转换成object，object转换成XML的代码
 */
public class JaxbUtil
{
    /**
     * 将对象直接转换成String类型的 XML输出
     * 
     * @param obj
     * @return
     */
    public static String convertObjToXml(Object obj)
    {

        return convertObjToXml(obj, "UTF-8");
    }

    public static String convertObjToXml(Object obj, String charsetName)
    {

        return convertObjToXml(obj, charsetName, true);
    }

    /**
     * 将对象直接转换成String类型的 XML输出
     * 
     * @param obj
     * @return
     */
    public static String convertObjToXml(Object obj, String charsetName,
            boolean needFragment)
    {
        // 创建输出流
        StringWriter sw = new StringWriter();
        try
        {
            // 利用jdk中自带的转换类实现
            JAXBContext context = JAXBContext.newInstance(obj.getClass());

            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, charsetName);// //编码格式
            // 格式化xml输出的格式
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
                    Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);// 是否省略xm头声明信息
            // marshaller.setProperty("com.sun.xml.bind.xmlDeclaration",
            // Boolean.FALSE);
            // marshaller.setProperty("com.sun.xml.bind.xmlHeaders", "<?xml
            // version=\"1.0\" encoding=\"UTF-8\"?>");
            // 将对象转换成输出流形式的xml
            marshaller.marshal(obj, sw);
        }
        catch (JAXBException e)
        {
            e.printStackTrace();
        }
        if (needFragment)
        {
            StringBuilder sb = new StringBuilder(2);
            sb.append("<?xml version=\"1.0\" encoding=\"" + charsetName
                    + "\"?>\r\n");
            sb.append(sw.toString());
            return sb.toString();
        }
        return sw.toString();
    }

    /**
     * 将对象根据路径转换成xml文件
     * 
     * @param obj
     * @param path
     * @return
     */
    public static void convertObjToFile(Object obj, String path)
    {
        try
        {
            // 利用jdk中自带的转换类实现
            JAXBContext context = JAXBContext.newInstance(obj.getClass());

            Marshaller marshaller = context.createMarshaller();
            // 格式化xml输出的格式
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
                    Boolean.TRUE);
            // 将对象转换成输出流形式的xml
            // 创建输出流
            FileWriter fw = null;
            try
            {
                fw = new FileWriter(path);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            marshaller.marshal(obj, fw);
        }
        catch (JAXBException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 将String类型的xml转换成对象
     */
    public static Object convertXmlToObj(Class<?> clazz, String xml)
    {
        Object xmlObject = null;
        try
        {
            JAXBContext context = JAXBContext.newInstance(clazz);
            // 进行将Xml转成对象的核心接口
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StringReader sr = new StringReader(xml);
            xmlObject = unmarshaller.unmarshal(sr);
        }
        catch (JAXBException e)
        {
            e.printStackTrace();
        }
        return xmlObject;
    }

    public static Object convertFileToObj(Class<?> clazz, String path)
    {
        Object xmlObject = null;
        try
        {
            JAXBContext context = JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            FileReader fr = null;
            try
            {
                fr = new FileReader(path);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            xmlObject = unmarshaller.unmarshal(fr);
        }
        catch (JAXBException e)
        {
            e.printStackTrace();
        }
        return xmlObject;
    }

    /**
     * 将file类型的xml转换成对象
     */
    public static Object convertFileToObj(Class<?> clazz, String path,
            String charsetName)
    {
        Object xmlObject = null;
        try
        {
            JAXBContext context = JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            InputStreamReader reader = null;
            try
            {
                if (com.gsoft.cos3.util.Assert.isEmpty(charsetName))
                {
                    reader = new InputStreamReader(new FileInputStream(path));
                }
                else
                {
                    reader = new InputStreamReader(new FileInputStream(path),
                            charsetName);
                }
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }
            xmlObject = unmarshaller.unmarshal(reader);
        }
        catch (JAXBException e)
        {
            e.printStackTrace();
        }
        return xmlObject;
    }

    /**
     * 将file类型的xml转换成对象
     */
    public static Object convertInputStreamToObj(Class<?> clazz, InputStream is,
            String charsetName)
    {
        Object xmlObject = null;
        try
        {
            JAXBContext context = JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            InputStreamReader reader = null;
            try
            {
                if (com.gsoft.cos3.util.Assert.isEmpty(charsetName))
                {
                    reader = new InputStreamReader(is);
                }
                else
                {
                    reader = new InputStreamReader(is, charsetName);
                }
            }
            catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }
            xmlObject = unmarshaller.unmarshal(reader);
        }
        catch (JAXBException e)
        {
            e.printStackTrace();
        }
        return xmlObject;
    }

}
