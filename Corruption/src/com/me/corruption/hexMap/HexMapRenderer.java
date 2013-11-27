package com.me.corruption.hexMap;

import static com.badlogic.gdx.graphics.g2d.SpriteBatch.C1;
import static com.badlogic.gdx.graphics.g2d.SpriteBatch.C2;
import static com.badlogic.gdx.graphics.g2d.SpriteBatch.C3;
import static com.badlogic.gdx.graphics.g2d.SpriteBatch.C4;
import static com.badlogic.gdx.graphics.g2d.SpriteBatch.U1;
import static com.badlogic.gdx.graphics.g2d.SpriteBatch.U2;
import static com.badlogic.gdx.graphics.g2d.SpriteBatch.U3;
import static com.badlogic.gdx.graphics.g2d.SpriteBatch.U4;
import static com.badlogic.gdx.graphics.g2d.SpriteBatch.V1;
import static com.badlogic.gdx.graphics.g2d.SpriteBatch.V2;
import static com.badlogic.gdx.graphics.g2d.SpriteBatch.V3;
import static com.badlogic.gdx.graphics.g2d.SpriteBatch.V4;
import static com.badlogic.gdx.graphics.g2d.SpriteBatch.X1;
import static com.badlogic.gdx.graphics.g2d.SpriteBatch.X2;
import static com.badlogic.gdx.graphics.g2d.SpriteBatch.X3;
import static com.badlogic.gdx.graphics.g2d.SpriteBatch.X4;
import static com.badlogic.gdx.graphics.g2d.SpriteBatch.Y1;
import static com.badlogic.gdx.graphics.g2d.SpriteBatch.Y2;
import static com.badlogic.gdx.graphics.g2d.SpriteBatch.Y3;
import static com.badlogic.gdx.graphics.g2d.SpriteBatch.Y4;

import java.util.HashSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.me.corruption.entities.Entity;
import com.me.corruption.entities.Entity_Settings;
import com.me.corruption.entities.PlayerEntity;
import com.me.corruption.hexMap.HexMap.AnimatedSprite;
import com.me.corruption.hexMap.HexMap.Building;
import com.me.corruption.hexMap.HexMap.Cell;
import com.me.corruption.hexMap.HexMap.Resource;

public class HexMapRenderer {

	private HexMap map;
	private SpriteBatch batch;
	private BitmapFont font;

	private Rectangle viewBounds = new Rectangle();
	private float unitScale = 1.0f;

	private Cell mouseOverCell = null;

	private HexMapSpriteObject playerAttack = null;
	private HexMapSpriteObject corruptionAttack = null;

	private final float width_offset = 0.75f;
	// private final float height_offset = 0.5f;

	/*
	 * chemical top left wind, bottom right sun, bottom left
	 */

	private static final Vector2[] IconLookup = new Vector2[Entity_Settings.RESOURCE_MAX];
	private static final Vector2 energyLookup = new Vector2(0.15f, 0.25f);
	static {
		IconLookup[Entity_Settings.RESOURCE_WIND] = new Vector2(0.35f, 0.75f);
		IconLookup[Entity_Settings.RESOURCE_SOLAR] = new Vector2(0.35f, 0.35f);
		IconLookup[Entity_Settings.RESOURCE_CHEMICAL] = new Vector2(0.65f, 0.35f);
	}

	private float[] vertices = new float[20];

	private boolean showResources = false;
	private boolean showEnergy = false;

	private boolean showEnergyOverride = true;

	public boolean isShowResources() {
		return showResources;
	}

	public void setShowResources(boolean showResources) {
		this.showResources = showResources;
	}

	public boolean isShowEnergy() {
		return showEnergy;
	}

	public void setShowEnergy(boolean showEnergy) {
		this.showEnergy = showEnergy;
	}

	/**
	 * Constructor
	 */
	public HexMapRenderer() {

	}

	/**
	 * Constructor with map instance
	 * 
	 * @param batch
	 * @param map
	 */
	public HexMapRenderer(SpriteBatch batch, HexMap map) {
		setMap(map);
		this.batch = batch;
		this.font = new BitmapFont();
		this.font.setColor(1, 1, 1, 1);

	}

	public HexMap getMap() {
		return map;
	}

