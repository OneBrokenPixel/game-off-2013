package com.me.corruption.hexMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.me.corruption.entities.CorruptionEntity;
import com.me.corruption.entities.Entity;
import com.me.corruption.entities.PlayerEntity;
import com.me.corruption.hexMap.HexMap.Cell;
import com.me.corruption.hexMap.HexMap.Resource;

public class HexMapGenerator {

	
	private static class Fractal {
		
		private int[][] data;
		
		private int min,max;
		
		public Fractal(int size, int min, int max) {
			this.data = new int[size][size];
			this.min = min;
			this.max = max;
			
			GridPoint2 p0 = setData( new GridPoint2(0,       0),        MathUtils.random(min, max));
			GridPoint2 pc = setData( new GridPoint2(size-1, size-1), MathUtils.random(min, max));
			
			square(p0,pc,size);
			
			//System.out.println(this.data[0][0]);
		}
		
		private GridPoint2 setData(GridPoint2 point, int dataPoint) {
			data[point.x][point.y] = dataPoint;
			return point;
		}
		
		private int getData(GridPoint2 point) {
			return data[point.x][point.y];

		}		

		public int getData(int x, int y) {
			// TODO Auto-generated method stub
			return data[x][y];
		}
		
		private void diamond( final GridPoint2 p0, final GridPoint2  p1, final GridPoint2 p2, final GridPoint2 p3, final float step ) {
			final int d0 = getData(p0);
			final int d1 = getData(p1);
			final int d2 = getData(p1);
			final int d3 = getData(p1);
			
			//System.out.println(step);
			
			
			int value = (MathUtils.random(min, max)+d0+d1+d2+d3)/5;
			//System.out.println("Value: " + value);
			
			// this is the problem
			GridPoint2 pc = setData(new GridPoint2(p0.x+((p2.x-p0.x)/2),p0.y+((p2.y-p0.y)/2)), value);
			
			square(p0, pc, step);
			square(p1, pc, step);
			square(p2, pc, step);
			square(p3, pc, step);
			

			//System.out.println(step);
		}
		
		private void square( final GridPoint2 p0, final GridPoint2 pc, final float step) {
			final int d0 = getData(p0);
			final int dc = getData(pc);
			
			int value1 = (MathUtils.random(min, max)+d0+dc)/3;
			int value2 = (MathUtils.random(min, max)+d0+dc)/3;
			
			//System.out.println("Value: " + value1);
			//System.out.println("Value: " + value2);
			
			GridPoint2 p1 = setData( new GridPoint2(p0.x,pc.y), value1);
			GridPoint2 p3 = setData( new GridPoint2(pc.x,p0.y), value2);
			
			if(step>=1f) {
				diamond(p0, p1, pc, p3,step/2);
			}
		}

	}
	
	private static void generateResorces(Cell[][] cells, int width, int height ) {
		final int size = Math.max(width, height);
		
		// control the rand values here.
		Fractal wind		= new Fractal(size, 1, 6);
		Fractal solar		= new Fractal(size, 1, 5);
		Fractal chemical	= new Fractal(size, 1, 4);
		
		for( int x = 0; x < width; x++) {
			for( int y = 0; y < height; y++) {
				//System.out.println(x+ " "+y);
				Cell cell = cells[x][y];
				
				int max = 4;
				final int solar_value = MathUtils.clamp(solar.getData(x,y), 1, 3);
				final int wind_value = MathUtils.clamp(wind.getData(x,y), 1, 3);
				final int chemical_value = MathUtils.clamp(chemical.getData(x,y), 1,3);
				if( max > 0 && solar_value >= chemical_value && solar_value >= wind_value ) {
					
					cell.resources[HexMap.RESOURCE_SOLAR] = new Resource("solar",(max>solar_value)?solar_value:max);
					max -= solar_value;
				}

				if( max > 0 && wind_value >= chemical_value && wind_value >= solar_value ) {

					cell.resources[HexMap.RESOURCE_WIND] = new Resource("wind",(max>wind_value)?wind_value:max);
					max -= wind_value;
				}

				if( max > 0 && chemical_value >= wind_value && chemical_value >= solar_value ) {

					cell.resources[HexMap.RESOURCE_CHEMICAL] = new Resource("chemical",(max>chemical_value)?chemical_value:max);

					max -= chemical_value;
				}
			}
		}
	}
	
	/**
	 * Generates the starting energy using a fractal ish algorithm.
	 * @param map
	 */
	private static void generateTileEnergy( Cell[][] cells, int width, int height ) {
		
		final int size = Math.max(width, height);
		
		Fractal f = new Fractal(size, 0, 15);
		
		for( int x = 0; x < width; x++) {
			for( int y = 0; y < height; y++) {
				//System.out.println(x+ " "+y);
				Cell cell = cells[x][y];
				//System.out.println(cell);
				cell.unit = f.getData(x,y);
			}
		}
	}
	
	
	private static Cell getRandomTile(Cell[][] cells, int min, int width, int height, GridPoint2 deadZoneCP, int deadZoneSize){
		
		int x = MathUtils.random(min, width-(deadZoneSize*2));
		int y = MathUtils.random(min, height-(deadZoneSize*2));
		if( deadZoneCP !=null) 
			System.out.println(deadZoneCP.x + " " + deadZoneCP.y);
		
		if( deadZoneCP !=null && x >= deadZoneCP.x-(deadZoneSize/2))
			x+=deadZoneSize;
		if( deadZoneCP !=null && y >= deadZoneCP.y-(deadZoneSize/2))
			y+=deadZoneSize;
		
		
		return cells[x][y];
	}
	
	/**
	 * Sets the starting tile for a specific owner.
	 * @param owner  - of the tile to set.
	 * @param map    - the map instance
	 */
	private static void setStartingTiles( Cell[][] cells, int width, int height, PlayerEntity player, CorruptionEntity corruption ) {
		
	
		Cell playerCell = getRandomTile(cells, 1, width-2, height-2, null, 0);
		Integer[] a = playerCell.sortResources();
		System.out.println(a[0]);
		switch(a[0]) {
		case HexMap.RESOURCE_CHEMICAL:
			playerCell.setBuilding("chemicalplant");
			break;
		case HexMap.RESOURCE_SOLAR:
			playerCell.setBuilding("solarplant");
			break;
		case HexMap.RESOURCE_WIND:
			playerCell.setBuilding("windplant");
			break;
		default:
			break;
		}		
		//playerCell.unit
		player.addOwnedCell(playerCell);
		
		
		Cell corruptionCell = getRandomTile(cells, 1, width-2, height-2, playerCell.point, 2);
		corruptionCell.unit = 0;
		corruption.addOwnedCell(corruptionCell);
		
	}
	/**
	 * Generates tile map, white tiles with black border
	 * @param width
	 * @param height
	 * @return returns the map generated
	 */
	public static HexMap generateTestMap( final int width, final int height) {
		
		HexMap map = new HexMap();
		
		Cell[][] cells = new Cell[width][height];
		
		for( int row = 0; row < height; row++) {
			for( int col = 0; col < width; col++) {

				Cell cell = map.new Cell();
				cell.point.set(col, row);
				map.getNeutral().addOwnedCell(cell);
				
				//cell.energy.unit = -10;
				cells[col][row] = cell;

			}
		}

		map.initalise(width, height, cells);
		generateTileEnergy(cells, width, height);
		generateResorces(cells, width, height);
		setStartingTiles(cells, width, height, map.getPlayer(), map.getCorruption());

		
		return map;
	}
}
