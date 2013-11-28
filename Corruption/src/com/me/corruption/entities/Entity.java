package com.me.corruption.entities;

import java.util.HashSet;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.me.corruption.hexMap.HexMap;
import com.me.corruption.hexMap.HexMap.AnimatedSprite;
import com.me.corruption.hexMap.HexMap.AnimatedSpriteCallback;
import com.me.corruption.hexMap.HexMap.Cell;

public abstract class Entity {

	
	private Array<Cell> attacks = new Array<Cell>();
	
	
	protected HashSet<Cell> ownedCells = new HashSet<Cell>();
	protected String owner = "";

	protected HexMap map;
	
	protected Entity(HexMap map, String owner) {
		this.map = map;
		this.owner = owner;
	}
	
	/*
	public float getAttackRate() {
		return attackRate;
	}
	*/
	public HashSet<Cell> getOwnedCells() {
		return ownedCells;
	}
	
	public void addOwnedCell(Cell cell) {
		ownedCells.add(cell);
		cell.setOwner(this);
	}
	
	public String getOwnerName() {
		return owner;
	}
	
	private Entity parent = this;

	public class AttackCallback implements AnimatedSpriteCallback, Poolable {
		
		public Cell target = null;
		public GridPoint2 start;
		public GridPoint2 end;
		public TextureRegion texture;
		private Cell source;
		
		public void set( Cell source, Cell target,TextureRegion texture) {
			this.texture = texture;
			this.target = target;
			this.source = source;
			this.start = source.point;
			this.end = target.point;
		}
		
		@Override
		public void runCallback(AnimatedSprite sprite) {
			//System.out.println(parent);
			
			boolean attacking = false;
			for( Entity e : target.attackers) {
				attacking |= e.equals(parent);
			}
			
			if( !source.owner.equals(parent) || target.owner.equals(parent) || attacking == false) {
				//System.out.println(target.owner);
				callbackPool.free(this);
				sprite.reset();
			}
			else {
				sprite.getPos().set(sprite.getFrom());
				sprite.setActive(true);
				sprite.setCooldown(1.0f);
			}
		}

		@Override
		public void reset() {
			target = null;
			this.start = null;
			this.end = null;
		}
	};
	
	final Pool<AttackCallback> callbackPool = new Pool<AttackCallback>() {
		@Override
		protected AttackCallback newObject() {
			return new AttackCallback();
		}
	};		
	
	public void update(float dt) {
		
		
		Cell[] temp = new Cell[this.attacks.size];
		
		for( int i = 0; i<this.attacks.size; i++ ) {
			temp[i] = this.attacks.get(i);
		}
	
		for( Cell target : temp) {
			
			Cell[] attakingCells = getNeighbouringCellsWithOwners(target, this.getClass());
			
			if( attakingCells.length == 0 ) {
				this.attacks.removeValue(target, false);
				continue;
			}
			
			int count = 0;
			for( Cell att : attakingCells) {
				float thisAttack = Entity_Settings.attackRate* dt ;
				if( att.unit >= thisAttack ) {
					att.unit -= thisAttack;
					count += 1;
				}
			}
			
			target.unit -= Entity_Settings.attackRate * count * dt;
			
			if( target.unit <= 0.0f ) {
				this.resolveAttack(target);
				for( Entity att : target.attackers) {
					att.stopAttack(target);
				}
				
			}
		}
		
		tick(dt);
	}
	
	public abstract void resolveAttack(Cell cell);
	
	public abstract void tick(float dt);

	public void removeCell(Cell cell) {
		ownedCells.remove(cell);
		//cell.setOwner(null);
	}
	
	public void attack(Cell target) {
		
		Cell[] neighbours = getNeighbouringCellsWithOwners(target, this.getClass());
		TextureRegion attackTexure = (this.getClass().equals(PlayerEntity.class))? HexMap.getPlayerAttack():HexMap.getCorruptionAttack();
		if( neighbours.length != 0 ) {
			attacks.add(target);
			target.attackers.add(this);
			for( Cell c : neighbours) {
				AttackCallback callback = callbackPool.obtain();
				callback.set(c, target, attackTexure);
				map.attack(callback);
			}
		}
	}
	
	public boolean  isAttacking(Cell cell) {
		return attacks.contains(cell, false);
	}
	
	public void stopAttack(Cell cell) {
		attacks.removeValue(cell, false);
		cell.attackers.removeValue(this, false);
	}
	
	public Array<Cell> getAttacks() {
		// TODO Auto-generated method stub
		return attacks;
	}
	
	public int ownedAmount() {
		return ownedCells.size();
		
	}

	private static GridPoint2 even[] = {	new GridPoint2(-1, -1),
											new GridPoint2(0, -1),
											new GridPoint2(1, -1),
											new GridPoint2(-1, 0),
											new GridPoint2(1, 0),
											new GridPoint2(0, 1)};

	private static GridPoint2 odd[] = {		new GridPoint2(-1, 1),
											new GridPoint2(0, 1),
											new GridPoint2(1, 1),
											new GridPoint2(-1, 0),
											new GridPoint2(1, 0),
											new GridPoint2(0, -1)};	
	
	private Array<Cell> cells = new Array<Cell>(6);
	
	protected Cell[] getNeighbouringCells(Cell cell) {

		final GridPoint2 point = cell.point;
		int col = point.x;

		GridPoint2 points[] = ((col&1) == 0)? even:odd;
		cells.clear();
		for( GridPoint2 p : points) {
			final Cell c = map.getCell(point.x+p.x, point.y+p.y);
			if( c != null) {
				cells.add(c);
			}
		}
		
		Cell[] cellsArray = new Cell[cells.size];
		
		for( int i = 0; i<cells.size; i++ ) {
			cellsArray[i] = cells.get(i);
		}
		
		return cellsArray;
	}
	
	protected Cell[] getNeighbouringCellsWithOwners(Cell cell, Class<?>...owners) {

		final GridPoint2 point = cell.point;
		int col = point.x;

		GridPoint2 points[] = ((col&1) == 0)? even:odd;
		cells.clear();
		for( GridPoint2 p : points) {
			final Cell c = map.getCell(point.x+p.x, point.y+p.y);
			if( c != null) {
				boolean isTargetOwner = true;
				for( Class<?> targetOwner : owners) {
					isTargetOwner = isTargetOwner && (c.owner.getClass().equals(targetOwner));
				}
				if( isTargetOwner ) {
					cells.add(c);
				}
			}
		}
		
		Cell[] cellsArray = new Cell[cells.size];
		
		for( int i = 0; i<cells.size; i++ ) {
			cellsArray[i] = cells.get(i);
		}
		
		return cellsArray;
	}	
}
