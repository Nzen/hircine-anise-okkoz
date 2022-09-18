
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.app.service;


import atc.v1.Event.StreamResponse;
import atc.v1.Game.GetGameStateResponse;
import atc.v1.Game.StartGameResponse;

import java.util.Collection;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ws.nzen.game.sim.hao.uses.any.Quittable;


/** Relays messages without prejudice */
public class CoalesceBlobs implements Quittable, Runnable
{

	private static final Logger log = LoggerFactory.getLogger( CoalesceBlobs.class );

	private boolean quit = false;
	private int millisecondsToSleep = 200;
	private final Queue<GetGameStateResponse> gameStateResponses;
	private final Queue<Object> outwardQueue;
	private final Queue<StartGameResponse> startGameResponses;


	public CoalesceBlobs(
			Queue<Object> outwardQueue,
			Queue<GetGameStateResponse> gameStateResponses,
			Queue<StartGameResponse> startGameResponses
	) {
		if ( outwardQueue == null )
			throw new NullPointerException( "outwardQueue must not be null" );
		else if ( gameStateResponses == null )
			throw new NullPointerException( "gameStateResponses must not be null" );
		else if ( startGameResponses == null )
			throw new NullPointerException( "startGameResponses must not be null" );
		this.outwardQueue = outwardQueue;
		this.gameStateResponses = gameStateResponses;
		this.startGameResponses = startGameResponses;
	}


	@Override
	public void quit(
	) {
		quit = true;
	}


	@Override
	/** Check queue for requests to show */
	public void run(
	) {
		try
		{
			while ( true )
			{
				while ( ! gameStateResponses.isEmpty() )
					outwardQueue.offer( gameStateResponses.poll() );
				while ( ! startGameResponses.isEmpty() )
					outwardQueue.offer( startGameResponses.poll() );

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


















