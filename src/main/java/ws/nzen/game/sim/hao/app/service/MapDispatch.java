
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.app.service;


import java.util.Collection;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ws.nzen.game.sim.hao.game.AtcEventGameStarted;
import ws.nzen.game.sim.hao.game.AtcRoutingNode;
import ws.nzen.game.sim.hao.game.AtcTeamTag;
import ws.nzen.game.sim.hao.uses.atc.KnowsMap;


/**

*/
public class MapDispatch implements Runnable, KnowsMap
{

	private static final Logger log = LoggerFactory
			.getLogger( MapDispatch.class );
	private int millisecondsToSleep = 200;
	private final Queue<AtcEventGameStarted> gameStartEvents;
	private final MapCache map;


	public MapDispatch(
			MapCache mapCache,
			Queue<AtcEventGameStarted> mapEvents
	) {
		if ( mapCache == null )
			throw new NullPointerException( "mapCache must not be null" );
		else if ( mapEvents == null )
			throw new NullPointerException( "mapEvents must not be null" );
		gameStartEvents = mapEvents;
		map = mapCache;
	}


	public Collection<AtcRoutingNode> getAirportNodesOf(
			AtcTeamTag team
	) {
		return map.getAirportNodesOf( team );
	}


	/** Check queue for requests to print */
	@Override
	public void run(
	) {
		try
		{
			while ( true )
			{
				while ( ! gameStartEvents.isEmpty() )
				{
					AtcEventGameStarted mapEvent = gameStartEvents.poll();
					if ( mapEvent == null )
						break;
					map.save( mapEvent.getMap() );
				}

				Thread.sleep( millisecondsToSleep );
			}
		}
		catch ( InterruptedException ie )
		{
			log.error( ie.toString() );
		}
	}



}


















