package kr.hyosang.cardiary.server.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;

public abstract class BaseData {
	protected static DatastoreService mService = null;
	
	protected static void obtainService() {
		if(mService == null) {
			mService = DatastoreServiceFactory.getDatastoreService();
		}
	}
	
	
	public static void delete(Key key) {
		obtainService();
		
		mService.delete(key);
	}
	
	
	public static Key put(Entity e) {
		obtainService();
		
		return mService.put(e);
	}
}
