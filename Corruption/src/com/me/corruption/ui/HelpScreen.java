package com.me.corruption.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.me.corruption.hexMap.HexMapInterface;

public class HelpScreen implements Screen {

	HexMapInterface hexmap;
	HelpStage stage;
	Skin skin;
	
	private class HelpStage extends Stage {
		Table layout;
		Label info;
		
		TextButton button;
		
		public HelpStage() {
			layout = new Table();
			layout.setFillParent(true);
			layout.setBackground(skin.newDrawable("lgrey"));
			
			button = new TextButton("quit", skin);
			layout.add(button).expandX().fillX();
			
			button.addListener(new InputListener() {
				@Override
				public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
					System.out.println("quit out of help");
					hexmap.setScreen("gameScreen");
					return true;
				}
			});
			this.addActor(layout);
		}
	}
	
	public HelpScreen(HexMapInterface hexmap) {
		this.hexmap = hexmap;
		this.skin = GameUI.skin;
		stage = new HelpStage();
	}
	
	@Override
	public void render(float delta) {
		stage.act();
		stage.draw();
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
