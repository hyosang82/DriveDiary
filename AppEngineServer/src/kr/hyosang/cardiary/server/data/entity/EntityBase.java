package kr.hyosang.cardiary.server.data.entity;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public abstract class EntityBase {
	protected Key key;
	
	public EntityBase() { }
	public EntityBase(Entity e){ }
	public abstract Entity toEntityUpdate(Key currKey);
	public abstract Entity toEntityFromParent(Key parentKey);
	
	public void setKey(Key key) {
		this.key = key;
	}
	
	public Key getKey() {
		return this.key;
	}
	
	public String getEncodedKey() {
		return KeyFactory.keyToString(this.key);
	}
	
	protected String getString(Entity e, String prop, String def) {
		try {
			return (String)e.getProperty(prop);
		}catch(Exception ee) {
			return def;
		}
	}
	
	protected float getFloat(Entity e, String prop, float def) {
		try {
			return (float)(Float)e.getProperty(prop);
		}catch(Exception ee) {
			ee.printStackTrace();
			return def;
		}
	}
	
	protected double getDouble(Entity e, String prop, double def) {
		try {
			return (double)(Double)e.getProperty(prop);
		}catch(Exception ee) {
			return def;
		}
	}

}
