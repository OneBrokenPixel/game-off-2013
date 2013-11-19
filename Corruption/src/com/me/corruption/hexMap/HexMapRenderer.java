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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.me.corruption.entities.PlayerEntity;
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
	
	private final float width_offset = 0.75f;
	//private final float height_offset = 0.5f;
	
	/*
	 * chemical top left
	 * wind, bottom right
	 * sun, bottom left
	 */
	
	private static final Vector2[] IconLookup = new Vector2[HexMap.RESOURCE_MAX];
	private static final Vector2 energyLookup = new Vector2(0.15f, 0.25f);
	static {
		IconLookup[HexMap.RESOURCE_WIND] = new Vector2(0.65f, 0.35f);
		IconLookup[HexMap.RESOURCE_SOLAR] = new Vector2(0.35f, 0.35f);
		IconLookup[HexMap.RESOURCE_CHEMICAL] = new Vector2(0.35f, 0.75f);
	}
	
	private float[] vertices = new float[20];
	
	
	private boolean showResources = false;
	private boolean showEnergy = false;
	
	private boolean showEnergyOverride = false;
	
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
		if( map.isValid() ) {
			this.map = map;
		}
	}
	
	public int getMapPixelWidth() {
		return (int)(map.getWidth()*map.getTile_width()*width_offset);
	}
	
	public int getMapPixelHeight() {
		return (int)(map.getHeight()*map.getTile_height());
	}
	
	/**
	 * @param camera camera to use to render the map.
	 */
	public void setView(OrthographicCamera camera) {
		
		if(this.batch != null){
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
	public void setView(Matrix4 projectionMatrix, float viewboundsX,
			float viewboundsY, float viewboundsWidth, float viewboundsHeight) {
		
		this.batch.setProjectionMatrix(projectionMatrix);
		this.viewBounds.set(viewboundsX, viewboundsY, viewboundsWidth, viewboundsHeight);
	}
	
	private float sqrt3 = (float) Math.sqrt(3);
	
	/**
	 * Renders Tiles
	 * @param rowFrom 
	 * @param rowTo 
	 * @param colFrom 
	 * @param colTo 
	 * @param color 
	 * @param layerTileHeight, final 
	 * @param layer to be rendered.
	 */
	private void renderTiles(final int rowFrom, final int rowTo, final int colFrom, final int colTo,
							 final float layerTileWidth, final float layerTileHeight, final float color) {
		final float[] vertices = this.vertices;
		
		for (int row = rowFrom; row < rowTo; row++) {
			for (int col = colFrom; col < colTo; col++) {
				
				
				float x = (layerTileWidth*0.5f) * 3/2 * col;
				float y = (float) ((layerTileWidth*0.5f) * sqrt3 * (row + 0.5 * (col&1)));
				
				final Cell cell = map.getCell(col, row);
				if(cell == null) {
					//x += layerTileWidth * width_offset;
					continue;
				}
				final HexMapSpriteObject tile = cell.tile;

				if (tile != null) {

					TextureRegion region = tile.getTexture();
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
			 final float layerTileWidth, final float layerTileHeight, final float color){
		final float[] vertices = this.vertices;
		
		// ToDo read viable for the playerRendering or move player rendering somewhere else
		
		final HashSet<Cell> visible = map.getPlayer().getVisible();
		//final HashSet<Cell> visible = new HashSet<HexMap.Cell>();
		//System.out.println("Row: " + rowFrom + "-" + rowTo);
		
		if( this.showResources ) {
		
			for (int row = rowFrom; row < rowTo; row++) {
				for (int col = colFrom; col < colTo; col++) {
					
					float x = (layerTileWidth*0.5f) * 3/2 * col;
					float y = (float) ((layerTileWidth*0.5f) * sqrt3 * (row + 0.5 * (col&1)));
					
					final Cell cell = map.getCell(col, row);
					//System.out.println(cell.point.x + "-" + cell.point.y + " " + col + "-" + row);
					if(cell == null || (!visible.contains(cell) && !(cell.owner instanceof PlayerEntity)) || cell.getBuilding() != null) {
						//x += layerTileWidth * width_offset;
						continue;
					}
					for( int resIndex = 0; resIndex < HexMap.RESOURCE_MAX; resIndex++) {
						
						final Resource res = cell.resources[resIndex];
	
						
						if( res == null ) {
							continue;
						}
	
						final Vector2 offset = IconLookup[resIndex];
						final HexMapSpriteObject icon = res.sprite;
						//System.out.println(res.name + res.amount);
						TextureRegion region =  icon.getTexture();
						
						final float hWidth = layerTileWidth * 0.5f;
						final float hHeight = layerTileWidth * 0.5f;
						
						float x1 = (int)((x - hWidth) + (layerTileWidth*offset.x - (region.getRegionWidth()*0.5f)));
						float y1 = (int)((y - hHeight) + (layerTileHeight*offset.y - (region.getRegionHeight()*0.5f)));
						
						float x2 = (int)(x1 + region.getRegionWidth() * unitScale);
						float y2 = (int)(y1 + region.getRegionHeight() * unitScale);
						
						//System.out.println("xy: " + x1 + ", " + y1);
						
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
		else if( mouseOverCell != null ) {
			
			final Cell cell = mouseOverCell;
			
			float x = (layerTileWidth*0.5f) * 3/2 * cell.point.x;
			float y = (float) ((layerTileWidth*0.5f) * sqrt3 * (cell.point.y + 0.5 * (cell.point.x&1)));
			
			//System.out.println(cell.point.x + "-" + cell.point.y + " " + col + "-" + row);
			if(cell == null || (!visible.contains(cell) && !(cell.owner instanceof PlayerEntity)) || cell.getBuilding() != null ) {
				//x += layerTileWidth * width_offset;
				return;
			}
		
			for( int resIndex = 0; resIndex < HexMap.RESOURCE_MAX; resIndex++) {
				
				final Resource res = cell.resources[resIndex];

				
				if( res == null ) {
					continue;
				}

				final Vector2 offset = IconLookup[resIndex];
				final HexMapSpriteObject icon = res.sprite;
				//System.out.println(res.name + res.amount);
				TextureRegion region =  icon.getTexture();
				
				final float hWidth = layerTileWidth * 0.5f;
				final float hHeight = layerTileWidth * 0.5f;
				
				float x1 = (int)((x - hWidth) + (layerTileWidth*offset.x - (region.getRegionWidth()*0.5f)));
				float y1 = (int)((y - hHeight) + (layerTileHeight*offset.y - (region.getRegionHeight()*0.5f)));
				
				float x2 = (int)(x1 + region.getRegionWidth() * unitScale);
				float y2 = (int)(y1 + region.getRegionHeight() * unitScale);
				
				//System.out.println("xy: " + x1 + ", " + y1);
				
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
			 final float layerTileWidth, final float layerTileHeight, final float color){
		final float[] vertices = this.vertices;
		
		//final HashSet<Cell> visable = map.getVisibleCells();
		
		//System.out.println("Row: " + rowFrom + "-" + rowTo);
		
		for (int row = rowFrom; row < rowTo; row++) {
			for (int col = colFrom; col < colTo; col++) {	
				float x = (layerTileWidth*0.5f) * 3/2 * col;
				float y = (float) ((layerTileWidth*0.5f) * sqrt3 * (row + 0.5 * (col&1)));
				
				final Cell cell = map.getCell(col, row);
				//System.out.println(cell.point.x + "-" + cell.point.y + " " + col + "-" + row);
				if(cell == null || cell.getBuilding() == null) {
					//x += layerTileWidth * width_offset;
					continue;
				}
				final Building res = cell.getBuilding();

				final HexMapSpriteObject sprite = res.sprite;

				TextureRegion region =  sprite.getTexture();
				
				
				float x1 = (int)((x) -(region.getRegionWidth() * unitScale* 0.5f));
				float y1 = (int)((y) - (region.getRegionHeight() * unitScale * 0.5f));
				
				float x2 = (int)(x1 + region.getRegionWidth() * unitScale);
				float y2 = (int)(y1 + region.getRegionHeight() * unitScale);
				
				//System.out.println("xy: " + x1 + ", " + y1);
				
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
			 final float layerTileWidth, final float layerTileHeight, final float color){
		

		final HashSet<Cell> visible = map.getPlayer().getVisible();
		if( this.showEnergy )
		{
			
			for (int row = rowFrom; row < rowTo; row++) {
				for (int col = colFrom; col < colTo; col++) {
					
					float x = (layerTileWidth*0.5f) * 3/2 * col;// + (layerTileWidth*energyLookup.x);
					float y = (float) ((layerTileWidth*0.5f) * sqrt3 * (row + 0.5 * (col&1))+ (layerTileHeight*energyLookup.y));
					
					final Cell cell = map.getCell(col, row);
					//System.out.println(cell.point.x + "-" + cell.point.y + " " + col + "-" + row);
					if((cell == null || (!visible.contains(cell) && !(cell.owner instanceof PlayerEntity)) )) {
						//x += layerTileWidth * width_offset;
						continue;
					}
					//System.out.println(cell.energy.unit);
					font.draw(batch, ""+((int)cell.unit), x, y);
				}
			}
		}
		else if( mouseOverCell != null ) {

			final Cell cell = mouseOverCell;
			
			if((cell == null || (!visible.contains(cell) && !(cell.owner instanceof PlayerEntity)) )) {
				return;
			}
			
			float x = (layerTileWidth*0.5f) * 3/2 * cell.point.x;// + (layerTileWidth*energyLookup.x);
			float y = (float) ((layerTileWidth*0.5f) * sqrt3 * (cell.point.y + 0.5 * (cell.point.x&1))+ (layerTileHeight*energyLookup.y));
			
			//System.out.println(cell.energy.unit);
			font.draw(batch, ""+((int)cell.unit), x, y);
		}
	}
	
	public Cell getMouseOverCell() {
		return mouseOverCell;
	}

	public void setMouseOverCell(Cell mouseOverCell) {
		this.mouseOverCell = mouseOverCell;
	}

	public void render() {

		if( batch != null) {
			final Color batchColor = this.batch.getColor();
			final float color = Color.toFloatBits(batchColor.r, batchColor.g, batchColor.b, batchColor.a * 1.0f);
			//final float color1 = Color.toFloatBits(batchColor.r, batchColor.g, batchColor.b, 0.25f);
			
			final int layerWidth = map.getWidth();
			final int layerHeight = map.getHeight();
	
			final float layerTileWidth = map.getTile_width() * unitScale ;
			final float layerTileHeight = map.getTile_height() * unitScale;
			
			final int colFrom = Math.max(0, (int) (viewBounds.x / (layerTileWidth*width_offset))-1);
			final int colTo = Math.min(layerWidth, (int) ((viewBounds.x + viewBounds.width + layerTileWidth) / (layerTileWidth*width_offset)));
			
			final int rowFrom = Math.max(0, (int) (viewBounds.y / layerTileHeight)-1);
			final int rowTo = Math.min(layerHeight, (int) ((viewBounds.y + viewBounds.height + layerTileHeight) / layerTileHeight));		
			
			this.batch.begin();
				this.renderTiles(rowFrom, rowTo, colFrom, colTo, layerTileWidth, layerTileHeight, color);
				this.renderBuildings(rowFrom, rowTo, colFrom, colTo, layerTileWidth, layerTileHeight, color);
		
				this.renderResourceIcons(rowFrom, rowTo, colFrom, colTo, layerTileWidth, layerTileHeight, color);
		
				this.renderEnergy(rowFrom, rowTo, colFrom, colTo, layerTileWidth, layerTileHeight, color);
			this.batch.end();
		}
	}
	/*
	private void toCubeCoord(int r, int q, GridPoint3 cubeCoord) {
		cubeCoord.x = q;
		cubeCoord.z = r - (q - (q&1)) / 2;
		cubeCoord.y = -cubeCoord.x-cubeCoord.z;
	}

	private void fromCubeCoord(GridPoint3 cubeCoord, GridPoint2 oddQ) {
		oddQ.x = cubeCoord.x;
		oddQ.y = cubeCoord.z + (cubeCoord.x - (cubeCoord.x&1)) / 2;
	}

	private void roundToNearestHex(Vector3 cubeCoord, GridPoint3 finalCoord) {

	    float rx = (int)(cubeCoord.x);
	    float ry = (int)(cubeCoord.y);
	    float rz = (int)(cubeCoord.z);

	    float x_diff = Math.abs(rx - cubeCoord.x);
	    float  y_diff = Math.abs(ry - cubeCoord.y);
	    float z_diff = Math.abs(rz - cubeCoord.z);

	    if(x_diff > y_diff && x_diff > z_diff) {
	        rx = -ry-rz;
	    }
	    else if(y_diff > z_diff){
	        ry = -rx-rz;
	    }
	    else{
	        rz = -rx-ry;
	    }

	    finalCoord.set((int)rx, (int)ry, (int)rz);
	    
	}
	
	private void axialToCube( Vector2 axial, Vector3 cube) {
		cube.x = axial.y;
		cube.z = axial.x;
		cube.y = -cube.x-cube.z;
	}
	*/
	
	public Cell getCellFromTouchCoord( final Vector3 coord ) {
		
		final int layerWidth = map.getWidth();
		final int layerHeight = map.getHeight();

		final float layerTileWidth = map.getTile_width() * unitScale ;
		final float layerTileHeight = map.getTile_height() * unitScale;
		
		final int colFrom = Math.max(0, (int) (viewBounds.x / (layerTileWidth*width_offset))-1);
		final int colTo = Math.min(layerWidth, (int) ((viewBounds.x + viewBounds.width + layerTileWidth) / (layerTileWidth*width_offset)));
		
		final int rowFrom = Math.max(0, (int) (viewBounds.y / layerTileHeight)-1);
		final int rowTo = Math.min(layerHeight, (int) ((viewBounds.y + viewBounds.height + layerTileHeight) / layerTileHeight));		
		
		Vector2 touch = new Vector2();
		float len2 = (layerTileWidth*0.5f)*(layerTileWidth*0.5f);
		
		for (int row = rowFrom; row < rowTo; row++) {
			for (int col = colFrom; col < colTo; col++) {
				float x = ((layerTileWidth*0.5f) * 3/2 * col);
				float y = (float) ((layerTileWidth*0.5f) * sqrt3 * (row + 0.5 * (col&1)));
				
				final Cell cell = map.getCell(col, row);
				
				touch.set(coord.x-x,coord.y-y);
				
				if( touch.len2() < len2 ) {
					return cell;
				}
				
			}
		}
		
		return null;
	}
}
