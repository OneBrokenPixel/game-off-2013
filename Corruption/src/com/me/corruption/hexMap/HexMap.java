package com.me.corruption.hexMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import sun.org.mozilla.javascript.internal.InterfaceAdapter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;

import com.me.corruption.entities.PlayerEntity;
import com.me.corruption.hexMap.HexMapSpriteObject;
/**
 * Main map class
 * 
 * @author Anthony
 *
 */
public class HexMap implements Disposable {
	
	public static final int RESOURCE_WIND		= 0;	
	public static final int RESOURCE_SOLAR		= 1;
	public static final int RESOURCE_CHEMICAL	= 2;
	public static final int RESOURCE_MAX		= 3;

	private static final float BUILDING_ENERGY[] = {0.0f,0.0f,0.0f};
	
	
	/**
	 * Resource for tile cells
	 */
	public class Resource implements Comparable<Resource> {
		String name;
		int amount;
		HexMapSpriteObject sprite;
		
		public Resource( String name, int amount) {
			this.name = name;
			this.amount = MathUtils.clamp(amount, 0, 3);
			this.sprite = resourceIcons.get(this.name+this.amount);
		}

		@Override
		public int compareTo(Resource arg0) {
			if(arg0 != null) {
				return Integer.compare(this.amount, arg0.amount);
			}
			else {
				return 1;
			}
		}
		
	}
	
	public class Building {
		public String name = null;
		public HexMapSpriteObject sprite = null;
		
		public float energyBonus = 0.0f;
				
		public void set(String name) {
			this.name = name;
			if(this.name != null) {
				this.sprite = buildings.get(this.name);
			}
			else {
				this.sprite = null;
			}

			if( this.name.contains("chemicalplant") ) {
				this.energyBonus = BUILDING_ENERGY[RESOURCE_CHEMICAL];
			}
			else if( this.name.contains("solarplant")) {
				this.energyBonus = BUILDING_ENERGY[RESOURCE_SOLAR];
			}
			else if( this.name.contains("windplant")) {
				this.energyBonus = BUILDING_ENERGY[RESOURCE_WIND];
			}
			//this.energyBonus = BUILDING_ENERGY
		}
	}
	
	/**
	 * Tile Cell class
	 */
	public class Cell
	{
		public GridPoint2 point = new GridPoint2();
		
		public HexMapSpriteObject tile = null;
		private Building building = new Building();
		
		// energy
		public float unit;
		public float rechargeRate;
		public float max;
		
		public boolean recharge = false;
		
		public Resource[] resources = new Resource[3];
		public String owner = "neutral";
		
		public void setOwner( String owner ) {
			this.owner = owner;
			this.tile = tiles.get(this.owner+"Hex");
			/*
			if( this.owner.contains("player") ) {
				getPlayer().setOwnedCell(this);
			}
			else if( this.owner.contains("corruption") ) {
				getCorruption().setOwnedCell(this);
			}
			*/
		}
		
		public void addEnergy( float energy) {
			unit += energy;
		}
		
		public void setRecharge(boolean r) {
			this.recharge = r;
			
			HexMapSpriteObject toogleTile = tiles.get(this.owner+"Hex"+"_toggle");
		
			if( toogleTile != null) {
				if(this.recharge) {
					tile = toogleTile;
				}
				else {
					tile = tiles.get(this.owner+"Hex");
				}
			}
		}
		
		public void setBuilding( String name ) {
			building.set(name);
		}

		public Building getBuilding() {
			// TODO Auto-generated method stub
			return building;
		}
		
		public Integer[] sortResources() {
			
			Comparator<Integer> comp = new Comparator<Integer>() {

				// i think this is how it works
				@Override
				public int compare(Integer arg0, Integer arg1) {
					final Resource res1 = resources[arg0];
					final Resource res2 = resources[arg1];
					
					if(res1 != null) {
						return res1.compareTo(res2);
					}
					else {
						return -1;
					}
				}
				
			}; 
			
			Integer a[] = { RESOURCE_WIND, RESOURCE_SOLAR, RESOURCE_CHEMICAL};
			
			Arrays.sort(a, comp);
			
			return a;
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
	
	//private HexMapInterface ui_if;
	

	
	private PlayerEntity player = new PlayerEntity(this);
	
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
				   new HexMapSpriteObject("playerHex_toggle", atlas),
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
		//ui_if = new HexMapInterface(this);
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

	//public HexMapInterface getInterface() {
	//	return ui_if;
	//}
	
	/**
	 * @param col
	 * @param row
	 * @return null if out of range width/ height
	 */
	public Cell getCell(int col, int row) {
		if( col >= 0 && col < width && row >= 0 && row <height){
			return cells[col][row];
		}
		return null;
	}	


	public PlayerEntity getPlayer() {
		return player;
	}
	/*
	public PlayerEntity getCorruption() {
		return corruption;
	}
	*/
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
