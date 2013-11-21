package com.me.corruption.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.me.corruption.hexMap.HexMap;
import com.me.corruption.hexMap.HexMapInterface;

public class ResearchWindow extends Window {
	HexMapInterface hexmap;
	Skin skin;
	
	private ImageButton wind;
	private ImageButton chemical;
	private ImageButton solar;
	private TextButton close;
	
	public ResearchWindow(String title, Skin skin, HexMapInterface hexmap) {
		super(title, skin, "dialog");
		this.skin = skin;
		this.hexmap = hexmap;
		//this.setBackground(skin.newDrawable("lgrey"));
		//this.getButtonTable().setBackground(skin.newDrawable("grey"));
		
		wind = new ImageButton(skin.newDrawable("buttonBack"));
		chemical = new ImageButton(skin.newDrawable("buttonBack"));
		solar = new ImageButton(skin.newDrawable("buttonBack"));
		close = new TextButton("Close", skin);
		
		this.left();
		wind.setSize(300,150);
		this.defaults().pad(2).padLeft(30).left();
		
		this.add(wind);
		this.row();
		this.add(chemical);
		this.row();
		this.add(solar);
		this.row();
		//this.pack();
		
		wind.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("research wind");
				return true;
			}
		});
		
		chemical.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("research chemical");
				return true;
			}
		});
		
		solar.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("research solar");
				return true;
			}
		});
		
		close.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {

				return false;
			}
		});
		

		
		this.setSize(600, 300);
	}
	
	
}
