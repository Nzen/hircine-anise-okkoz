
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao;


import atc.v1.Event.StreamRequest;
import atc.v1.Event.StreamResponse;
import atc.v1.Game.GetGameStateRequest;
import atc.v1.Game.GetGameStateResponse;
import atc.v1.Game.StartGameRequest;
import atc.v1.Game.StartGameResponse;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ws.nzen.game.sim.hao.app.service.Factory;
import ws.nzen.game.sim.hao.app.service.MapDispatch;
import ws.nzen.game.sim.hao.game.AtcEvent;
import ws.nzen.game.sim.hao.game.AtcEventGameStarted;
import ws.nzen.game.sim.hao.uses.atc.KnowsMap;
import ws.nzen.game.sim.hao.uses.atc.ManagesGameState;
import ws.nzen.game.sim.hao.uses.atc.RequestsEvents;
import ws.nzen.game.sim.hao.uses.view.ShowsEvents;


/** Demonstrates connecting and then starting Auto-Traffic-Control. */
public class HaoStarter
{

	/** Connects to localhost port 4747 and starts game */
	public static void main(
			String[] args
	) {
		HaoStarter game = new HaoStarter( "localhost", 4747 );
		game.start();
		// CanvasEndpoint view = new CanvasEndpoint( 9998 ); view.start();
	}

	private static final Logger					log	= LoggerFactory.getLogger( HaoStarter.class );
	private final ExecutorService threads;
	private final KnowsMap map;
	private final ManagesGameState				gameService;
	private final Queue<AtcEvent>				atcEvents;
	private final Queue<AtcEventGameStarted> gameStartEvents;
	private final Queue<GetGameStateRequest>	gameStateRequests;
	private final Queue<GetGameStateResponse>	gameStateResponses;
	private final Queue<StartGameRequest>		startGameRequests;
	private final Queue<StartGameResponse>		startGameResponses;
	private final Queue<StreamRequest>			streamRequests;
	private final Queue<StreamResponse>			streamResponses;
	private final Queue<String>					messageForStdOut;
	private final RequestsEvents				eventService;
	private final ShowsEvents					stdOut;


	public HaoStarter(
			String host, int port
	) {
		atcEvents = new ConcurrentLinkedQueue<>();
		gameStartEvents = new ConcurrentLinkedQueue<>();
		gameStateRequests = new ConcurrentLinkedQueue<>();
		gameStateResponses = new ConcurrentLinkedQueue<>();
		startGameRequests = new ConcurrentLinkedQueue<>();
		startGameResponses = new ConcurrentLinkedQueue<>();
		streamRequests = new ConcurrentLinkedQueue<>();
		streamResponses = new ConcurrentLinkedQueue<>();
		messageForStdOut = new ConcurrentLinkedQueue<>();
		threads = Executors.newCachedThreadPool();
		gameService = Factory.managesGameState(
				host,
				port,
				gameStateRequests,
				gameStateResponses,
				startGameRequests,
				startGameResponses
		);
		eventService = Factory.requestsEvents(
				host,
				port,
				streamRequests,
				streamResponses,
				atcEvents,
				gameStartEvents
		);
		threads.execute( eventService );
		stdOut = Factory.showsEvents( messageForStdOut, atcEvents );
		threads.execute( stdOut );
		MapDispatch mapThing = Factory.mapDispatch( gameStartEvents );
		map = mapThing;
		threads.execute( mapThing );
	}


	public void quit(
	) {
		gameService.quit();
		eventService.quit();
		stdOut.quit();
		threads.shutdownNow();
	}


	public void start(
	) {
		int millisecondsToSleep = 1_000;
		try
		{
			eventService.requestMoreEvents();
			Thread.sleep( millisecondsToSleep );
			gameService.startGame();
			Thread.sleep( millisecondsToSleep );
			gameService.requestGameState();
			Thread.sleep( millisecondsToSleep );
if ( ! gameStateResponses.isEmpty() )
	log.info( gameStateResponses.poll().getGameState().toString() );
			Thread.sleep( millisecondsToSleep * 30 );
			quit();
			return;
		}
		catch ( InterruptedException ie )
		{
			log.error( ie.toString() );
		}
	}

}




