	public void setMap(HexMap map) {
		if (map.isValid()) {
			this.map = map;
			this.playerAttack = map.getTileTexture("playerHex_Attack");
			this.corruptionAttack = map.getTileTexture("corruptionHex_Attack");
		}
	}

	public int getMapPixelWidth() {
		return (int) (map.getWidth() * map.getTile_width() * width_offset);
	}

	public int getMapPixelHeight() {
		return (int) (map.getHeight() * map.getTile_height());
	}

	/**
	 * @param camera
	 *            camera to use to render the map.
	 */
	public void setView(OrthographicCamera camera) {

		if (this.batch != null) {
			this.batch.setProjectionMatrix(camera.combined);
		}
		float width = camera.viewportWidth * camera.zoom;
		float height = camera.viewportHeight * camera.zoom;
		this.viewBounds.set(camera.position.x - width / 2, camera.position.y - height / 2, width, height);
	}

	/**
	 * same as set view only with custom variables.
	 * 
	 * @param projectionMatrix
	 * @param viewboundsX
	 * @param viewboundsY
	 * @param viewboundsWidth
	 * @param viewboundsHeight
	 */
	public void setView(Matrix4 projectionMatrix, float viewboundsX, float viewboundsY, float viewboundsWidth,
			float viewboundsHeight) {

		this.batch.setProjectionMatrix(projectionMatrix);
		this.viewBounds.set(viewboundsX, viewboundsY, viewboundsWidth, viewboundsHeight);
	}

	public static final float sqrt3 = (float) Math.sqrt(3);

	/**
	 * Renders Tiles
	 * 
	 * @param rowFrom
	 * @param rowTo
	 * @param colFrom
	 * @param colTo
	 * @param color
	 * @param layerTileHeight
	 *            , final
	 * @param layer
	 *            to be rendered.
	 */
	private void renderTiles(final int rowFrom, final int rowTo, final int colFrom, final int colTo,
			final float layerTileWidth, final float layerTileHeight, final float color) {
		final float[] vertices = this.vertices;

		for (int row = rowFrom; row < rowTo; row++) {
			for (int col = colFrom; col < colTo; col++) {

				float x = (layerTileWidth * 0.5f) * 3 / 2 * col;
				float y = (float) ((layerTileWidth * 0.5f) * sqrt3 * (row + 0.5 * (col & 1)));

				final Cell cell = map.getCell(col, row);
				if (cell == null) {
					// x += layerTileWidth * width_offset;
					continue;
				}
				final HexMapSpriteObject tile = cell.tile;

				if (tile != null) {

					TextureRegion region = tile.getTexture();
					final float hWidth = region.getRegionWidth() * 0.5f;
					final float hHeight = region.getRegionHeight() * 0.5f;

					float x1 = x - hWidth;
					float y1 = y - hHeight;
					float x2 = (x1 + region.getRegionWidth());
					float y2 = (y1 + region.getRegionHeight());

					float u1 = region.getU();
					float v1 = region.getV2();
					float u2 = region.getU2();
					float v2 = region.getV();

					vertices[X1] = x1;
					vertices[Y1] = y1;
					vertices[C1] = color;
					vertices[U1] = u1;
					vertices[V1] = v1;

					vertices[X2] = x1;
					vertices[Y2] = y2;
					vertices[C2] = color;
					vertices[U2] = u1;
					vertices[V2] = v2;

					vertices[X3] = x2;
					vertices[Y3] = y2;
					vertices[C3] = color;
					vertices[U3] = u2;
					vertices[V3] = v2;

					vertices[X4] = x2;
					vertices[Y4] = y1;
					vertices[C4] = color;
					vertices[U4] = u2;
					vertices[V4] = v1;

					this.batch.draw(region.getTexture(), vertices, 0, 20);
				}
			}
		}
	}

