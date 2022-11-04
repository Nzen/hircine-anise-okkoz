
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.service;


import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ws.nzen.game.sim.hao.game.AtcMapPoint;
import ws.nzen.game.sim.hao.game.AtcNodePoint;
import ws.nzen.game.sim.hao.game.AtcRoutingNode;
import ws.nzen.game.sim.hao.uses.atc.LocatesNodes;


/**
Fetches the AtcMapPoints of AtcRoutingNodes and calculates their zones.
*/
public class ZonesMap implements LocatesNodes
{

	private class RightTriangle
	{
		AtcNodePoint origin = null;
		AtcNodePoint downward = null;
		AtcNodePoint rightward = null;
	}

	private static final Logger log = LoggerFactory.getLogger( ZonesMap.class );

	private boolean quit = false;
	private Collection<AtcRoutingNode> nodes;
	private int millisecondsToSleep = HaoConstants.queueDelayMilliseconds;
	private final AtcMapPoint fakePoint = new AtcMapPoint( Integer.MIN_VALUE, Integer.MIN_VALUE );
	private final Queue<Collection<AtcRoutingNode>> nodesToZone;
	private final Queue<Map<AtcRoutingNode, Rectangle>> nodeZones;
	private final Queue<AtcRoutingNode> nodeToPointRequests;
	private final Queue<AtcNodePoint> nodeLocationResponses;
	private RightTriangle exemplarNodes = new RightTriangle();


	public ZonesMap(
			Queue<Collection<AtcRoutingNode>> nodesToZone,
			Queue<Map<AtcRoutingNode, Rectangle>> nodeZones,
			Queue<AtcRoutingNode> nodeToPointRequests,
			Queue<AtcNodePoint> nodeLocationResponses
	) {
		if ( nodesToZone == null )
			throw new NullPointerException( "nodesToZone must not be null" );
		else if ( nodeZones == null )
			throw new NullPointerException( "nodeZones must not be null" );
		else if ( nodeLocationResponses == null )
			throw new NullPointerException( "nodeLocationResponses must not be null" );
		else if ( nodeToPointRequests == null )
			throw new NullPointerException( "nodeToPointRequests must not be null" );
		this.nodesToZone = nodesToZone;
		this.nodeZones = nodeZones;
		this.nodeToPointRequests = nodeToPointRequests;
		this.nodeLocationResponses = nodeLocationResponses;
		nodes = new LinkedList<>();
	}


	@Override
	public void quit(
	) {
		quit = true;
	}


	@Override
	/** Check queue for requests to print */
	public void run(
	) {
		try
		{
			while ( true )
			{
				while ( ! nodesToZone.isEmpty() )
				{
					nodes = nodesToZone.poll();
					chooseExemplarNodes();
					nodeToPointRequests.offer( exemplarNodes.origin.getRoutingNode() );
					nodeToPointRequests.offer( exemplarNodes.downward.getRoutingNode() );
					nodeToPointRequests.offer( exemplarNodes.rightward.getRoutingNode() );
				}

				while ( ! nodeLocationResponses.isEmpty() )
				{
					AtcNodePoint nodePoint = nodeLocationResponses.poll();
					AtcRoutingNode correspondingNode = nodePoint.getRoutingNode();
					if ( correspondingNode.equals( exemplarNodes.origin.getRoutingNode() )  )
						exemplarNodes.origin = nodePoint;
					else if ( correspondingNode.equals( exemplarNodes.downward.getRoutingNode() )  )
						exemplarNodes.downward = nodePoint;
					else if ( correspondingNode.equals( exemplarNodes.rightward.getRoutingNode() )  )
						exemplarNodes.rightward = nodePoint;

					if ( exemplarNodes.origin.getPoint().getXx() != fakePoint.getXx()
							&& exemplarNodes.downward.getPoint().getXx() != fakePoint.getXx()
							&& exemplarNodes.rightward.getPoint().getXx() != fakePoint.getXx() )
					{
						nodeZones.offer( estimateZonesFromTriangle() );
					}
				}

				if ( quit )
					return;
				Thread.sleep( millisecondsToSleep );
			}
		}
		catch ( InterruptedException ie )
		{
			log.error( ie.toString() );
		}
	}


	private void chooseExemplarNodes(
	) {
		AtcRoutingNode topLeft = new AtcRoutingNode(
				Integer.MAX_VALUE, Integer.MIN_VALUE, true );
		for ( AtcRoutingNode node : nodes )
			if ( ( node.getLatitude() <= topLeft.getLatitude()
						&& node.getLongitude() > topLeft.getLongitude() )
					|| ( node.getLatitude() < topLeft.getLatitude()
						&& node.getLongitude() >= topLeft.getLongitude() ) )
				topLeft = node;
		AtcRoutingNode nextRight = new AtcRoutingNode(
				topLeft.getLatitude() +1, topLeft.getLongitude(), false );
		AtcRoutingNode nextDown = new AtcRoutingNode(
				topLeft.getLatitude(), topLeft.getLongitude() -1, false );
		exemplarNodes.origin = new AtcNodePoint( fakePoint, topLeft );
		exemplarNodes.downward = new AtcNodePoint( fakePoint, nextDown );
		exemplarNodes.rightward = new AtcNodePoint( fakePoint, nextRight );
	}


	private Map<AtcRoutingNode, Rectangle> estimateZonesFromTriangle(
	) {
		int pointHeightBetweenNodes = exemplarNodes.origin.getPoint().getYy()
				- exemplarNodes.downward.getPoint().getYy();
		int pointWidthBetweenNodes = exemplarNodes.rightward.getPoint().getXx()
				- exemplarNodes.origin.getPoint().getXx();
		int halfOfHeight = (int)Math.round( pointHeightBetweenNodes / 2D );
		int halfOfWidth = (int)Math.round( pointWidthBetweenNodes / 2D );
		AtcRoutingNode originNode = exemplarNodes.origin.getRoutingNode();
		Rectangle originZone = new Rectangle(
				exemplarNodes.origin.getPoint().getXx() - halfOfWidth,
				exemplarNodes.origin.getPoint().getYy() + halfOfHeight,
				pointWidthBetweenNodes,
				pointHeightBetweenNodes );
		Point pixelTopLeft = originZone.getLocation();
		Point pixelBottomLeft = new Point(
				pixelTopLeft.x,
				pixelTopLeft.y - pointHeightBetweenNodes );
		Map<AtcRoutingNode, Rectangle> zones = new HashMap<>( nodes.size() );
		for ( AtcRoutingNode node : nodes )
		{
			Rectangle nodeZone = new Rectangle(
					pointWidthBetweenNodes, pointHeightBetweenNodes );
			nodeZone.setLocation(
					( node.getLatitude() - originNode.getLatitude() )
							* pointWidthBetweenNodes + pixelBottomLeft.x,
					( node.getLongitude() - originNode.getLongitude() )
							* pointHeightBetweenNodes + pixelBottomLeft.y );
			zones.put( node, nodeZone );
		}
		return zones;
	}

}










































































