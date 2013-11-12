package com.me.corruption.hexMap;

import com.badlogic.gdx.math.MathUtils;
import com.me.corruption.hexMap.HexMap.Cell;

public class HexMapGenerator {

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
	public static HexMap generateTestMap( final int width, final int height) {
		
		HexMap map = new HexMap();
		
		Cell[][] cells = new Cell[width][height];
		
		for( int row = 0; row < height; row++) {
			for( int col = 0; col < width; col++) {

				Cell cell = map.new Cell();
				cell.point.set(col, row);
				cell.setOwner("neutral");
				cell.resources[HexMap.RESOURCE_WIND] = map.new Resource("wind",2);
				cell.resources[HexMap.RESOURCE_SOLAR] = map.new Resource("solar",1);
				cell.resources[HexMap.RESOURCE_CHEMICAL] = map.new Resource("chemical",1);
				cells[col][row] = cell;

			}
		}
		map.initalise(width, height, cells);
		
		setStartingTile("player",map);
		setStartingTile("corruption",map);
		
		//map.setPlayerStartCell(MathUtils.random.nextInt(width),MathUtils.random.nextInt(height));
		//map.setCorruptionStartCell(MathUtils.random.nextInt(width),MathUtils.random.nextInt(height));
		return map;
	}
}
