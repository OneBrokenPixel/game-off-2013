package com.me.corruption.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.me.corruption.entities.PlayerEntity;
import com.me.corruption.hexMap.HexMap.Cell;
import com.me.corruption.hexMap.HexMap.Resource;
import com.me.corruption.hexMap.HexMapInterface;
import com.me.corruption.hexMap.HexMapRenderer;

public class GameUI extends Stage {
	//final private HexMapRenderer renderer;
	final private HexMapInterface hexmap;
	
	private Pixmap pixmap;
	public static Skin skin;
	
	private Table sidebar;
	private Table layout; 
	
	private LabelStyle lstyle;
	private Label energyDisp;
	//private ImageButton energyDisp;
	private int energy; // for ui testing purposes only

	private ImageButton resourcesBtn;
	private ImageButton energyBtn;
	private TextButton tbResearch;
	
	private TextButton tbMute;   // change mute and pause to icons
	private TextButton tbPause;
	private ImageButton tbQuit;
	
	private ImageButton tbHelp;

	private BuildingWindow buildWin;
	private ResearchWindow researchWin;
	private EndWindow endWin;
	private PauseScreen pauseScreen;
	
	
	public GameUI(final HexMapInterface hexmap) {

		// interaction with the rest of the game (mainly hexmap)
		this.hexmap = hexmap;

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
		//sidebar.setBackground(skin.newDrawable("lgrey"));
		sidebar.setBackground(skin.newDrawable("sideBack"));
		layout.left().bottom();
		layout.add(sidebar).expandY().fill().pad(10);

		sidebar.left().top();
		//sidebar.defaults().width(120).height(40).padLeft(20).padRight(20).padBottom(4);
		//sidebar.defaults().padLeft(20).padRight(20).padBottom(4);
		sidebar.defaults().padBottom(4).padLeft(-13f);
		sidebar.row();
		
		
		lstyle = new LabelStyle();
		//lstyle.background = skin.newDrawable("energyback");
		lstyle.font = skin.getFont("default-font");
		lstyle.fontColor = Color.WHITE;
		energyDisp = new Label("Energy: 0", lstyle);
		//energyDisp.setFontScale(1.2f);
		//energyDisp.setSize(163f, 48f);
		
		 
		//energyDisp = new ImageButton(skin.newDrawable("energyback"));
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
		
		final TextButton addEnergy = new TextButton("Cheat!", tbStyle);  // this will be deleted later

		resourcesBtn = new ImageButton(skin.newDrawable("showresources"), skin.newDrawable("hideresources"), skin.newDrawable("hideresources"));
		energyBtn = new ImageButton(skin.newDrawable("showenergy"), skin.newDrawable("hideenergy"), skin.newDrawable("hideenergy"));

		//sidebar.add(addEnergy);
		//sidebar.row();
		sidebar.add(resourcesBtn);
		sidebar.row();
		sidebar.add(energyBtn).padBottom(150);
		sidebar.row();
		//sidebar.add(tbResearch).padBottom(150);
		//sidebar.row();
		
		Table rowTable = new Table();
		sidebar.add(rowTable).expandX().fillX().pad(2);
		
		tbMute = new TextButton("Mute", skin, "toggle");
		tbPause = new TextButton("Pause", skin, "toggle");
		tbHelp = new ImageButton(skin.newDrawable("help"), skin.newDrawable("helpdown"));
		tbQuit = new ImageButton(skin.newDrawable("quit"), skin.newDrawable("quitdown"));

		
		rowTable.add(tbMute).pad(2).height(45);
		rowTable.add(tbPause).pad(2).height(45);
		sidebar.row();
		sidebar.add(tbQuit).padTop(2);
		sidebar.row();
		sidebar.add(tbHelp);

		// uncomment these if you want table lines to show
		//layout.debug();
		//sidebar.debug();
		
		
		//hexmap.addScreen("helpScreen", new HelpScreen(hexmap));

		// pop up window with plant building/demolish options 
		buildWin = new BuildingWindow("Building Window", skin, hexmap);
		buildWin.setVisible(false);
		
		researchWin = new ResearchWindow("Research Menu", skin, hexmap);
		researchWin.setPosition(300, 100);
		researchWin.setVisible(false);
		
		// modal pop up window when you win or lose
		endWin = new EndWindow("End Game", skin, hexmap);
		endWin.setPosition(500,300);
		endWin.setVisible(false);
		
		// show pause screen when game is paused
		ImageButtonStyle pauseStyle = new ImageButtonStyle();
		pauseStyle.up = skin.newDrawable("pauseUp");
		pauseStyle.down = skin.newDrawable("pauseDown");
		skin.add("pauseStyle", pauseStyle);
		
		pauseScreen = new PauseScreen(hexmap);
		hexmap.addScreen("pauseScreen", pauseScreen);
		
		this.addActor(buildWin);
		//this.addActor(researchWin);
		this.addActor(endWin);
		
		// start with energy shown
		energyBtn.setChecked(true);
		hexmap.showEnergy(true);
		
		this.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				
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
						
						buildWin.setX(x);
						buildWin.setY(y);
						buildWin.setVisible(true);
						
						if (demolish) {
							buildWin.populate(false, false, false, true);
						}
						else {
							buildWin.populate(wind,chemical,solar,false);
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
		
		addEnergy.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				energy += 1;
				energyDisp.setText("Energy: " + energy);
				hexmap.addEnergy(1);
				return true;
			}
		});
		
		resourcesBtn.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				hexmap.showResourceIcons(!resourcesBtn.isChecked());
				return true;
			}
		});
		
		energyBtn.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				hexmap.showEnergy(!energyBtn.isChecked());
				return true;
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
		
		tbMute.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				if (!tbMute.isChecked())
					hexmap.mute(true);
				else
					hexmap.mute(false);
				return true;
			}
		});
		
		tbPause.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				if (!tbPause.isChecked())
					hexmap.pause(true);
				else
					hexmap.pause(false);
				return true;
			}
		});
				
		tbQuit.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				// TODO are you sure you want to quit?
				System.out.println(getWidth());
				hexmap.quit();
				return true;
			}
		});
		
		tbHelp.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				//helpWin.setVisible(true);
				hexmap.setScreen("helpScreen");
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
		endWin.populate(win);
		endWin.setVisible(true);
		endWin.setModal(true);
	}
	
}
