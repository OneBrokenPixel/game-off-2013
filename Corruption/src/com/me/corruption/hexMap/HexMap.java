package com.me.corruption.hexMap;

import java.util.HashSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;

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
			this.sprite = resourseIcons.get(this.name+this.amount);
		}
		
	}
	
	/**
	 * Tile Cell class
	 */
	public class Cell
	{
		public GridPoint2 point = new GridPoint2();
		
		public HexMapSpriteObject tile = null;
		public HexMapSpriteObject building = null;
		public Energy energy = new Energy();
		public Resource[] resources = new Resource[3];
		public Owner owner = Owner.NEUTRAL;
		
		public void setOwner( Owner owner ) {
			this.owner = owner;
			this.tile = tiles.get(this.owner.toString().toLowerCase()+"Hex");
			if( this.owner == Owner.PLAYER ) {
				setPlayerCell(this);
			}
			else if( this.owner == Owner.CORRUPTION ) {
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
	private static HexMapSpriteList resourseIcons;
	private static HexMapSpriteList units;
	
	
	private HashSet<Cell> playerCells = new HashSet<HexMap.Cell>();
	private HashSet<Cell> visableCells = new HashSet<HexMap.Cell>();
	private HashSet<Cell> corruptedCells = new HashSet<HexMap.Cell>();
	
	/**
	 * static initialisation for sprite from the texture atlas.
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
		
		resourseIcons = new HexMapSpriteList();
		
		resourseIcons.add(new HexMapSpriteObject("chemical1", atlas),
						  new HexMapSpriteObject("chemical2", atlas),
						  new HexMapSpriteObject("chemical3", atlas),
						  new HexMapSpriteObject("solar1", atlas),
						  new HexMapSpriteObject("solar2", atlas),
						  new HexMapSpriteObject("solar3", atlas),
						  new HexMapSpriteObject("wind1", atlas),
						  new HexMapSpriteObject("wind2", atlas),
						  new HexMapSpriteObject("wind3", atlas));
		
		
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

	public HexMapSpriteList getResourseIcons() {
		return resourseIcons;
	}

	public HexMapSpriteList getUnits() {
		return units;
	}

	public Cell[][] getCells() {
		return cells;
	}
	
	public Cell getCell(int col, int row) {
		return cells[col][row];
	}	

	public HashSet<Cell> getVisableCells() {
		return visableCells;
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
	
	public void setPlayerCell( Cell cell ) {
		playerCells.add(cell);
		System.out.println(playerCells);
		
		if( visableCells.contains(cell)){
			visableCells.remove(cell);
		}
		
		updateVisableCells(cell);
	}
	
	/**
	 * takes a cell and updates the tiles visable to the player.
	 * @param cell
	 */
	private void updateVisableCells(Cell cell) {
		/*
		for(int row = Math.max(cell.point.y-1,0); row < Math.min(cell.point.y+2, this.height); row++) {
			for(int col = Math.max(cell.point.x-1,0); col < Math.min(cell.point.x+2, this.width); col++) {
				final Cell c = getCell(col, row);
				if( c.owner != Owner.PLAYER) {
					visableCells.add(c);
				}
			}
		}*/
		
		final GridPoint2 point = cell.point;
		
		if( point.x % 2 == 0) {
			visableCells.add(getCell(point.x-1, point.y-1));
			visableCells.add(getCell(point.x-0, point.y-1));
			visableCells.add(getCell(point.x+1, point.y-1));
			visableCells.add(getCell(point.x-1, point.y+0));
			visableCells.add(getCell(point.x+1, point.y+0));
			visableCells.add(getCell(point.x-0, point.y+1));
		}
		else {
			
			visableCells.add(getCell(point.x-1, point.y+1));
			visableCells.add(getCell(point.x-0, point.y+1));
			visableCells.add(getCell(point.x+1, point.y+1));
			visableCells.add(getCell(point.x-1, point.y+0));
			visableCells.add(getCell(point.x+1, point.y+0));
			visableCells.add(getCell(point.x-0, point.y-1));
		}
		
	}

	/**
	 * Updates corrupted tiles
	 * @param cell
	 */
	public void setCorruptionCell( Cell cell ) {
		corruptedCells.add(cell);
		System.out.println(corruptedCells);
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
