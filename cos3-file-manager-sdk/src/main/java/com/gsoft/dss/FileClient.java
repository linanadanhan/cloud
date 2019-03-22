package com.gsoft.dss;


import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.annotation.Resource;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * dss文件客户端操作类
 */
@Component
public class FileClient {

    @Resource(name = "httpClientManagerFactoryBen")
    private CloseableHttpClient closeableHttpClient;


    @Autowired
    ConnectionSettings conn;

    /**
     * 根据传入的referenceId，下载对应的文件流
     *
     * @param referenceId 文件对应的referenceId
     * @return 文件InputStream
     */
    public HttpEntity download(String referenceId) {
        HttpEntity entity = null;
        HttpGet httpGet = new HttpGet("http://" + conn.getRemoteAddress() + ":" + conn.getPort() + "/file/download/" + referenceId);
        try {
            CloseableHttpResponse response = closeableHttpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                entity = response.getEntity();
                System.out.println("服务器正常响应.....下载成功！");
                return entity;
            } else {
                System.out.println("错误码：" + statusCode + " 服务器正常错误.....下载失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }

    /**
     * 读取本地文件上传
     *
     * @param fileAddress 本地文件全路径
     * @return FileNode的json对象
     */
    public JSONObject webUploader(String fileAddress) {
        JSONObject jsonObject = null;
        HttpEntity entity = null;
        try {
            //新建一个httpclient Post 请求
            HttpPost httppost = new
                    HttpPost("http://" + conn.getRemoteAddress() + ":" + conn.getPort() + "/file/uploadFile");
            //由于只是测试使用 这里的路径对应本地文件的物理路径
            File myfile = new File(fileAddress);
            FileBody bin = new FileBody(myfile);
            //向MultipartEntity添加必要的数据
            StringBody comment = new StringBody(myfile.getName(), ContentType.TEXT_PLAIN);
            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .addPart("file", bin)
                    .addPart("filename", comment)
                    .build();
            httppost.setEntity(reqEntity);

            CloseableHttpResponse response = closeableHttpClient.execute(httppost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                System.out.println("服务器正常响应.....上传成功！");
                entity = response.getEntity();
                jsonObject = new JSONObject(EntityUtils.toString(entity));
            } else {
                System.out.println("错误码：" + statusCode + " 服务器正常错误.....上传失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != entity) {
                // 释放连接
                EntityUtils.consumeQuietly(entity);
            }
        }
        return jsonObject;
    }

    /**
     * 根据得到流上传
     *
     * @param inputStream 流
     * @param name        文件名
     * @return
     */
    public JSONObject webUploader(InputStream inputStream, String name) {
        JSONObject jsonObject = null;
        HttpEntity entity = null;
        try {
            //新建一个httpclient Post 请求
            HttpPost httppost = new
                    HttpPost("http://" + conn.getRemoteAddress() + ":" + conn.getPort() + "/file/uploadFile");

//            ContentType contentType = ContentType.create("text/plain", Charset.forName("UTF-8"));
            //向MultipartEntity添加必要的数据
            ContentType contentType = ContentType.create("text/plain", "UTF-8");
            StringBody stringBody = new StringBody(name, contentType);
//            builder.addPart("filename", stringBody); 
            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .setCharset(Charset.forName("UTF-8"))
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .addBinaryBody("file", inputStream, ContentType.MULTIPART_FORM_DATA, name)
                    .addPart("filename", stringBody)
//                    .addTextBody("filename", name, contentType)
                    .build();

            httppost.setEntity(reqEntity);

            CloseableHttpResponse response = closeableHttpClient.execute(httppost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                System.out.println("服务器正常响应.....上传成功！");
                entity = response.getEntity();
                jsonObject = new JSONObject(EntityUtils.toString(entity));
            } else {
                System.out.println("错误码：" + statusCode + " 服务器正常错误.....上传失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != entity) {
                // 释放连接
                EntityUtils.consumeQuietly(entity);
            }
        }
        return jsonObject;
    }

    /**
     * 根据得到byte数组上传
     *
     * @param bytes
     * @param name  文件名
     * @return
     */
    public JSONObject webUploader(byte[] bytes, String name) {
        JSONObject jsonObject = null;
        HttpEntity entity = null;
        try {
            //新建一个httpclient Post 请求
            HttpPost httppost = new
                    HttpPost("http://" + conn.getRemoteAddress() + ":" + conn.getPort() + "/file/uploadFile");

            ContentType contentType = ContentType.create("text/plain", "UTF-8");
            StringBody stringBody = new StringBody(name, contentType);
            //向MultipartEntity添加必要的数据
            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .setCharset(Charset.forName("UTF-8"))
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .addBinaryBody("file", bytes, ContentType.DEFAULT_BINARY, name)
                    .addPart("filename", stringBody)
                    .build();

            httppost.setEntity(reqEntity);

            CloseableHttpResponse response = closeableHttpClient.execute(httppost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                System.out.println("服务器正常响应.....上传成功！");
                entity = response.getEntity();
                jsonObject = new JSONObject(EntityUtils.toString(entity));
            } else {
                System.out.println("错误码：" + statusCode + " 服务器正常错误.....上传失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != entity) {
                // 释放连接
                EntityUtils.consumeQuietly(entity);
            }
        }
        return jsonObject;
    }

}





