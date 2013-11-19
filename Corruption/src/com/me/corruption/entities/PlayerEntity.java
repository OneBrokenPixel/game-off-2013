package com.me.corruption.entities;

import java.util.HashSet;

import sun.rmi.transport.proxy.CGIHandler;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.me.corruption.hexMap.HexMap;
import com.me.corruption.hexMap.HexMap.Building;
import com.me.corruption.hexMap.HexMap.Cell;
import com.me.corruption.hexMap.HexMap.Resource;

public class PlayerEntity extends Entity {

	private HashSet<Cell> visible = new HashSet<Cell>();
	private float energyBank;
	private float rechargeRate = 2.0f;
	// wind, solar, chemical
	private static final float BUILDING_ENERGY[] = { 0.2f, 0.4f, 0.8f };

	public PlayerEntity(HexMap map) {
		super(map, "player");
		energyBank = 0.0f;
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
						this.energyBank += (BUILDING_ENERGY[b.id] * r.getAmount() * dt);
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

				float capped = MathUtils.clamp(rechargeEnergy, 0, rechargeRate * dt);

				c.addEnergy(capped);
				this.energyBank -= capped;
			}
		}

		// System.out.println(this.energyBank);

	}
}
