
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.game;


import java.time.LocalDateTime;


public class AtcEventGameStopped extends AtcEvent
{

	private final int sessionScore;
	private final LocalDateTime createdTime;


	public AtcEventGameStopped(
			int sessionScore,
			LocalDateTime createdTime
	) {
		super();
		if ( createdTime == null )
			throw new NullPointerException( "createdTime must not be null" );
		this.sessionScore = sessionScore;
		this.createdTime = createdTime;
	}


	public LocalDateTime getCreatedTime(
	) {
		return createdTime;
	}


	@Override
	public AtcEventType getType(
	) {
		return AtcEventType.GAME_STOPPED;
	}


	public int getSessionScore(
	) {
		return sessionScore;
	}


	@Override
	public String toString(
	) {
		return "GameStopped [sessionScore=" + sessionScore + "]";
	}

}
