package com.me.corruption.entities;

import java.util.HashSet;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.me.corruption.hexMap.HexMap;
import com.me.corruption.hexMap.HexMap.Cell;

public abstract class Entity {

	protected HashSet<Cell> ownedCells = new HashSet<Cell>();
	protected String owner = "";

	protected HexMap map;
	
	protected Entity(HexMap map, String owner) {
		this.map = map;
		this.owner = owner;
	}
	
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
	
	public abstract void tick(float dt);

	public void removeCell(Cell cell) {
		ownedCells.remove(cell);
		//cell.setOwner(null);
	}
	
	public boolean attackCell(Cell target) {
		
		Cell[] attackers = getNeighbouringCellsWithOwners(target, this.getClass());
		
		
		float totalAttackingEnergy = 0.0f;
		
		for(Cell c : attackers) {
			totalAttackingEnergy += c.unit;
		}
		
		//System.out.println(attekers.length);
		
		if( totalAttackingEnergy >=  target.unit ) {
			float energyUnit = target.unit/totalAttackingEnergy;
			
			target.unit = 0;
			
			for(Cell c : attackers) {
				c.unit -= energyUnit * c.unit;
			}
			target.unit = 0;
			
			this.addOwnedCell(target);
			
			return true;
		}
		
		return false;
	}
	

	private static GridPoint2 odd[] = {	new GridPoint2(-1, -1),
										new GridPoint2(0, -1),
										new GridPoint2(1, -1),
										new GridPoint2(1, 0),
										new GridPoint2(1, 0),
										new GridPoint2(0, 1)};

	private static GridPoint2 even[] = {	new GridPoint2(1, 1),
											new GridPoint2(0, 1),
											new GridPoint2(1, 1),
											new GridPoint2(-1, 0),
											new GridPoint2(1, 0),
											new GridPoint2(0, -1)};	
	
	private Array<Cell> cells = new Array<Cell>(6);
	
	protected Cell[] getNeighbouringCells(Cell cell) {

		final GridPoint2 point = cell.point;
		int col = point.x;

		GridPoint2 points[] = ((col&1) == 0)? odd:even;
		cells.clear();
		for( GridPoint2 p : points) {
			final Cell c = map.getCell(point.x+p.x, point.y+p.y);
			if( c != null) {
				cells.add(c);
			}
		}
		
		return cells.toArray(Cell.class);
	}
	
	protected Cell[] getNeighbouringCellsWithOwners(Cell cell, Class<?>...owners) {

		final GridPoint2 point = cell.point;
		int col = point.x;

		GridPoint2 points[] = ((col&1) == 0)? odd:even;
		cells.clear();
		for( GridPoint2 p : points) {
			final Cell c = map.getCell(point.x+p.x, point.y+p.y);
			if( c != null) {
				boolean isTargetOwner = true;
				for( Class<?> targetOwner : owners) {
					//System.out.println(c.owner.getClass());
					//System.out.println(targetOwner);
					isTargetOwner = isTargetOwner && (c.owner.getClass().equals(targetOwner));
				}
				if( isTargetOwner ) {
					cells.add(c);
				}
			}
		}
		
		return cells.toArray(Cell.class);
	}
		
}
