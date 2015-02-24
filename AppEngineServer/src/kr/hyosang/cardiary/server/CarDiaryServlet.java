package kr.hyosang.cardiary.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.hyosang.cardiary.server.data.TrackLog;
import kr.hyosang.cardiary.server.data.TrackLog.TrackLogData;
import kr.hyosang.cardiary.server.data.VehicleData;
import kr.hyosang.cardiary.server.data.entity.VehicleInfo;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Text;

@SuppressWarnings("serial")
public class CarDiaryServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		//작업용 서블릿
		PrintWriter writer = resp.getWriter();
		
		DatastoreService dss = DatastoreServiceFactory.getDatastoreService();
		
		Query query = new Query("Vehicle");
		Key key = KeyFactory.createKey("Vehicle", "KNMC4C2HM8P093510");
		
			Entity e = new Entity(key);
			e.setProperty("user", "hy054nz@gmail.com");
			e.setProperty("vendor", "Renault Samsung");
			e.setProperty("year", "2008");
			e.setProperty("model", "SM3");
			
			dss.put(e);
		
		/*
		VehicleInfo v = VehicleData.getVehicleByVin("KNMC4C2HM8P093510");
		
		Query query = new Query("Maintenance");
		PreparedQuery pq = dss.prepare(query);
		
		for(Entity me : pq.asIterable()) {
			String mainKey = String.format("%s_%d", me.getProperty("vin"), me.getProperty("odo"));
			
			Entity nme = new Entity("Maintenance", v.key);
			nme.setProperty("date", me.getProperty("date"));
			nme.setProperty("garage", me.getProperty("garage"));
			nme.setProperty("memo", me.getProperty("memo"));
			nme.setProperty("odo", me.getProperty("odo"));
			nme.setProperty("price", me.getProperty("price"));
			
			Key newKey = dss.put(nme);

			Query query2 = new Query("MaintenancePart");
			FilterPredicate filter2 = new FilterPredicate("main_key", FilterOperator.EQUAL, mainKey);
			query2.setFilter(filter2);
			PreparedQuery pq2 = dss.prepare(query2);
			
			for(Entity mpe : pq2.asIterable()) {
				Entity nmpe = new Entity("MaintenancePart", newKey);
				nmpe.setProperty("part_key", mpe.getProperty("part_key"));
				dss.put(nmpe);
			}
		}
		*/
		
		
		//FilterPredicate filter = new FilterPredicate("vin", FilterOperator.EQUAL, "");
		//query.setFilter(filter);
		/*
		PreparedQuery pq = dss.prepare(query);
		
		VehicleInfo vehicle = VehicleData.getVehicleByVin("KNMC4C2HM8P093510");
		
		for(Entity e : pq.asIterable()) {
			Key k = e.getKey();
			writer.write("id="+k.getId()+", name="+k.getName());
			
			Entity ge = null;
			try {
				ge = dss.get(KeyFactory.createKey("GpsLog", (long)e.getProperty("parent_key")));
			} catch (EntityNotFoundException e1) {
			}
			
			if(ge != null) {
				long ts = (long)ge.getProperty("time_key");
				TrackLog log = TrackLog.getByTimestamp("KNMC4C2HM8P093510", ts);
				if(log != null) {
					Entity tag_e = TrackLog.Tag.createEntity(log.key, (String)e.getProperty("tag_name"));
					dss.put(tag_e);
				}else {
					writer.write("TrackLog is null");
				}
			}else {
				writer.write("GpsLog is null");
			}
			
			writer.write("<br />");
		}


*/
		


	}
}
