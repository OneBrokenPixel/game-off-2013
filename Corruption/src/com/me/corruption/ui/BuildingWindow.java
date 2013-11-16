package com.me.corruption.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.me.corruption.hexMap.HexMapInterface;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class BuildingWindow extends Window {
	
	HexMapInterface hexmap;
	Skin skin;
	
	private TextButton wind;
	private TextButton chemical;
	private TextButton solar;
	private TextButton nothing;
	private TextButton demolish;
	
	public BuildingWindow(String title, Skin skin, HexMapInterface hexmap) {
		super(title, skin);
		this.skin = skin;
		this.hexmap = hexmap;
		
		wind = new TextButton("Build Wind Farm", skin);
		chemical = new TextButton("Build Chemical Plant", skin);
		solar = new TextButton("Build Solar Energy Farm", skin);
		demolish = new TextButton("Demolish Building", skin);
		nothing = new TextButton("Close Menu", skin);

		this.defaults().pad(2);
		
		wind.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("Build wind farm");
				return true;
			}
		});
		
		chemical.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("Build chemical plant");
				return true;
			}
		});
		
		solar.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("Build solar farm");
				return true;
			}
		});
		
		demolish.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("Demolish building");
				return true;
			}
		});
		
		nothing.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				
				return false;
			}
		});
	}
	
	public void populate(boolean hasWind, boolean hasChemical, boolean hasSolar, boolean hasDemolish) {
		
		this.clear();
		
		this.add(new Label("Some explainaton text goes here\n", skin));
		this.row();
		
		if (hasWind) {
			this.add(wind).expandX().fillX();
			this.row();
		}
		if (hasChemical) {
			this.add(chemical).expandX().fillX();
			this.row();
		}
		if (hasSolar) {
			this.add(solar).expandX().fillX();
			this.row();
		}
		if (hasDemolish) {
			this.add(demolish).expandX().fillX();
			this.row();
		}
		this.add(nothing).expandX().fillX();
		this.pack();
	}
	
	
}
