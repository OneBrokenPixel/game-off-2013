package com.me.corruption.hexMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pool.Poolable;

import com.me.corruption.entities.CorruptionEntity;
import com.me.corruption.entities.Entity;
import com.me.corruption.entities.Entity.AttackCallback;
import com.me.corruption.entities.NeutralEntity;
import com.me.corruption.entities.PlayerEntity;
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
	
	
	/**
	 * Resource for tile cells
	 */
	public static class Resource implements Comparable<Resource> {
		String name;
		int amount;
		HexMapSpriteObject sprite;
		
		public Resource( String name, int amount) {
			this.name = name;
			this.amount = MathUtils.clamp(amount, 0, 3);
			this.sprite = resourceIcons.get(this.name+this.amount);
		}
		
		public int getAmount() {
			return amount;
		}
		
		public String getName() {
			return name;
		}

		@Override
		public int compareTo(Resource arg0) {
			if(arg0 != null) {
				return this.amount - arg0.amount;
			}
			else {
				return -1;
			}
		}
		
	}
	
	/**
	 * Class for building in each cell
	 * @author Anthony/Marie
	 *
	 */
	public class Building {
		public String name = null;
		public HexMapSpriteObject sprite = null;
				
		public int id;
		
		public float energyCap = 0;
		
		public void set(String name) {
			this.name = name;// can be null
			if(this.name != null) {
				this.name.toLowerCase();
				this.sprite = buildings.get(this.name);
				if( this.name.contains("chemicalplant") ) {
					this.id = RESOURCE_CHEMICAL;
				}
				else if( this.name.contains("solarplant")) {
					this.id = RESOURCE_SOLAR;
				}
				else if( this.name.contains("windplant")) {
					this.id = RESOURCE_WIND;
				}
			}
			else {
				this.sprite = null;
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
		private Building building = null;
		
		// energy
		public float unit;
		public float rechargeRate;
		//public float max;
		
		public boolean recharge = false;
		
		public Resource[] resources = new Resource[3];
		public Entity owner = null;
		
		public Array<Entity> attackers = new Array<Entity>(2);
		
		public void setOwner( Entity owner ) {
			if( this.owner != null ) {
				this.owner.removeCell(this);
			}
			this.owner = owner;
			if(this.owner != null) {
				this.tile = tiles.get(this.owner.getOwnerName()+"Hex");
			}
		}
		
		public void clearOwner() {
			this.owner.removeCell(this);
			this.owner = null;
		}
		
		//public void addEnergy( float energy) {
		//	unit += energy;
		//}
		
		public void setRecharge(boolean r) {
			this.recharge = r;
			
			HexMapSpriteObject toogleTile = tiles.get(this.owner.getOwnerName()+"Hex"+"_toggle");
		
			if( toogleTile != null) {
				if(this.recharge) {
					tile = toogleTile;
				}
				else {
					tile = tiles.get(this.owner.getOwnerName()+"Hex");
				}
			}
		}
		
		public void setBuilding( String name ) {
			if( building == null) {
				building = new Building();
			}
			if( name == null) {
				building = null;
			}
			else {
				building.set(name);
			}
		}

		public Building getBuilding() {
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
						return 1;
					}
				}
				
			}; 
			
			Integer a[] = { RESOURCE_WIND, RESOURCE_SOLAR, RESOURCE_CHEMICAL};
			
			Arrays.sort(a, comp);
			
			return a;
		}
		
		public Resource getResourceForBuilding(String name) {
			name = name.toLowerCase();
			////System.out.println(name);
			if( name.contains("windplant")) {
				return resources[RESOURCE_WIND];
			}
			else if(name.contains("solarplant")){
				return resources[RESOURCE_SOLAR];
			}
			else if(name.contains("chemicalplant")){
				//System.out.println(resources[RESOURCE_CHEMICAL]);
				return resources[RESOURCE_CHEMICAL];
			}
			return null;
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
	private NeutralEntity neutral = new NeutralEntity(this);
	private CorruptionEntity corruption = new CorruptionEntity(this);
	

	private static TextureRegion powerUpTexture;
	private static TextureRegion sporeTexture;
	private static TextureRegion playerAttack;
	private static TextureRegion corruptionAttack;
	
	
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
				   new HexMapSpriteObject("playerHex_Attack", atlas),
				   new HexMapSpriteObject("neutralHex", atlas),
				   new HexMapSpriteObject("corruptionHex", atlas),
				   new HexMapSpriteObject("corruptionHex_Attack", atlas));
		
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
		
		powerUpTexture = atlas.findRegion("powerUp");
		
		sporeTexture = atlas.findRegion("spore");
		
		playerAttack = atlas.findRegion("player_attack");
		corruptionAttack = atlas.findRegion("corruption_attack");
	}
	
	public HexMap() {
		//ui_if = new HexMapInterface(this);
	}
	
	public HexMapSpriteObject getTileTexture(String name) {
		return tiles.get(name);
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

	public NeutralEntity getNeutral() {
		return neutral;
	}
	
	public CorruptionEntity getCorruption() {
		return corruption;
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

	public interface AnimatedSpriteCallback {
		
		void runCallback( AnimatedSprite sprite );
		
	}
	
	private Vector2 temp = new Vector2();
	
	public class AnimatedSprite implements Poolable {

		//private String text = "";
		private TextureRegion texture;
		private Vector2 from = new Vector2();
		private Vector2 to = new Vector2();
		private Vector2 pos = new Vector2();
		private float sqLength = 0.0f;
		private boolean active = false;
		private float alpha = 0.0f;
		private float speed = 1.0f;
		private boolean rotate = false;
		private AnimatedSpriteCallback callback;
		private float cooldown = 0.0f;
		
		public AnimatedSprite() {
		}		

		public TextureRegion getTexture() {
			return texture;
		}
		
		public void setTexture(TextureRegion texture) {
			this.texture = texture;
		}

		public Vector2 getFrom() {
			return from;
		}

		public void setFrom(Vector2 from) {
			this.from = from;
		}

		public Vector2 getTo() {
			return to;
		}

		public void setTo(Vector2 to) {
			this.to = to;
		}

		public Vector2 getPos() {
			return pos;
		}

		public void setPos(Vector2 pos) {
			this.pos = pos;
		}

		public boolean isActive() {
			return active;
		}

		public void setActive(boolean active) {
			this.active = active;
		}

		public float getAlpha() {
			return alpha;
		}

		public void setAlpha(float alpha) {
			this.alpha = alpha;
		}

		public float getSpeed() {
			return speed;
		}

		public void setSpeed(float speed) {
			this.speed = speed;
		}

		public void update(float dt) {
			
			if( this.active ){
				
				if( cooldown <= 0.0f )
				{
					//alpha += speed*dt;
					pos.add(temp.set(to).scl(speed).scl(dt));
	
					float len2 = temp.set(pos).sub(from).len2();
					
					//System.out.println(from);
					
					if( sqLength <= len2) {
						active = false;
						if( callback != null) {
							callback.runCallback(this);
						}
						else {
							this.reset();
						}
						//System.out.println("reset");
						return;
					}
				}
				else
				{
					cooldown -= dt;
				}
				
			}
		}
		
		@Override
		public void reset() {
			//this.text = "";
			this.from.set(0, 0);
			this.to.set(0, 0);
			this.active = false;
			//this.alpha = 0.0f;
			this.speed = 1.0f;
			this.sqLength = 0.0f;
			this.callback = null;
			this.rotate = false;
			this.cooldown = 0.0f;
		}

		public boolean isRotate() {
			return rotate;
		}

		public void setRotate(boolean rotate) {
			this.rotate = rotate;
		}

		public boolean isReady() {
			// TODO Auto-generated method stub
			return cooldown <= 0.0f;
		}

		public void setCooldown(float f) {
			this.cooldown = f;
		}
		
	}
	
	private final Array<AnimatedSprite> activeSprites = new Array<AnimatedSprite>();
	
	    // bullet pool.
	final Pool<AnimatedSprite> spritePool = new Pool<AnimatedSprite>() {
		@Override
		protected AnimatedSprite newObject() {
			return new AnimatedSprite();
		}
	};	
	

	
	public void createAnimatedPowerUp(GridPoint2 from, float tox, float toy, float speed) {
		
		float x = (tile_width*0.5f) * 3/2 * from.x;
		float y = (float) ((tile_width*0.5f) * HexMapRenderer.sqrt3 * (from.y + 0.5 * (from.x&1)));
		
		AnimatedSprite sprite = spritePool.obtain();
		
		sprite.texture = powerUpTexture;
		sprite.pos.set(x,y);
		sprite.from.set(x, y);
		sprite.to.set(tox,toy);
		
		sprite.sqLength = sprite.to.len2();
		sprite.to.nor();
		
		
		sprite.speed = speed;
		sprite.active = true;
		
		activeSprites.add(sprite);
	}
	
	public void createSpore(GridPoint2 from, GridPoint2 to, float speed, AnimatedSpriteCallback callback) {
		float x = (tile_width*0.5f) * 3/2 * from.x;
		float y = (float) ((tile_width*0.5f) * HexMapRenderer.sqrt3 * (from.y + 0.5 * (from.x&1)));
		
		float tox = (tile_width*0.5f) * 3/2 * to.x;
		float toy = (float) ((tile_width*0.5f) * HexMapRenderer.sqrt3 * (to.y + 0.5 * (to.x&1)));
		
		AnimatedSprite sprite = spritePool.obtain();
		
		sprite.texture = sporeTexture;
		sprite.pos.set(x,y);
		sprite.from.set(x, y);
		sprite.to.set(tox-x,toy-y);
		
		//System.out.println("From: " + sprite.from + " To: " + sprite.to);
		
		sprite.sqLength = sprite.to.len2();
		sprite.to=sprite.to.nor();
		
		
		sprite.speed = speed;
		sprite.active = true;
		
		sprite.callback = callback;
		
		activeSprites.add(sprite);

		//System.out.println("Spore!");
	}

	public void attack(AttackCallback callback) {
		
		final GridPoint2 from = callback.start;
		final GridPoint2 to = callback.end;
		
		float x = (tile_width*0.5f) * 3/2 * from.x;
		float y = (float) ((tile_width*0.5f) * HexMapRenderer.sqrt3 * (from.y + 0.5 * (from.x&1)));
		
		float tox = (tile_width*0.5f) * 3/2 * to.x;
		float toy = (float) ((tile_width*0.5f) * HexMapRenderer.sqrt3 * (to.y + 0.5 * (to.x&1)));
		
		AnimatedSprite sprite = spritePool.obtain();
		
		sprite.texture = callback.texture;
		sprite.pos.set(x,y);
		sprite.from.set(x, y);
		sprite.to.set(tox-x,toy-y);
		
		sprite.sqLength = sprite.to.len2()*0.75f;
		sprite.to=sprite.to.nor();
		
		
		sprite.speed = 80f;
		sprite.active = true;
		sprite.rotate = true;
		
		sprite.callback = callback;
		
		activeSprites.add(sprite);
	}		
	
	public Array<AnimatedSprite> getActiveSprites() {
		return activeSprites;
	}

	public static TextureRegion getPlayerAttack() {
		return playerAttack;
	}

	public static TextureRegion getCorruptionAttack() {
		return corruptionAttack;
	}

}