	private void renderResourceIcons(final int rowFrom, final int rowTo, final int colFrom, final int colTo,
			final float layerTileWidth, final float layerTileHeight, final float color) {
		final float[] vertices = this.vertices;

		// ToDo read viable for the playerRendering or move player rendering
		// somewhere else

		final HashSet<Cell> visible = map.getPlayer().getVisible();
		// final HashSet<Cell> visible = new HashSet<HexMap.Cell>();
		// System.out.println("Row: " + rowFrom + "-" + rowTo);

		if (this.showResources) {

			for (int row = rowFrom; row < rowTo; row++) {
				for (int col = colFrom; col < colTo; col++) {

					float x = (layerTileWidth * 0.5f) * 3 / 2 * col;
					float y = (float) ((layerTileWidth * 0.5f) * sqrt3 * (row + 0.5 * (col & 1)));

					final Cell cell = map.getCell(col, row);
					// System.out.println(cell.point.x + "-" + cell.point.y +
					// " " + col + "-" + row);
					if (cell == null || (!visible.contains(cell) && !(cell.owner instanceof PlayerEntity))
							|| cell.getBuilding() != null) {
						// x += layerTileWidth * width_offset;
						continue;
					}
					for (int resIndex = 0; resIndex < Entity_Settings.RESOURCE_MAX; resIndex++) {

						final Resource res = cell.resources[resIndex];

						if (res == null) {
							continue;
						}

						final Vector2 offset = IconLookup[resIndex];
						final HexMapSpriteObject icon = res.sprite;
						// System.out.println(res.name + res.amount);
						TextureRegion region = icon.getTexture();

						final float hWidth = layerTileWidth * 0.5f;
						final float hHeight = layerTileWidth * 0.5f;

						float x1 = (int) ((x - hWidth) + (layerTileWidth * offset.x - (region.getRegionWidth() * 0.5f)));
						float y1 = (int) ((y - hHeight) + (layerTileHeight * offset.y - (region.getRegionHeight() * 0.5f)));

						float x2 = (int) (x1 + region.getRegionWidth() * unitScale);
						float y2 = (int) (y1 + region.getRegionHeight() * unitScale);

						// System.out.println("xy: " + x1 + ", " + y1);

						float u1 = region.getU();
						float v1 = region.getV2();
						float u2 = region.getU2();
						float v2 = region.getV();

						vertices[X1] = x1;
						vertices[Y1] = y1;
						vertices[C1] = color;
						vertices[U1] = u1;
						vertices[V1] = v1;

						vertices[X2] = x1;
						vertices[Y2] = y2;
						vertices[C2] = color;
						vertices[U2] = u1;
						vertices[V2] = v2;

						vertices[X3] = x2;
						vertices[Y3] = y2;
						vertices[C3] = color;
						vertices[U3] = u2;
						vertices[V3] = v2;

						vertices[X4] = x2;
						vertices[Y4] = y1;
						vertices[C4] = color;
						vertices[U4] = u2;
						vertices[V4] = v1;

						this.batch.draw(region.getTexture(), vertices, 0, 20);
					}
				}
			}
		} else if (mouseOverCell != null) {

			final Cell cell = mouseOverCell;

			float x = (layerTileWidth * 0.5f) * 3 / 2 * cell.point.x;
			float y = (float) ((layerTileWidth * 0.5f) * sqrt3 * (cell.point.y + 0.5 * (cell.point.x & 1)));

			// System.out.println(cell.point.x + "-" + cell.point.y + " " + col
			// + "-" + row);
			if (cell == null || (!visible.contains(cell) && !(cell.owner instanceof PlayerEntity))
					|| cell.getBuilding() != null) {
				// x += layerTileWidth * width_offset;
				return;
			}

			for (int resIndex = 0; resIndex < Entity_Settings.RESOURCE_MAX; resIndex++) {

				final Resource res = cell.resources[resIndex];

				if (res == null) {
					continue;
				}

				final Vector2 offset = IconLookup[resIndex];
				final HexMapSpriteObject icon = res.sprite;
				// System.out.println(res.name + res.amount);
				TextureRegion region = icon.getTexture();

				final float hWidth = layerTileWidth * 0.5f;
				final float hHeight = layerTileWidth * 0.5f;

				float x1 = (int) ((x - hWidth) + (layerTileWidth * offset.x - (region.getRegionWidth() * 0.5f)));
				float y1 = (int) ((y - hHeight) + (layerTileHeight * offset.y - (region.getRegionHeight() * 0.5f)));

				float x2 = (int) (x1 + region.getRegionWidth() * unitScale);
				float y2 = (int) (y1 + region.getRegionHeight() * unitScale);

				// System.out.println("xy: " + x1 + ", " + y1);

				float u1 = region.getU();
				float v1 = region.getV2();
				float u2 = region.getU2();
				float v2 = region.getV();

				vertices[X1] = x1;
				vertices[Y1] = y1;
				vertices[C1] = color;
				vertices[U1] = u1;
				vertices[V1] = v1;

				vertices[X2] = x1;
				vertices[Y2] = y2;
				vertices[C2] = color;
				vertices[U2] = u1;
				vertices[V2] = v2;

				vertices[X3] = x2;
				vertices[Y3] = y2;
				vertices[C3] = color;
				vertices[U3] = u2;
				vertices[V3] = v2;

				vertices[X4] = x2;
				vertices[Y4] = y1;
				vertices[C4] = color;
				vertices[U4] = u2;
				vertices[V4] = v1;

				this.batch.draw(region.getTexture(), vertices, 0, 20);
			}
		}
	}

