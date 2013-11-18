package com.me.corruption.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
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
	private int energy; // for ui testing purposes only
	
	private ButtonGroup renderGroup;
	
	private TextButton tbResources;
	private TextButton tbEnergy;
	private TextButton tbResearch;
	private TextButton tbShields;
	
	private TextButton tbMute;   // change mute and pause to icons
	private TextButton tbPause;
	private TextButton tbQuit;
	
	private TextButton tbHelp;
	//private Window helpWin;
	//private TextButton helpQuit;
	//private HelpScreen hScreen;
	
	private BuildingWindow buildWin;
	private ResearchWindow researchWin;
	
	public GameUI(final HexMapInterface hexmap) {
		
		//this.renderer = renderer;
		this.hexmap = hexmap;

		
		skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		pixmap = new Pixmap(1,1, Format.RGBA8888);
		
		addColour("black", Color.BLACK);
		addColour("white", Color.WHITE);
		addColour("lgrey", Color.LIGHT_GRAY);
		addColour("red", Color.RED);
		
		skin.add("bitmapFont", new BitmapFont());

		layout = new Table();
		layout.setFillParent(true);
		this.addActor(layout);
		
		sidebar = new Table();
		sidebar.setBackground(skin.newDrawable("lgrey"));
		layout.left().bottom();
		layout.add(sidebar).expandY().fill();

		sidebar.top();
		sidebar.defaults().width(120).height(40).padLeft(20).padRight(20).padBottom(4);
		sidebar.row();
		
		lstyle = new LabelStyle();
		lstyle.font = skin.getFont("bitmapFont");
		lstyle.fontColor = Color.BLACK; // <--- why does this look fuzzy???
		
		energyDisp = new Label("Energy: 0", lstyle);
		sidebar.add(energyDisp).padLeft(10).padTop(20).padBottom(30);
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
		tbResources = new TextButton("Show Resources", skin, "toggle");
		tbEnergy = new TextButton("Show Energy", skin, "toggle");
		tbResearch = new TextButton("Research", skin);
		tbShields = new TextButton("Shields", skin, "toggle");

		sidebar.add(addEnergy);
		sidebar.row();
		sidebar.add(tbResources);
		sidebar.row();
		sidebar.add(tbEnergy);
		sidebar.row();
		sidebar.add(tbShields);
		sidebar.row();
		sidebar.add(tbResearch).padBottom(150);
		sidebar.row();
		
		Table rowTable = new Table();
		sidebar.add(rowTable).expandX().fillX().pad(2);
		
		tbMute = new TextButton("Mute", skin, "toggle");
		tbPause = new TextButton("Pause", skin, "toggle");
		tbQuit = new TextButton("Quit", skin);
		tbHelp = new TextButton("Help", skin);
		
		//sidebar.defaults().width(50).height(50).pad(2);
		rowTable.add(tbMute).pad(2).height(45);
		//sidebar.row();
		rowTable.add(tbPause).pad(2).height(45);
		sidebar.row();
		sidebar.add(tbQuit).padTop(2);
		sidebar.row();
		sidebar.add(tbHelp);

		layout.debug();
		sidebar.debug();
		
		/*
		renderGroup = new ButtonGroup();
		renderGroup.add(tbResources);
		renderGroup.add(tbEnergy);
		//renderGroup.add(tbShields);
		renderGroup.setMaxCheckCount(1);
		renderGroup.setMinCheckCount(0);
		renderGroup.uncheckAll();
		*/
		
		//WindowStyle winStyle = new WindowStyle();
		//winStyle.titleFont = skin.getFont("bitmapFont");
		
		//skin.add("default", winStyle);
	
		//helpQuit = new TextButton("Quit", skin);
		
		hexmap.addScreen("helpScreen", new HelpScreen(hexmap));

		buildWin = new BuildingWindow("Building Window", skin, hexmap);
		buildWin.setVisible(false);
		researchWin = new ResearchWindow("Research Menu", skin, hexmap);
		researchWin.setPosition(300, 300);
		researchWin.setVisible(false);
		
		this.addActor(buildWin);
		this.addActor(researchWin);
		
		this.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				
				if (buildWin.isVisible()) {
					buildWin.setVisible(false);
					return true;
				}
				
				if (button == Buttons.LEFT) {
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
				
				if (researchWin.isVisible()) {
					researchWin.setVisible(false);
					return true;
				}
				
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
		
		tbResources.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				//toggleRenderGroup(tbResources);
				//resetRenderGroup();
				if (!tbResources.isChecked()) {
					tbResources.setText("Hide Resources");
					hexmap.showResourceIcons(true);
				}
				else {
					tbResources.setText("Show Resources");
					hexmap.showResourceIcons(false);
				}
				return true;
			}
		});
		
		tbEnergy.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				//resetRenderGroup();
				if (!tbEnergy.isChecked()) {
					tbEnergy.setText("Hide Energy");
					hexmap.showEnergy(true);
				}
				else {
					tbEnergy.setText("Show Resources");
					hexmap.showEnergy(false);
				}
				return true;
			}
		});
		
		tbShields.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				if (!tbShields.isChecked()) {
					hexmap.addEnergyMode(true);
				}
				else {
					hexmap.addEnergyMode(false);
				}
				return true;
			}
		});
		
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

	private void resetRenderGroup() {
		//renderGroup.uncheckAll();
		tbResources.setText("Show Resources");
		tbEnergy.setText("Show Energy");
		
	}
	
	private void toggleRenderGroup(Button button) {
		if (renderGroup.getButtons().contains(button, false)) {

		}
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
}
