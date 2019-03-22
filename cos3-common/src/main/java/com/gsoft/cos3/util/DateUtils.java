/**
 * 
 */
package com.gsoft.cos3.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

import com.gsoft.cos3.exception.BusinessException;

/**
 * 日期相关工具
 * 
 * @author shencq
 * 
 */
public class DateUtils extends DateFormatUtils {

	private static final String[] DEFAULT_DATE_PATTERNS = new String[] { "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm",
			"yyyy-MM-dd", "yyyy-MM", "yyyy" };

	/**
	 * 解析日期字符串
	 * 
	 * @param date
	 * @param parsePatterns
	 * @return
	 * @throws ParseException
	 */
	public static Date parseDate(String date, String... parsePatterns) throws ParseException {
		return org.apache.commons.lang3.time.DateUtils.parseDate(date, parsePatterns);
	}

	/**
	 * 解析日期字符串
	 * 
	 * @param date
	 * @param parsePatterns
	 * @return
	 */
	public static Date parseDate(String date, String parsePatterns) {
		if (Assert.isEmpty(date)) {
			return null;
		}
		try {
			return org.apache.commons.lang3.time.DateUtils.parseDate(date, parsePatterns);
		} catch (ParseException e) {
			throw new BusinessException("日期字符串解析错误", e);
		}
	}

	/**
	 * 采用默认的时间格式解析日期字符串
	 * 
	 * @param date
	 * @return
	 */
	public static Date parseDate(Object date) {
		if (date == null) {
			return null;
		}
		if (date instanceof String && ((String) date).length() > 0) {
			try {
				return parseDate((String) date, DEFAULT_DATE_PATTERNS);
			} catch (ParseException e) {
				throw new BusinessException("日期字符串解析错误", e);
			}
		}
		if (date instanceof Long) {
			return new Date((Long) date);
		}
		throw new BusinessException("无法解析为正确的日期");
	}

	/**
	 * 2016-6-1 23:59:59
	 * 
	 * @param sYear
	 * @param sMonth
	 * @param sDate
	 * @return
	 */
	public static Date combineDateEnd(String sYear, String sMonth, String sDate) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, Integer.parseInt(sYear));
		cal.set(Calendar.MONTH, Integer.parseInt(sMonth) - 1);
		cal.set(Calendar.DATE, Integer.parseInt(sDate));
		setEndTime(cal);
		return cal.getTime();
	}

	/**
	 * 2016-6-1 00:00:00
	 * 
	 * @param sYear
	 * @param sMonth
	 * @param sDate
	 * @return
	 */
	public static Date combineDateStart(String sYear, String sMonth, String sDate) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, Integer.parseInt(sYear));
		cal.set(Calendar.MONTH, Integer.parseInt(sMonth) - 1);
		cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(sDate));
		setStartTime(cal);
		return cal.getTime();
	}

	/**
	 * Set the calendar time to 23:59:59
	 */
	public static void setEndTime(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
	}

	public static void setStartTime(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
	}

	/**
	 * 得到本週一的
	 * 
	 * @param sYear
	 * @param sMonth
	 * @param sDate
	 * @return
	 * @throws ParseException
	 */
	public static Date monday() {
		Calendar cal = Calendar.getInstance();
		int day_of_week = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (day_of_week == 0) {
			day_of_week = 7;
			cal.add(Calendar.DATE, -day_of_week + 1);
		} else {
			cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		}
		setStartTime(cal);
		return cal.getTime();
	}

	/**
	 * 得到本週日的
	 * 
	 * @param sYear
	 * @param sMonth
	 * @param sDate
	 * @return
	 * @throws ParseException
	 */
	public static Date sunday() {
		Calendar cal = Calendar.getInstance();
		int day_of_week = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (day_of_week == 0) {
			day_of_week = 7;
			cal.add(Calendar.DATE, -day_of_week + 7);
		} else {
			cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			cal.add(Calendar.WEEK_OF_YEAR, 1);

		}
		setEndTime(cal);
		return cal.getTime();
	}

	/**
	 * 得到本月 yyyy-MM
	 * 
	 * @return
	 */
	public static String month() {
		Calendar c = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
		String time = format.format(c.getTime());
		return time;
	}

	/**
	 * 得到本月 yyyyMM
	 * 
	 * @return
	 */
	public static String getYearAdnMonth() {
		Calendar c = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
		String time = format.format(c.getTime());
		return time;
	}

	/**
	 * 得到今天 yyyy-MM-dd
	 * 
	 * @return
	 */
	public static String newDate() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
		String date = df.format(new Date());
		return date;
	}

	/**
	 * 得到今天 yyyyMMdd
	 * 
	 * @return
	 */
	public static String getDay() {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");// 设置日期格式
		String date = df.format(new Date());
		return date;
	}

	/**
	 * 得到今年
	 * 
	 * @return
	 */
	public static Integer getYear(Date date) {
		if (date == null) {
			date = new Date();
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy");// 设置日期格式
		String year = df.format(date);
		return Integer.valueOf(year);
	}

	/**
	 * 得到本月 MM
	 * 
	 * @return
	 */
	public static Integer getMonth(Date date) {
		if (date == null) {
			date = new Date();
		}
		SimpleDateFormat df = new SimpleDateFormat("MM");// 设置日期格式
		String month = df.format(date);
		return Integer.valueOf(month);
	}

	/**
	 * 得到今年
	 * 
	 * @return
	 */
	public static Integer getYear() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy");// 设置日期格式
		String year = df.format(new Date());
		return Integer.valueOf(year);
	}

	/**
	 * 得到本月 MM
	 * 
	 * @return
	 */
	public static Integer getMonth() {
		SimpleDateFormat df = new SimpleDateFormat("MM");// 设置日期格式
		String month = df.format(new Date());
		return Integer.valueOf(month);
	}

	/**
	 * 本月第一天
	 * 
	 * @return
	 */
	public static String firstDay() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
		Calendar cal_1 = Calendar.getInstance();// 获取当前日期
		cal_1.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
		return df.format(cal_1.getTime());
	}

	/**
	 * 判断时间是否在时间段内
	 * 
	 * @param nowTime
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	public static boolean belongCalendar(Date nowTime, Date beginTime, Date endTime) {
		Calendar date = Calendar.getInstance();
		date.setTime(nowTime);
		Calendar begin = Calendar.getInstance();
		begin.setTime(beginTime);
		Calendar end = Calendar.getInstance();
		end.setTime(endTime);
		if (date.after(begin) && date.before(end)) {
			return true;
		} else if (nowTime.compareTo(beginTime) == 0 || nowTime.compareTo(endTime) == 0) {
			return true;
		} else {
			return false;
		}
	}

}
