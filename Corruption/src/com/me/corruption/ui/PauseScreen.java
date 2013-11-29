package com.me.corruption.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.me.corruption.hexMap.HexMapInterface;


public class PauseScreen implements Screen {
	
	private HexMapInterface hexmap;
	private Skin skin;
	private Stage stage;
	
	private Table layout;
	//private TextButton resume;
	private ImageButton resume;
	private SpriteBatch spriteBatch;
	private Texture splsh;
	private Image image;
	
	public PauseScreen(final HexMapInterface hexmap)
	{
		this.hexmap = hexmap;
		this.skin = GameUI.skin;
		
		stage = new Stage();
		
		layout = new Table();
		image = new Image(skin, "pauseImage");
		//resume = new TextButton("Change me to an arrow", skin);
		//resume = new ImageButton(skin, "pauseStyle");
		
		layout.setFillParent(true);
		//layout.setBackground(skin.newDrawable("transparent"));
		layout.add(image);
		//layout.add(resume);//.expandX().fillX();
		stage.addActor(layout);
		
		stage.addListener(new InputListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				//hexmap.setScreen("gameScreen");
				//return super.touchDown(event, x, y, pointer, button);
				return true;
			}
			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				hexmap.pause(false);
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
        //spriteBatch = new SpriteBatch();
        //splsh = new Texture(Gdx.files.internal("spritesheets/pauseSplash.png"));

		
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

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(float delta) {
		/*
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        spriteBatch.begin();
        spriteBatch.draw(splsh, 0, 0);
        spriteBatch.end();
        
        if(Gdx.input.justTouched())
        	hexmap.setScreen("gameScreen");
		*/
		stage.act();
		stage.draw();
		
	}
}
