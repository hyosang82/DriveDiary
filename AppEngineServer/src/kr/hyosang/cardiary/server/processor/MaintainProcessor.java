package kr.hyosang.cardiary.server.processor;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.hyosang.cardiary.server.data.MaintainData;
import kr.hyosang.cardiary.server.data.MaintainPartData;
import kr.hyosang.cardiary.server.data.PartItemData;
import kr.hyosang.cardiary.server.data.entity.MaintainInfo;
import kr.hyosang.cardiary.server.data.entity.PartItemInfo;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONWriter;

@SuppressWarnings("serial")
public class MaintainProcessor extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String uri = req.getRequestURI();
		String [] arr = uri.split("/");
		if(arr != null && arr.length > 0) {
			uri = arr[arr.length-1];
		}
		
		log("[MAINTAIN] " + uri);
		
		if("AddPartsItem".equals(uri)) {
			addPartsItem(req, resp);			
		}else if("GetPartsList".equals(uri)) {
			getPartList(req, resp);
		}else if("SaveMaintenance".equals(uri)) {
			saveMaintenance(req, resp);
		}
	}
	
	private void addPartsItem(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log("Add maintenance part item...");
		
		String name = req.getParameter("itemName");
		String respData = "OK";
		
		if(name == null || name.length() == 0) {
			respData = "ERROR: No parameter";
		}else {
			if(PartItemData.isExists(name)) {
				respData = "ERROR: Already exists : " + name;
			}else {
				PartItemData.addItem(name);
			}			
		}
		
		resp.getWriter().write(respData);
	}
	
	private void getPartList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log("Get parts item");
		
		List<PartItemInfo> list = PartItemData.getList();
		
		try {
			JSONWriter jw = new JSONWriter(resp.getWriter());
			jw.array();
			
			for(PartItemInfo info : list) {
				jw.object();
				jw.key("id").value(info.getId());
				jw.key("name").value(URLEncoder.encode(info.name, "UTF-8"));
				jw.endObject();
			}
			
			jw.endArray();
		}catch(JSONException e) {
			log(e.getLocalizedMessage());
		}
	}
	
	private void saveMaintenance(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log("Save maintenance...");
		
		MaintainInfo info = new MaintainInfo();
		info.setOdo(req.getParameter("inputOdo"));
		info.garage = req.getParameter("inputGarageAdd");
		info.memo = req.getParameter("inputMemo");
		info.vin = req.getParameter("inputVehicle");
		info.date = req.getParameter("inputDate");
		
		MaintainData.save(info);
		
		//항목 저장
		String parts = req.getParameter("inputParts");
		if(parts != null && parts.length() > 0) {
			String [] pList = parts.split("\\|");
			for(String p : pList) {
				if(p != null && p.length() > 0) {
					MaintainPartData.savePart(info.getKey(), p);
					log("Save mainkey=" + info.getKey() + ", part key=" + p);
				}
			}
		}
		
		resp.sendRedirect("/?page=4");
	}


}
