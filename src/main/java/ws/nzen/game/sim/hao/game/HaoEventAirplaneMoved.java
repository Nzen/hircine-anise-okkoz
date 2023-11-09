
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.game;


/**  */
public class HaoEventAirplaneMoved
{

	private AtcNodePoint currentPositionAndNode;
	private String airplaneId;


	/** @param currentPositionAndNode
	/** @param airplaneId */
	public HaoEventAirplaneMoved(
			AtcNodePoint currentPositionAndNode, String airplaneId
	) {
		if ( currentPositionAndNode == null )
			throw new NullPointerException( "currentPositionAndNode must not be null" );
		else if ( airplaneId == null || airplaneId.isEmpty() )
			throw new NullPointerException( "airplaneId must not be null" );
		this.currentPositionAndNode = currentPositionAndNode;
		this.airplaneId = airplaneId;
	}


	
	public String getAirplaneId(
	) {
		return airplaneId;
	}


	
	public AtcNodePoint getCurrentPositionAndNode(
	) {
		return currentPositionAndNode;
	}

}


