	private void renderBuildings(final int rowFrom, final int rowTo, final int colFrom, final int colTo,
			final float layerTileWidth, final float layerTileHeight, final float color) {
		final float[] vertices = this.vertices;

		// final HashSet<Cell> visable = map.getVisibleCells();

		// System.out.println("Row: " + rowFrom + "-" + rowTo);

		for (int row = rowFrom; row < rowTo; row++) {
			for (int col = colFrom; col < colTo; col++) {
				float x = (layerTileWidth * 0.5f) * 3 / 2 * col;
				float y = (float) ((layerTileWidth * 0.5f) * sqrt3 * (row + 0.5 * (col & 1)));

				final Cell cell = map.getCell(col, row);
				// System.out.println(cell.point.x + "-" + cell.point.y + " " +
				// col + "-" + row);
				if (cell == null || cell.getBuilding() == null) {
					// x += layerTileWidth * width_offset;
					continue;
				}
				final Building res = cell.getBuilding();

				final HexMapSpriteObject sprite = res.sprite;

				TextureRegion region = sprite.getTexture();

				float x1 = (int) ((x) - (region.getRegionWidth() * unitScale * 0.5f));
				float y1 = (int) ((y) - (region.getRegionHeight() * unitScale * 0.5f));

				float x2 = (int) (x1 + region.getRegionWidth() * unitScale);
				float y2 = (int) (y1 + region.getRegionHeight() * unitScale);

				// System.out.println("xy: " + x1 + ", " + y1);

				float u1 = region.getU();
				float v1 = region.getV2();
				float u2 = region.getU2();
				float v2 = region.getV();

				vertices[X1] = x1;
				vertices[Y1] = y1;
				vertices[C1] = color;
				vertices[U1] = u1;
				vertices[V1] = v1;

				vertices[X2] = x1;
				vertices[Y2] = y2;
				vertices[C2] = color;
				vertices[U2] = u1;
				vertices[V2] = v2;

				vertices[X3] = x2;
				vertices[Y3] = y2;
				vertices[C3] = color;
				vertices[U3] = u2;
				vertices[V3] = v2;

				vertices[X4] = x2;
				vertices[Y4] = y1;
				vertices[C4] = color;
				vertices[U4] = u2;
				vertices[V4] = v1;

				this.batch.draw(region.getTexture(), vertices, 0, 20);
			}
		}
	}

	private void renderEnergy(final int rowFrom, final int rowTo, final int colFrom, final int colTo,
			final float layerTileWidth, final float layerTileHeight, final float color) {

		final HashSet<Cell> visible = map.getPlayer().getVisible();
		if (this.showEnergy) {

			for (int row = rowFrom; row < rowTo; row++) {
				for (int col = colFrom; col < colTo; col++) {

					final Cell cell = map.getCell(col, row);

					if (cell != null && (visible.contains(cell) || cell.owner instanceof PlayerEntity) || showEnergyOverride ) {

						float x = (layerTileWidth * 0.5f) * 3 / 2 * col + (layerTileHeight * energyLookup.x);
																		// (layerTileWidth*energyLookup.x);
						float y = (float) ((layerTileWidth * 0.5f) * sqrt3 * (row + 0.5 * (col & 1)) + (layerTileHeight * energyLookup.y));

						font.draw(batch, "" + ((int) cell.unit), x, y);
					}
				}
			}
		} else if (mouseOverCell != null) {

			final Cell cell = mouseOverCell;

			if (cell != null && (visible.contains(cell) || cell.owner instanceof PlayerEntity) || showEnergyOverride ) {
				float x = (layerTileWidth * 0.5f) * 3 / 2 * cell.point.x + (layerTileHeight * energyLookup.x);
				float y = (float) ((layerTileWidth * 0.5f) * sqrt3 * (cell.point.y + 0.5 * (cell.point.x & 1)) + (layerTileHeight * energyLookup.y));

				font.draw(batch, "" + ((int) cell.unit), x, y);
			}

		}
	}

