
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.game;


/**

*/
public class AtcNodePoint
{

	private final AtcMapPoint point;
	private final AtcRoutingNode routingNode;


	public AtcNodePoint(
			AtcMapPoint point,
			 AtcRoutingNode routingNode
	) {
		if ( point == null )
			throw new NullPointerException( "point must not be null" );
		else if ( routingNode == null )
			throw new NullPointerException( "routingNode must not be null" );
		this.point = point;
		this.routingNode = routingNode;
	}



	
	public AtcMapPoint getPoint(
	) {
		return point;
	}



	
	public AtcRoutingNode getRoutingNode(
	) {
		return routingNode;
	}



	@Override
	public String toString(
	) {
		return "NodePoint " + routingNode + " " + point + "]";
	}

}
