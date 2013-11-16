package com.me.corruption.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.me.corruption.hexMap.HexMap;
import com.me.corruption.hexMap.HexMapInterface;

public class ResearchWindow extends Window {
	HexMapInterface hexmap;
	Skin skin;
	
	private TextButton wind;
	private TextButton chemical;
	private TextButton solar;
	private TextButton close;
	
	public ResearchWindow(String title, Skin skin, HexMapInterface hexmap) {
		super(title, skin);
		this.skin = skin;
		this.hexmap = hexmap;
		
		wind = new TextButton("Wind(1->2) - Cost: 4 Energy", skin);
		chemical = new TextButton("Chemical(1->2) - Cost: 4 Energy", skin);
		solar = new TextButton("Solar(1->2) - Cost: 4 Energy", skin);
		close = new TextButton("Close research", skin);
		
		this.defaults().pad(2);
		
		
		wind.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("research wind");
				return true;
			}
		});
		
		chemical.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("research chemical");
				return true;
			}
		});
		
		solar.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("research solar");
				return true;
			}
		});
		
		close.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {

				return false;
			}
		});
		
		this.add(wind);
		this.row();
		this.add(chemical);
		this.row();
		this.add(solar);
		this.row();
		this.pack();
	}
	
	
}
