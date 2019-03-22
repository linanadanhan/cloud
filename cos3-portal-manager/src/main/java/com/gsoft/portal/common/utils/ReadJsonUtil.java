package com.gsoft.portal.common.utils;

import java.io.File;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

/**
 * 读取指定目录json文件
 * @author SN
 *
 */
public class ReadJsonUtil {
	
	public static JSONObject readJsonFile(String tarPath, String fileName) throws Exception {
		
		JSONObject jsonObj = null;
		
		try {
			
			if (!tarPath.endsWith("/")) {
				tarPath = tarPath + "/";
			}
			
			File nFile = new File(tarPath + fileName);
			
			String nContent = FileUtils.readFileToString(nFile, "UTF-8");
			jsonObj = new JSONObject(nContent);
			
		}catch(Exception e) {
			throw e;
		}
		
		return jsonObj;
	}
	
}
