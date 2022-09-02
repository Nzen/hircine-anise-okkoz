
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.game;


import java.time.LocalDateTime;


public class AtcEventLandingAborted extends AtcEvent
{

	private final String airplaneId;
	private final LocalDateTime createdTime;


	public AtcEventLandingAborted(
			String airplaneId,
			LocalDateTime createdTime
	) {
		super();
		if ( airplaneId == null )
			throw new NullPointerException( "airplaneId must not be null" );
		else if ( createdTime == null )
			throw new NullPointerException( "createdTime must not be null" );
		else if ( airplaneId.isEmpty() )
			throw new IllegalArgumentException( "airplaneId must not be blank" );
		this.airplaneId = airplaneId;
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


	@Override
	public AtcEventType getType(
	) {
		return AtcEventType.LANDING_ABORTED;
	}


	@Override
	public String toString(
	) {
		return "LandingAborted [airplaneId=" + airplaneId + "]";
	}

}
