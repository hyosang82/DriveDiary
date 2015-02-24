package kr.hyosang.cardiary.server.data.entity;

import kr.hyosang.cardiary.server.data.FuelData;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;

public class FuelInfo extends EntityBase {
	public int odo;
	public int unitPrice;
	public int totalPrice;
	public double volume;
	public boolean isFull;
	public String date;
	public String station;
	public double efficient;
	public double accuEfficient;
	
	public FuelInfo() {
	}
	
	public FuelInfo(Entity e) {
		this.key = e.getKey();
		this.odo = (int)(long)e.getProperty(FuelData.ENTITY_ODO);
		this.unitPrice = (int)(long)e.getProperty(FuelData.ENTITY_UNIT_PRICE);
		this.totalPrice = (int)(long)e.getProperty(FuelData.ENTITY_TOTAL_PRICE);
		this.volume = (double)e.getProperty(FuelData.ENTITY_VOLUME);
		this.isFull = "Y".equals((String)e.getProperty(FuelData.ENTITY_ISFULL)) ? true : false;
		this.date = (String)e.getProperty(FuelData.ENTITY_DATE);
		this.station = (String)e.getProperty(FuelData.ENTITY_STATION);
		try {
			this.efficient = (double)e.getProperty(FuelData.ENTITY_EFFICIENT);
			this.accuEfficient = (double)e.getProperty(FuelData.ENTITY_ACCU_EFFICIENT);		
		}catch(NullPointerException ee) {
			this.efficient = 0;
			this.accuEfficient = 0;
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
	
	public void setUnitPrice(String p) {
		this.unitPrice = parseInt(p, 0);
	}
	
	public void setTotalPrice(String p) {
		this.totalPrice = parseInt(p, 0);
	}
	
	public void setVolume(String p) {
		try {
			this.volume = Double.parseDouble(p);
		}catch(NumberFormatException e) {
			this.volume = 0;
		}
	}
	
	public void setIsFull(String p) {
		if("Y".equals(p)) {
			this.isFull = true;
		}else {
			this.isFull = false;
		}
	}

	private Entity setEntityData(Entity e) {
		e.setProperty(FuelData.ENTITY_ODO, odo);
		e.setProperty(FuelData.ENTITY_UNIT_PRICE, unitPrice);
		e.setProperty(FuelData.ENTITY_TOTAL_PRICE, totalPrice);
		e.setProperty(FuelData.ENTITY_VOLUME, volume);
		e.setProperty(FuelData.ENTITY_ISFULL, (isFull ? "Y" : "N"));
		e.setProperty(FuelData.ENTITY_DATE, date);
		e.setProperty(FuelData.ENTITY_STATION, station);
		e.setProperty(FuelData.ENTITY_EFFICIENT, efficient);
		e.setProperty(FuelData.ENTITY_ACCU_EFFICIENT, accuEfficient);
		
		return e;
	}
	
	public Entity toEntityUpdate(Key currKey) {
		Entity e = new Entity(currKey);
		return setEntityData(e);
	}
	
	@Override
	public Entity toEntityFromParent(Key parent) {
		Entity e = new Entity(FuelData.KIND_NAME, parent);
		
		return setEntityData(e);
	}
	
	
	
	


}
