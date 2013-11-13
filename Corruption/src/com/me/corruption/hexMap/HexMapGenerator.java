package com.me.corruption.hexMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.me.corruption.hexMap.HexMap.Cell;

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
			
			System.out.println(this.data[0][0]);
		}
		
		private GridPoint2 setData(GridPoint2 point, int dataPoint) {
			data[point.x][point.y] = dataPoint;
			return point;
		}
		
		private int getData(GridPoint2 point) {
			return data[point.x][point.y];

		}		

		public float getData(int x, int y) {
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
			System.out.println("Value: " + value);
			
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
			
			System.out.println("Value: " + value1);
			System.out.println("Value: " + value2);
			
			GridPoint2 p1 = setData( new GridPoint2(p0.x,pc.y), value1);
			GridPoint2 p3 = setData( new GridPoint2(pc.x,p0.y), value2);
			
			if(step>=1f) {
				diamond(p0, p1, pc, p3,step/2);
			}
		}

	}
	
	/**
	 * Generates the starting energy using a fractal ish algorithm.
	 * @param map
	 */
	private static void generateTileEnergy( HexMap map ) {
		
		Fractal f = new Fractal(map.getWidth(), 0, 15);
		
		for( int x = 0; x < map.getHeight(); x++) {
			for( int y = 0; y < map.getHeight(); y++) {
				//System.out.println(x+ " "+y);
				Cell cell = map.getCell(x, y);
				//System.out.println(cell);
				cell.energy.unit = f.getData(x,y);
			}
		}
	}
	
	/**
	 * Sets the starting tile for a specific owner.
	 * @param owner  - of the tile to set.
	 * @param map    - the map instance
	 */
	private static void setStartingTile( String owner, HexMap map ) {
		
		final int width = map.getWidth();
		final int height = map.getHeight(); 
		
		do {
	
			final int x = MathUtils.random.nextInt(width-2)+1;
			final int y = MathUtils.random.nextInt(height-2)+1;
			
			final Cell cell = map.getCell(x, y);
			
			if( cell.owner.contains("neutral") ) {
				
				cell.setOwner(owner);
				
				break;
			}
			
		}while( true );
		
		
	}
	/**
	 * Generates tile map, white tiles with black border
	 * @param width
	 * @param height
	 * @return returns the map generated
	 */
	public static HexMap generateTestMap( final int size) {
		
		HexMap map = new HexMap();
		
		Cell[][] cells = new Cell[size][size];
		
		for( int row = 0; row < size; row++) {
			for( int col = 0; col < size; col++) {

				Cell cell = map.new Cell();
				cell.point.set(col, row);
				cell.setOwner("neutral");
				cell.resources[HexMap.RESOURCE_WIND] = map.new Resource("wind",2);
				cell.resources[HexMap.RESOURCE_SOLAR] = map.new Resource("solar",1);
				cell.resources[HexMap.RESOURCE_CHEMICAL] = map.new Resource("chemical",1);
				
				cell.energy.unit = -10;
				cells[col][row] = cell;

			}
		}
		map.initalise(size, size, cells);
		
		setStartingTile("player",map);
		setStartingTile("corruption",map);
		
		generateTileEnergy(map);
		
		//map.setPlayerStartCell(MathUtils.random.nextInt(width),MathUtils.random.nextInt(height));
		//map.setCorruptionStartCell(MathUtils.random.nextInt(width),MathUtils.random.nextInt(height));
		return map;
	}
}
