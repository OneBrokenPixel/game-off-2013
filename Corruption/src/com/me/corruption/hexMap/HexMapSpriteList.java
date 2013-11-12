package com.me.corruption.hexMap;

import java.util.Iterator;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;

public class HexMapSpriteList implements Iterable<HexMapSpriteObject> {

	private Array<HexMapSpriteObject> objects;
	
	/**
	 * Constructor
	 */
	public HexMapSpriteList() {
		this.objects = new Array<HexMapSpriteObject>();
	}
	
	/**
	 * @param index
	 * @return HexMapSpriteObject at index
	 */
	public HexMapSpriteObject get(int index) {
		return objects.get(index);
	}
	
	public HexMapSpriteObject get(String name) {
		for (HexMapSpriteObject obj : objects) {
			if (name.equals(obj.getName())) {
				return obj;
			}
		}
		return null;
	}
	
	/**
	 * @param obj instance to be added to the collection
	 */
	public void add(HexMapSpriteObject obj) {
		this.objects.add(obj);
	}

	/**
	 * @param instances of HexMapSpriteObject to be added to the collection
	 */
	public void add(HexMapSpriteObject... objects) {
		for( HexMapSpriteObject obj : objects) {
			this.objects.add(obj);
		}
	}	
	
	/**
	 * @param index removes HexMapTile instance at index
	 */
	public void remove(int index) {
		this.objects.removeIndex(index);
	}
	
	/**
	 * @param object instance to be removed
	 */
	public void remove(HexMapSpriteObject obj) {
		this.objects.removeValue(obj, true);
	}
	
	/**
	 * @return number of objects in the collection
	 */
	public int getCount() {
		return this.objects.size;
	}

	/**
	 * @param type class of the tiles we want to retrieve
	 * @return array filled with all the tiles in the collection matching type
	 */
	public <T extends HexMapSpriteObject> Array<T> getByType(Class<T> type) {
		return getByType(type, new Array<T>());
	}
	
	/**
	 * @param type class of the object we want to retrieve
	 * @param fill collection to put the returned tiles in
	 * @return array filled with all the tiles in the collection matching type
	 */
	@SuppressWarnings("unchecked")
	public <T extends HexMapSpriteObject> Array<T> getByType(Class<T> type, Array<T> fill) {
		fill.clear();
		for (HexMapSpriteObject obj : this.objects) {
			if (ClassReflection.isInstance(type, obj)) {
				fill.add((T) obj);
			}
		}
		return fill;
	}

	/**
	 * @return iterator for the tiles within the collection
	 */
	@Override
	public Iterator<HexMapSpriteObject> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

}
