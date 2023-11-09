
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
	private Collection<Entity> oldFlightPlanSprites = new LinkedList<>();

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
		Point maxMapDim = new Point( 23, 17 );
		Collection<Entity> entities = new LinkedList<>();
		int airplaneSeen = 0;
		final String planeColor = "Silver",
				flightPlanNodeColor = "DarkGreen",
				airportColor = "Blue",
				oldFlightPlanNodeColor = "Black";

		for ( Entity oldFlightPlanNodeSprite : oldFlightPlanSprites )
			entities.add( oldFlightPlanNodeSprite );

		for ( AtcAirplane airplane : airplanes )
		{
			for ( AtcRoutingNode mapNode : airplane.getApprovedFlightPlan().getRoute() )
			{
				Entity flightPlanSprite = new Entity(
						asAwtPoint( mapNode ),
						airplaneIdentifier( airplane ),
						flightPlanNodeColor,
						airplane.getAtcId() +"-plan",
						false,
						null,
						null );
				entities.add( flightPlanSprite );
				oldFlightPlanSprites.add( new Entity(
						asAwtPoint( mapNode ),
						airplaneIdentifier( airplane ),
						oldFlightPlanNodeColor,
						airplane.getAtcId() +"-plan",
						false,
						null,
						null ) );
			}
			Entity airplaneSprite = new Entity(
					asAwtPoint( airplane.getClosestRoutingNode() ),
					airplaneIdentifier( airplane ),
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


	private String airplaneIdentifier(
			AtcAirplane airplane
	) {
		String withoutTextualPart = airplane.getAtcId().substring( "AT-".length() );
		int rawId = Integer.parseInt( withoutTextualPart ) %90; // ¶ for visible ascii char range
		Character baseChar = '\'';
		Character offsetChar = Character.valueOf( (char)( baseChar.charValue() + rawId ) );
		return offsetChar.toString();
	}


}







