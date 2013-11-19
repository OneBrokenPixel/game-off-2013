package com.me.corruption.entities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.me.corruption.hexMap.HexMap;
import com.me.corruption.hexMap.HexMap.Cell;

public abstract class Entity {

	
	private Array<Cell> attacks = new Array<Cell>();
	
	private float attackRate = 1.0f;
	
	
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
	
	
	public void update(float dt) {
		
		
		Cell[] temp = new Cell[this.attacks.size];
		
		for( int i = 0; i<this.attacks.size; i++ ) {
			temp[i] = this.attacks.get(i);
		}
	
		for( Cell target : temp) {
			
			Cell[] attakingCells = getNeighbouringCellsWithOwners(target, this.getClass());
			
			int count = 0;
			for( Cell att : attakingCells) {
				float thisAttack = this.attackRate* dt ;
				if( att.unit >= thisAttack ) {
					att.unit -= thisAttack;
					count += 1;
				}
			}
			
			target.unit -= this.attackRate * count * dt;
			
			if( target.unit <= 0.0f ) {
				addOwnedCell(target);
				for( Entity att : target.attackers) {
					att.stopAttack(target);
				}
				
			}
		}
		
		tick(dt);
	}
	
	public abstract void tick(float dt);

	public void removeCell(Cell cell) {
		ownedCells.remove(cell);
		//cell.setOwner(null);
	}
	
	public void attack(Cell target) {
		if( getNeighbouringCellsWithOwners(target, this.getClass()).length != 0 ) {
			attacks.add(target);
			target.attackers.add(this);
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
					//System.out.println(c.owner.getClass());
					//System.out.println(targetOwner);
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