	public Cell getMouseOverCell() {
		return mouseOverCell;
	}

	public void setMouseOverCell(Cell mouseOverCell) {
		this.mouseOverCell = mouseOverCell;
	}

	private float fade = 0f;
	private float corruptionFadeOffset = (float) Math.PI / 2;

	private void renderAttacks(final int rowFrom, final int rowTo, final int colFrom, final int colTo,
			final float layerTileWidth, final float layerTileHeight, final Color color) {

		final Entity player = map.getPlayer();
		final Entity corruption = map.getCorruption();

		// System.out.println("Attacks");

		Color attackColour = new Color(color);
		attackColour.a = Math.abs(MathUtils.sin(fade));
		float colour = attackColour.toFloatBits();

		for (Cell c : player.getAttacks()) {

			// System.out.println("Attacks:" + c.toString());
			float x = (layerTileWidth * 0.5f) * 3 / 2 * c.point.x;
			float y = (float) ((layerTileWidth * 0.5f) * sqrt3 * (c.point.y + 0.5 * (c.point.x & 1)));

			final Cell cell = c;

			final HexMapSpriteObject tile = cell.tile;

			if (tile != null) {

				TextureRegion region = playerAttack.getTexture();
				final float hWidth = layerTileWidth * 0.5f;
				final float hHeight = layerTileHeight * 0.5f;

				float x1 = x - hWidth;
				float y1 = y - hHeight;
				float x2 = (x1 + region.getRegionWidth());
				float y2 = (y1 + region.getRegionHeight());

				float u1 = region.getU();
				float v1 = region.getV2();
				float u2 = region.getU2();
				float v2 = region.getV();

				vertices[X1] = x1;
				vertices[Y1] = y1;
				vertices[C1] = colour;
				vertices[U1] = u1;
				vertices[V1] = v1;

				vertices[X2] = x1;
				vertices[Y2] = y2;
				vertices[C2] = colour;
				vertices[U2] = u1;
				vertices[V2] = v2;

				vertices[X3] = x2;
				vertices[Y3] = y2;
				vertices[C3] = colour;
				vertices[U3] = u2;
				vertices[V3] = v2;

				vertices[X4] = x2;
				vertices[Y4] = y1;
				vertices[C4] = colour;
				vertices[U4] = u2;
				vertices[V4] = v1;

				this.batch.draw(region.getTexture(), vertices, 0, 20);
			}
		}

		// System.out.println(attackColour.a);
		attackColour.a = 1 - Math.abs(MathUtils.cos(fade));
		// System.out.println(attackColour.a);
		colour = attackColour.toFloatBits();

		for (Cell c : corruption.getAttacks()) {

			// System.out.println("Attacks:" + c.toString());
			float x = (layerTileWidth * 0.5f) * 3 / 2 * c.point.x;
			float y = (float) ((layerTileWidth * 0.5f) * sqrt3 * (c.point.y + 0.5 * (c.point.x & 1)));

			final Cell cell = c;

			final HexMapSpriteObject tile = cell.tile;

			if (tile != null) {

				TextureRegion region = corruptionAttack.getTexture();
				final float hWidth = layerTileWidth * 0.5f;
				final float hHeight = layerTileHeight * 0.5f;

				float x1 = x - hWidth;
				float y1 = y - hHeight;
				float x2 = (x1 + layerTileWidth);
				float y2 = (y1 + layerTileHeight);

				float u1 = region.getU();
				float v1 = region.getV2();
				float u2 = region.getU2();
				float v2 = region.getV();

				vertices[X1] = x1;
				vertices[Y1] = y1;
				vertices[C1] = colour;
				vertices[U1] = u1;
				vertices[V1] = v1;

				vertices[X2] = x1;
				vertices[Y2] = y2;
				vertices[C2] = colour;
				vertices[U2] = u1;
				vertices[V2] = v2;

				vertices[X3] = x2;
				vertices[Y3] = y2;
				vertices[C3] = colour;
				vertices[U3] = u2;
				vertices[V3] = v2;

				vertices[X4] = x2;
				vertices[Y4] = y1;
				vertices[C4] = colour;
				vertices[U4] = u2;
				vertices[V4] = v1;

				this.batch.draw(region.getTexture(), vertices, 0, 20);
			}
		}

		fade += 1 * Gdx.graphics.getDeltaTime();
	}
	
