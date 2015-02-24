package kr.hyosang.cardiary.server.data;

import java.util.ArrayList;
import java.util.List;

import kr.hyosang.cardiary.server.data.entity.MaintainInfo;
import kr.hyosang.cardiary.server.data.entity.VehicleInfo;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;

public class MaintainData extends BaseData {
	public static final String KIND_NAME = "Maintenance";
	
	public static final String ENTITY_VIN = "vin";
	public static final String ENTITY_GARAGE = "garage";
	public static final String ENTITY_ODO = "odo";
	public static final String ENTITY_DATE = "date";
	public static final String ENTITY_PRICE = "price";
	public static final String ENTITY_MEMO = "memo";

	public static List<MaintainInfo> getList(String vin) {
		obtainService();
		
		ArrayList<MaintainInfo> list = new ArrayList<>();
		VehicleInfo vinfo = VehicleData.getVehicleByVin(vin);
		
		if(vinfo != null) {
			Query query = new Query(KIND_NAME, vinfo.getKey());
			query.addSort(ENTITY_ODO, SortDirection.DESCENDING);
			
			PreparedQuery pq = mService.prepare(query);
	
			for(Entity e : pq.asIterable()) {
				list.add(new MaintainInfo(e));
			}
		}
		
		return list;
	}
	
	public static void save(MaintainInfo info) {
		obtainService();
		
		VehicleInfo vInfo = VehicleData.getVehicleByVin(info.vin);
		
		Key newKey = mService.put(info.toEntityFromParent(vInfo.getKey()));
		info.setKey(newKey);
	}
	
	public static List<String> getMaintainParts(String parentKey) {
		obtainService();
		
		List<String> list = new ArrayList<>();
		
		Query query = new Query(MaintainPartData.KIND_NAME, KeyFactory.stringToKey(parentKey));
		PreparedQuery pq = mService.prepare(query);
		
		for(Entity e : pq.asIterable()) {
			String partKey = (String)e.getProperty("part_key");
			String part = PartItemData.getPart(partKey);
			
			list.add(part);
		}
		
		return list;
	}
	
	public static List<String> getGarageList(String email) {
		obtainService();

		List<String> list = new ArrayList<>();

		Query query1 = new Query(VehicleData.KIND_NAME);
		FilterPredicate filter1 = new FilterPredicate(VehicleData.ENTITY_USER, FilterOperator.EQUAL, email);
		query1.setFilter(filter1);
		PreparedQuery pq1 = mService.prepare(query1);
		
		for(Entity e1 : pq1.asIterable()) {
			Query query2 = new Query(KIND_NAME, e1.getKey());
			PreparedQuery pq2 = mService.prepare(query2);
			
			for(Entity e2 : pq2.asIterable()) {
				String g = (String)e2.getProperty(ENTITY_GARAGE);
				
				if(!list.contains(g)) {
					list.add(g);
				}
			}
		}
		
		return list;
	}
	

}
