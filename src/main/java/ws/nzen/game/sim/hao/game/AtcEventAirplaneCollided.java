
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.game;


import java.time.LocalDateTime;


/**

*/
public class AtcEventAirplaneCollided extends AtcEvent
{

	private final String airplaneId1;
	private final String airplaneId2;
	private final LocalDateTime createdTime;


	/** @param airplaneId1
	 * @param airplaneId2
	 */
	public AtcEventAirplaneCollided(
			String airplaneId1,
			String airplaneId2,
			LocalDateTime createdTime
	) {
		super();
		if ( airplaneId1 == null )
			throw new NullPointerException( "airplaneId1 must not be null" );
		else if ( airplaneId2 == null )
			throw new NullPointerException( "airplaneId2 must not be null" );
		else if ( createdTime == null )
			throw new NullPointerException( "createdTime must not be null" );
		else if ( airplaneId1.isEmpty() )
			throw new IllegalArgumentException( "airplaneId1 must not be blank" );
		else if ( airplaneId2.isEmpty() )
			throw new IllegalArgumentException( "airplaneId2 must not be blank" );
		this.airplaneId1 = airplaneId1;
		this.airplaneId2 = airplaneId2;
		this.createdTime = createdTime;
	}


	public String getAirplaneId1(
	) {
		return airplaneId1;
	}


	public String getAirplaneId2(
	) {
		return airplaneId2;
	}


	public LocalDateTime getCreatedTime(
	) {
		return createdTime;
	}


	@Override
	public AtcEventType getType(
	) {
		return AtcEventType.AIRPLANE_COLLIDED;
	}


	@Override
	public String toString(
	) {
		return "AirplaneCollided [airplaneId1=" + airplaneId1
				+ ", airplaneId2=" + airplaneId2 + "]";
	}

}
