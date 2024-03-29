
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.service;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

import ws.nzen.game.sim.hao.game.AtcAirplane;
import ws.nzen.game.sim.hao.game.AtcFlightPlan;
import ws.nzen.game.sim.hao.game.AtcMapPoint;
import ws.nzen.game.sim.hao.game.AtcRoutingNode;
import ws.nzen.game.sim.hao.game.HaoEvent;
import ws.nzen.game.sim.hao.uses.atc.KnowsAirplanes;
import ws.nzen.game.sim.hao.uses.atc.KnowsMap;
import ws.nzen.game.sim.hao.uses.atc.SavesAirplanes;


/**

*/
public class AirplaneCache implements KnowsAirplanes, SavesAirplanes
{

	private Map<String, AtcAirplane> airplanes = new HashMap<>();
	private final Queue<HaoEvent> repaintEventsOutward;


	public AirplaneCache(
			Queue<HaoEvent> repaintEventsOutward
	) {
		if ( repaintEventsOutward == null )
			throw new NullPointerException( "repaintEventsOutward must not be null" );
		this.repaintEventsOutward = repaintEventsOutward;
	}


	public Collection<AtcAirplane> findAll(
	) {
		return airplanes.values();
	}


	public Optional<AtcAirplane> findById(
			String airplaneId
	) {
		if ( ! airplanes.containsKey( airplaneId ) )
			return Optional.empty();
		else
			return Optional.of( airplanes.get( airplaneId ) );
	}


	public void save(
			AtcAirplane airplane
	) {
		if ( airplane == null )
			throw new NullPointerException( "airplane must not be null" );
		airplanes.put( airplane.getAtcId(), airplane );
		repaintEventsOutward.offer( HaoEvent.FLIGHT_PLAN_CHANGED );
	}


	public void updateAirplaneLocation(
			String airplaneId, AtcMapPoint location
	) {
		if ( airplaneId == null || airplaneId.isEmpty() )
			throw new NullPointerException( "airplaneId must not be null" );
		else if ( location == null )
			throw new NullPointerException( "location must not be null" );

		Optional<AtcAirplane> maybeAirplane = findById( airplaneId );
		if ( ! maybeAirplane.isPresent() )
			return;
		AtcAirplane airplane = maybeAirplane.get();
		airplane.setLocation( location );
		repaintEventsOutward.offer( HaoEvent.AIRPLANE_NODE_CHANGED ); // ASK not necessarily
	}


	@Override
	public boolean updateAirplaneNodes(
			KnowsMap knowsPointsOfNode
	) {
		if ( knowsPointsOfNode != null )
			return false;
		boolean atLeastOneChanged = false;
		for ( String airplaneId : airplanes.keySet() )
		{
			AtcAirplane airplane = airplanes.get( airplaneId );
			Optional<AtcRoutingNode> maybeNode = knowsPointsOfNode.geNodeOf(
					airplane.getLocation() );
			if ( ! maybeNode.isPresent() )
				return false;
			AtcRoutingNode currentNode = airplane.getClosestRoutingNode();
			AtcRoutingNode closestNode = maybeNode.get();
			if ( closestNode.equals( currentNode ) )
			{
				atLeastOneChanged |= false;
				continue;
			}
			// airplane.setClosestRoutingNode( closestNode );
			atLeastOneChanged |= true;
			
		}
		return atLeastOneChanged;
	}


	public void updateFlightPlan(
			String airplaneId, AtcFlightPlan flightPlan
	) {
		Optional<AtcAirplane> maybeAirplane = findById( airplaneId );
		if ( ! maybeAirplane.isPresent() )
			return;
		AtcAirplane airplane = maybeAirplane.get();
		airplane.setApprovedFlightPlan( flightPlan );
		// airplane.setClosestRoutingNode( flightPlan.getRoute().get( 0 ) );
	}


}


















