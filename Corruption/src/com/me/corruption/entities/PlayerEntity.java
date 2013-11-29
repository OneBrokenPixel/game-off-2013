package com.me.corruption.entities;

import java.util.HashSet;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.me.corruption.hexMap.HexMap;
import com.me.corruption.hexMap.HexMap.Building;
import com.me.corruption.hexMap.HexMap.Cell;
import com.me.corruption.hexMap.HexMap.Resource;

public class PlayerEntity extends Entity {

	private HashSet<Cell> visible = new HashSet<Cell>();
	private float energyBank;
	//private float rechargeRate = 2.0f;


	public PlayerEntity(HexMap map) {
		super(map, "player");
		energyBank = Entity_Settings.player_StartingEnergy;
	}

	public float getEnergyBank() {
		return energyBank;
	}

	public HashSet<Cell> getVisible() {
		return visible;
	}

	@Override
	public void addOwnedCell(Cell cell) {
		super.addOwnedCell(cell);
		repocessVisible();
	}

	@Override
	public void removeCell(Cell cell) {
		super.removeCell(cell);
		repocessVisible();
	}

	private void setCellAsVisible(Cell cell) {
		if (cell != null && !(cell.owner instanceof PlayerEntity)) {
			visible.add(cell);
		}
	}

	private void updateVisible(Cell cell) {
		final GridPoint2 point = cell.point;

		if (point.x % 2 == 0) {
			setCellAsVisible(map.getCell(point.x - 1, point.y - 1));
			setCellAsVisible(map.getCell(point.x - 0, point.y - 1));
			setCellAsVisible(map.getCell(point.x + 1, point.y - 1));
			setCellAsVisible(map.getCell(point.x - 1, point.y + 0));
			setCellAsVisible(map.getCell(point.x + 1, point.y + 0));
			setCellAsVisible(map.getCell(point.x - 0, point.y + 1));
		} else {

			setCellAsVisible(map.getCell(point.x - 1, point.y + 1));
			setCellAsVisible(map.getCell(point.x - 0, point.y + 1));
			setCellAsVisible(map.getCell(point.x + 1, point.y + 1));
			setCellAsVisible(map.getCell(point.x - 1, point.y + 0));
			setCellAsVisible(map.getCell(point.x + 1, point.y + 0));
			setCellAsVisible(map.getCell(point.x - 0, point.y - 1));
		}
	}

	private void repocessVisible() {
		visible.clear();
		for (Cell c : getOwnedCells()) {
			updateVisible(c);
		}
	}

	public boolean removeEnergy(int cost) {
		if (energyBank >= cost) {
			energyBank -= cost;
			return true;
		}
		return false;
	}
	
	private HashSet<Cell> recharging = new HashSet<Cell>();

	@Override
	public void tick(float dt) {

		// this.energyBank += 200f*dt;

		recharging.clear();

		for (Cell c : ownedCells) {
			if (c.getBuilding() != null) {
				// calculate energy generated here!

				final Building b = c.getBuilding();
				if (b != null) {
					final Resource r = c.getResourceForBuilding(b.name);
					// System.out.println(r);
					if (r != null) {
						if( !b.name.contains("_rubble") ) {
							final float energy = (Entity_Settings.BUILDING_ENERGY[b.id] * r.getAmount() * dt);
							//this.energyBank += energy;
							b.energyCap += energy;
							
							if( b.energyCap >= Entity_Settings.energyCapasity ) {
								b.energyCap -= Entity_Settings.energyCapasity;
								this.energyBank += Entity_Settings.energyCapasity;
								map.createAnimatedPowerUp(c.point, 0,32f, 20f);
							}
						}
						
						if( b.lifeTime <= 0.0f ) {
							c.setBuilding(b.name.substring(0, b.name.length()-5)+"_rubble");
						}
						else if ( !b.name.contains("_half") && b.lifeTime <= Entity_Settings.BUILDING_LIFE[b.id]*0.5f ) {
							c.setBuilding(b.name+"_half");
						}
						//System.out.println(b.lifeTime);
						b.lifeTime -=dt;
					}
				}
			}
			if (c.recharge) {
				recharging.add(c);
			}
		}

		if (recharging.size() != 0) {
			float rechargeEnergy = this.energyBank / recharging.size();

			for (Cell c : recharging) {
				float capped = MathUtils.clamp(rechargeEnergy, 0, Entity_Settings.attackRate*dt);
				c.unit += capped;
				this.energyBank -= capped;
			}
		}
	}
	
	@Override
	public void resolveAttack(Cell cell) {

		addOwnedCell(cell);
		//int neighbours = getNeighbouringCellsWithOwners(cell, this.getClass()).length*2;
		//int energyCost = (int) Math.min(neighbours, this.energyBank);
		//cell.unit = energyCost;
		//energyBank -= energyCost;
		
		
		//cell.setRecharge(true);
	}
}
