package com.me.corruption.hexMap;

import java.util.HashSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;

import com.me.corruption.hexMap.HexMapSpriteObject;
/**
 * Main map class
 * 
 * @author Anthony
 *
 */
public class HexMap implements Disposable {

	/**
	 * Owner enumeration
	 */
	public static enum Owner {
		PLAYER, NEUTRAL, CORRUPTION;
	}
	
	public static final int RESOURCE_WIND		= 0;	
	public static final int RESOURCE_SOLAR		= 1;
	public static final int RESOURCE_CHEMICAL	= 2;
	public static final int RESOURCE_MAX		= 3;
	
	
	/**
	 * Energy for tile cells
	 */
	public class Energy {
		public float unit;
		public float recharge;
		public float max;
	}
	
	/**
	 * Resource for tile cells
	 */
	public class Resource {
		String name;
		int amount;
		HexMapSpriteObject sprite;
		
		public Resource( String name, int amount) {
			this.name = name;
			this.amount = MathUtils.clamp(amount, 0, 3);
			this.sprite = resourceIcons.get(this.name+this.amount);
		}
		
	}
	
	public class Building {
		String name = null;
		HexMapSpriteObject sprite = null;
		
		public Building() {
		}
		
		public void set(String name) {
			this.name = name;
			if(this.name != null) {
				this.sprite = buildings.get(this.name);
			}
			else {
				this.sprite = null;
			}
		}
	}
	
	/**
	 * Tile Cell class
	 */
	public class Cell
	{
		public GridPoint2 point = new GridPoint2();
		
		public HexMapSpriteObject tile = null;
		public Building building = new Building();
		public Energy energy = new Energy();
		public Resource[] resources = new Resource[3];
		public String owner = "neutral";
		
		public void setOwner( String owner ) {
			this.owner = owner;
			this.tile = tiles.get(this.owner+"Hex");
			if( this.owner.contains("player") ) {
				getPlayer().setPlayerCell(this);
			}
			else if( this.owner.contains("corruption") ) {
				setCorruptionCell(this);
			}
		}
	}
	
	private int width;
	private int height;
	

	
	private Cell cells[][] = null;
	
	private static TextureAtlas atlas;
	private static int tile_width;
	private static int tile_height;
	private static HexMapSpriteList tiles;
	private static HexMapSpriteList buildings;
	private static HexMapSpriteList resourceIcons;
	private static HexMapSpriteList units;
	
	
	class PlayerEntity {
		private HashSet<Cell> owned = new HashSet<HexMap.Cell>();
		private HashSet<Cell> visible = new HashSet<HexMap.Cell>();
		
		
		public HashSet<Cell> getVisible() {
			return visible;
		}
		
		public HashSet<Cell> getOwned(){
			return owned;
		}
		
		/**
		 * Sets a Cell as belonging to the player
		 * @param cell
		 */
		public void setPlayerCell( Cell cell ) {
			owned.add(cell);
			//System.out.println(playerCells);
			
			if( visible.contains(cell)){
				visible.remove(cell);
			}
			
			updateVisibleCells(cell);
		}
		
		/**
		 * 
		 * @param cell
		 */
		private void setCellAsVisible(Cell cell) {
			if(cell != null && cell.owner.contains("neutral")) {
				visible.add(cell);
			}
		}
		
		/**
		 * takes a cell and updates the tiles visible to the player.
		 * @param cell
		 */
		private void updateVisibleCells(Cell cell) {
			
			final GridPoint2 point = cell.point;
			
			if( point.x % 2 == 0) {
				setCellAsVisible(getCell(point.x-1, point.y-1));
				setCellAsVisible(getCell(point.x-0, point.y-1));
				setCellAsVisible(getCell(point.x+1, point.y-1));
				setCellAsVisible(getCell(point.x-1, point.y+0));
				setCellAsVisible(getCell(point.x+1, point.y+0));
				setCellAsVisible(getCell(point.x-0, point.y+1));
			}
			else {
				
				setCellAsVisible(getCell(point.x-1, point.y+1));
				setCellAsVisible(getCell(point.x-0, point.y+1));
				setCellAsVisible(getCell(point.x+1, point.y+1));
				setCellAsVisible(getCell(point.x-1, point.y+0));
				setCellAsVisible(getCell(point.x+1, point.y+0));
				setCellAsVisible(getCell(point.x-0, point.y-1));
			}
			
		}
	}
	
	
	private PlayerEntity player = new PlayerEntity();
	
