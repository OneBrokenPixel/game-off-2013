package com.me.corruption.entities;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;


import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.me.corruption.hexMap.HexMap;
import com.me.corruption.hexMap.HexMap.AnimatedSprite;
import com.me.corruption.hexMap.HexMap.AnimatedSpriteCallback;
import com.me.corruption.hexMap.HexMap.Cell;
import com.me.corruption.hexMap.HexMap.Resource;

public class CorruptionEntity extends Entity {


	private HashSet<Cell> targetCells = new HashSet<Cell>();
	
	private float sporeTimmer = 0.0f;
	

	public CorruptionEntity(HexMap map) {
		super(map, "corruption");
		resetSporeTimer();
	}

	private int evalueateCell(Cell cell) {
		int eval = (int) cell.unit + 1;
		
		if( cell.owner instanceof PlayerEntity || cell.attackers.contains(map.getPlayer(), false) ) {
			eval*=10;
		}
		return eval;
	}
	
	private void resetSporeTimer() {
		sporeTimmer = Entity_Settings.corruption_sporeTime + MathUtils.random(15f)-15f - MathUtils.random(ownedCells.size());
	}

	private void setTarget(Cell cell) {
		if(cell != null && !(cell.owner instanceof CorruptionEntity)) {
			//System.out.println(cell.owner.toString() + cell.owner.getOwnerName());
			targetCells.add(cell);
		}
	}
	
	private void updateTargets(Cell cell) {
		final GridPoint2 point = cell.point;
		
		if( point.x % 2 == 0) {
			setTarget(map.getCell(point.x-1, point.y-1));
			setTarget(map.getCell(point.x-0, point.y-1));
			setTarget(map.getCell(point.x+1, point.y-1));
			setTarget(map.getCell(point.x-1, point.y+0));
			setTarget(map.getCell(point.x+1, point.y+0));
			setTarget(map.getCell(point.x-0, point.y+1));
		}
		else {
			
			setTarget(map.getCell(point.x-1, point.y+1));
			setTarget(map.getCell(point.x-0, point.y+1));
			setTarget(map.getCell(point.x+1, point.y+1));
			setTarget(map.getCell(point.x-1, point.y+0));
			setTarget(map.getCell(point.x+1, point.y+0));
			setTarget(map.getCell(point.x-0, point.y-1));
		}
	}
	
	public class SproreCallback implements AnimatedSpriteCallback, Poolable {
		
		private Cell target = null;
		
		public void setTarget( Cell target )  {
			this.target = target;
		}
		
		@Override
		public void runCallback(AnimatedSprite sprite) {

			//System.out.println("attacking");
			
			if( !(target.owner instanceof CorruptionEntity) ) {
				target.unit -= MathUtils.random(5)+5f;
				
				if( target.unit <= 0.0f ) {
					resolveAttack(target);
				}
			}
			
			callbackPool.free(this);
			sprite.reset();
		}

		@Override
		public void reset() {
			target = null;
		}
	};
	
	final Pool<SproreCallback> callbackPool = new Pool<SproreCallback>() {
		@Override
		protected SproreCallback newObject() {
			return new SproreCallback();
		}
	};	
	
	@Override
	public void tick(float dt) {

		sporeTimmer -= dt;
		
		if( sporeTimmer <= 0.0f ) {
		
			final PlayerEntity player = map.getPlayer();
			final int playerCells = player.getOwnedCells().size();
			
			if( playerCells > 0) {
				Cell targetCell = (Cell)(player.getOwnedCells().toArray()[MathUtils.random(playerCells-1)]);
				Cell sourceCell = (Cell)(this.getOwnedCells().toArray()[MathUtils.random(this.getOwnedCells().size()-1)]);
	
				SproreCallback callback = callbackPool.obtain();
				callback.setTarget(targetCell);
				
				//System.out.println("spore!");
				map.createSpore(sourceCell.point, targetCell.point, 30f, callback);
			
			}
			resetSporeTimer();
			//sportTimmer /= (ownedCells.size()/2);
		}
		
		
		float maxCellEnergy = Float.NEGATIVE_INFINITY;
		float minCellEnergy = Float.POSITIVE_INFINITY;
		float totalEnergy = 0.0f;
		float cellEnergyDefisite = 0;
		int resourseCount = 0;
		
		for( Cell c : ownedCells) {
			maxCellEnergy = Math.max(maxCellEnergy, c.unit);
			minCellEnergy = Math.min(minCellEnergy, c.unit);
			totalEnergy += c.unit;
			for(Resource r : c.resources) {
				if(r!=null){
					resourseCount += r.getAmount();
				}
			}
			
		}		
		
		System.out.println(totalEnergy);
		
		//float meanCellEnergy = minCellEnergy+((maxCellEnergy-minCellEnergy)*0.5f);

		float leachEnergy = resourseCount * Entity_Settings.corruption_handicap * dt;

		//System.out.println(resourseC;
		
		for( Cell c : ownedCells) {
			
			c.rechargeRate = (maxCellEnergy+1-c.unit)*dt;
			//System.out.println(c.rechargeRate);
			cellEnergyDefisite += c.rechargeRate;
			
		}	
		
		float rechargeUnit = leachEnergy / cellEnergyDefisite;
		

		targetCells.clear();
		
		for( Cell c : ownedCells) {
			
			c.unit += MathUtils.clamp(rechargeUnit * c.rechargeRate, 0,Entity_Settings.attackRate*dt);
			updateTargets(c);
		}	
		
		Cell[] targets = new Cell[targetCells.size()];
		
		targetCells.toArray(targets);
		
		Comparator<Cell> eveluator = new Comparator<Cell>() {
			
			@Override
			public int compare(Cell o1, Cell o2) {
				return evalueateCell(o1) - evalueateCell(o2);
			}
		};
		
		Arrays.sort(targets, eveluator);
		if( minCellEnergy >= 10) {
			if(targets.length != 0) {
				if( !isAttacking(targets[0])) {
					attack(targets[0]);
				}
			}
		}
	}

	@Override
	public void resolveAttack(Cell cell) {

		addOwnedCell(cell);

		cell.unit += getNeighbouringCellsWithOwners(cell, this.getClass()).length;
		cell.setBuilding(null);
	}
}
