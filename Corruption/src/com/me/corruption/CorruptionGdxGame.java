package com.me.corruption;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.me.corruption.hexMap.HexMap;
import com.me.corruption.hexMap.HexMap.Cell;
import com.me.corruption.hexMap.HexMapGenerator;
import com.me.corruption.hexMap.HexMapRenderer;
import com.me.corruption.ui.GameUI;


/**
 * 
 * @author Anthony/Marie
 *
 */
public class CorruptionGdxGame extends Game {
	
	public class GameScreen implements Screen {

		private OrthographicCamera camera;
		private SpriteBatch batch;

		private HexMap map;
		private HexMapRenderer renderer;
		
		private CorruptionGdxGame gameInstance;	
		private GameUI stage;
		
		private InputMultiplexer multiplexer;
		
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
					
					Cell cell = renderer.getCellFromTouchCoord(v);
					if( cell != null ) {
						
						if(cell.owner.contains("player")){
							cell.building.set("chemicalplant");
						}
						else {
							cell.setOwner("player");
						}
						System.out.println(cell);
					}
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
		
	
		
		public GameScreen(CorruptionGdxGame instance ) {
			
			this.gameInstance = instance;


		}
		
		@Override
		public void render(float delta) {
			Gdx.gl.glClearColor(0, 0, 0.0f, 1);
			Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			
			renderer.setView(camera);
			renderer.render();
			
			stage.act();
			stage.draw();
			//Table.drawDebug(stage);
		}

		@Override
		public void resize(int width, int height) {
			camera.viewportWidth = width;
			camera.viewportHeight = height;
			centerMap();
		}

		public void centerMap() {
			final float w = Gdx.graphics.getWidth();
			final float h = Gdx.graphics.getHeight();
			
			final float map_w = map.getWidth()  * (map.getTile_width()*0.75f);
			final float map_h = map.getHeight() * map.getTile_height() + map.getTile_height()*0.5f;
			
			final float sidebar = 100f+20f+20f;
			
			final int camera_x = (int) (((map_w-sidebar)/2)-(map.getTile_width()*0.5f));
			final int camera_y = (int) ((map_h/2)-(map.getTile_height()*0.5f));
			camera.position.set(camera_x,camera_y, 0f);
			camera.update();
		}
		
		@Override
		public void show() {
			
			stage = new GameUI();
			
			multiplexer = new InputMultiplexer();
			multiplexer.addProcessor(stage);
			multiplexer.addProcessor(processor);			
			
			
			final float w = Gdx.graphics.getWidth();
			final float h = Gdx.graphics.getHeight();
			
			camera = new OrthographicCamera(w, h);
			batch = new SpriteBatch();
			
			map = HexMapGenerator.generateTestMap(14,8);
			renderer = new HexMapRenderer(batch,map);
			
			centerMap();
			
			Gdx.input.setInputProcessor(multiplexer);

		}

		@Override
		public void hide() {
			// TODO Auto-generated method stub
			
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
			batch.dispose();
		}
		
	}
	
	GameScreen game = new GameScreen(this);
	
	@Override
	public void create() {
		setScreen(game);
	}
	

}
