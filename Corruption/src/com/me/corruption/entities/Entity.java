package com.me.corruption.entities;

import java.util.HashSet;

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
	}
	
}
