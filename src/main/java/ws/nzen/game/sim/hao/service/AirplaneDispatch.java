
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.service;


import java.util.Collection;
import java.util.Optional;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ws.nzen.game.sim.hao.game.AtcAirplane;
import ws.nzen.game.sim.hao.game.AtcEventAirplaneDetected;
import ws.nzen.game.sim.hao.game.AtcEventFlightPlanUpdated;
import ws.nzen.game.sim.hao.uses.atc.KnowsAirplanesRunnably;


/**

*/
public class AirplaneDispatch implements KnowsAirplanesRunnably
{

	private static final Logger log = LoggerFactory
			.getLogger( AirplaneDispatch.class );
	private boolean quit = false;
	private int millisecondsToSleep = 200;
	private final AirplaneCache airplaneCache; // IMPROVE use Knows and Saves Airplanes
	private final Queue<AtcEventAirplaneDetected> atcEventsAirplaneDetected;
	private final Queue<AtcEventFlightPlanUpdated> aeFlightChanged;


	public AirplaneDispatch(
			AirplaneCache airplaneCache,
			Queue<AtcEventAirplaneDetected> atcEventsAirplaneDetected,
			Queue<AtcEventFlightPlanUpdated> aeFlightChanged
	) {
		if ( airplaneCache == null )
			throw new NullPointerException( "airplaneCache must not be null" );
		else if ( atcEventsAirplaneDetected == null )
			throw new NullPointerException( "atcEventsAirplaneDetected must not be null" );
		else if ( aeFlightChanged == null )
			throw new NullPointerException( "aeFlightChanged must not be null" );
		this.atcEventsAirplaneDetected = atcEventsAirplaneDetected;
		this.airplaneCache = airplaneCache;
		this.aeFlightChanged = aeFlightChanged;
	}


	public Collection<AtcAirplane> findAll(
	) {
		return airplaneCache.findAll();
	}


	public Optional<AtcAirplane> findById(
			String airplaneId
	) {
		return airplaneCache.findById( airplaneId );
	}


	@Override
	public void quit(
	) {
		quit = true;
	}


	@Override
	/** Check queue for requests to print */
	public void run(
	) {
		try
		{
			while ( true )
			{
				while ( ! atcEventsAirplaneDetected.isEmpty() )
				{
					AtcEventAirplaneDetected airplaneEvent = atcEventsAirplaneDetected.poll();
					airplaneCache.save( airplaneEvent.getAirplane() );
				}

				while ( ! aeFlightChanged.isEmpty() )
				{
					AtcEventFlightPlanUpdated airplaneEvent = aeFlightChanged.poll();
					airplaneCache.updateFlightPlan(
							airplaneEvent.getAirplaneId(),
							airplaneEvent.getFlightPlan() );
				}

				Thread.sleep( millisecondsToSleep );
				if ( quit )
					return;
			}
		}
		catch ( InterruptedException ie )
		{
			log.error( ie.toString() );
		}
	}



}


















