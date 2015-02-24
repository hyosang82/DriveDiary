package kr.hyosang.cardiary.server.processor;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.hyosang.cardiary.server.Util;
import kr.hyosang.cardiary.server.data.VehicleData;
import kr.hyosang.cardiary.server.data.entity.VehicleInfo;

@SuppressWarnings("serial")
public class VehicleProcessor extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String uri = req.getRequestURI();
		String [] arr = uri.split("/");
		if(arr != null && arr.length > 0) {
			uri = arr[arr.length-1];
		}
		
		log("[VEHICLE] " + uri);
		
		if("SaveVehicle".equals(uri)) {
			saveVehicle(req, resp);			
		}
	}
	
	private void saveVehicle(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log("Save process...");
		
		String user = Util.getLoggedUserEmail();
		if(user == null) {
			resp.sendError(500);
		}
		
		VehicleInfo info = new VehicleInfo();
		info.vin = req.getParameter("inputVin");
		info.vendor = req.getParameter("inputVendor");
		info.model = req.getParameter("inputModel");
		info.year = req.getParameter("inputYear");
		info.user = user;
		
		VehicleData.save(info);
		
		resp.sendRedirect("/?page=1");
		
	}

}
