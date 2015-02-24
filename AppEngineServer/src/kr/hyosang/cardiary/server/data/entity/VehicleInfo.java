package kr.hyosang.cardiary.server.data.entity;

import kr.hyosang.cardiary.server.data.VehicleData;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;

public class VehicleInfo extends EntityBase {
	public String user;
	public String vin;
	public String vendor;
	public String model;
	public String year;
	
	public VehicleInfo() {
	}
	
	public VehicleInfo(Entity e) {
		this.key = e.getKey();
		this.user = (String) e.getProperty(VehicleData.ENTITY_USER);
		this.vin = e.getKey().getName();
		this.vendor = (String) e.getProperty(VehicleData.ENTITY_VENDOR);
		this.model = (String) e.getProperty(VehicleData.ENTITY_MODEL);
		this.year = (String) e.getProperty(VehicleData.ENTITY_YEAR);
	}
	
	
	@Override
	public Entity toEntityUpdate(Key currKey) {
		Entity e = new Entity(currKey);
		
		return setEntityData(e);
	}
	
	public Entity asEntity() {
		Entity e;
		
		if(this.key == null) {
			e = new Entity(VehicleData.KIND_NAME, vin);
		}else {
			e = new Entity(this.key);
		}
		
		return setEntityData(e);
	}
	
	private Entity setEntityData(Entity e) {
		e.setProperty(VehicleData.ENTITY_USER, user);
		e.setProperty(VehicleData.ENTITY_VENDOR, vendor);
		e.setProperty(VehicleData.ENTITY_MODEL, model);
		e.setProperty(VehicleData.ENTITY_YEAR, year);
		
		return e;
	}

	@Deprecated
	@Override
	public Entity toEntityFromParent(Key parentKey) {
		// TODO Auto-generated method stub
		return null;
	}



}
