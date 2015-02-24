package kr.hyosang.cardiary.server.data;

import java.util.ArrayList;
import java.util.List;

import kr.hyosang.cardiary.server.data.entity.PartItemInfo;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;

public class PartItemData extends BaseData {
	public static final String KIND_NAME = "PartItem";
	
	public static final String ENTITY_NAME = "name";
	
	public static boolean isExists(String nm) {
		obtainService();
		
		Query query = new Query(KIND_NAME);
		FilterPredicate filter = new FilterPredicate(ENTITY_NAME, FilterOperator.EQUAL, nm);
		query.setFilter(filter);
		
		PreparedQuery pq = mService.prepare(query);
		
		if(pq.asSingleEntity() == null) {
			return false;
		}else {
			return true;
		}
	}
	
	public static String getPart(String id) {
		obtainService();
		
		Key key = KeyFactory.createKey(KIND_NAME, id);
		
		Query query = new Query(KIND_NAME, key);
		PreparedQuery pq = mService.prepare(query);
		
		if(pq.asSingleEntity() == null) {
			return null;
		}else {
			return (String)pq.asSingleEntity().getProperty("name");
		}
	}
	
	public static boolean addItem(String nm) {
		obtainService();
		
		PartItemInfo item = new PartItemInfo();
		item.name = nm;
		
		mService.put(item.newEntity());
		
		return true;
		
	}
	
	public static List<PartItemInfo> getList() {
		obtainService();
		
		Query query = new Query(KIND_NAME);
		query.addSort(ENTITY_NAME, SortDirection.ASCENDING);
		
		PreparedQuery pq = mService.prepare(query);
		
		List<PartItemInfo> list = new ArrayList<>();
		for(Entity e : pq.asIterable()) {
			list.add(new PartItemInfo(e));
		}
		
		return list;
	}
	
	
	
	/*
	public static List<MaintainInfo> getList(String vin) {
		obtainService();
		
		Query query = new Query(KIND_NAME);
		
		if(vin != null) {
			FilterPredicate filter = new FilterPredicate(ENTITY_VIN, FilterOperator.EQUAL, vin);
			query.setFilter(filter);
			
			query.addSort(ENTITY_ODO, SortDirection.DESCENDING);
		}
		
		PreparedQuery pq = mService.prepare(query);

		ArrayList<MaintainInfo> list = new ArrayList<>();
		for(Entity e : pq.asIterable()) {
			list.add(new MaintainInfo(e));
		}
		
		return list;
	}
	*/
	
	/*
	private static void calcEfficient(String vin) {
		if(vin != null && !vin.isEmpty()) {
			List<FuelInfo> fList = getList(vin);
			int count = fList.size();
			
			FuelInfo beforeItem = fList.get(count-1);
			double accuLiter = 0;
			double totalAccuLiter = beforeItem.volume;
			int baseOdo = beforeItem.odo;
			beforeItem.accuEfficient = 0;
			
			for(int i=count-2;i>=0;i--) {
				FuelInfo item = fList.get(i);
				accuLiter += item.volume;
				if(item.isFull) {
					long accuDist = item.odo - beforeItem.odo;
					
					double eff = ((double)accuDist) / accuLiter;
					item.efficient = eff;
					
					beforeItem = item;
					accuLiter = 0;
				}else {
					//누적
					
					item.efficient = 0;
				}
				
				//누적연비 계산
				item.accuEfficient = ((double)(item.odo - baseOdo)) / totalAccuLiter;
				totalAccuLiter += item.volume;
			}
			
			obtainService();
			double lastEff = 0;
			for(FuelInfo item : fList) {
				if(item.isFull) {
					lastEff = item.efficient;
				}else {
					item.efficient = lastEff;
				}
				
				mService.put(item.toEntity());
			}
			
			
		}
		
		
	}
	
	public static boolean save(FuelInfo info) {
		obtainService();
		
		mService.put(info.toEntity());
		
		calcEfficient(info.vin);
		
		return true;
		
	}
	*/

}
