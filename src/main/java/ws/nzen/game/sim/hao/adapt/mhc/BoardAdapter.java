
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.adapt.mhc;


import java.awt.Point;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import ws.nzen.game.adventure.mhc.Entity;
import ws.nzen.game.adventure.mhc.location.Tile;
import ws.nzen.game.adventure.mhc.message.Cell;
import ws.nzen.game.sim.hao.game.*;


/**

*/
public class BoardAdapter
{

	public MhcBoard asMhcBoard(
			AtcMap map, Collection<AtcAirplane> airplanes
	) {
		throw new RuntimeException( "unimplemented" );
		/* Point maxMapDim = new Point( 17, 23 );
		 * return new MhcBoard( maxMapDim );
		 * Collection<Entity> entities = new LinkedList<>();
		 * // for airport on map, entity
		 * int airplaneSeen = 0;
		 * final String planeColor = "Silver", flightPlanNodeColor = "DarkGreen", airportColor = "Blue";
		 * for ( AtcAirplane airplane : airplanes )
		 * {
		 * for ( AtcRoutingNode mapNode : airplane.getFlightPlan() )
		 * {
		 * Entity airplaneSprite = new Entity(
		 * mapNode.getwhere, // probably translate
		 * Integer.toString( airplaneSeen ),
		 * flightPlanNodeColor,
		 * airplane.getId() +"-plan",
		 * false,
		 * null,
		 * null );
		 * entities.add( airplaneSprite );
		 * }
		 * // draw the airplan on top of its flight plan, though that's more of a canvas endpoint responsibility
		 * Entity airplaneSprite = new Entity(
		 * airplane.getwhere, // probably translate
		 * Integer.toString( airplaneSeen ),
		 * planeColor,
		 * airplane.getId(),
		 * true,
		 * null,
		 * null );
		 * entities.add( airplaneSprite );
		 * airplaneSeen += 1;
		 * }
		 * /*
		 * Turn airplanes into entities, airports into entities ?, reserved into impassibles; maybe, I don't need to use
		 * the old assumptions about wall avatar; yeah, all three are entities
		 * 
		 * Collection<AtcAirport> airports;
		 * Collection<AtcRoutingNode> nodes;
		 * int width;
		 * int height;
		 * public MhcBoard(
		 * Point lowerRight
		 * 
		 * public Entity(
		 * Point where, String represents, String shade,
		 * String called, boolean resistant,
		 * Fighter attackNature:nullable, Ai goalNature:nullable )
		 * position = where;
		 * symbol = represents;
		 * color = shade;
		 * name = called;
		 * blocks = resistant; */
	}

}
