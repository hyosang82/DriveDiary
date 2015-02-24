package kr.hyosang.cardiary.server.data;

import java.util.ArrayList;
import java.util.List;

import kr.hyosang.cardiary.server.data.entity.StationInfo;
import kr.hyosang.cardiary.server.data.entity.VehicleInfo;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

public class StationData extends BaseData {
	public static final String KIND_NAME = "Station";
	
	public static final String ENTITY_NAME = "name";
	public static final String ENTITY_COMPANY = "company";
	
	public static List<StationInfo> getList() {
		obtainService();
		
		Query query = new Query(KIND_NAME);
		
		PreparedQuery pq = mService.prepare(query);

		ArrayList<StationInfo> list = new ArrayList<>();
		for(Entity e : pq.asIterable()) {
			list.add(new StationInfo(e));
		}
		
		return list;
	}
	
	public static boolean save(StationInfo info) {
		obtainService();
		
		mService.put(info.toEntityUpdate(info.getKey()));
		
		return true;
		
	}
}
