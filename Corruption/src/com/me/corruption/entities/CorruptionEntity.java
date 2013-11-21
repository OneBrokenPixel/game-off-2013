package com.me.corruption.entities;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;


import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.me.corruption.hexMap.HexMap;
import com.me.corruption.hexMap.HexMap.Cell;
import com.me.corruption.hexMap.HexMap.Resource;

public class CorruptionEntity extends Entity {

	private static final float handicap = 0.05f;

	private HashSet<Cell> targetCells = new HashSet<Cell>();
	

	public CorruptionEntity(HexMap map) {
		super(map, "corruption");
		// TODO Auto-generated constructor stub
	}

	private int evalueateCell(Cell cell) {
		int eval = (int) cell.unit + 1;
		
		if( cell.owner instanceof PlayerEntity || cell.attackers.contains(map.getPlayer(), false) ) {
			eval*=10;
		}
		return eval;
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
	
	
	
	@Override
	public void tick(float dt) {

		float maxCellEnergy = Float.NEGATIVE_INFINITY;
		float minCellEnergy = Float.POSITIVE_INFINITY;
		float cellEnergyDefisite = 0;
		int resourseCount = 0;
		
		for( Cell c : ownedCells) {
			maxCellEnergy = Math.max(maxCellEnergy, c.unit);
			minCellEnergy = Math.min(minCellEnergy, c.unit);
			for(Resource r : c.resources) {
				if(r!=null){
					resourseCount += r.getAmount();
				}
			}
			
		}		
		
		//float meanCellEnergy = minCellEnergy+((maxCellEnergy-minCellEnergy)*0.5f);

		float leachEnergy = resourseCount * CorruptionEntity.handicap * dt;

		//System.out.println(resourseC;
		
		for( Cell c : ownedCells) {
			
			c.rechargeRate = (maxCellEnergy+1-c.unit)*dt;
			//System.out.println(c.rechargeRate);
			cellEnergyDefisite += c.rechargeRate;
			
		}	
		
		float rechargeUnit = leachEnergy / cellEnergyDefisite;
		

		targetCells.clear();
		
		for( Cell c : ownedCells) {
			
			c.unit += MathUtils.clamp(rechargeUnit * c.rechargeRate, 0,this.getAttackRate()*dt);
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
		
		if(targets.length != 0) {
			if( !isAttacking(targets[0])) {
				//Cell[] attackers = getNeighbouringCellsWithOwners(targets[0], this.getClass());
				//float tEnergy = 0;
				//for( Cell c : attackers) {
				//	tEnergy += c.unit;
				//}
				//if( tEnergy >= (targets[0].unit/4)) {
					attack(targets[0]);
				//}
			}
		}
	}

	@Override
	public void resolveAttack(Cell cell) {

		addOwnedCell(cell);
	}
}
