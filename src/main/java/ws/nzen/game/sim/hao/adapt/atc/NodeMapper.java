
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


	public List<Node> asAtcFlightPlan(
			AtcFlightPlan haoFlightPlan
	) {
		List<AtcRoutingNode> haoNodesOfFlightPlan = haoFlightPlan.getRoute();
		List<Node> atcFlightPlanNodes = new ArrayList<>( haoNodesOfFlightPlan.size() );
		for ( AtcRoutingNode haoNode : haoNodesOfFlightPlan )
			atcFlightPlanNodes.add( asAtcNode( haoNode ) );
		return atcFlightPlanNodes;
	}


	public Node asAtcNode(
			AtcRoutingNode haoNode
	) {
		Node nativeNode = Node.newBuilder()
				.setLatitude( haoNode.getLatitude() )
				.setLongitude( haoNode.getLongitude() ).build();
		return nativeNode;
	}


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
