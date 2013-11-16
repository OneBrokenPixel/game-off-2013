package com.me.corruption.entities;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.me.corruption.hexMap.HexMap;
import com.me.corruption.hexMap.HexMap.Building;
import com.me.corruption.hexMap.HexMap.Cell;
import com.me.corruption.hexMap.HexMap.Resource;

public class CorruptionEntity extends Entity {

	private HashSet<Cell> visible = new HashSet<Cell>();


	public CorruptionEntity(HexMap map) {
		super(map, "corruption");
		// TODO Auto-generated constructor stub
	}

	/*
	@Override
	public void addOwnedCell(Cell cell)
	{
		super.addOwnedCell(cell);
		repocessVisible();
	}
	
	@Override
	public void removeCell(Cell cell) {
		super.removeCell(cell);
		repocessVisible();
	}
	
	private void setCellAsVisible(Cell cell) {
		if(cell != null && !(cell.owner instanceof CorruptionEntity)) {
			//System.out.println(cell.owner.toString() + cell.owner.getOwnerName());
			visible.add(cell);
		}
	}	
	
	private void updateVisible(Cell cell) {
		final GridPoint2 point = cell.point;
		
		if( point.x % 2 == 0) {
			setCellAsVisible(map.getCell(point.x-1, point.y-1));
			setCellAsVisible(map.getCell(point.x-0, point.y-1));
			setCellAsVisible(map.getCell(point.x+1, point.y-1));
			setCellAsVisible(map.getCell(point.x-1, point.y+0));
			setCellAsVisible(map.getCell(point.x+1, point.y+0));
			setCellAsVisible(map.getCell(point.x-0, point.y+1));
		}
		else {
			
			setCellAsVisible(map.getCell(point.x-1, point.y+1));
			setCellAsVisible(map.getCell(point.x-0, point.y+1));
			setCellAsVisible(map.getCell(point.x+1, point.y+1));
			setCellAsVisible(map.getCell(point.x-1, point.y+0));
			setCellAsVisible(map.getCell(point.x+1, point.y+0));
			setCellAsVisible(map.getCell(point.x-0, point.y-1));
		}
	}
	
	private void repocessVisible() {
		visible.clear();
		for(Cell c : getOwnedCells()) {
			updateVisible(c);
		}
	}
		
	*/
	private int evalueateCell(Cell cell) {
		int eval = (int) cell.unit + 1;
		if( cell.owner instanceof PlayerEntity) {
			eval*=100;
		}
		return eval;
	}
	

	private HashSet<Cell> targetCells = new HashSet<Cell>();
	
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
		
		float meanCellEnergy = minCellEnergy+((maxCellEnergy-minCellEnergy)*0.5f);

		float leachEnergy = resourseCount * 0.1f * dt;

		//System.out.println(resourseCount);
		
		for( Cell c : ownedCells) {
			
			c.rechargeRate = (maxCellEnergy+1-c.unit)*dt;
			//System.out.println(c.rechargeRate);
			cellEnergyDefisite += c.rechargeRate;
			
		}	
		
		float rechargeUnit = leachEnergy / cellEnergyDefisite;
		

		targetCells.clear();
		
		for( Cell c : ownedCells) {
			
			c.unit += (rechargeUnit * c.rechargeRate);
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
		
		for( Cell c : targets) {
			//System.out.println(c.unit);
			
			//Cell[] attackers = getNeighbouringCellsWithOwners(c, CorruptionEntity.class);
			attackCell(c);
		
		}
	}
}
