package com.me.corruption;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.me.corruption.hexMap.HexMap;
import com.me.corruption.hexMap.HexMapGenerator;
import com.me.corruption.hexMap.HexMapRenderer;

public class CorruptionGdxGame implements ApplicationListener {
	private OrthographicCamera camera;
	private SpriteBatch batch;

	
	private HexMap map;
	private HexMapRenderer renderer;
	
	InputProcessor processor = new InputProcessor() {
		
		private Vector3 v = new Vector3();
		
		@Override
		public boolean touchUp(int screenX, int screenY, int pointer, int button) {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public boolean touchDragged(int screenX, int screenY, int pointer) {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public boolean touchDown(int screenX, int screenY, int pointer, int button) {
			// TODO Auto-generated method stub
			
			if( renderer != null ){
				
				v.set(screenX, screenY, 0f);
				camera.unproject(v);
				
				
				renderer.getCellFromTouchCoord(v);
			}
			
			return false;
		}
		
		@Override
		public boolean scrolled(int amount) {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public boolean mouseMoved(int screenX, int screenY) {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public boolean keyUp(int keycode) {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public boolean keyTyped(char character) {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public boolean keyDown(int keycode) {
			// TODO Auto-generated method stub
			return false;
		}
	};
	
	@Override
	public void create() {		
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera(w, h);
		batch = new SpriteBatch();
		
		map = HexMapGenerator.generateTestMap(10, 10);
		renderer = new HexMapRenderer(batch,map);
		
		camera.position.set(renderer.getMapPixelWidth()/2, renderer.getMapPixelHeight()/2, 0f);
		camera.update();
		
		Gdx.input.setInputProcessor(processor);
	}

	@Override
	public void dispose() {
		batch.dispose();
	}

	@Override
	public void render() {		
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		renderer.setView(camera);
		renderer.render();
		
	}

	@Override
	public void resize(int width, int height) {
		camera.viewportWidth = width;
		camera.viewportHeight = height;
		camera.update();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
