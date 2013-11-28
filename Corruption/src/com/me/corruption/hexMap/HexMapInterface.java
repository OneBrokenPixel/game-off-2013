package com.me.corruption.hexMap;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.me.corruption.CorruptionGdxGame;
import com.me.corruption.entities.PlayerEntity;
import com.me.corruption.hexMap.HexMap.Cell;
import com.me.corruption.ui.GameUI;

/**
 * 
 * @author Marie
 * 
 */
public class HexMapInterface implements Disposable {
	private HexMapRenderer renderer;
	private HexMap map;
	private PlayerEntity player;
	private CorruptionGdxGame game;
	private HashMap<String, Screen> screens;
	private GameUI gameUI;

	private int windInitalCost = 10;
	private int solarInitalCost = 40;
	private int chemicalInitalCost = 120;

	private int windCount = 0;
	private int solarCount = 0;
	private int chemicalCount = 0;

	private int windCost = windInitalCost;
	private int solarCost = solarInitalCost;
	private int chemicalCost = chemicalInitalCost;

	private float rateOfIncrese = 0.25f;

	private static Sound buttonClickOn;
	private static Sound buttonClickOff;

	private static final float uiVolume = 0.4f;

	private static Sound sporeLaunch;

	private static boolean mute = false;
	private static boolean muteMusic = false;
	
	private static Music gameMusic;
	
	
	private static boolean gameStarted = false;

	static {
		buttonClickOn = Gdx.audio.newSound(Gdx.files.internal("audio/Button Click On-SoundBible.com-459633989.mp3"));
		buttonClickOff = Gdx.audio.newSound(Gdx.files.internal("audio/Button Click Off-SoundBible.com-1730098776.mp3"));

		sporeLaunch = Gdx.audio.newSound(Gdx.files.internal("audio/Squishy 2-SoundBible.com-1775292371.mp3"));

		gameMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/07 How Long Can You Remember A Voice.mp3"));
		gameMusic.play();
	}

	public HexMapInterface(CorruptionGdxGame game, HexMapRenderer renderer) {

		this.map = renderer.getMap();
		this.renderer = renderer;
		this.player = this.map.getPlayer();
		this.game = game;

		screens = new HashMap<String, Screen>();
	}
	
	public static void setMute(boolean value) {
		mute = value;
		if (mute || muteMusic) {
			gameMusic.pause();
		} else {
			gameMusic.play();
		}
	}

	public static void muteMusic(boolean value) {

		muteMusic = value;
		if (mute || muteMusic) {
			gameMusic.pause();
		} else {
			gameMusic.play();
		}
	}

	public static void playButtonClickOn() {
		if (!mute)
			buttonClickOn.play(uiVolume);
	}

	public static void playButtonClickOff() {
		if (!mute)
			buttonClickOff.play(uiVolume);
	}

	public static void playSporeLaunch() {
		if (!mute)
			sporeLaunch.play();
	}

	public int getWindCost() {
		return (int) (windInitalCost * Math.pow(1 + rateOfIncrese, windCount));
	}

	public int getSolarCost() {
		return (int) (solarInitalCost * Math.pow(1 + rateOfIncrese, solarCount));
	}

	public int getChemicalCost() {
		return (int) (chemicalInitalCost * Math.pow(1 + rateOfIncrese, chemicalCount));
	}

	/**
	 * cheat method, remember to take this out!
	 * 
	 * @param amount
	 *            we cheat by!
	 */
	public void addEnergy(int amount) {

	}

	/**
	 * 
	 * @param show
	 *            , boolean value to show/hide energy
	 */
	public void showEnergy(boolean show) {
		renderer.setShowEnergy(show);
	}

	/**
	 * 
	 * @param show
	 *            , boolean value to show/hide resource icons
	 */
	public void showResourceIcons(boolean show) {
		renderer.setShowResources(show);
	}

	/**
	 * @param on
	 *            , boolean value to start/stop add energy mode If mode is on,
	 *            any player-owned tile the player clicks on stops/starts energy
	 *            transfer into that tile
	 */
	public void addEnergyMode(boolean on) {
	}

	public void mute(boolean b) {

	}

	public void addScreen(String name, Screen screen) {
		screens.put(name, screen);
	}

	public void quit() {
		// TODO Auto-generated method stub

	}

	public void setScreen(String name) {
		// System.out.println(screens.values());
		Screen screen = screens.get(name);
		if (screen != null) {
			// System.out.println("Setting screen: " + name);
			setScreen(screen);
		} else {
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

	public void setClickedOn(Cell cell) {
		clickedOn = cell;
	}

	private void build(String name) {
		final Cell cell = clickedOn;
		if (cell != null) {
			cell.setBuilding(name);
			// System.out.println(cell.getBuilding().name);
		}
	}

	public boolean buyBuilding(int cost) {
		return player.removeEnergy(cost);
	}

	public void buildWind() {

		windCost = (int) (windInitalCost * Math.pow(1 + rateOfIncrese, windCount));

		// windCost = windInitalCost + windCount*3;
		// System.out.println(windCost);
		boolean bought = buyBuilding(windCost);
		if (bought) {
			windCount++;
			build("windplant");
		}
	}

	public void buildSolar() {

		solarCost = (int) (solarInitalCost * Math.pow(1 + rateOfIncrese, solarCount));
		// solarCost = solarInitalCost + solarCount*3;

		boolean bought = buyBuilding(solarCost);
		if (bought) {
			solarCount++;
			build("solarplant");
		}
	}

	public void buildChemical() {

		chemicalCost = (int) (chemicalInitalCost * Math.pow(1 + rateOfIncrese, chemicalCount));
		// chemicalCost = chemicalInitalCost + chemicalCount*3;

		boolean bought = buyBuilding(chemicalCost);
		if (bought) {
			chemicalCount++;
			build("chemicalplant");
		}
	}

	public void demolishBuilding() {
		build(null);
	}

	public void gameLost() {
		System.out.println("You lose");
	}

	public void pause(boolean setpause) {
		if (setpause) {
			this.game.pause();
			this.setScreen("pauseScreen");
		} else {
			this.game.resume();
			this.setScreen("gameScreen");
		}

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		buttonClickOn.dispose();
		buttonClickOff.dispose();
		sporeLaunch.dispose();

		gameMusic.dispose();
	}

	public void setStage(GameUI stage) {
		this.gameUI = stage;

	}

	public void start() {
		gameStarted = true;
		this.gameUI.start();
	}

	public void startMuted() {
		gameStarted = true;
		this.mute(true);
		this.muteMusic(true);
		this.gameUI.start();
	}
	
	public static boolean getGameStarted() {
		return gameStarted;
	}
}
