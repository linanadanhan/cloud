package com.gsoft.cos3.util;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class MsgUtils {
	
//	public static void sendMsg(String server,String key,String value) {
//		CloseableHttpClient httpCilent = HttpClients.createDefault();
//		HttpGet httpGet = new HttpGet(server + "/" + key + "/" + value);
//		try {
//			 HttpResponse httpResponse = httpCilent.execute(httpGet);
//			 if(httpResponse.getStatusLine().getStatusCode() == 200){
//				 	System.out.println("请求成功！");
//	            }else if(httpResponse.getStatusLine().getStatusCode() == 400){
//	            	System.out.println("400错误");
//	            }else if(httpResponse.getStatusLine().getStatusCode() == 500){
//	            	System.out.println("500错误");
//	            }
//		} catch (IOException e) {
//		    e.printStackTrace();
//		}finally {
//		    try {
//		        httpCilent.close();//释放资源
//		    } catch (IOException e) {
//		        e.printStackTrace();
//		    }
//		}
//	}
	
	public static void noticeMsgCenter(String server,String key,String userId,boolean isDto) {
		CloseableHttpClient httpCilent = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(server + "/" + key + "/" + userId + "/" + isDto );
		try {
			 HttpResponse httpResponse = httpCilent.execute(httpGet);
			 if(httpResponse.getStatusLine().getStatusCode() == 200){
				 	System.out.println("请求成功！");
	            }else if(httpResponse.getStatusLine().getStatusCode() == 400){
	            	System.out.println("400错误");
	            }else if(httpResponse.getStatusLine().getStatusCode() == 500){
	            	System.out.println("500错误");
	            }
		} catch (IOException e) {
		    e.printStackTrace();
		}finally {
		    try {
		        httpCilent.close();//释放资源
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}
	}

}
