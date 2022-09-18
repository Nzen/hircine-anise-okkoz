
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.app.service;


import java.util.Collection;
import java.util.Optional;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ws.nzen.game.sim.hao.game.AtcEventGameStarted;
import ws.nzen.game.sim.hao.game.AtcMap;
import ws.nzen.game.sim.hao.game.AtcRoutingNode;
import ws.nzen.game.sim.hao.game.AtcTeamTag;
import ws.nzen.game.sim.hao.uses.any.Quittable;
import ws.nzen.game.sim.hao.uses.atc.KnowsMapRunnably;


/**

*/
public class MapDispatch implements Runnable, KnowsMapRunnably, Quittable
{

	private static final Logger log = LoggerFactory
			.getLogger( MapDispatch.class );
	private boolean quit = false;
	private int millisecondsToSleep = 200;
	private final MapCache map;
	private final Queue<AtcEventGameStarted> atcEventGameStarted;


	public MapDispatch(
			MapCache mapCache,
			Queue<AtcEventGameStarted> atcEventGameStarted
	) {
		if ( mapCache == null )
			throw new NullPointerException( "mapCache must not be null" );
		else if ( atcEventGameStarted == null )
			throw new NullPointerException( "atcEventGameStarted must not be null" );
		this.atcEventGameStarted = atcEventGameStarted;
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
			log.info( "map save" );
					map.save( mapEvent.getMap() );
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


















