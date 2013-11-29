package com.me.corruption.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.me.corruption.hexMap.HexMapInterface;

public class StartMenu extends Window {
	HexMapInterface hexmap;
	Skin skin;
	
	private TextButton start;
	private TextButton startMuted;
	private TextButton help;
	private TextButton credits;
	
	public StartMenu(String title, Skin skin, HexMapInterface ui_if) {
		super(title, skin, "dialog");
		this.skin = skin;
		this.hexmap = ui_if;
		

		
		start = new TextButton("Start Game", skin);
		startMuted = new TextButton("Start Game (muted)", skin);
		help = new TextButton("How To Play", skin);
		credits = new TextButton("Credits", skin);
		
		this.defaults().pad(5).padTop(2);
		
		this.add(start).height(45).width(150);
		this.row();
		this.add(startMuted).height(45).width(150);
		this.row();
		this.add(help).height(45).width(150);
		this.row();
		this.add(credits).height(45).width(150);
		this.pack();
		
		start.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				hexmap.start();
				return true;
			}
		});
		
		startMuted.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				hexmap.startMuted();
				return true;
			}
		});
		
		help.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				Gdx.net.openURI("http://darkhexxa.github.io/corruption/help.html");
				return true;
			}
		});
		
		credits.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
					Gdx.net.openURI("http://darkhexxa.github.io/corruption/credits.html");
				return true;
			}
		});
		
	}

}
