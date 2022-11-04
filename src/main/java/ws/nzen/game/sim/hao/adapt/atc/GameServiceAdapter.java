
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.adapt.atc;


import atc.v1.Game.GetGameStateRequest;
import atc.v1.Game.GetGameStateResponse;
import atc.v1.Game.StartGameRequest;
import atc.v1.Game.StartGameResponse;

import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ws.nzen.game.sim.hao.game.HaoMessage;
import ws.nzen.game.sim.hao.service.HaoConstants;
import ws.nzen.game.sim.hao.uses.any.Quittable;
import ws.nzen.game.sim.hao.uses.atc.ManagesGameState;


/**

*/
public class GameServiceAdapter implements ManagesGameState, Quittable
{

	private static final Logger log = LoggerFactory.getLogger( GameServiceAdapter.class );
	private boolean quit = false;
	private final GameServiceEndpoint gameService;
	private int millisecondsToSleep = HaoConstants.queueDelayMilliseconds;
	private final Queue<GetGameStateRequest> gameStateRequests;
	private final Queue<GetGameStateResponse> forGameStateResponses;
	private final Queue<StartGameRequest> startGameRequests;
	private final Queue<StartGameResponse> startGameResponses;
	private final Queue<HaoMessage> haoGameStartRequests;
	private final Thread runsGameService;


	public GameServiceAdapter(
			GameServiceEndpoint endpoint,
			Queue<GetGameStateRequest> gameStateRequests,
			Queue<GetGameStateResponse> forGameStateResponses,
			Queue<StartGameRequest> startGameRequests,
			Queue<StartGameResponse> forStartGameResponses,
			Queue<HaoMessage> haoGameStartRequests
	) {
		if ( endpoint == null )
			throw new NullPointerException( "endpoint must not be null" );
		else if ( gameStateRequests == null )
			throw new NullPointerException( "gameStateRequests must not be null" );
		else if ( forGameStateResponses == null )
			throw new NullPointerException( "forGameStateResponses must not be null" );
		else if ( startGameRequests == null )
			throw new NullPointerException( "forStartGameRequests must not be null" );
		else if ( forStartGameResponses == null )
			throw new NullPointerException( "forStartGameResponses must not be null" );
		else if ( haoGameStartRequests == null )
			throw new NullPointerException( "haoGameStartRequests must not be null" );
		gameService = endpoint;
		this.gameStateRequests = gameStateRequests;
		this.forGameStateResponses = forGameStateResponses;
		this.startGameRequests = startGameRequests;
		this.startGameResponses = forStartGameResponses;
		this.haoGameStartRequests = haoGameStartRequests;
		runsGameService = new Thread( endpoint );
		runsGameService.start();
	}


	@Override
	public void quit(
	) {
		gameService.quit();
		runsGameService.interrupt();
	}


	@Override
	/** Check queue for requests to print */
	public void run(
	) {
		try
		{
			while ( true )
			{
				while ( ! forGameStateResponses.isEmpty() )
				{
					GetGameStateResponse message = forGameStateResponses.poll();
					log.info( message.toString() );
				}

				while ( ! startGameResponses.isEmpty() )
				{
					StartGameResponse message = startGameResponses.poll();
					log.info( message.toString() );
				}

				while ( ! haoGameStartRequests.isEmpty() )
				{
					HaoMessage message = haoGameStartRequests.poll();
					if ( message == HaoMessage.START_GAME )
						startGame();
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


	@Override
	public void requestGameState(
	) {
		gameStateRequests.offer( GetGameStateRequest.newBuilder().build() );
	}


	private void startGame(
	) {
		startGameRequests.offer( StartGameRequest.newBuilder().build() );
	}

}
