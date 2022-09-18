
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.game;


import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ws.nzen.game.adventure.mhc.Entity;
import ws.nzen.game.adventure.mhc.location.Tile;
import ws.nzen.game.adventure.mhc.message.Cell;


/**

*/
public class MhcBoard
{

	private Point maxMapDim;
	private Map<Point, Tile> tiles;
	Map<Point, Tile> landmarks = new HashMap<>();
	Map<Point, Entity> agents = new HashMap<>();


	public MhcBoard(
			Point lowerRightCornerOfBoard,
			Map<Point, Tile> staticSprites,
			Collection<Entity> mobileSprites
	) {
		maxMapDim = lowerRightCornerOfBoard;
		tiles = initializeTiles();
		landmarks.putAll( staticSprites );
		for ( Entity who : mobileSprites )
			agents.put( who.getPosition(), who );
	}


	public List<List<Cell>> boardForClient(
	) {
		List<List<Cell>> everyLayer = new LinkedList<>();
		List<Cell> onlyLayer = new ArrayList<>();
		String colorDarkWall = "DarkBrown", colorDarkness = "DarkBlue",
				colorLitWall = "Chocolate", colorLightness = "DeepSkyBlue";
		String wallChar = "#", dirtChar = "-";
		Set<Point> occupiedTiles = new HashSet<>();

		for ( Point where : agents.keySet() )
		{
			occupiedTiles.add( where );
			Entity who = agents.get( where );
			Cell toDraw = new Cell(
					who.getSymbol(), who.getXxPos(),
					who.getYyPos(), who.getColor()
			);
			onlyLayer.add( toDraw );
		}

		for ( Point where : landmarks.keySet() )
		{
			occupiedTiles.add( where );
			Tile what = landmarks.get( where );
			String solid = what.isImpassable() ? wallChar : dirtChar;
			String tint = what.isImpassable() ? colorLitWall : colorLightness;
			Cell toDraw = new Cell( solid, where.x, where.y, tint );
			onlyLayer.add( toDraw );
		}

		/*
		for ( Point where : tiles.keySet() )
		{
			Cell toDraw = null;
			boolean occupied = false;
			if ( agents.containsKey( where ) )
			{
				Entity who = agents.get( where );
				if ( who.getXxPos() == where.x
						&& who.getYyPos() == where.y )
				{
					occupied = true;
					toDraw = new Cell(
							who.getSymbol(), who.getXxPos(),
							who.getYyPos(), who.getColor()
					);
					break;
				}
			}
			else if ( landmarks.containsKey( where ) )
			{
				Tile what = landmarks.get( where );
				String solid = what.isImpassable() ? wallChar : dirtChar;
				String tint = what.isImpassable() ? colorLitWall : colorLightness;
				toDraw = new Cell( solid, where.x, where.y, tint );
			}
			else
			{
				Tile what = tiles.get( where );
				String solid = what.isImpassable() ? wallChar : dirtChar;
				String tint = what.isImpassable() ? colorLitWall : colorLightness;
				toDraw = new Cell( solid, where.x, where.y, tint );
			}
			tiles.get( where ).setExperienced( true );
			onlyLayer.add( toDraw );
		}
		*/

		// ¶ show currently visible area
		for ( Point where : tiles.keySet() )
		{
			if ( occupiedTiles.contains( where ) )
				continue;
			Cell toDraw = null;
			/* Tile what = tiles.get( where );
			tiles.get( where ).setExperienced( true ); */
			toDraw = new Cell( dirtChar, where.x, where.y, colorLightness );
			onlyLayer.add( toDraw );
		}

		everyLayer.add( onlyLayer );
		return everyLayer;
	}


	private Map<Point, Tile> initializeTiles(
	) {
		Map<Point, Tile> terrain = new HashMap<>();
		for ( int xx = maxMapDim.x - 1; xx >= 0; xx-- )
		{
			for ( int yy = maxMapDim.y - 1; yy >= 0; yy-- )
			{
				terrain.put( new Point( xx, yy ), new Tile( false ) );
			}
		}
		return terrain;
	}

}
