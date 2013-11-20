package com.me.corruption.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.me.corruption.hexMap.HexMapInterface;


public class PauseScreen implements Screen {
	
	//private HexMapInterface hexmap;
	private Skin skin;
	private Stage stage;
	
	private Table layout;
	private TextButton resume;

	
	public PauseScreen(final HexMapInterface hexmap)
	{
		//this.hexmap = hexmap;
		this.skin = GameUI.skin;
		
		stage = new Stage();
		
		layout = new Table();
		resume = new TextButton("Change me to an arrow", skin);
		
		layout.setFillParent(true);
		layout.setBackground(skin.newDrawable("transparent"));
		layout.add(resume).expandX().fillX();
		stage.addActor(layout);
		
		resume.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				hexmap.setScreen("gameScreen");
				return true;
			}
		});
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
		stage.act();
		stage.draw();
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		
	}
}
