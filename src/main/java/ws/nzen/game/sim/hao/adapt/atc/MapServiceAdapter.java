
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.adapt.atc;


import atc.v1.MapOuterClass.Node;
import atc.v1.MapOuterClass.NodeToPointRequest;
import atc.v1.MapOuterClass.NodeToPointResponse;
import atc.v1.MapOuterClass.Point;

import java.util.ConcurrentModificationException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ws.nzen.game.sim.hao.game.AtcMapPoint;
import ws.nzen.game.sim.hao.game.AtcNodePoint;
import ws.nzen.game.sim.hao.game.AtcRoutingNode;
import ws.nzen.game.sim.hao.service.HaoConstants;
import ws.nzen.game.sim.hao.uses.atc.KnowsNodes;


/**  */
public class MapServiceAdapter implements KnowsNodes
{

	private static final Logger log = LoggerFactory.getLogger( MapServiceAdapter.class );

	private boolean quit = false;
	private int millisecondsToSleep = HaoConstants.queueDelayMilliseconds;
	private final MapMapper mapMapper;
	private final MapServiceEndpoint mapServiceEndpoint;
	private final NodeMapper nodeMapper;
	private final PointMapper pointMapper;
	private final Queue<NodeToPointRequest> nodeToPointRequests;
	private final Queue<NodeToPointResponse> nodeToPointResponses;
	private final Queue<AtcRoutingNode> nodeLocationRequests;
	private final Queue<AtcNodePoint> nodeLocationResponses;
	private final Queue<AtcRoutingNode> orderedNodeRequests;
	private final Thread runsMapServiceEndpoint;


	public MapServiceAdapter(
			MapMapper mapMapper,
			MapServiceEndpoint mapServiceEndpoint,
			NodeMapper nodeMapper,
			PointMapper pointMapper,
			Queue<NodeToPointRequest> nodeToPointRequests,
			Queue<NodeToPointResponse> nodeToPointResponses,
			Queue<AtcRoutingNode> nodeLocationRequests,
			Queue<AtcNodePoint> nodeLocationResponses
	) {
		if ( mapMapper == null )
			throw new NullPointerException( "mapMapper must not be null" );
		else if ( mapServiceEndpoint == null )
			throw new NullPointerException( "mapServiceEndpoint must not be null" );
		else if ( nodeMapper == null )
			throw new NullPointerException( "nodeMapper must not be null" );
		else if ( pointMapper == null )
			throw new NullPointerException( "pointMapper must not be null" );
		else if ( nodeToPointRequests == null )
			throw new NullPointerException( "nodeToPointRequests must not be null" );
		else if ( nodeToPointResponses == null )
			throw new NullPointerException( "nodeToPointResponses must not be null" );
		else if ( nodeLocationRequests == null )
			throw new NullPointerException( "nodeLocationRequests must not be null" );
		else if ( nodeLocationResponses == null )
			throw new NullPointerException( "nodeLocationResponses must not be null" );
		this.mapMapper = mapMapper;
		this.mapServiceEndpoint = mapServiceEndpoint;
		this.nodeMapper = nodeMapper;
		this.pointMapper = pointMapper;
		this.nodeToPointRequests = nodeToPointRequests;
		this.nodeToPointResponses = nodeToPointResponses;
		this.nodeLocationRequests = nodeLocationRequests;
		this.nodeLocationResponses = nodeLocationResponses;
		orderedNodeRequests = new ConcurrentLinkedQueue<>();
		runsMapServiceEndpoint = new Thread( mapServiceEndpoint );
		runsMapServiceEndpoint.start();
	}


	@Override
	public void quit(
	) {
		quit = true;
		runsMapServiceEndpoint.interrupt();
	}


	@Override
	/** Check for requests, copy these to outgoing queue. */
	public void run(
	) {
		try
		{
			while ( true )
			{
				while ( ! nodeLocationRequests.isEmpty() )
				{
					AtcRoutingNode unmappedNode = nodeLocationRequests.poll();
					Node atcNode = nodeMapper.asAtcNode( unmappedNode );
					orderedNodeRequests.offer( unmappedNode );
					mapServiceEndpoint.requestNodePoint( atcNode );
				}

				while ( ! nodeToPointResponses.isEmpty() )
				{
					NodeToPointResponse response = nodeToPointResponses.poll();
					Point atcPoint = response.getPoint();
					AtcMapPoint haoPoint = pointMapper.asHaoPoint( atcPoint );
					if ( orderedNodeRequests.isEmpty() )
						throw new ConcurrentModificationException(
								"given an extra point, "+ haoPoint );
					AtcRoutingNode unmappedNode = orderedNodeRequests.poll();
					AtcNodePoint nodePoint = new AtcNodePoint( haoPoint, unmappedNode );
					nodeLocationResponses.offer( nodePoint );
				}

				Thread.sleep( millisecondsToSleep );
				if ( quit )
					return;
			}
		}
		catch ( InterruptedException ie )
		{
			return;
		}
	}

}


















