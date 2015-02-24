package kr.hyosang.cardiary.server.processor;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.hyosang.cardiary.server.data.FuelData;
import kr.hyosang.cardiary.server.data.StationData;
import kr.hyosang.cardiary.server.data.entity.FuelInfo;
import kr.hyosang.cardiary.server.data.entity.StationInfo;

@SuppressWarnings("serial")
public class FuelProcessor extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String uri = req.getRequestURI();
		String [] arr = uri.split("/");
		if(arr != null && arr.length > 0) {
			uri = arr[arr.length-1];
		}
		
		log("[FUEL] " + uri);
		
		if("SaveStation".equals(uri)) {
			saveStation(req, resp);			
		}else if("SaveFuelLog".equals(uri)) {
			saveFuelLog(req, resp);
		}
	}
	
	private void saveStation(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log("Save process...");
		
		StationInfo data = new StationInfo();
		data.name = req.getParameter("inputName");
		data.company = req.getParameter("inputCompany");
		
		StationData.save(data);
		
		resp.sendRedirect("/?page=22");
	}
	
	private void saveFuelLog(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String vin = req.getParameter("inputVehicle");
		
		FuelInfo info = new FuelInfo();
		info.setOdo(req.getParameter("inputOdo"));
		info.setUnitPrice(req.getParameter("inputPrice"));
		info.setTotalPrice(req.getParameter("inputTotalPrice"));
		info.setVolume(req.getParameter("inputVolume"));
		info.setIsFull(req.getParameter("inputIsFull"));
		info.date = req.getParameter("inputDate");
		info.station = req.getParameter("inputStation");
		
		FuelData.save(vin, info);
		
		resp.sendRedirect("/?page=2");
	}

}
