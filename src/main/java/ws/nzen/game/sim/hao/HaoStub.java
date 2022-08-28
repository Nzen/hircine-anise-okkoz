
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao;

import atc.v1.Game.GetGameStateRequest;
import atc.v1.Game.GetGameStateResponse;
import atc.v1.Game.StartGameRequest;
import atc.v1.Game.StartGameResponse;

import ws.nzen.game.sim.hao.adapt.atc.GameServiceAdapter;
import ws.nzen.game.sim.hao.adapt.atc.GameServiceEndpoint;
import ws.nzen.game.sim.hao.app.service.Factory;
import ws.nzen.game.sim.hao.uses.atc.ManagesGameState;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Demonstrates connecting and then starting Auto-Traffic-Control.
 */
public class HaoStub
{

	/**
	 * Connects to localhost port 4747 and starts game
	 */
	public static void main(
			String[] args
	) {
		HaoStub game = new HaoStub( "localhost", 4747 );
		game.start();
		// CanvasEndpoint view = new CanvasEndpoint( 9998 ); view.start();
	}


	private static final Logger log = LoggerFactory.getLogger( HaoStub.class );
	private final ManagesGameState gameService;
	private final Queue<GetGameStateRequest> gameStateRequests;
	private final Queue<GetGameStateResponse> gameStateResponses;
	private final Queue<StartGameRequest> startGameRequests;
	private final Queue<StartGameResponse> startGameResponses;


	public HaoStub(
			String host, int port
	) {
		gameStateRequests = new ConcurrentLinkedQueue<>();
		gameStateResponses = new ConcurrentLinkedQueue<>();
		startGameRequests = new ConcurrentLinkedQueue<>();
		startGameResponses = new ConcurrentLinkedQueue<>();
		gameService = Factory.managesGameState(
				host,
				port,
				gameStateRequests,
				gameStateResponses,
				startGameRequests,
				startGameResponses );
	}


	public void start(
	) {
		int millisecondsToSleep = 1_000;
		gameService.startGame();
		try
		{
			Thread.sleep( millisecondsToSleep );
			gameService.requestGameState();
			Thread.sleep( millisecondsToSleep );
			if ( ! gameStateResponses.isEmpty() )
				log.info( gameStateResponses.poll().getGameState().toString() );
			Thread.sleep( millisecondsToSleep * 5 );
			gameService.quit();
		}
		catch ( InterruptedException ie )
		{
			log.error( ie.toString() );
		}
	}


}
































