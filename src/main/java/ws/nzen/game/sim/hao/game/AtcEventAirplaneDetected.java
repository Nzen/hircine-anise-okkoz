
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.game;


import java.time.LocalDateTime;


public class AtcEventAirplaneDetected extends AtcEvent
{

	private final AtcAirplane airplane;
	private final LocalDateTime createdTime;


	public AtcEventAirplaneDetected(
			AtcAirplane airplane,
			LocalDateTime createdTime
	) {
		super();
		if ( airplane == null )
			throw new NullPointerException( "airplane must not be null" );
		else if ( createdTime == null )
			throw new NullPointerException( "createdTime must not be null" );
		this.airplane = airplane;
		this.createdTime = createdTime;
	}


	public AtcAirplane getAirplane(
	) {
		return airplane;
	}


	public LocalDateTime getCreatedTime(
	) {
		return createdTime;
	}


	@Override
	public AtcEventType getType(
	) {
		return AtcEventType.AIRPLANE_DETECTED;
	}


	@Override
	public String toString(
	) {
		return "AirplaneDetected [airplane=" + airplane + "]";
	}

}
