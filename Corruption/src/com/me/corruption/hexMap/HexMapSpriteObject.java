package com.me.corruption.hexMap;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.sun.org.apache.bcel.internal.generic.ATHROW;

public class HexMapSpriteObject {

	private String name = null;
	private TextureRegion texture;
	
	public HexMapSpriteObject( String name, TextureAtlas atlas ) {
		this.name = name;
		this.texture = atlas.findRegion(name);
	}
	

	public TextureRegion getTexture() {
		return texture;
	}

	public void setTexture(TextureRegion texture) {
		this.texture = texture;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
