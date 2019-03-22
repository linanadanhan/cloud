package com.gsoft.cos3.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MathUtils {
	/**
	 * 判断传入的字符串是否为数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	/**
	 * 传入的对象转换成long
	 * 
	 * @param obj
	 * @return
	 */
	public static Long numObj2Long(Object obj) {
		if (obj instanceof Number) {
			return Number.class.cast(obj).longValue();
		} else if (obj instanceof String) {
			return Long.valueOf((String) obj);
		} else if (obj instanceof Timestamp){
			return Timestamp.class.cast(obj).getTime();
		}
		return null;
	}
	
	/**
	 * 传入的对象转换成long
	 * 
	 * @param obj
	 * @return emtyVal
	 */
	public static Long numObj2Long(Object obj, Long emtyVal) {
		try {
			if (obj instanceof Number) {
				return Number.class.cast(obj).longValue();
			} else if (obj instanceof String) {
				return Long.valueOf((String) obj);
			} else if (obj instanceof Timestamp){
				return Timestamp.class.cast(obj).getTime();
			}
		} catch(Exception e) {
			return emtyVal;
		}
		return emtyVal;
	}

	/**
	 * 传入的对象转换成boolean
	 * @param obj
	 * @return
	 */
	public static boolean obj2Boolean(Object obj){
		if(obj instanceof Boolean) {
			return Boolean.class.cast(obj);
		}else if(obj instanceof String){
			return Boolean.parseBoolean((String) obj);
		}else if(obj instanceof Number){
			return Number.class.cast(obj).intValue() != 0;
		}
		return false;
	}

	/**
	 * 对象转换为Boolean
	 * @param isLeaf
	 * @return
	 */
	public static Boolean booleanValueOf(Object obj) {
		try {
			return (Boolean) obj;
		} catch (Exception e) {
			return "1".equals(String.valueOf(obj)) || "T".equals(String.valueOf(obj));
		}
	}

	/**
	 * 传入的对象转成integer
	 * @param obj
	 * @return
	 */
	public static Integer numObj2Integer(Object obj) {
		if (obj instanceof Number) {
			return Number.class.cast(obj).intValue();
		} else if (obj instanceof String) {
			return Integer.valueOf((String) obj);
		}
		return null;
	}

	public static Integer numObj2Integer(Object obj,Integer emtyVal) {
		Integer result = numObj2Integer(obj);
		if(result == null) {
			return emtyVal;
		}
		return result;
	}
	
	   public static BigDecimal numObj2BigDecimal(Object value) {
	        BigDecimal ret = new BigDecimal(0);
	        if( value != null ) {
	            if( value instanceof BigDecimal ) {
	                ret = (BigDecimal) value;
	            } else if( value instanceof String ) {
	                ret = new BigDecimal( (String) value );
	            } else if( value instanceof BigInteger ) {
	                ret = new BigDecimal( (BigInteger) value );
	            } else if( value instanceof Number ) {
	                ret = new BigDecimal( ((Number)value).doubleValue());
	            }
	        }
	        return ret;
	    }
	   
	   /**
	    * bigdecimal的两个数相加 --加法
	    * @param bignum1
	    * @param bignum2
	    * @return
	    */
	   public static BigDecimal bigDeciamlToAdd(BigDecimal bignum1,BigDecimal bignum2) {
		   return bignum1.add(bignum2);
	   }
	   
	   /**
	    * bigdecimal的两个数相减 --减法
	    * @param bignum1
	    * @param bignum2
	    * @return
	    */
	   public static BigDecimal bigDeciamlToSubtract(BigDecimal bignum1,BigDecimal bignum2) {
		   return bignum1.subtract(bignum2);
	   }
	   
	   /**
	    * bigdecimal的两个数--乘法  
	    * @param bignum1
	    * @param bignum2
	    * @return
	    */
	   public static BigDecimal bigDeciamlToMultiply(BigDecimal bignum1,BigDecimal bignum2) {
		   return bignum1.multiply(bignum2);
	   }
	   
	   
	   /**
	    * bigdecimal的两个数除法--除法
	    * @param bignum1
	    * @param bignum2
	    * @return
	    */
	   public static BigDecimal bigDeciamlToDivide(BigDecimal bignum1,BigDecimal bignum2) {
		   return bignum1.divide(bignum2,4, BigDecimal.ROUND_HALF_UP);
	   }
	   
	   public static String stringObj(Object obj) {
		   return (Assert.isEmpty(obj)) ? "" : obj.toString();
	   }

	   public static List<Long> toList(String ids){
	   	if(Assert.isEmpty(ids)){
	   		return new ArrayList<Long>();
		}
		String[] arr=ids.split(",");
		List<Long> list=new ArrayList<>();
		for(String s:arr){
			if(Assert.isNotEmpty(s)){
				list.add(Long.valueOf(s));
			}
		}
		return list;
	}

}
