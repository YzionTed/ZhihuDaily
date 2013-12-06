package com.newandbie.fake.zhihudaily.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {

	// 格式化日期用
	private static final SimpleDateFormat yyyyMMdd = new SimpleDateFormat(
			"yyyyMMdd", Locale.US);
	private static final TimeZone TIMEZONE_CHINA = TimeZone
			.getTimeZone("GMT+08");

	/**
	 * 获得今天的日期
	 * 
	 * @return 以yyyyMMdd形式返回
	 */
	public static String getTodayDate() {
		return getDate(System.currentTimeMillis());
	}

	/**
	 * 获取制定时间的日期
	 * 
	 * @param time
	 * @return 以yyyyMMdd形式返回
	 */
	public static String getDate(long time) {
		yyyyMMdd.setTimeZone(TIMEZONE_CHINA);
		return yyyyMMdd.format(new Date(time));
	}
}
