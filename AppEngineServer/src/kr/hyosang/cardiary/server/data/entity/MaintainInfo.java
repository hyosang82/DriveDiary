package kr.hyosang.cardiary.server.data.entity;

import kr.hyosang.cardiary.server.data.MaintainData;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;

public class MaintainInfo extends EntityBase {
	public String vin;
	public int odo;
	public String garage;
	public String date;
	public long totalPrice;
	public String memo;
	
	public MaintainInfo() {
	}
	
	public MaintainInfo(Entity e) {
		this.key = e.getKey();
		this.vin = (String) e.getProperty(MaintainData.ENTITY_VIN);
		this.odo = (int)(long)e.getProperty(MaintainData.ENTITY_ODO);
		this.garage = (String)e.getProperty(MaintainData.ENTITY_GARAGE);
		this.date = (String)e.getProperty(MaintainData.ENTITY_DATE);
		this.memo = (String)e.getProperty(MaintainData.ENTITY_MEMO);
		try {
			this.totalPrice = (long)e.getProperty(MaintainData.ENTITY_PRICE);
		}catch(NullPointerException ee) {
			this.totalPrice = 0;
		}

	}
	
	private int parseInt(String n, int def) {
		try {
			return Integer.parseInt(n, 10);
		}catch(NumberFormatException e) {
			return def;
		}
	}
	
	public void setOdo(String p) {
		this.odo = parseInt(p, 0);
	}

	@Override
	public Entity toEntityFromParent(Key parentKey) {
		Entity e = new Entity(MaintainData.KIND_NAME, parentKey);
		
		return setEntityData(e);
	}
	
	private Entity setEntityData(Entity e) {
		e.setProperty(MaintainData.ENTITY_DATE, date);
		e.setProperty(MaintainData.ENTITY_GARAGE, garage);
		e.setProperty(MaintainData.ENTITY_MEMO, memo);
		e.setProperty(MaintainData.ENTITY_ODO, odo);
		e.setProperty(MaintainData.ENTITY_PRICE, totalPrice);
		
		return e;
	}

	@Override
	public Entity toEntityUpdate(Key currKey) {
		Entity e = new Entity(currKey);
		
		return setEntityData(e);
	}
}