	private HashSet<Cell> corruptedCells = new HashSet<HexMap.Cell>();
	
	/**
	 * Static initialisation for sprite from the texture atlas.
	 * Loads all texture assets into the map.
	 */	
	static {

		atlas = new TextureAtlas(Gdx.files.internal("spritesheets/HexTiles.atlas"));
		
		/**
		 * Load Tiles
		 * - playerHex
		 * - neutralHex
		 * - corruptionHex
		 */

		//AtlasRegion t = atlas.findRegion("playerHex");
		
		tiles = new HexMapSpriteList();
		
		tile_width = atlas.findRegion("playerHex").getRegionWidth();
		tile_height = atlas.findRegion("playerHex").getRegionHeight();
		
		tiles.add( new HexMapSpriteObject("playerHex", atlas),
				   new HexMapSpriteObject("neutralHex", atlas),
				   new HexMapSpriteObject("corruptionHex", atlas));
		
		resourceIcons = new HexMapSpriteList();
		
		resourceIcons.add(new HexMapSpriteObject("chemical1", atlas),
						  new HexMapSpriteObject("chemical2", atlas),
						  new HexMapSpriteObject("chemical3", atlas),
						  new HexMapSpriteObject("solar1", atlas),
						  new HexMapSpriteObject("solar2", atlas),
						  new HexMapSpriteObject("solar3", atlas),
						  new HexMapSpriteObject("wind1", atlas),
						  new HexMapSpriteObject("wind2", atlas),
						  new HexMapSpriteObject("wind3", atlas));
		
		buildings = new HexMapSpriteList();
		
		buildings.add(new HexMapSpriteObject("chemicalplant", atlas),
				  	  new HexMapSpriteObject("solarplant", atlas),
				  	  new HexMapSpriteObject("windplant", atlas));
		
		//System.out.println(buildings.getCount());
		
		
	}
	
	public HexMap() {
		
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getTile_width() {
		return tile_width;
	}

	public int getTile_height() {
		return tile_height;
	}

	public HexMapSpriteList getTiles() {
		return tiles;
	}

	public HexMapSpriteList getBuildings() {
		return buildings;
	}

	public HexMapSpriteList getResourceIcons() {
		return resourceIcons;
	}

	public HexMapSpriteList getUnits() {
		return units;
	}

	public Cell[][] getCells() {
		return cells;
	}
	
	/**
	 * @param col
	 * @param row
	 * @return null if out of range width/ height
	 */
	public Cell getCell(int col, int row) {
		if( col > 0 && col < width && row > 0 && row <height){
			return cells[col][row];
		}
		return null;
	}	

	public PlayerEntity getPlayer() {
		return player;
	}
	
	/**
	 * initalise the map with width height and tiles.
	 * @param width
	 * @param height
	 * @param cells
	 */
	public void initalise( int width, int height, Cell[][] cells) {
		this.width = width;
		this.height = height;
		
		this.cells = cells;
	}

	/**
	 * Updates corrupted tiles
	 * @param cell
	 */
	public void setCorruptionCell( Cell cell ) {
		corruptedCells.add(cell);
	}
	
	/**
	 * Tests map for validity
	 * @return boolean
	 * @retval true pass
	 * @retval false fail
	 */
	public boolean isValid() {
		return cells != null && width != 0 && height != 0;
	}	
	
	/**
	 * Clean up memory
	 */
	@Override
	public void dispose() {
		
	}


}
