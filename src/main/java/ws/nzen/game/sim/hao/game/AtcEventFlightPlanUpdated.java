
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.game;


import java.time.LocalDateTime;
import java.util.List;


public class AtcEventFlightPlanUpdated extends AtcEvent
{

	private final String airplaneId;
	private final AtcFlightPlan flightPlan;
	private final LocalDateTime createdTime;


	public AtcEventFlightPlanUpdated(
			String airplaneId,
			AtcFlightPlan flightPlan,
			LocalDateTime createdTime
	) {
		super();
		if ( airplaneId == null )
			throw new NullPointerException( "airplaneId must not be null" );
		else if ( flightPlan == null )
			throw new NullPointerException( "flightPlan must not be null" );
		else if ( createdTime == null )
			throw new NullPointerException( "createdTime must not be null" );
		else if ( airplaneId.isEmpty() )
			throw new IllegalArgumentException( "airplaneId must not be blank" );
		this.airplaneId = airplaneId;
		this.flightPlan = flightPlan;
		this.createdTime = createdTime;
	}


	public String getAirplaneId(
	) {
		return airplaneId;
	}


	public LocalDateTime getCreatedTime(
	) {
		return createdTime;
	}


	public AtcFlightPlan getFlightPlan(
	) {
		return flightPlan;
	}


	@Override
	public AtcEventType getType(
	) {
		return AtcEventType.FLIGHT_PLAN_UPDATED;
	}


	@Override
	public String toString(
	) {
		return "FlightPlanUpdated [airplaneId=" + airplaneId
				+ " " + flightPlan + "]";
	}

}
