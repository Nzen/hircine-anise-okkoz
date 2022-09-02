
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.adapt.atc;


import java.time.Clock;
import java.time.LocalDateTime;

import atc.v1.Event.*;
import atc.v1.Event.StreamResponse;
import ws.nzen.game.sim.hao.game.*;


/**

*/
public class EventMapper
{

	private final AirplaneMapper mapsAirplans;
	private final Clock clock = Clock.systemDefaultZone();
	private final MapMapper mapsMaps;
	private final NodeMapper mapsNodes;
	private final PointMapper mapsPoints;


	public EventMapper(
			AirplaneMapper mapsAirplans,
			MapMapper mapsMaps,
			NodeMapper mapsNodes,
			PointMapper mapsPoints
	) {
		super();
		if ( mapsAirplans == null )
			throw new NullPointerException( "mapsAirplans must not be null" );
		else if ( mapsMaps == null )
			throw new NullPointerException( "mapsMaps must not be null" );
		else if ( mapsNodes == null )
			throw new NullPointerException( "mapsNodes must not be null" );
		else if ( mapsPoints == null )
			throw new NullPointerException( "mapsPoints must not be null" );
		this.mapsAirplans = mapsAirplans;
		this.mapsMaps = mapsMaps;
		this.mapsNodes = mapsNodes;
		this.mapsPoints = mapsPoints;
	}


	public AtcEvent asHaoEvent(
			StreamResponse response
	) {
		LocalDateTime now = LocalDateTime.now( clock );
		if ( response.hasAirplaneCollided() )
		{
			AirplaneCollided nativeStyle = response.getAirplaneCollided();
			return new AtcEventAirplaneCollided(
					nativeStyle.getId1(), nativeStyle.getId2(), now
			);
		}
		else if ( response.hasAirplaneDetected() )
		{
			AirplaneDetected nativeStyle = response.getAirplaneDetected();
			return new AtcEventAirplaneDetected(
					mapsAirplans.asHaoAirplane( nativeStyle.getAirplane() ), now
			);
		}
		else if ( response.hasAirplaneLanded() )
		{
			AirplaneLanded nativeStyle = response.getAirplaneLanded();
			return new AtcEventAirplaneLanded( nativeStyle.getId(), now );
		}
		else if ( response.hasAirplaneMoved() )
		{
			AirplaneMoved nativeStyle = response.getAirplaneMoved();
			return new AtcEventAirplaneMoved(
					nativeStyle.getId(),
					mapsPoints.asHaoPoint( nativeStyle.getPoint() ), now
			);
		}
		else if ( response.hasFlightPlanUpdated() )
		{
			FlightPlanUpdated nativeStyle = response.getFlightPlanUpdated();
			return new AtcEventFlightPlanUpdated(
					nativeStyle.getId(),
					mapsNodes.asHaoFlightPlan( nativeStyle.getFlightPlanList() ), now
			);
		}
		else if ( response.hasGameStarted() )
		{
			GameStarted nativeStyle = response.getGameStarted();
			return new AtcEventGameStarted( mapsMaps.asHaoMap( nativeStyle.getMap() ), now );
		}
		else if ( response.hasGameStopped() )
		{
			GameStopped nativeStyle = response.getGameStopped();
			return new AtcEventGameStopped( nativeStyle.getScore(), now );
		}
		else if ( response.hasLandingAborted() )
		{
			LandingAborted nativeStyle = response.getLandingAborted();
			return new AtcEventLandingAborted( nativeStyle.getId(), now );
		}
		else
			throw new IllegalArgumentException( "unrecognized stream response" );
	}

}
