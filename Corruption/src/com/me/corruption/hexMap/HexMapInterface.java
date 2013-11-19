package com.me.corruption.hexMap;

import java.util.HashMap;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Screen;
import com.me.corruption.CorruptionGdxGame;
import com.me.corruption.entities.PlayerEntity;
import com.me.corruption.hexMap.HexMap.Cell;
/**
 * 
 * @author Marie
 *
 */
public class HexMapInterface {
	private HexMapRenderer renderer;
	private HexMap map;
	private PlayerEntity player;
	private CorruptionGdxGame game;
	private HashMap<String, Screen> screens;
	
	public HexMapInterface(CorruptionGdxGame game, HexMapRenderer renderer) {
		
		this.map = renderer.getMap();
		this.renderer = renderer;
		this.player = this.map.getPlayer();
		this.game = game;
		
		screens = new HashMap<String, Screen>();
	}
	

	/**
	 * cheat method, remember to take this out!
	 * @param amount we cheat by!
	 */
	public void addEnergy(int amount) {
		
	}

	
	/** 
	 * 
	 * @param show, boolean value to show/hide energy 
	 */
	public void showEnergy(boolean show) {
		renderer.setShowEnergy(show);
	}
	
	/**
	 * 
	 * @param show, boolean value to show/hide resource icons
	 */
	public void showResourceIcons(boolean show) {
		renderer.setShowResources(show);
	}
	
	/**
	 * @param on, boolean value to start/stop add energy mode
	 * If mode is on, any player-owned tile the player clicks on
	 * stops/starts energy transfer into that tile
	 */
	public void addEnergyMode(boolean on) {
	}

	public void mute(boolean b) {
		
	}

	public void addScreen(String name, Screen screen) {
		screens.put(name, screen);
	}
	
	public void pause(boolean b) {
		// TODO Auto-generated method stub
		
	}


	public void quit() {
		// TODO Auto-generated method stub
		
	}
	
	public void setScreen(String name) {
		System.out.println(screens.values());
		Screen screen = screens.get(name);
		if (screen != null) {
			System.out.println("Setting screen: " + name);
			setScreen(screen);
		}
		else {
			System.out.println("Can't find screen: " + name);
		}
	}
	
	public void setScreen(Screen screen) {
		game.setScreen(screen);
	}
	
	public int getEnergyBank() {
		return (int) player.getEnergyBank();
	}
	
	public void updateEnergyDisplay() {
		
	}
	
	public Cell getMouseOverTile() {
		return renderer.getMouseOverCell();
	}


	private Cell clickedOn = null;
	
	public void setClickedOn(Cell cell){
		clickedOn = cell;
	}
	
	private void build(String name) {
		final Cell cell = clickedOn;
		if( cell != null) {
			cell.setBuilding(name);
			//System.out.println(cell.getBuilding().name);
		}
	}
	
	public void buildWind() {
		build("windplant");
	}
	
	public void buildSolar() {
		build("solarplant");
	}
	
	public void buildChemical() {
		build("chemicalplant");
	}


	public void demolishBuilding() {
		build(null);	
	}


	public void gameLost() {
		System.out.println("You lose");
		
	}
}
