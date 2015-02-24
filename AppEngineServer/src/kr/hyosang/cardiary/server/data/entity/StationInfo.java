package kr.hyosang.cardiary.server.data.entity;

import kr.hyosang.cardiary.server.data.StationData;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@Deprecated
public class StationInfo extends EntityBase {
	public String name;
	public String company;
	
	public StationInfo() {
	}
	
	public StationInfo(Entity e) {
		this.key = e.getKey();
		this.name = (String)e.getProperty(StationData.ENTITY_NAME);
		this.company = (String)e.getProperty(StationData.ENTITY_COMPANY);
	}

	@Override
	public Entity toEntityUpdate(Key currKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Entity toEntityFromParent(Key parentKey) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*
	@Override
	public Entity toEntity() {
		Key key = KeyFactory.createKey(StationData.KIND_NAME, String.format("%s_%s", company, name));
		Entity e = new Entity(key);
		e.setProperty(StationData.ENTITY_NAME, name);
		e.setProperty(StationData.ENTITY_COMPANY, company);
		
		return e;
	}

	@Override
	public Entity toEntityFromParent(Key parentKey) {
		// TODO Auto-generated method stub
		return null;
	}
	*/


}
