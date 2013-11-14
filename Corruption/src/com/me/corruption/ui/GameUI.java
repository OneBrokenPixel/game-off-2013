package com.me.corruption.ui;

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
import com.me.corruption.hexMap.HexMapRenderer;

public class GameUI extends Stage {
	final private HexMapRenderer renderer;
	
	private Pixmap pixmap;
	private Skin skin;
	
	private LabelStyle lstyle;
	private Label energyDisp;
	private int energy; // for ui testing purposes only
	
	private ButtonGroup renderGroup;
	
	private TextButton tbResources;
	private TextButton tbEnergy;
	private TextButton tbResearch;
	
	private TextButton tbMute;   // change mute and pause to icons
	private TextButton tbPause;
	private TextButton tbQuit;
	
	private TextButton tbHelp;
	private Window helpWin;
	private TextButton helpQuit;
	
	public GameUI(HexMapRenderer renderer) {
		
		this.renderer = renderer;
		
		skin = new Skin();
		pixmap = new Pixmap(1,1, Format.RGBA8888);
		
		addColour("black", Color.BLACK);
		addColour("white", Color.WHITE);
		addColour("lgrey", Color.LIGHT_GRAY);
		addColour("red", Color.RED);
		
		skin.add("bitmapFont", new BitmapFont());

		Table layout = new Table();
		layout.setFillParent(true);
		this.addActor(layout);
		
		Table sidebar = new Table();
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
		
		TextButtonStyle toggleStyle = new TextButtonStyle();
		toggleStyle.font = skin.getFont("bitmapFont");
		toggleStyle.up = skin.newDrawable("black");
		toggleStyle.checked = skin.newDrawable("red");

		final TextButton addEnergy = new TextButton("Cheat!", tbStyle);  // this will be deleted later
		tbResources = new TextButton("Show Resources", toggleStyle);
		tbEnergy = new TextButton("Show Energy", toggleStyle);
		tbResearch = new TextButton("Research", tbStyle);

		sidebar.add(addEnergy);
		sidebar.row();
		sidebar.add(tbResources);
		sidebar.row();
		sidebar.add(tbEnergy);
		sidebar.row();
		sidebar.add(tbResearch).padBottom(180);
		sidebar.row();
		
		Table rowTable = new Table();
		sidebar.add(rowTable).expandX().fillX().pad(2);
		
		tbMute = new TextButton("Mute", toggleStyle);
		tbPause = new TextButton("Pause", toggleStyle);
		tbQuit = new TextButton("Quit", tbStyle);
		tbHelp = new TextButton("Help", tbStyle);
		
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
		
		renderGroup = new ButtonGroup();
		renderGroup.add(tbResources);
		renderGroup.add(tbEnergy);
		renderGroup.setMaxCheckCount(1);
		renderGroup.setMinCheckCount(0);
		renderGroup.uncheckAll();
		
		WindowStyle winStyle = new WindowStyle();
		winStyle.titleFont = skin.getFont("bitmapFont");
		
		skin.add("default", winStyle);
		skin.add("default",lstyle);
		skin.add("default", tbStyle);
	
		helpQuit = new TextButton("Quit", skin);
		
		helpWin = new Window("How to play", skin);
		//helpWin.getButtonTable().add(new TextButton("X", skin)).height(helpWin.getPadTop());
		helpWin.getButtonTable().add(helpQuit).height(50).width(100);
		helpWin.add("This is the how to play window");
		helpWin.setModal(true);
		helpWin.setVisible(false);
		helpWin.setWidth(400);
		helpWin.setHeight(150);

		helpWin.setX(300);
		helpWin.setY(300);
		helpWin.setBackground(skin.newDrawable("lgrey"));
		helpWin.setMovable(true);// <--- why doesn't this work?

		this.addActor(helpWin);
		addEnergy.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				energy += 1;
				energyDisp.setText("Energy: " + energy);
				return true;
			}
		});
		
		tbResources.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				//toggleRenderGroup(tbResources);
				resetRenderGroup();
				if (!tbResources.isChecked()) {
					tbResources.setText("Hide Resources");
				
				}
				return true;
			}
		});
		
		tbEnergy.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				resetRenderGroup();
				if (!tbEnergy.isChecked()) {
					tbEnergy.setText("Hide Energy");
				}
				return true;
			}
		});
		
		tbResearch.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("Research Menu");
				return true;
			}
		});
		
		tbMute.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("Shut up!");
				return true;
			}
		});
		
		tbPause.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("Pause");
				return true;
			}
		});
				
		tbQuit.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
					System.out.println("Quit");
					return true;
				}
		});
		
		tbHelp.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				//System.out.println("How to play");
				//if (helpWin.isVisible()) {
				//	helpWin.setVisible(false);
				//	System.out.println("ssshh");
				//}
				//else 
				
				helpWin.setVisible(true);
				return true;
			}
		});
		
		helpQuit.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("quit");
				helpWin.setVisible(false);
				return true;
			}
		});
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
}
