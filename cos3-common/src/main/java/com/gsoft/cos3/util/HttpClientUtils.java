package com.gsoft.cos3.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HttpClient 工具类
 * 
 * @author chenxx
 *
 */
public class HttpClientUtils {

	/**
	 * 日志对象
	 */
	private static Logger log = LoggerFactory.getLogger(HttpClientUtils.class); // 日志记录

	/**
	 * httpClient
	 */
	private static final CloseableHttpClient httpClient;

	/**
	 * 编码
	 */
	public static final String CHARSET = "UTF-8";

	// 采用静态代码块，初始化超时时间配置，再根据配置生成默认httpClient对象
	static {
		RequestConfig config = RequestConfig.custom().setConnectTimeout(60000).setSocketTimeout(60000).build();
		httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
	}

	/**
	 * GET 请求
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	public static JSONObject doGet(String url, Map<String, String> params) {
		return doGet(url, params, CHARSET);
	}

	/**
	 * POST 请求
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws IOException
	 */
	public static JSONObject doPost(String url, Map<String, String> params) throws IOException {
		return doPost(url, params, CHARSET);
	}

	/**
	 * HTTP Get 获取内容
	 * 
	 * @param url
	 *            请求的url地址 ?之前的地址
	 * @param params
	 *            请求的参数
	 * @param charset
	 *            编码格式
	 * @return 页面内容
	 */
	public static JSONObject doGet(String url, Map<String, String> params, String charset) {
		if (StringUtils.isBlank(url)) {
			return null;
		}
		try {
			if (params != null && !params.isEmpty()) {
				List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
				for (Map.Entry<String, String> entry : params.entrySet()) {
					String value = entry.getValue();
					if (value != null) {
						pairs.add(new BasicNameValuePair(entry.getKey(), value));
					}
				}
				// 将请求参数和url进行拼接
				url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, charset));
			}
			HttpGet httpGet = new HttpGet(url);
			CloseableHttpResponse response = httpClient.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				httpGet.abort();
				throw new RuntimeException("HttpClient,error status code :" + statusCode);
			}
			HttpEntity entity = response.getEntity();
			JSONObject jo = null;
			if (entity != null) {
				jo = new JSONObject(EntityUtils.toString(entity));
			}
			EntityUtils.consume(entity);
			response.close();
			return jo;
		} catch (Exception e) {
			log.error("doGet is error!", e);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * HTTP Post 获取内容
	 * 
	 * @param url
	 *            请求的url地址 ?之前的地址
	 * @param params
	 *            请求的参数
	 * @param charset
	 *            编码格式
	 * @return 页面内容
	 * @throws IOException
	 */
	public static JSONObject doPost(String url, Map<String, String> params, String charset) throws IOException {
		if (StringUtils.isBlank(url)) {
			return null;
		}
		List<NameValuePair> pairs = null;
		if (params != null && !params.isEmpty()) {
			pairs = new ArrayList<NameValuePair>(params.size());
			for (Map.Entry<String, String> entry : params.entrySet()) {
				String value = entry.getValue();
				if (value != null) {
					pairs.add(new BasicNameValuePair(entry.getKey(), value));
				}
			}
		}
		HttpPost httpPost = new HttpPost(url);
		if (pairs != null && pairs.size() > 0) {
			httpPost.setEntity(new UrlEncodedFormEntity(pairs, CHARSET));
		}
		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				httpPost.abort();
				throw new RuntimeException("HttpClient,error status code :" + statusCode);
			}
			HttpEntity entity = response.getEntity();
			JSONObject jo = null;
			if (entity != null) {
				jo = new JSONObject(EntityUtils.toString(entity));
			}
			EntityUtils.consume(entity);
			return jo;
		} catch (ParseException e) {
			log.error("doPost is error!", e);
			e.printStackTrace();
		} finally {
			if (response != null)
				response.close();
		}
		return null;
	}

	/**
	 * HTTP Get 下载文件
	 * @param string
	 * @return
	 */
	public static HttpEntity download(String path) {
		HttpEntity entity = null;
        HttpGet httpGet = new HttpGet(path);
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
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
}
