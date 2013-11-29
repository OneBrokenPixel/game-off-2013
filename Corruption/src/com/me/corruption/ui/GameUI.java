package com.me.corruption.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.me.corruption.entities.PlayerEntity;
import com.me.corruption.hexMap.HexMap.Cell;
import com.me.corruption.hexMap.HexMapInterface;

public class GameUI extends Stage {
	private HexMapInterface hexmap;
	
	private Pixmap pixmap;
	public static Skin skin;
	
	private Table sidebar;
	private Table layout; 
	
	private LabelStyle lstyle;
	private Label energyDisp;
	//private int energy; // for ui testing purposes only

	public ImageButton resourcesBtn;
	public ImageButton energyBtn;
	private TextButton tbResearch;
	
	private ImageButton soundBtn;
	private ImageButton musicBtn;
	private ImageButton pauseBtn;
	private ImageButton quitBtn;
	
	private ImageButton helpBtn;

	private BuildingWindow buildWin;
	private ResearchWindow researchWin;
	private EndWindow endWin;
	private PauseScreen pauseScreen;
	private StartMenu startMenu;
	
	public GameUI(final HexMapInterface ui_if) {

		// interaction with the rest of the game (mainly hexmap)
		this.hexmap = ui_if;

		skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		skin.addRegions(new TextureAtlas(Gdx.files.internal("spritesheets/ResearchTiles.atlas")));
		skin.addRegions(new TextureAtlas(Gdx.files.internal("spritesheets/UIimgs.atlas")));
		
		pixmap = new Pixmap(1,1, Format.RGBA8888);
		
		addColour("black", Color.BLACK);
		addColour("white", Color.WHITE);
		addColour("grey", Color.GRAY);
		addColour("lgrey", Color.LIGHT_GRAY);
		addColour("red", Color.RED);
		addColour("transparent", new Color(0.0f,0.0f,0.0f,0.01f));
		
		skin.add("bitmapFont", new BitmapFont());
		skin.add("pauseUp",  new Texture("spritesheets/pauseUp.png"));
		skin.add("pauseDown", new Texture("spritesheets/pauseDown.png"));
		skin.add("pauseImage", new Texture("spritesheets/pauseSplash.png"));
		
		layout = new Table();
		layout.setFillParent(true);
		this.addActor(layout);
		
		sidebar = new Table();
		sidebar.setBackground(skin.newDrawable("sideBack"));
		layout.left().bottom();
		layout.add(sidebar).expandY().fill().pad(10);

		sidebar.left().top();
		sidebar.defaults().padBottom(4).padLeft(-13f);
		sidebar.row();
		
		
		lstyle = new LabelStyle();
		lstyle.font = skin.getFont("default-font");
		lstyle.fontColor = Color.WHITE;
		energyDisp = new Label("Energy: 0", lstyle);
		
		sidebar.add(energyDisp).padTop(45).padBottom(30);
		sidebar.row();
		
		TextButtonStyle tbStyle = new TextButtonStyle();
		tbStyle.font = skin.getFont("bitmapFont");
		tbStyle.up = skin.newDrawable("black");
		tbStyle.down = skin.newDrawable("red");
		skin.add("default", tbStyle);
		
		TextButtonStyle toggleStyle = new TextButtonStyle();
		toggleStyle.font = skin.getFont("bitmapFont");
		toggleStyle.up = skin.newDrawable("black");
		toggleStyle.checked = skin.newDrawable("red");
		skin.add("toggle", toggleStyle);
		
		//final TextButton addEnergy = new TextButton("Cheat!", tbStyle);  // this will be deleted later

		resourcesBtn = new ImageButton(skin.newDrawable("showresources"), skin.newDrawable("hideresources"), skin.newDrawable("hideresources"));
		energyBtn = new ImageButton(skin.newDrawable("showenergy"), skin.newDrawable("hideenergy"), skin.newDrawable("hideenergy"));

		sidebar.add(resourcesBtn);
		sidebar.row();
		sidebar.add(energyBtn).padBottom(150);
		sidebar.row();
		
		Table rowTable = new Table();
		sidebar.add(rowTable).expandX().fillX().pad(2);
		
		soundBtn = new ImageButton(skin.newDrawable("sound"), skin.newDrawable("mute"), skin.newDrawable("mute"));
		musicBtn = new ImageButton(skin.newDrawable("musicon"), skin.newDrawable("musicoff"), skin.newDrawable("musicoff"));
		pauseBtn = new ImageButton(skin.newDrawable("pause"));
		helpBtn = new ImageButton(skin.newDrawable("help"), skin.newDrawable("helpdown"));
		quitBtn = new ImageButton(skin.newDrawable("quit"), skin.newDrawable("quitdown"));

		
		rowTable.add(soundBtn).pad(2).padLeft(-2);//.height(45);
		rowTable.add(musicBtn).pad(2);
		rowTable.add(pauseBtn).pad(2);//.height(45);
		sidebar.row();
		sidebar.add(quitBtn).padTop(2);
		sidebar.row();
		sidebar.add(helpBtn);

		// uncomment these if you want table lines to show
		//layout.debug();
		//sidebar.debug();
		
		
		//hexmap.addScreen("helpScreen", new HelpScreen(hexmap));

		// start menu
		startMenu = new StartMenu("Start", skin, hexmap);
		//hexmap.pause(true);
		startMenu.setVisible(true);
		startMenu.setPosition(500, 200);
		
		// pop up window with plant building/demolish options 
		buildWin = new BuildingWindow("Building Window", skin, hexmap);
		buildWin.setVisible(false);
		
		researchWin = new ResearchWindow("Research Menu", skin, hexmap);
		researchWin.setPosition(300, 100);
		researchWin.setVisible(false);
		
		// modal pop up window when you win or lose
		endWin = new EndWindow(this, "End Game", skin, hexmap);
		endWin.setPosition(500,300);
		endWin.setVisible(false);

		pauseScreen = new PauseScreen(hexmap);
		hexmap.addScreen("pauseScreen", pauseScreen);
		
		this.addActor(startMenu);
		this.addActor(buildWin);
		//this.addActor(researchWin);
		this.addActor(endWin);
		
		// start with energy shown
		energyBtn.setChecked(true);
		hexmap.showEnergy(true);
		
		this.addListener(new InputListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				
				/*
				if (pauseBtn.isChecked()) {
					pauseBtn.setChecked(false);
					return true;
				}
				*/
				
				if (buildWin.isVisible()) {
					buildWin.setVisible(false);
					return true;
				}
				
				if (button == Buttons.RIGHT) {
					Cell cell = hexmap.getMouseOverTile();
				
					if (cell != null && cell.owner instanceof PlayerEntity) {
						hexmap.setClickedOn(cell);
						
						boolean wind = cell.getResourceForBuilding("windplant") != null;
						boolean chemical = cell.getResourceForBuilding("chemicalplant") != null;
						boolean solar = cell.getResourceForBuilding("solarplant") != null;
						boolean demolish = cell.getBuilding() != null;
						boolean repair = cell.getRepair();
						boolean clear = cell.getClear();
						
						buildWin.setX(x);
						buildWin.setY(y);
						buildWin.setVisible(true);
						
						if (clear) {
							buildWin.populate(false, false, false, false, false, clear);
						}
						else if (demolish) {
							buildWin.populate(false, false, false, demolish, repair, false);
						}
						else {
							buildWin.populate(wind,chemical,solar,false, false, false);
						}

						
						return true;
					}
					
				}
				
				if (buildWin.isVisible()) {
					buildWin.setVisible(false);
					return true;
				}
				
				/*
				if (researchWin.isVisible()) {
					researchWin.setVisible(false);
					return true;
				}
				*/
				return false;
			}
			
		});
		
		/*
		addEnergy.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				energy += 1;
				energyDisp.setText("Energy: " + energy);
				hexmap.addEnergy(1);
				return true;
			}
		});
		*/
		
		resourcesBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(resourcesBtn.isChecked()) {
					HexMapInterface.playButtonClickOn();
				} else {
					HexMapInterface.playButtonClickOff();
				}
				hexmap.showResourceIcons(resourcesBtn.isChecked());
			}
		});
		
		energyBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(energyBtn.isChecked()) {
					HexMapInterface.playButtonClickOn();
				} else {
					HexMapInterface.playButtonClickOff();
				}
				hexmap.showEnergy(energyBtn.isChecked());
			}
		});

		/*
		tbResearch.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				if (!researchWin.isVisible()) {
					researchWin.setVisible(true);
				}
				else {
					researchWin.setVisible(false);
				}
				return true;
			}
		});
		*/
		
		soundBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				if (!soundBtn.isChecked()) {
					HexMapInterface.playButtonClickOn();
					//hexmap.mute(true);
					HexMapInterface.setMute(true);
				}
				else{
					HexMapInterface.setMute(false);
					//hexmap.mute(false);
					HexMapInterface.playButtonClickOff();
				}
				return true;
			}
		});
		
		musicBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				if (!musicBtn.isChecked()) {
					HexMapInterface.muteMusic(true);
					HexMapInterface.muteMusic(true);
				}
				else{
					HexMapInterface.muteMusic(false);
					HexMapInterface.muteMusic(false);
				}
				return true;
			}
		});
		pauseBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				if (!pauseBtn.isChecked()) {
					pauseBtn.setBackground(skin.newDrawable("play"));
					//pauseBtn.setChecked(true);
					HexMapInterface.playButtonClickOn();
				}
				else {
					HexMapInterface.playButtonClickOff();
					hexmap.pause(false);
				}
				return true;
			}
			
			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				pauseBtn.setBackground(skin.newDrawable("play"));
				hexmap.pause(true);
			}
		});
				
		quitBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {

				//HexMapInterface.playButtonClickOn();
				hexmap.quit();
				//hexmap.pause(true);
				startMenu.setVisible(true);
				return true;
			}
		});
		
		helpBtn.addListener(new InputListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				//helpWin.setVisible(true);
				HexMapInterface.playButtonClickOn();
				//hexmap.setScreen("helpScreen");
				hexmap.pause(true);
				Gdx.net.openURI("http://darkhexxa.github.io/corruption/help.html");
				return true;
			}
		});
		
		/*
		helpQuit.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				//System.out.println("quit");
				//helpWin.setVisible(false);
				return true;
			}
		});
		*/
	}
	
	
	private void addColour(String cName, Color colour) {
		pixmap.setColor(colour);
		pixmap.fill();
		skin.add(cName, new Texture(pixmap));
	}
	
	public void setEnergy(int energy) {
		energyDisp.setText("Energy: " + energy);
	}
	
	public float getSidebarWidth() {
		return sidebar.getWidth();
	}
	
	public float getSidebarHeight() {
		return sidebar.getHeight();
	}
	
	public void endGame(boolean win) {
		if (!endWin.isVisible()) {
			endWin.populate(win);
			endWin.setVisible(true);
			endWin.setModal(true);
		}
	}
	
	public void unpause() {
		//pauseBtn.setChecked(false);
	}
	
	public void openStartWindow() {
		startMenu.setVisible(true);
	}
	
	public void start() {
		startMenu.setVisible(false);
	}
	
}
