
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.game;


import java.time.LocalDateTime;


public class AtcEventGameStarted extends AtcEvent
{

	private final AtcMap toPlayWith;
	private final LocalDateTime createdTime;


	public AtcEventGameStarted(
			AtcMap toPlayWith,
			LocalDateTime createdTime
	) {
		super();
		if ( toPlayWith == null )
			throw new NullPointerException( "toPlayWith must not be null" );
		else if ( createdTime == null )
			throw new NullPointerException( "createdTime must not be null" );
		this.toPlayWith = toPlayWith;
		this.createdTime = createdTime;
	}


	public LocalDateTime getCreatedTime(
	) {
		return createdTime;
	}


	public AtcMap getMap(
	) {
		return toPlayWith;
	}


	@Override
	public AtcEventType getType(
	) {
		return AtcEventType.GAME_STARTED;
	}


	@Override
	public String toString(
	) {
		return "GameStarted with " + toPlayWith;
	}

}
