package com.gsoft.cos3.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gsoft.cos3.util.Assert;

/**
 * 发送文件
 */
public class DataExchangeUtil
{
    /**
     * 日志对象
     */
    private static Logger log = LoggerFactory.getLogger(DataExchangeUtil.class);

    /**
     * 编码
     */
    protected static String CHARSET = "UTF-8";

    /**
     * 
     */
    protected static final ContentType TEXT_PLAIN = ContentType
            .create("text/plain", Consts.UTF_8);

    public static String submitPost(String businessId, String businessName,
            String projectName, String receiver, String receiverName,
            String text, String fileName,String webServiceUrl,String webServiceCode,String webServicePassword) throws IOException
    {

        if (Assert.isEmpty(text))
        {
            return "";
        }

        byte[] bytes = text.getBytes();
        // 创建HttpClientBuilder
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        // HttpClient
        CloseableHttpClient httpClient = httpClientBuilder.build();
        String url = webServiceUrl + "/dex/webservice/sendFile.ws?code=" + webServiceCode
                + "&password=" + webServicePassword;
        HttpPost httpPost = new HttpPost(url);
        log.info("发送报文【名称：" + fileName + "， 大小：" + bytes.length + "】");
        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder
                .create();
        multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        multipartEntityBuilder.setBoundary(null);
        multipartEntityBuilder.setCharset(Consts.UTF_8);

        multipartEntityBuilder.addBinaryBody("file", bytes,
                ContentType.DEFAULT_BINARY, fileName);
        multipartEntityBuilder.addTextBody("receiver", receiver, TEXT_PLAIN);// 接收者名称，与消息服务器中的本地队列命名规则保持一致。
        multipartEntityBuilder.addTextBody("receiverName",
                (Assert.isEmpty(receiverName) ? "" : receiverName), TEXT_PLAIN); // 接收者中文名称
        multipartEntityBuilder.addTextBody("projectName",
                (Assert.isEmpty(projectName) ? "" : projectName), TEXT_PLAIN); // 项目名
        multipartEntityBuilder.addTextBody("businessName", businessName,
                TEXT_PLAIN); // 业务名
        multipartEntityBuilder.addTextBody("businessID", businessId,
                TEXT_PLAIN); // 业务编码

        // 生成 HTTP实体
        HttpEntity reqEntity = multipartEntityBuilder.build();
        httpPost.setEntity(reqEntity);
        HttpResponse response = httpClient.execute(httpPost);
        int statusCode = response.getStatusLine().getStatusCode();
        String result = "";
        if (statusCode == HttpStatus.SC_OK)
        {
            result = getContent(response);
            if (log.isDebugEnabled())
            {
                log.debug("文件发送完成，服务器响应内容：" + result);
            }
        }
        else
        {
            log.info("服务器响应状态：" + statusCode);
        }

        httpClient.close();
        return result;
    }

    private static String getContent(HttpResponse response)
    {
        StringBuffer buffer = new StringBuffer();
        HttpEntity entity = response.getEntity();
        InputStream is = null;
        try
        {
            if (entity != null)
            {
                is = entity.getContent();
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(is, "GBK"));
                String line = "";
                while ((line = in.readLine()) != null)
                {
                    buffer.append(line);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return buffer.toString();
    }

}