	private Vector2 up = new Vector2(0,1);
	
	private void renderPopupText() {
		
		final float dt = Gdx.graphics.getDeltaTime();
		final Array<AnimatedSprite> activeArray = map.getActiveSprites();
		
		AnimatedSprite[] array = new AnimatedSprite[activeArray.size];
		
		
		
		for(int i = 0; i<activeArray.size; i++) {
			final AnimatedSprite a = activeArray.get(i);
			float halfW = a.getTexture().getRegionWidth()/2;
			float halfH = a.getTexture().getRegionHeight()/2;
			
			float angle = 0.0f;
			
			if( a.isRotate() ) {
				angle = a.getTo().angle()-90f;
			}
			
			a.update(dt);
			//font.draw(batch, a.getText(), a.getPos().x, a.getPos().y);
			//batch.draw(a.getTexture(), a.getPos().x, a.getPos().y ,-halfW,-halfH,a.getTexture().getRegionWidth(), a.getTexture().getRegionHeight(),1,1,angle);
			if( a.isReady() ) {
				batch.draw(a.getTexture(), a.getPos().x-halfW, a.getPos().y-halfH, halfW, halfH, a.getTexture().getRegionWidth(), a.getTexture().getRegionHeight(), 1, 1, angle);
			}
			array[i] = a;
		}
	
		for(int i = 0; i<activeArray.size; i++) {
			
			if(array[i].isActive() == false) {
				//System.out.println("remove");
				activeArray.removeValue(array[i], false);
				map.spritePool.free(array[i]);
			}
		}		
		
	}
	
	public void render() {

		if (batch != null) {
			final Color batchColor = this.batch.getColor();
			final float color = Color.toFloatBits(batchColor.r, batchColor.g, batchColor.b, batchColor.a * 1.0f);
			// final float color1 = Color.toFloatBits(batchColor.r,
			// batchColor.g, batchColor.b, 0.25f);

			//final int layerWidth = map.getWidth();
			//final int layerHeight = map.getHeight();

			final float layerTileWidth = map.getTile_width() * unitScale;
			final float layerTileHeight = map.getTile_height() * unitScale;

			final int colFrom = 0;
			final int colTo = map.getWidth();

			final int rowFrom = 0;
			final int rowTo = map.getHeight();

			this.batch.begin();
				this.renderTiles(rowFrom, rowTo, colFrom, colTo, layerTileWidth, layerTileHeight, color);
				this.renderAttacks(rowFrom, rowTo, colFrom, colTo, layerTileWidth, layerTileHeight, batchColor);
				
				this.renderBuildings(rowFrom, rowTo, colFrom, colTo, layerTileWidth, layerTileHeight, color);
		
				this.renderResourceIcons(rowFrom, rowTo, colFrom, colTo, layerTileWidth, layerTileHeight, color);
		
				this.renderEnergy(rowFrom, rowTo, colFrom, colTo, layerTileWidth, layerTileHeight, color);
				
				this.renderPopupText();
			this.batch.end();
		}
	}

	public Cell getCellFromTouchCoord(Vector3 coord) {
		//final int layerWidth = map.getWidth();
		//final int layerHeight = map.getHeight();

		final float layerTileWidth = map.getTile_width() * unitScale ;

		final int colFrom = 0;
		final int colTo = map.getWidth();

		final int rowFrom = 0;
		final int rowTo = map.getHeight();

		Vector2 touch = new Vector2();
		float len2 = (layerTileWidth * 0.5f) * (layerTileWidth * 0.5f);

		for (int row = rowFrom; row < rowTo; row++) {
			for (int col = colFrom; col < colTo; col++) {
				float x = ((layerTileWidth * 0.5f) * 3 / 2 * col);
				float y = (float) ((layerTileWidth * 0.5f) * sqrt3 * (row + 0.5 * (col & 1)));

				final Cell cell = map.getCell(col, row);

				touch.set(coord.x - x, coord.y - y);

				if (touch.len2() < len2) {
					return cell;
				}

			}
		}
		return null;
	}
		


	

}
