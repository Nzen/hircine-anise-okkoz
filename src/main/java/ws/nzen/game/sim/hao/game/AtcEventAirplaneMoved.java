
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.game;


import java.time.LocalDateTime;


public class AtcEventAirplaneMoved extends AtcEvent
{

	private final String airplaneId;
	private final LocalDateTime createdTime;
	private final AtcMapPoint position;


	public AtcEventAirplaneMoved(
			String airplaneId,
			AtcMapPoint position,
			LocalDateTime createdTime
	) {
		super();
		if ( airplaneId == null )
			throw new NullPointerException( "airplaneId must not be null" );
		else if ( createdTime == null )
			throw new NullPointerException( "createdTime must not be null" );
		else if ( position == null )
			throw new NullPointerException( "position must not be null" );
		else if ( airplaneId.isEmpty() )
			throw new IllegalArgumentException( "airplaneId must not be blank" );
		this.airplaneId = airplaneId;
		this.position = position;
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


	public AtcMapPoint getPosition(
	) {
		return position;
	}


	@Override
	public AtcEventType getType(
	) {
		return AtcEventType.AIRPLANE_MOVED;
	}


	@Override
	public String toString(
	) {
		return "AirplaneMoved [airplaneId=" + airplaneId + " " + position + "]";
	}

}
