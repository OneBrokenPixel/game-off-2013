package com.me.corruption.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;

public class GameUI extends Stage {
	
	private Pixmap pixmap;
	private Skin skin;
	
	public GameUI() {
		skin = new Skin();
		pixmap = new Pixmap(1,1, Format.RGBA8888);
		
		addColour("black", Color.BLACK);
		addColour("white", Color.WHITE);
		addColour("lgrey", Color.LIGHT_GRAY);
		skin.add("bitmapFont", new BitmapFont());
		

		//test.setHeight(200);
		//test.setWidth(500);
		//test.setBackground(skin.newDrawable("white"));
		
		Table layout = new Table();
		layout.setFillParent(true);
		this.addActor(layout);
		
		Table sidebar = new Table();
		sidebar.setBackground(skin.newDrawable("lgrey"));
		layout.left().bottom();
		layout.add(sidebar).expandY().fill();

		TextButtonStyle tbstyle = new TextButtonStyle();
		tbstyle.font = skin.getFont("bitmapFont");
		tbstyle.up = skin.newDrawable("black");
		
		TextButton test = new TextButton("test", tbstyle);
		TextButton test2 = new TextButton("test2", tbstyle);
		TextButton test3 = new TextButton("test3", tbstyle);
		TextButton test4 = new TextButton("test4", tbstyle);
		
		sidebar.top();
		sidebar.defaults().width(100).height(50).pad(10);
		sidebar.add(test);
		sidebar.row();
		sidebar.add(test2);
		
		sidebar.bottom();
		sidebar.defaults().width(50).height(50).pad(2);
		sidebar.add(test3);
		sidebar.add(test4);
		layout.debug();
		sidebar.debug();
		
		test.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				System.out.println("test down " + x + " " + y);
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
