
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.service;


import java.awt.Rectangle;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ws.nzen.game.sim.hao.game.AtcEventGameStarted;
import ws.nzen.game.sim.hao.game.AtcMap;
import ws.nzen.game.sim.hao.game.AtcMapPoint;
import ws.nzen.game.sim.hao.game.AtcRoutingNode;
import ws.nzen.game.sim.hao.game.AtcTeamTag;
import ws.nzen.game.sim.hao.uses.atc.KnowsMapRunnably;


/**

*/
public class MapDispatch implements KnowsMapRunnably
{

	private static final Logger log = LoggerFactory
			.getLogger( MapDispatch.class );
	private boolean quit = false;
	private int millisecondsToSleep = HaoConstants.queueDelayMilliseconds;
	private final MapCache map;
	private final Queue<AtcEventGameStarted> atcEventGameStarted;
	private final Queue<Collection<AtcRoutingNode>> nodesToZone;
	private final Queue<Map<AtcRoutingNode, Rectangle>> nodeZones;


	public MapDispatch(
			MapCache mapCache,
			Queue<AtcEventGameStarted> atcEventGameStarted,
			Queue<Collection<AtcRoutingNode>> nodesToZone,
			Queue<Map<AtcRoutingNode, Rectangle>> nodeZones
	) {
		if ( mapCache == null )
			throw new NullPointerException( "mapCache must not be null" );
		else if ( atcEventGameStarted == null )
			throw new NullPointerException( "atcEventGameStarted must not be null" );
		else if ( nodesToZone == null )
			throw new NullPointerException( "nodesToZone must not be null" );
		else if ( nodeZones == null )
			throw new NullPointerException( "nodeZones must not be null" );
		this.atcEventGameStarted = atcEventGameStarted;
		this.nodesToZone = nodesToZone;
		this.nodeZones = nodeZones;
		map = mapCache;
	}


	public Collection<AtcRoutingNode> getAirportNodesOf(
			AtcTeamTag team
	) {
		return map.getAirportNodesOf( team );
	}


	public Optional<AtcMap> getMap(
	) {
		return map.getMap();
	}


	@Override
	public Optional<AtcRoutingNode> geNodeOf(
			AtcMapPoint pointwiseLocation
	) {
		return map.geNodeOf( pointwiseLocation );
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
				while ( ! atcEventGameStarted.isEmpty() )
				{
					AtcEventGameStarted mapEvent = atcEventGameStarted.poll();
					if ( mapEvent == null )
						break;
					map.save( mapEvent.getMap() );
					nodesToZone.offer( mapEvent.getMap().getNodes() );
				}

				while ( ! nodeZones.isEmpty() )
				{
					Map<AtcRoutingNode, Rectangle> zonedNodes = nodeZones.poll();
					map.save( zonedNodes );
				}

				Thread.sleep( millisecondsToSleep );
				if ( quit )
					return;
			}
		}
		catch ( InterruptedException ie )
		{
			log.error( ie.toString() );
		}
	}



}


















