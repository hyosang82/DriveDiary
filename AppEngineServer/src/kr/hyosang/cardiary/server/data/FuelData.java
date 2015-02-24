package kr.hyosang.cardiary.server.data;

import java.util.ArrayList;
import java.util.List;

import kr.hyosang.cardiary.server.data.entity.FuelInfo;
import kr.hyosang.cardiary.server.data.entity.VehicleInfo;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

public class FuelData extends BaseData {
	public static final String KIND_NAME = "Fuel";
	
	public static final String ENTITY_ODO = "odo";
	public static final String ENTITY_UNIT_PRICE = "unit_price";
	public static final String ENTITY_TOTAL_PRICE = "total_price";
	public static final String ENTITY_VOLUME = "volume";
	public static final String ENTITY_ISFULL = "is_full";
	public static final String ENTITY_DATE = "date";
	public static final String ENTITY_STATION = "station";
	public static final String ENTITY_EFFICIENT = "efficient";
	public static final String ENTITY_ACCU_EFFICIENT = "accu_efficient";

	public static List<FuelInfo> getList(String vin) {
		obtainService();
		
		VehicleInfo vehicle = VehicleData.getVehicleByVin(vin);
		Query query = new Query(KIND_NAME, vehicle.getKey());
		query.addSort(ENTITY_ODO, SortDirection.DESCENDING);
		
		PreparedQuery pq = mService.prepare(query);

		ArrayList<FuelInfo> list = new ArrayList<>();
		for(Entity e : pq.asIterable()) {
			list.add(new FuelInfo(e));
		}
		
		return list;
	}
	
	private static void calcEfficient(String vin) {
		if(vin != null && !vin.isEmpty()) {
			List<FuelInfo> fList = getList(vin);
			int count = fList.size();
			Key vehicleKey = VehicleData.getVehicleByVin(vin).getKey();
			
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
				
				mService.put(item.toEntityUpdate(item.getKey()));
			}
			
			
		}
		
		
	}
	
	public static boolean save(String vin, FuelInfo info) {
		obtainService();
		
		VehicleInfo vehicle = VehicleData.getVehicleByVin(vin);
		
		mService.put(info.toEntityFromParent(vehicle.getKey()));
		
		calcEfficient(vin);
		
		return true;
		
	}

}
