package com.me.corruption;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.me.corruption.entities.Entity;
import com.me.corruption.entities.PlayerEntity;
import com.me.corruption.hexMap.HexMap;
import com.me.corruption.hexMap.HexMap.Cell;
import com.me.corruption.hexMap.HexMapGenerator;
import com.me.corruption.hexMap.HexMapInterface;
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
		private HexMapInterface ui_if;

		private CorruptionGdxGame gameInstance;
		private GameUI stage;

		private InputMultiplexer multiplexer;

		private boolean pauseGame = false;
		
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

				if (renderer != null) {

					Cell cell = renderer.getMouseOverCell();
					if (cell != null) {
						if (button == Buttons.LEFT) {
							if (cell.owner instanceof PlayerEntity) {
								cell.setRecharge(!cell.recharge);
							}
							else if( map.getPlayer().isAttacking(cell)) {
								map.getPlayer().stopAttack(cell);
							}
							else {
								map.getPlayer().attack(cell);
							}
						}
						
						if (button == Buttons.MIDDLE){
							if (cell.owner instanceof PlayerEntity) {
								map.getPlayer().removeCell(cell);
							}
							map.getCorruption().addOwnedCell(cell);
						
						}
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
				if (renderer != null) {

					v.set(screenX, screenY, 0f);
					camera.unproject(v);

					final Cell mouseOverCell = renderer.getCellFromTouchCoord(v);

					renderer.setMouseOverCell(mouseOverCell);

				}
				return true;
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

		public GameScreen(CorruptionGdxGame instance) {

			this.gameInstance = instance;

			final float w = Gdx.graphics.getWidth();
			final float h = Gdx.graphics.getHeight();
			batch = new SpriteBatch();

			camera = new OrthographicCamera(w, h);

			map = HexMapGenerator.generateTestMap(14, 8);
			renderer = new HexMapRenderer(batch, map);
			ui_if = new HexMapInterface(gameInstance, renderer);

			stage = new GameUI(ui_if);

			multiplexer = new InputMultiplexer();
			multiplexer.addProcessor(stage);
			multiplexer.addProcessor(processor);

			ui_if.addScreen("gameScreen", this);
			centerMap();
		
		}

		@Override
		public void render(float delta) {

			// all update is here! 

			if (!pauseGame) {
				this.map.getPlayer().update(delta);
				this.map.getCorruption().update(delta);

				stage.setEnergy(ui_if.getEnergyBank());

				if (this.map.getPlayer().ownedAmount() <= 0) {
					pauseGame = true;
					stage.endGame(false);
				}
				if (this.map.getCorruption().ownedAmount() <= 0) {
					pauseGame = true;
					stage.endGame(true);
				}
			}
			
			Gdx.gl.glClearColor(0, 0, 0.0f, 1);
			Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

			if (camera != null) {
				renderer.setView(camera);
				renderer.render();
			}

			stage.act();
			stage.draw();

			// Table.drawDebug(stage);
		}

		@Override
		public void resize(int width, int height) {
			camera.viewportWidth = width;
			camera.viewportHeight = height;
			centerMap();
		}

		public void centerMap() {

			final float map_w = map.getWidth() * (map.getTile_width() * 0.75f);
			final float map_h = map.getHeight() * map.getTile_height() + map.getTile_height() * 0.5f;

			final float sidebar = 100f + 20f + 20f;

			final int camera_x = (int) (((map_w - sidebar) / 2) - (map.getTile_width() * 0.5f));
			final int camera_y = (int) ((map_h / 2) - (map.getTile_height() * 0.5f));
			camera.position.set(camera_x, camera_y, 0f);
			camera.update();
		}

		@Override
		public void show() {
			Gdx.input.setInputProcessor(multiplexer);
		}

		@Override
		public void hide() {
			// TODO Auto-generated method stub

			// batch.dispose();
			Gdx.input.setInputProcessor(null);
		}

		@Override
		public void pause() {
			pauseGame = true;
		}

		@Override
		public void resume() {
			pauseGame = false;
		}

		@Override
		public void dispose() {
			batch.dispose();
		}

	}

	GameScreen game = null;

	@Override
	public void create() {
		this.game = new GameScreen(this);
		setScreen(game);
	}

}
