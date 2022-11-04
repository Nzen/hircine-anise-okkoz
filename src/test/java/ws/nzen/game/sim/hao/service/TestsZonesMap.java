/* see ../../../../../LICENSE for release details */
package ws.nzen.game.sim.hao.service;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Rectangle;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.junit.jupiter.api.Test;

import ws.nzen.game.sim.hao.game.AtcMapPoint;
import ws.nzen.game.sim.hao.game.AtcNodePoint;
import ws.nzen.game.sim.hao.game.AtcRoutingNode;

/**

*/
class TestsZonesMap
{

	/**
	 * Test method for {@link ws.nzen.game.sim.hao.service.ZonesMap#run()}.
	 */
	@Test
	void testRun()
	{
		Factory factory = new Factory();
		Queue<Collection<AtcRoutingNode>> nodesToZone = factory.queueNodesToZone();
		Queue<Map<AtcRoutingNode, Rectangle>> nodeZones = factory.queueZonedNodes();
		Queue<AtcRoutingNode> nodeToPointRequests = factory.queueNodeLocationRequests();
		Queue<AtcNodePoint> nodeLocationResponses = factory.queueNodeLocationResponses();
		ZonesMap zoner = new ZonesMap(
				nodesToZone,
				nodeZones,
				nodeToPointRequests,
				nodeLocationResponses );
		// ¶ add nodes to zone
		AtcRoutingNode nodeBottomLeft = new AtcRoutingNode( -1, 0, false );
		AtcRoutingNode nodeBottomRight = new AtcRoutingNode( 0, 0, false );
		AtcRoutingNode nodeTopLeft = new AtcRoutingNode( -1, 1, false );
		AtcRoutingNode nodeTopRight = new AtcRoutingNode( 0, 1, false );
		AtcMapPoint pointCenterOfBottomLeft = new AtcMapPoint( -3, -18 );
		AtcMapPoint pointCenterOfBottomRight = new AtcMapPoint( 17, -18 );
		AtcMapPoint pointCenterOfTopLeft = new AtcMapPoint( -3, 22 );
		AtcMapPoint pointCenterOfTopRight = new AtcMapPoint( 17, 22 );
		AtcNodePoint nodePointBottomLeft = new AtcNodePoint( pointCenterOfBottomLeft, nodeBottomLeft );
		// AtcNodePoint nodePointBottomRight = new AtcNodePoint( pointBottomRight, nodeBottomRight );
		AtcNodePoint nodePointTopLeft = new AtcNodePoint( pointCenterOfTopLeft, nodeTopLeft );
		AtcNodePoint nodePointTopRight = new AtcNodePoint( pointCenterOfTopRight, nodeTopRight );

		Collection<AtcRoutingNode> unzonedNodeBundle = new LinkedList<>();
		unzonedNodeBundle.add( nodeBottomLeft );
		unzonedNodeBundle.add( nodeBottomRight );
		unzonedNodeBundle.add( nodeTopLeft );
		unzonedNodeBundle.add( nodeTopRight );
		nodesToZone.offer( unzonedNodeBundle );
		
		// ¶ add points for the nodes
		nodeLocationResponses.offer( nodePointBottomLeft );
		nodeLocationResponses.offer( nodePointTopLeft );
		nodeLocationResponses.offer( nodePointTopRight );

		zoner.quit(); // ¶ ensure that run terminates
		zoner.run();

		// ¶ ensure that other node has the expected point
		Map<AtcRoutingNode, Rectangle> zonesByNode = nodeZones.remove();

		assertNotNull(
				zonesByNode.containsKey( nodeBottomRight ),
				"did not create a rectangle for "+ nodeBottomRight );
		Rectangle zoneBottomRight = zonesByNode.get( nodeBottomRight );
		assertTrue( zoneBottomRight.contains(
				pointCenterOfBottomRight.getXx(), pointCenterOfBottomRight.getYy() ),
				"zone does not contain the expected point" );

		assertNotNull(
				zonesByNode.containsKey( nodeBottomLeft ),
				"did not create a rectangle for "+ nodeBottomLeft );
		Rectangle zoneBottomLeft = zonesByNode.get( nodeBottomLeft );
		assertTrue( zoneBottomLeft.contains(
				pointCenterOfBottomLeft.getXx(), pointCenterOfBottomLeft.getYy() ),
				"zone does not contain the expected point" );

		assertNotNull(
				zonesByNode.containsKey( nodeTopLeft ),
				"did not create a rectangle for "+ nodeTopLeft );
		Rectangle zoneTopLeft = zonesByNode.get( nodeTopLeft );
		assertTrue( zoneTopLeft.contains(
				pointCenterOfTopLeft.getXx(), pointCenterOfTopLeft.getYy() ),
				"zone does not contain the expected point" );

		assertNotNull(
				zonesByNode.containsKey( nodeTopRight ),
				"did not create a rectangle for "+ nodeTopRight );
		Rectangle zoneTopRight = zonesByNode.get( nodeTopRight );
		assertTrue( zoneTopRight.contains(
				pointCenterOfTopRight.getXx(), pointCenterOfTopRight.getYy() ),
				"zone does not contain the expected point" );		
	}

}


















