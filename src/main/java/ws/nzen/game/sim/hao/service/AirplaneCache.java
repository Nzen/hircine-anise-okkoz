
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.service;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

import ws.nzen.game.sim.hao.game.AtcAirplane;
import ws.nzen.game.sim.hao.game.HaoEvent;
import ws.nzen.game.sim.hao.uses.atc.KnowsAirplanes;
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


}


















