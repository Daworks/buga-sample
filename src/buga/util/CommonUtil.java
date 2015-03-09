package buga.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CommonUtil {
	
	public static String getRandomKey() {
		Calendar cal 		= Calendar.getInstance();
		Date date 			= new Date();
		SimpleDateFormat fm = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		
		cal.setTime(date);
		
		String now 		= fm.format(date);
		String part1 	= "M" + now.substring(2);
		long  part2 	= CommonUtil.random(6);
		
		return part1 + part2;
	}
	
	public static long random(int length){
		if(Math.pow(10, length) > Long.MAX_VALUE){
			return -1;
		}
		if(length <= 0)
			return 0;
		else
			return randomRange((long) Math.pow(10, length - 1), (long) (Math.pow(10, length) - 1));
	}
	
	public static long randomRange(long min, long max){
		return (long)(Math.random() * (max - min + 1) + min);
	}
	
}
