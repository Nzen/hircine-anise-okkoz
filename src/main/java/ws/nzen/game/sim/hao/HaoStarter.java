
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ws.nzen.game.sim.hao.app.service.Factory;
import ws.nzen.game.sim.hao.app.service.MapDispatch;
import ws.nzen.game.sim.hao.game.AtcEvent;
import ws.nzen.game.sim.hao.game.AtcEventGameStarted;
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
	private final ManagesGameState				gameService;
	private final MapDispatch map;
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
	private final Thread runsEvents;
	private final Thread runsStdout;
	private final Thread runsMap;


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
		runsEvents = new Thread( eventService );
		runsEvents.start();
		stdOut = Factory.showsEvents( messageForStdOut, atcEvents );
		runsStdout = new Thread( stdOut );
		runsStdout.start();
		map = Factory.mapDispatch( gameStartEvents );
		runsMap = new Thread( map );
		runsMap.start();
	}


	public void quit(
	) {
		gameService.quit();
		eventService.quit();
		stdOut.quit();
		runsStdout.interrupt();
		runsEvents.interrupt();
		runsMap.interrupt();
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




































