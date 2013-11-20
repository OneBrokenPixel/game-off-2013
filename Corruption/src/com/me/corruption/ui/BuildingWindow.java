package com.me.corruption.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.me.corruption.hexMap.HexMapInterface;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class BuildingWindow extends Window {
	
	private HexMapInterface hexmap;
	private Skin skin;
	
	private TextButton wind;
	private TextButton chemical;
	private TextButton solar;
	private TextButton nothing;
	private TextButton demolish;
	
	public BuildingWindow(String title, Skin skin, final HexMapInterface hexmap) {
		super(title, skin);
		this.skin = skin;
		this.hexmap = hexmap;
		
		wind = new TextButton("Build Wind Farm (10 energy)", skin);
		chemical = new TextButton("Build Chemical Plant (30 energy)", skin);
		solar = new TextButton("Build Solar Energy Farm (20 energy)", skin);
		demolish = new TextButton("Demolish Building", skin);
		nothing = new TextButton("Close Menu", skin);

		this.defaults().pad(2);
		
		wind.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				hexmap.buildWind();
				return true;
			}
		});
		
		chemical.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				hexmap.buildChemical();
				return true;
			}
		});
		
		solar.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				hexmap.buildSolar();
				return true;
			}
		});
		
		demolish.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				hexmap.demolishBuilding();
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
		if (hasSolar) {
			this.add(solar).expandX().fillX();
			this.row();
		}
		if (hasChemical) {
			this.add(chemical).expandX().fillX();
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
