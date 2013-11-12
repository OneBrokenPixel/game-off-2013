package com.me.corruption.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

public class GameUI extends Stage {
	
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
	
	public GameUI() {
		skin = new Skin();
		pixmap = new Pixmap(1,1, Format.RGBA8888);
		
		addColour("black", Color.BLACK);
		addColour("white", Color.WHITE);
		addColour("lgrey", Color.LIGHT_GRAY);
		skin.add("bitmapFont", new BitmapFont());

		Table layout = new Table();
		layout.setFillParent(true);
		this.addActor(layout);
		
		Table sidebar = new Table();
		sidebar.setBackground(skin.newDrawable("lgrey"));
		layout.left().bottom();
		layout.add(sidebar).expandY().fill();

		sidebar.top();
		sidebar.defaults().width(120).height(40).padLeft(20).padRight(20);
		sidebar.row();
		
		lstyle = new LabelStyle();
		lstyle.font = skin.getFont("bitmapFont");
		lstyle.fontColor = Color.BLACK; // <--- why does this look fuzzy???
		
		energyDisp = new Label("Energy: 0", lstyle);
		sidebar.add(energyDisp).padLeft(10).padTop(20).padBottom(30);
		sidebar.row();
		
		TextButtonStyle tbstyle = new TextButtonStyle();
		tbstyle.font = skin.getFont("bitmapFont");
		tbstyle.up = skin.newDrawable("black");
		
		TextButton addEnergy = new TextButton("Cheat!", tbstyle);  // this will be deleted later
		tbResources = new TextButton("Show Resources", tbstyle);
		tbEnergy = new TextButton("Show Energy", tbstyle);
		tbResearch = new TextButton("Research", tbstyle);

		sidebar.add(addEnergy).padBottom(4);
		sidebar.row();
		sidebar.add(tbResources).padBottom(4);
		sidebar.row();
		sidebar.add(tbEnergy).padBottom(4);
		sidebar.row();
		sidebar.add(tbResearch).padBottom(90);
		sidebar.row();
		
		tbMute = new TextButton("Mute", tbstyle);
		tbPause = new TextButton("Pause", tbstyle);
		tbQuit = new TextButton("Quit", tbstyle);
		tbHelp = new TextButton("Help", tbstyle);
		
		sidebar.defaults().width(50).height(50).pad(2);
		sidebar.add(tbMute);
		sidebar.row();
		sidebar.add(tbPause);
		sidebar.row();
		sidebar.add(tbQuit);
		sidebar.row();
		sidebar.add(tbHelp);

		layout.debug();
		sidebar.debug();
		
		addEnergy.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				energy += 1;
				energyDisp.setText("Energy: " + energy);
				return true;
			}
		});
		
		tbResources.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				System.out.println(tbResources.getText());
				tbResources.setText("Hide Resources");
				return true;
			}
		});
		
		tbEnergy.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				System.out.println(tbEnergy.getText());
				tbEnergy.setText("Hide Energy");
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
				System.out.println("How to play");
				return true;
			}
		});
	}
	
	
	
	
	private void addColour(String cName, Color colour) {
		pixmap.setColor(colour);
		pixmap.fill();
		skin.add(cName, new Texture(pixmap));
	}
}
