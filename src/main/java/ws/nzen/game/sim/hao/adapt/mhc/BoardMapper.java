
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.adapt.mhc;


import java.awt.Point;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;

import ws.nzen.game.adventure.mhc.Entity;
import ws.nzen.game.adventure.mhc.location.Tile;
import ws.nzen.game.sim.hao.game.*;


/**

*/
public class BoardMapper
{

	public Point asAwtPoint(
			AtcRoutingNode latitudeLongitude
	) {
		return new Point(
				latitudeLongitude.getLongitude() +11,
				latitudeLongitude.getLatitude() +8 );
	}


	public MhcBoard asMhcBoard(
			AtcMap map, Collection<AtcAirplane> airplanes
	) {
		Point maxMapDim = new Point( 23, 17 );
		Collection<Entity> entities = new LinkedList<>();
		int airplaneSeen = 0;
		final String planeColor = "Silver", flightPlanNodeColor = "DarkGreen", airportColor = "Blue";
		for ( AtcAirplane airplane : airplanes )
		{
			for ( AtcRoutingNode mapNode : airplane.getFlightPlan().getRoute() )
			{
				Entity airplaneSprite = new Entity(
						asAwtPoint( mapNode ),
						Integer.toString( airplaneSeen ),
						flightPlanNodeColor,
						airplane.getAtcId() +"-plan",
						false,
						null,
						null );
				entities.add( airplaneSprite );
			}
			Entity airplaneSprite = new Entity(
					asAwtPoint( airplane.getFlightPlan().getRoute().get( 0 ) ), // FIX use closest node
					Integer.toString( airplaneSeen ),
					planeColor,
					airplane.getAtcId(),
					true,
					null,
					null );
			entities.add( airplaneSprite );
			airplaneSeen += 1;
		}

		for ( AtcAirport airport : map.getAirports() )
		{
			Entity airportSprite = new Entity(
					asAwtPoint( airport.getEntranceNode() ),
					airport.getTeam() == AtcTeamTag.BLUE ? "B" : "R",
					planeColor,
					airport.getTeam().name(),
					true,
					null,
					null );
			entities.add( airportSprite );
			airplaneSeen += 1;
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
