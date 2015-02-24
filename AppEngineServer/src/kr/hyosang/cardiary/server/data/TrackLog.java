package kr.hyosang.cardiary.server.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.StringTokenizer;

import kr.hyosang.cardiary.server.Util;
import kr.hyosang.cardiary.server.data.entity.VehicleInfo;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Text;

public class TrackLog extends BaseData {
	public static final String KIND_NAME = "TrackLog";
	
	public static final String ENTITY_DIST = "distance";
	public static final String ENTITY_TIME = "timestamp";
	
	public Key key;
	public String encodedKey;
	public double distance = 0;
	public long timestamp;
	
	public TrackLog(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public TrackLog(Entity e) {
		key = e.getKey();
		encodedKey = KeyFactory.keyToString(key);
		distance = (double)e.getProperty(TrackLog.ENTITY_DIST);
		timestamp = (long)e.getProperty(TrackLog.ENTITY_TIME);
	}

	public Entity toEntity(Key parentKey) {
		Entity e;
		
		if(this.key == null) {
			if(parentKey != null) {
				e = new Entity(TrackLog.KIND_NAME, parentKey);
				this.key = e.getKey();
			}else {
				return null;
			}
		}else {
			e = new Entity(this.key);
		}
		
		e.setProperty(TrackLog.ENTITY_DIST, distance);
		e.setProperty(TrackLog.ENTITY_TIME, timestamp);
		
		return e;
	}

	public static TrackLog getByEncodedKey(String encodedKey) {
		obtainService();
		
		try {
			return new TrackLog(mService.get(KeyFactory.stringToKey(encodedKey)));
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static VehicleInfo getTrackVehicle(Key key) {
		obtainService();
		
		try {
			Entity trackData = mService.get(key);
			
			Key parentKey = trackData.getParent();
			
			Entity parent = mService.get(parentKey);
			
			if(VehicleData.KIND_NAME.equals(parent.getKind())) {
				return (new VehicleInfo(parent));
			}
			
		}catch(EntityNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean mergeToPrevLog(Key currKey) {
		obtainService();
		
		try {
			Entity currEntity = mService.get(currKey);
			VehicleInfo vehicle = getTrackVehicle(currEntity.getKey());
			
			List<TrackLog> tracklist = TrackLog.getList(vehicle.vin);
			
			//find previous track
			int currIdx = -1;
			for(int i=0;i<tracklist.size();i++) {
				if(tracklist.get(i).key.equals(currKey)) {
					currIdx = i;
					break;
				}
			}
			
			if(currIdx != 0) {
				TrackLog prevLog = tracklist.get(currIdx - 1);
				
				//해당 데이터 키 변경
				List<Entity> gpsEntities = getGpsDataEntities(currKey);
				for(Entity e : gpsEntities) {
					//삭제하고
					mService.delete(e.getKey());

					//넣음
					Entity ne = (new TrackLogData(e)).toEntity(prevLog.key);
					mService.put(ne);
				}
				
				//부모 키도 삭제
				mService.delete(currKey);
				
				//트랙 정보 변경
				prevLog.distance = 0;
				prevLog.save();
				
				//태그 부모 변경
				Tag.changeParent(currKey, prevLog.key);
				
				return true;
			}
			
		}catch(EntityNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		
		return false;
	}
	
	public static TrackLog getByTimestamp(String vin, long timestamp) {
		obtainService();
		
		VehicleInfo vehicle = VehicleData.getVehicleByVin(vin);
		
		Query query = new Query(KIND_NAME, vehicle.getKey());
		FilterPredicate filter = new FilterPredicate(ENTITY_TIME, FilterOperator.EQUAL, timestamp);
		query.setFilter(filter);
		
		PreparedQuery pq = mService.prepare(query);
		
		Entity e = pq.asSingleEntity();
		
		if(e == null) {
			return null;
		}else {
			return new TrackLog(e);		
		}
	}
	
	public static List<GpsLog> getGpsData(Key key) {
		List<Entity> dataEntities = getGpsDataEntities(key);
		ArrayList<GpsLog> logList = new ArrayList<GpsLog>();
		
		for(Entity e : dataEntities) {
			StringTokenizer token = new StringTokenizer(((Text)e.getProperty(TrackLogData.ENTITY_LOGDATA)).getValue(), "$");
			while(token.hasMoreElements()) {
				String data = token.nextToken();
				String [] ele = data.split("\\|");
				
				if(ele != null && ele.length >= 5) {
					GpsLog log = new GpsLog();
					try {
						log.latitude = Double.parseDouble(ele[0]);
						log.longitude = Double.parseDouble(ele[1]);
						log.altitude = Double.parseDouble(ele[2]);
						log.speed = Double.parseDouble(ele[3]);
						log.timestamp = Long.parseLong(ele[4]);
						
						logList.add(log);
					}catch(NumberFormatException ee) {
						ee.printStackTrace();
					}
				}
			}
		}
		
		return logList;
	}
	
	public static List<Entity> getGpsDataEntities(Key trackKey) {
		obtainService();
		
		Query query = new Query(TrackLogData.KIND_NAME, trackKey);
		query.addSort(TrackLogData.ENTITY_TIME, SortDirection.ASCENDING);
		
		PreparedQuery pq = mService.prepare(query);
		
		return pq.asList(FetchOptions.Builder.withDefaults());
	}
		
	
	
	public static List<TrackLog> getList(String vin) {
		return getList(vin, null);
	}
	
	public static List<TrackLog> getList(String vin, String dateFilter) {
		obtainService();
		
		VehicleInfo info = VehicleData.getVehicleByVin(vin);
		
		Query query = new Query(KIND_NAME, info.getKey());
		
		if(dateFilter != null) {
			Calendar st = null;
			Calendar ed = null;
			
			if(dateFilter.length() == 6) {
				int year = Util.parseInt(dateFilter.substring(0, 4));
				int month = Util.parseInt(dateFilter.substring(4)) - 1;
			
				//필터링할 날짜 선택
				st = new GregorianCalendar(year, month, 1);
				ed = new GregorianCalendar(year, month+1, 1);
				ed.add(Calendar.MILLISECOND, -1);
			}else if(dateFilter.length() == 4) {
				int year = Util.parseInt(dateFilter);
				
				st = new GregorianCalendar(year, 0, 1);
				ed = new GregorianCalendar(year+1, 0, 1);
				ed.add(Calendar.MILLISECOND, -1);
			}
			
			if(st != null && ed != null) {
				Filter filter = CompositeFilterOperator.and(
						new FilterPredicate(ENTITY_TIME, FilterOperator.GREATER_THAN_OR_EQUAL, st.getTimeInMillis()),
						new FilterPredicate(ENTITY_TIME, FilterOperator.LESS_THAN_OR_EQUAL, ed.getTimeInMillis())
				);
				query.setFilter(filter);
			}
		}
		
		query.addSort(ENTITY_TIME, SortDirection.ASCENDING);
		
		PreparedQuery pq = mService.prepare(query);

		ArrayList<TrackLog> list = new ArrayList<>();
		for(Entity e : pq.asIterable()) {
			list.add(new TrackLog(e));
		}
		
		return list;
	}
	
	public Key save(String vin) {
		obtainService();
		
		if(vin == null) {
			//기존 엔티티 업데이트.
			mService.put(this.toEntity(null));
		}else {
			VehicleInfo vehicle = VehicleData.getVehicleByVin(vin);
			
			this.key = mService.put(this.toEntity(vehicle.getKey()));
		}
		
		return this.key;
	}
	
	public void save() {
		if(this.key != null) {
			obtainService();
			
			mService.put(this.toEntity(null));
		}
	}
	
	
	
	public static class TrackLogData {
		public static final String KIND_NAME = "TrackLogData";
		
		public static final String ENTITY_LOGDATA = "logdata";
		public static final String ENTITY_TIME = "timestamp";
		
		public Key key = null;
		public long timestamp;
		public String logdata;
		
		public TrackLogData() {
		
		}
		
		public TrackLogData(Entity e) {
			this.key = e.getKey();
			this.timestamp = (long)e.getProperty(ENTITY_TIME);
			this.logdata = ((Text)e.getProperty(ENTITY_LOGDATA)).getValue();
		}
		
		public Entity toEntity(Key parentKey) {
			Entity e;
			if(parentKey != null) {
				e = new Entity(KIND_NAME, parentKey);
			}else if(this.key != null) {
				e = new Entity(KIND_NAME, this.key);
			}else {
				e = new Entity(KIND_NAME);
			}
			e.setProperty(ENTITY_TIME, timestamp);
			e.setProperty(ENTITY_LOGDATA, new Text(logdata));
			
			return e;
		}
		
		public Key save(Key parent) {
			obtainService();
			
			this.key = mService.put(this.toEntity(parent));
			
			return key;
		}
	
	}
	
	public static class Tag {
		public static final String KIND_NAME = "TrackTag";
		
		public static final String ENTITY_TAG = "tag";
		
		public String tag;
		
		public static Entity createEntity(Key parent, String tag) {
			Entity e = new Entity(KIND_NAME, parent);
			e.setProperty(ENTITY_TAG, tag);
			
			return e;
		}
		
		public static List<String> getList(Key parent) {
			obtainService();
			
			Query query = new Query(KIND_NAME, parent);
			PreparedQuery pq = mService.prepare(query);
			
			ArrayList<String> list = new ArrayList<>();
			for(Entity e : pq.asIterable()) {
				list.add((String) e.getProperty(ENTITY_TAG));
			}
			
			return list;
		}
		
		public static boolean changeParent(Key oldParent, Key newParent) {
			obtainService();
			
			Query query = new Query(KIND_NAME, oldParent);
			PreparedQuery pq = mService.prepare(query);
			
			for(Entity e : pq.asIterable()) {
				mService.delete(e.getKey());
				
				mService.put(createEntity(newParent, (String)e.getProperty(ENTITY_TAG)));
			}
			
			return true;
		}
	}
	
	public static class GpsLog {
		public double longitude;
		public double latitude;
		public double altitude;
		public double speed;
		public long timestamp;
	}	

}
