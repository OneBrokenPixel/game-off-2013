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
	private TextButton repair;
	private TextButton clear;
	
	public BuildingWindow(String title, Skin skin, final HexMapInterface hexmap) {
		super(title, skin);
		this.skin = skin;
		this.hexmap = hexmap;
		
		wind = new TextButton("Build Wind Farm ("+ hexmap.getWindCost() +" energy)", skin);
		chemical = new TextButton("Build Chemical Plant ("+ hexmap.getChemicalCost() +" energy)", skin);
		solar = new TextButton("Build Solar Energy Farm ("+ hexmap.getSolarCost() +" energy)", skin);
		demolish = new TextButton("Demolish Building", skin);
		nothing = new TextButton("Close Menu", skin);
		repair = new TextButton("Repair Building ("+hexmap.getRepareCost()+" energy)", skin);
		clear = new TextButton("Clear Tile", skin);
		
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
		
		repair.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {	
				hexmap.repairBuilding();
				return true;
			}
		});
		
		clear.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {	
				hexmap.clearTile();
				return true;
			}
		});
	}
	public void populate(boolean hasWind, boolean hasChemical, boolean hasSolar, boolean hasDemolish, boolean hasRepair, boolean clearHex) {
		
		this.clear();
		
		wind.setText("Build Wind Farm ("+ hexmap.getWindCost() +" energy)");
		chemical.setText("Build Chemical Plant ("+ hexmap.getChemicalCost() +" energy)");
		solar.setText("Build Solar Energy Farm ("+ hexmap.getSolarCost() +" energy)");
		repair.setText("Repair Building ("+hexmap.getRepareCost()+" energy)");
		
		
		this.add(new Label("Choose an energy plant to build", skin));
		this.row();
		
		if (hasWind) {
			this.add(wind).expandX().fillX().height(30);
			this.row();
		}
		if (hasSolar) {
			this.add(solar).expandX().fillX().height(30);
			this.row();
		}
		if (hasChemical) {
			this.add(chemical).expandX().fillX().height(30);
			this.row();
		}
		if (hasRepair) {
			this.add(repair).expandX().fillX().height(30);
			this.row();
		}
		if (clearHex) {
			this.add(clear).expandX().fillX().height(30);
			this.row();
		}
		if (hasDemolish) {
			this.add(demolish).expandX().fillX().height(30);
			this.row();
		}
		this.add(nothing).expandX().fillX().height(30);
		this.pack();
	}
	
}

