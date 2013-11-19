package com.me.corruption.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.me.corruption.hexMap.HexMapInterface;

public class EndWindow extends Window {
	
	private HexMapInterface hexmap;
	private Skin skin;
	
	private Label label;
	private TextButton ok;
	
	public EndWindow(String title, Skin skin, final HexMapInterface hexmap) {
		super(title,skin);
		this.skin = skin;
		this.hexmap = hexmap;
	}
	
	public void populate(boolean win) {
		if (win) {
			label = new Label("Congratulations, you win!", skin);
		}
		else {
			label = new Label("Too bad, you lost!", skin);
		}
		
		
		ok = new TextButton("OK", skin);
		
		this.add(label);
		this.row();
		this.add(ok);
		this.pack();
		
		ok.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("I don't know what sys.exit(0) does in javascript, let's find out");
				System.out.println("In other words, sort out a damned start screen");
				System.exit(0);
				return true;
			}
		});
		
	}
}
