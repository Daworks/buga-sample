package buga.util.logger;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BugaLoggerImpl implements BugaLogger{
	
	@Override
	public void log(String msg) {
		System.out.println(getTime() + " " + msg);
	}
	
	public static String getTime(){
		String name = Thread.currentThread().getName();
		SimpleDateFormat f = new SimpleDateFormat("[yyyy/MM/dd hh:mm:ss]");
		return f.format(new Date()) + name;
	}

}
