package kr.hyosang.cardiary.server.data.entity;

import kr.hyosang.cardiary.server.data.PartItemData;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class PartItemInfo extends EntityBase {
	public String name;
	
	public PartItemInfo() {
	}
	
	public PartItemInfo(Entity e) {
		this.key = e.getKey();
		this.name = (String) e.getProperty(PartItemData.ENTITY_NAME);
	}
	
	public String getId() {
		if(this.key != null) {
			return this.key.getName();
		}else {
			return null;
		}
	}
	
	
	public Entity newEntity() {
		String keyStr = String.format("%x%03d", name.hashCode(), name.length());
		Key key = KeyFactory.createKey(PartItemData.KIND_NAME, keyStr);
		Entity e = new Entity(key);
		
		return setEntityData(e);
	}
	
	private Entity setEntityData(Entity e) {
		e.setProperty(PartItemData.ENTITY_NAME, name);

		return e;
	}

	@Override
	public Entity toEntityFromParent(Key parentKey) {
		Entity e = new Entity(PartItemData.KIND_NAME, parentKey);
		
		return setEntityData(e);
	}

	@Override
	public Entity toEntityUpdate(Key currKey) {
		Entity e = new Entity(currKey);
		
		return setEntityData(e);
	}
}
