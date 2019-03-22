/**
 * 
 */
package com.gsoft.cos3.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shencq
 * 
 */
public class JsonMapper {
	private static ObjectMapper mapper = new ObjectMapper();

	private static XmlMapper xmlMapper = new XmlMapper(); 
	
	static {
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
				false);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.setSerializationInclusion(Include.NON_NULL);
	}

	/**
	 * 将对象转换成json，并输出
	 * 
	 * @param writer
	 * @param result
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static void writeJson(PrintWriter writer, Object result)
			throws JsonGenerationException, JsonMappingException, IOException {
		mapper.writeValue(writer, result);
	}

	/**
	 * 将对象转换为Json字符串
	 * 
	 * @param value
	 * @return
	 * @throws JsonProcessingException
	 */
	public static String toJson(Object value) throws JsonProcessingException {
		return mapper.writeValueAsString(value);
	}

	/**
	 * 将json字符串转换为指定对象
	 * 
	 * @param json
	 * @param elementClass
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static <T> List<T> fromJsonList(String json, Class<T> elementClass)
			throws JsonParseException, JsonMappingException, IOException {
		JavaType type = mapper.getTypeFactory().constructParametricType(
				ArrayList.class, elementClass);
		return mapper.readValue(json, type);
	}

	/**
	 * 将json字符串转换为指定对象
	 * 
	 * @param json
	 * @param keyClass
	 * @param valueClass
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static <K, T> Map<K, T> fromJsonMap(String json, Class<K> keyClass,
			Class<T> valueClass) throws JsonParseException,
			JsonMappingException, IOException {
		JavaType type = mapper.getTypeFactory().constructParametricType(
				HashMap.class, keyClass, valueClass);
		return mapper.readValue(json, type);
	}
	
	/**
	 * Description：将json字符串转换为Map
	 * 
	 * @param @param json
	 * @param @param javaType
	 * @param @return
	 * @param @throws JsonParseException
	 * @param @throws JsonMappingException
	 * @param @throws IOException 入参描述
	 * @return Map<K,T> 返回值描述 *
	 * @Exception 异常描述
	 */
	@SuppressWarnings("deprecation")
	public static <K, T> Map<K, T> fromJsonComplexMap(String json,
			Class<?> c) throws JsonParseException, JsonMappingException,
			IOException {
		JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, c);
		JavaType type = mapper.getTypeFactory().constructMapType(HashMap.class,
				SimpleType.construct(String.class), javaType);
		return  mapper.readValue(json, type);
	}

	/**
	 * 将json字符串转换为指定对象
	 * 
	 * @param json
	 * @param keyClass
	 * @param valueClass
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static <T> T fromJson(String json, Class<T> typeClass)
			throws JsonParseException, JsonMappingException, IOException {
		return mapper.readValue(json, typeClass);
	}

	/**
	 * 将json字符串转换为指定对象
	 * 
	 * @param json
	 * @param type
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static <T> T fromJson(String json, JavaType type)
			throws JsonParseException, JsonMappingException, IOException {
		return mapper.readValue(json, type);
	}

	/**
	 * 创建泛型类型对象
	 * 
	 * @param parametrized
	 * @param parameterClasses
	 * @return
	 */
	public static JavaType constructParametricType(Class<?> parametrized,
			Class<?>... parameterClasses) {
		return mapper.getTypeFactory().constructParametricType(parametrized,
				parameterClasses);
	}

	/**
	 * 创建泛型类型对象
	 * 
	 * @param parametrized
	 * @param parameterClasses
	 * @return
	 */
	public static JavaType constructParametricType(Class<?> parametrized,
			JavaType... parameterTypes) {
		return mapper.getTypeFactory().constructParametricType(parametrized,
				parameterTypes);
	}

	 /** 
     * json string convert to xml string 
     */  
    public static String json2xml(String jsonStr)throws Exception{  
        JsonNode root = mapper.readTree(jsonStr);  
        String xml = xmlMapper.writeValueAsString(root);  
        return xml;  
    }  
      
    /** 
     * xml string convert to json string 
     */  
    public static String xml2json(String xml)throws Exception{  
        StringWriter w = new StringWriter();  
        JsonParser jp = xmlMapper.getFactory().createParser(xml);  
        JsonGenerator jg = mapper.getFactory().createGenerator(w);  
        while (jp.nextToken() != null) {  
            jg.copyCurrentEvent(jp);  
        }  
        jp.close();  
        jg.close();  
        return w.toString();  
    }  
}
