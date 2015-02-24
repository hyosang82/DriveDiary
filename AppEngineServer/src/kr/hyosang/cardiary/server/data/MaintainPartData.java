package kr.hyosang.cardiary.server.data;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;

public class MaintainPartData extends BaseData {
	public static final String KIND_NAME = "MaintenancePart";
	
	public static final String ENTITY_PART_KEY = "part_key";
	
	public static void savePart(Key mainKey, String partKey) {
		obtainService();
		
		Entity e = new Entity(KIND_NAME, mainKey);
		e.setProperty(ENTITY_PART_KEY, partKey);
		
		mService.put(e);
	}
	

}
