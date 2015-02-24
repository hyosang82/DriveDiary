package kr.hyosang.cardiary.server;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.hyosang.cardiary.server.PageUtil.PageInfo;

public class PageGenerator extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		ArrayList<PageInfo> list = new ArrayList<>();
		list.add(new PageInfo("0", "Main", "/WEB-INF/include/main.jsp", PageUtil.SIDEBAR_MAIN, false));
		list.add(new PageInfo("1", "Vehicle", "/WEB-INF/include/vehicle/list.jsp", PageUtil.SIDEBAR_VEHICLE, true));
		list.add(new PageInfo("11", "Add vehicle", "/WEB-INF/include/vehicle/add.jsp", PageUtil.SIDEBAR_VEHICLE, true));
		list.add(new PageInfo("2", "Fuel", "/WEB-INF/include/fuel/list.jsp", PageUtil.SIDEBAR_FUEL, true));
		list.add(new PageInfo("21", "Add Fuel log", "/WEB-INF/include/fuel/add.jsp", PageUtil.SIDEBAR_FUEL, true));
		list.add(new PageInfo("22", "Manage Stations","/WEB-INF/include/fuel/station.jsp", PageUtil.SIDEBAR_FUEL, true));
		list.add(new PageInfo("3", "Track Log", "/WEB-INF/include/log/list.jsp", PageUtil.SIDEBAR_LOG, true));
		list.add(new PageInfo("31", "Track Detail", "/WEB-INF/include/log/detail.jsp", PageUtil.SIDEBAR_LOG, true));
		list.add(new PageInfo("4", "Maintenance", "/WEB-INF/include/maintain/list.jsp", PageUtil.SIDEBAR_MAINTAIN, true));
		list.add(new PageInfo("41", "Add maintenance", "/WEB-INF/include/maintain/add.jsp", PageUtil.SIDEBAR_MAINTAIN, true));
		
		if(PageUtil.addPageSet(list)) {
			resp.getWriter().write("OK");
		}else {
			resp.getWriter().write("FAIL");
		}
	}
}
