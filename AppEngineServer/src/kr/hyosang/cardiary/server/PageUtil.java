package kr.hyosang.cardiary.server;

import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class PageUtil {
	public static final String KIND_PAGE = "PageInfo";
	
	public static final String PROP_TITLE = "pageTitle";
	public static final String PROP_INC_PATH = "includePath";
	public static final String PROP_SIDEBAR_INDEX = "sidebarIndex";
	public static final String PROP_LOGIN_REQUIRED = "login_required";
	
	public static final int SIDEBAR_MAIN = 0;
	public static final int SIDEBAR_VEHICLE = 1;
	public static final int SIDEBAR_FUEL = 2;
	public static final int SIDEBAR_LOG = 3;
	public static final int SIDEBAR_MAINTAIN = 4;
	
	public static boolean addPageSet(List<PageInfo> list) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		for(PageInfo info : list) {
			Key pageKey = KeyFactory.createKey(KIND_PAGE, info.pageId);

			Entity entity = new Entity(pageKey);
			entity.setProperty(PROP_TITLE, info.pageTitle);
			entity.setProperty(PROP_INC_PATH, info.includePath);
			entity.setProperty(PROP_SIDEBAR_INDEX, info.sidebarIndex);
			entity.setProperty(PROP_LOGIN_REQUIRED, info.bLoginRequired);
			
			datastore.put(entity);
		}
		
		return true;
		
	}
	
	public static PageInfo getPageInfo(String pageId) {
		if(pageId == null) {
			//�몃뜳���섏씠吏�
			pageId = "0";
		}
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		Key key = KeyFactory.createKey(KIND_PAGE, pageId);
		
		PageInfo pi = new PageInfo();
		try {
			Entity entity = datastore.get(key);
			
			pi.pageId = String.valueOf(entity.getKey().getName());
			pi.pageTitle = (String)entity.getProperty(PROP_TITLE);
			pi.includePath = (String)entity.getProperty(PROP_INC_PATH);
			pi.sidebarIndex = (int)(long)entity.getProperty(PROP_SIDEBAR_INDEX);
			pi.bLoginRequired = (boolean)(Boolean)entity.getProperty(PROP_LOGIN_REQUIRED);
		}catch(EntityNotFoundException e) {
			return getErrorPage(404);
		}
		
		return pi;
		
	}
	
	public static PageInfo getErrorPage(int httpError) {
		PageInfo info = new PageInfo();
		info.pageId = "_ERROR_" + httpError;
		info.sidebarIndex = 0;
		info.bLoginRequired = false;
		
		switch(httpError) {
		case 401:
			info.pageTitle = "Unauthorized";
			info.includePath = "/WEB-INF/include/401.jsp";
			break;
					
		case 404:
			info.pageTitle = "Not Found";
			info.includePath = "/WEB-INF/include/404.jsp";
			break;
			
		default:
			info.pageTitle = "Page unknown";
			info.includePath = "/WEB-INF/include/404.jsp";
			break;
				
		}
		
		return info;
		
		
	}
	
	
	
	public static class PageInfo {
		public String pageId;
		public String pageTitle;
		public String includePath;
		public int sidebarIndex;
		public boolean bLoginRequired = false;
		
		public PageInfo() { }
		
		public PageInfo(String pid, String ptitle, String incPath, int sidx, boolean bLogin) {
			this.pageId = pid;
			this.pageTitle = ptitle;
			this.includePath = incPath;
			this.sidebarIndex = sidx;
			this.bLoginRequired = bLogin;
		}
		
		public boolean isMain() {
			return (this.sidebarIndex == SIDEBAR_MAIN);
		}
		
		public boolean isVehicle() {
			return (this.sidebarIndex == SIDEBAR_VEHICLE);
		}
		
		public boolean isFuel() {
			return (this.sidebarIndex == SIDEBAR_FUEL);
		}
		
		public boolean isLog() {
			return (this.sidebarIndex == SIDEBAR_LOG);
		}
		
		public boolean isMaintain() {
			return (this.sidebarIndex == SIDEBAR_MAINTAIN);
		}
		
	}
}
