
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.adapt.mhc;


import java.awt.Color;
import java.awt.Point;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import ws.nzen.game.adventure.mhc.Entity;
import ws.nzen.game.adventure.mhc.location.Tile;
import ws.nzen.game.sim.hao.game.*;


/**

*/
public class BoardMapper
{
	private List<Entity> oldFlightPlanSprites = new LinkedList<>();

	public Point asAwtPoint(
			AtcRoutingNode latitudeLongitude
	) {
		return new Point(
				latitudeLongitude.getLongitude() +11,
				Math.abs( latitudeLongitude.getLatitude() -8 ) );
	}


	public MhcBoard asMhcBoard(
			AtcMap map, Collection<AtcAirplane> airplanes
	) {
		Collection<Entity> entities = new LinkedList<>();

		final String planeColor = "Silver",
				airportColor = "Yellow",
				oldFlightPlanNodeColor = "Black";
		Color baseFlightPlanColor = new Color( 30, 200, 30 );
		int mask = 0x00FFFFFF, colorOffset = 10;
		String hexcodeColorFomat = "#%06x";

		for ( Entity oldFlightPlanNodeSprite : oldFlightPlanSprites )
			entities.add( oldFlightPlanNodeSprite );

		for ( AtcAirplane airplane : airplanes )
		{
			int flightPlanNodes = 0;
			for ( AtcRoutingNode mapNode : airplane.getApprovedFlightPlan().getRoute() )
			{
				Color flightPlanColor = new Color(
						baseFlightPlanColor.getRed() + ( colorOffset * flightPlanNodes ),
						baseFlightPlanColor.getGreen() - ( colorOffset * flightPlanNodes ),
						baseFlightPlanColor.getBlue() + ( colorOffset * flightPlanNodes ) );

				Entity flightPlanSprite = new Entity(
						asAwtPoint( mapNode ),
						airplane.getAtcIdAsSingleCharacter(),
						String.format(
								hexcodeColorFomat,
								flightPlanColor.getRGB() & mask),
						airplane.getAtcId() +"-plan",
						false,
						null,
						null );
				entities.add( flightPlanSprite );
				flightPlanNodes += 1;
				oldFlightPlanSprites.add( new Entity(
						asAwtPoint( mapNode ),
						airplane.getAtcIdAsSingleCharacter(),
						oldFlightPlanNodeColor,
						airplane.getAtcId() +"-plan",
						false,
						null,
						null ) );
			}
			Entity airplaneSprite = new Entity(
					asAwtPoint( airplane.getClosestRoutingNode() ),
					airplane.getAtcIdAsSingleCharacter(),
					planeColor,
					airplane.getAtcId(),
					true,
					null,
					null );
			entities.add( airplaneSprite );
		}

		for ( AtcAirport airport : map.getAirports() )
		{
			Entity airportSprite = new Entity(
					asAwtPoint( airport.getEntranceNode() ),
					airport.getTeam() == AtcTeamTag.BLUE ? "ß" : "Ħ",
					airportColor,
					airport.getTeam().name(),
					true,
					null,
					null );
			entities.add( airportSprite );
		}

		Map<Point, Tile> impassibleNodes = new HashMap<>();
		for ( AtcRoutingNode mapNode : map.getNodes() )
		{
			if ( mapNode.isRestricted() )
				impassibleNodes.put( asAwtPoint( mapNode ), new Tile( true ) );
		}

		return new MhcBoard(
				new Point( map.getWidth(), map.getHeight() ),
				impassibleNodes,
				entities );
	}


}







