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
	private GameUI p;
	
	public EndWindow(GameUI parent, String title, Skin skin, final HexMapInterface hexmap) {
		super(title,skin);
		this.skin = skin;
		this.hexmap = hexmap;
		this.p = parent;
	}
	
	public void populate(boolean win) {
		this.clear();
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
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				//System.out.println("I don't know what sys.exit(0) does in javascript, let's find out");
				//System.out.println("In other words, sort out a damned start screen");
				//System.exit(0);
				//hexmap.startMuted();
				setVisible(false);
				hexmap.quit();
				p.openStartWindow();
				return true;
			}
		});
		
	}
}
