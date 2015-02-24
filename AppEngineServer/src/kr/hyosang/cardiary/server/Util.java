package kr.hyosang.cardiary.server;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class Util {
	public static String getFormattedDateTime(long ts) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT+0900"));
		
		return sdf.format(new Date(ts));
	}
	
	public static String getLoggedUserEmail() {
		UserService userService = UserServiceFactory.getUserService();
		User currUser = userService.getCurrentUser();
		if(currUser != null) {
			return currUser.getEmail();
		}
		
		return null;
	}
	
	public static String join(List<?> list, String delim) {
		StringBuffer sb = new StringBuffer();
		
		if(list != null && list.size() > 0) {
			sb.append(list.get(0).toString());
			
			for(int i=1;i<list.size();i++) {
				sb.append(delim).append(list.get(i).toString());
			}
		}
		
		return sb.toString();
	}
	
	public static int parseInt(String val) {
		try {
			return Integer.parseInt(val, 10);
		}catch(Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public static long parseLong(String val) {
		try {
			return Long.parseLong(val, 10);
		}catch(Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public static float parseFloat(String val) {
		try {
			return Float.parseFloat(val);
		}catch(Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public static double parseDouble(String val) {
		try {
			return Double.parseDouble(val);
		}catch(Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

}
