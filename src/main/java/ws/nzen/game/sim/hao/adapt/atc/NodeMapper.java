
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.adapt.atc;


import atc.v1.MapOuterClass.Node;

import java.util.ArrayList;
import java.util.List;

import ws.nzen.game.sim.hao.game.AtcFlightPlan;
import ws.nzen.game.sim.hao.game.AtcRoutingNode;


/**  */
public class NodeMapper
{

	public AtcFlightPlan asHaoFlightPlan(
			List<Node> nativeNodes
	) {
		List<AtcRoutingNode> haoNodes = new ArrayList<>( nativeNodes.size() );
		for ( Node atcNode : nativeNodes )
			haoNodes.add( asHaoRoutingNode( atcNode ) );
		return new AtcFlightPlan( haoNodes );
	}


	public AtcRoutingNode asHaoRoutingNode(
			Node nativeNode
	) {
		return new AtcRoutingNode(
				nativeNode.getLatitude(),
				nativeNode.getLongitude(),
				nativeNode.getRestricted()
		);
	}

}
