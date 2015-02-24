package kr.hyosang.cardiary.server.processor;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.hyosang.cardiary.server.Util;
import kr.hyosang.cardiary.server.data.BaseData;
import kr.hyosang.cardiary.server.data.TrackLog;
import kr.hyosang.cardiary.server.data.TrackLog.TrackLogData;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONWriter;

@SuppressWarnings("serial")
public class LogProcessor extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String uri = req.getRequestURI();
		String [] arr = uri.split("/");
		if(arr != null && arr.length > 0) {
			uri = arr[arr.length-1];
		}
		
		log("[LOGProc] " + uri);
		
		if("UploadLog".equals(uri)) {
			uploadLog(req, resp);
		}else if("LogUpdate".equals(uri)) {
			logUpdate(req, resp);
		}else if("MergeLog".equals(uri)) {
			mergeLog(req, resp);
		}else if("DeleteLog".equals(uri)) {
			deleteLog(req, resp);
		}else if("AddTag".equals(uri)) {
			addTag(req, resp);
		}else if("GetTag".equals(uri)) {
			getTag(req, resp);
		}
	}
	
	private void uploadLog(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log("Upload log...");
		
		try {
			String vin = req.getHeader("VIN");
			long time_key = Long.parseLong(req.getParameter("time_key"), 10);
			
			TrackLog log = TrackLog.getByTimestamp(vin, time_key);
			if(log == null) {
				log = new TrackLog(time_key);
			}else {
				//로그가 추가되었으므로 길이데이터는 0으로 셋팅
				log.distance = 0;
			}
			log.save(vin);

			
			//로그 데이터도 저장
			TrackLogData logdata = new TrackLogData();
			logdata.logdata = req.getParameter("log_data");
			logdata.timestamp = Long.parseLong(req.getParameter("timestamp"), 10);
			
			logdata.save(log.key);
		}catch(NumberFormatException e) {
			log("Error : " + e);
			
			resp.sendError(500);
			return;
		}
		
		resp.sendError(200);
	}
	
	private void logUpdate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log("Log update...");
		
		String encodedKey = req.getParameter("key");
		double dist = Util.parseDouble(req.getParameter("dist"));
		
		TrackLog track = TrackLog.getByEncodedKey(encodedKey);
		track.distance = dist;
		track.save(null);
	}
	
	private void mergeLog(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log("Merge log..");
		
		String encodedKey = req.getParameter("log_key");
		String result = "FAIL";
		TrackLog track = TrackLog.getByEncodedKey(encodedKey);
		if(track != null) {
			if(TrackLog.mergeToPrevLog(track.key)) {
				result = "OK";
			}
		}
		
		try {
			JSONWriter jw = new JSONWriter(resp.getWriter());
			jw.object();
			jw.key("result");
			jw.value(result);
			jw.endObject();
		}catch(JSONException e) {
			log("ERROR", e);
		}
	}
	
	private void deleteLog(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log("Delete log..");
		
		String encodedKey = req.getParameter("log_key");
		String result = "FAIL";
		TrackLog track = TrackLog.getByEncodedKey(encodedKey);
		
		if(track != null) {
			BaseData.delete(track.key);
			
			result = "OK";
		}
		
		try {
			JSONWriter jw = new JSONWriter(resp.getWriter());
			jw.object();
			jw.key("result");
			jw.value(result);
			jw.endObject();
		}catch(JSONException e) {
			log("ERROR", e);
		}
	}
	
	private void addTag(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log("Add tag...");
		
		String encodedKey = req.getParameter("log_key");
		String tag = req.getParameter("tag");
		
		TrackLog.put(TrackLog.Tag.createEntity(KeyFactory.stringToKey(encodedKey), tag));
	}
	
	private void getTag(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log("Get tag...");
		
		String encodedKey = req.getParameter("log_key");
		
		
		List<String> tagList = TrackLog.Tag.getList(KeyFactory.stringToKey(encodedKey));
		try {
			JSONWriter jw = new JSONWriter(resp.getWriter());
			jw.array();
			
			for(String tag : tagList) {
				jw.value(URLEncoder.encode(tag, "UTF-8"));
			}
			jw.endArray();
		}catch(JSONException e) {
			log("ERROR", e);
		}
		
	}
}
