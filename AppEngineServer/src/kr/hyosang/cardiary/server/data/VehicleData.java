package kr.hyosang.cardiary.server.data;

import java.util.ArrayList;
import java.util.List;

import kr.hyosang.cardiary.server.data.entity.VehicleInfo;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class VehicleData extends BaseData {
	public static final String KIND_NAME = "Vehicle";
	
	public static final String ENTITY_USER = "user";
	public static final String ENTITY_VIN = "vin";
	public static final String ENTITY_VENDOR = "vendor";
	public static final String ENTITY_MODEL = "model";
	public static final String ENTITY_YEAR = "year";
	
	public static List<VehicleInfo> getList(String email) {
		obtainService();
		
		Query query = new Query(KIND_NAME);
		
		FilterPredicate filter = new FilterPredicate(ENTITY_USER, FilterOperator.EQUAL, email);
		query.setFilter(filter);
		
		PreparedQuery pq = mService.prepare(query);

		ArrayList<VehicleInfo> list = new ArrayList<>();
		for(Entity e : pq.asIterable()) {
			list.add(new VehicleInfo(e));
		}
		
		return list;
	}
	
	public static boolean save(VehicleInfo info) {
		obtainService();
		
		mService.put(info.asEntity());
		
		return true;
		
	}
	
	public static VehicleInfo getVehicleByVin(String vin) {
		obtainService();
		
		Key key = KeyFactory.createKey(KIND_NAME, vin);
		
		Query query = new Query(KIND_NAME, key);
		
		PreparedQuery pq = mService.prepare(query);
		
		return new VehicleInfo(pq.asSingleEntity());
		
		
	}
}
