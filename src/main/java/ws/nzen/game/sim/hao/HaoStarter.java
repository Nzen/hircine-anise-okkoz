
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

import ws.nzen.game.adventure.mhc.message.Quit;
import ws.nzen.game.sim.hao.app.service.CoalesceBlobs;
import ws.nzen.game.sim.hao.app.service.Factory;
import ws.nzen.game.sim.hao.game.AtcEvent;
import ws.nzen.game.sim.hao.game.AtcEventAirplaneDetected;
import ws.nzen.game.sim.hao.game.AtcEventGameStarted;
import ws.nzen.game.sim.hao.game.HaoEvent;
import ws.nzen.game.sim.hao.uses.any.Quittable;
import ws.nzen.game.sim.hao.uses.atc.KnowsAirplanesRunnably;
import ws.nzen.game.sim.hao.uses.atc.KnowsMapRunnably;
import ws.nzen.game.sim.hao.uses.atc.ManagesGameState;
import ws.nzen.game.sim.hao.uses.atc.RequestsEvents;
import ws.nzen.game.sim.hao.uses.view.BookendsGames;
import ws.nzen.game.sim.hao.uses.view.ShowsEvents;
import ws.nzen.game.sim.hao.uses.view.ShowsMap;


/** Connects to and starts Auto-Traffic-Control. */
public class HaoStarter
{

	/** Connects to localhost port 4747 and starts game */
	public static void main(
			String[] args
	) {
		HaoStarter game = new HaoStarter( "localhost", 4747, 9998 );
		game.start();
	}

	private static final Logger log = LoggerFactory.getLogger( HaoStarter.class );
	private final ExecutorService threads;
	private final KnowsAirplanesRunnably knowsAirplanes;
	private final KnowsMapRunnably knowsMap;
	private final ManagesGameState gameService;
	private final Queue<AtcEvent> atcEvents;
	private final Queue<AtcEventGameStarted> gameStartEvents;
	private final Queue<AtcEventAirplaneDetected> atcEventsAirplaneDetected;
	private final Queue<GetGameStateRequest> gameStateRequests;
	private final Queue<GetGameStateResponse> gameStateResponses;
	private final Queue<HaoEvent> haoEvents;
	private final Queue<HaoEvent> endGameEvents;
	private final Queue<Object> anything;
	private final Queue<Quit> mhcQuitChannel;
	private final Queue<StartGameRequest> startGameRequests;
	private final Queue<StartGameResponse> startGameResponses;
	private final Queue<StreamRequest> streamRequests;
	private final Queue<StreamResponse> streamResponses;
	private final Queue<String> messageForStdOut;
	private final RequestsEvents eventService;
	private final ShowsEvents stdOut;
	private final ShowsMap showsMap;


	public HaoStarter(
			String host, int atcPort, int viewPort
	) {
		Factory factory = new Factory();
		anything = new ConcurrentLinkedQueue<>();
		atcEvents = new ConcurrentLinkedQueue<>();
		atcEventsAirplaneDetected = new ConcurrentLinkedQueue<>();
		endGameEvents = new ConcurrentLinkedQueue<>();
		gameStartEvents = new ConcurrentLinkedQueue<>();
		gameStateRequests = new ConcurrentLinkedQueue<>();
		gameStateResponses = new ConcurrentLinkedQueue<>();
		haoEvents = new ConcurrentLinkedQueue<>();
		mhcQuitChannel = new ConcurrentLinkedQueue<>();
		messageForStdOut = new ConcurrentLinkedQueue<>();
		startGameRequests = new ConcurrentLinkedQueue<>();
		startGameResponses = new ConcurrentLinkedQueue<>();
		streamRequests = new ConcurrentLinkedQueue<>();
		streamResponses = new ConcurrentLinkedQueue<>();
		threads = Executors.newCachedThreadPool();
		GameRunner startAndQuit = new GameRunner( endGameEvents );
		threads.execute( startAndQuit );
		knowsAirplanes = factory.knowsAirplanesRunnably(
				haoEvents, atcEventsAirplaneDetected );
		threads.execute( knowsAirplanes );
		gameService = factory.managesGameState(
				host,
				atcPort,
				gameStateRequests,
				gameStateResponses,
				startGameRequests,
				startGameResponses
		);
		eventService = factory.requestsEvents(
				host,
				atcPort,
				streamRequests,
				streamResponses,
				atcEvents,
				gameStartEvents,
				atcEventsAirplaneDetected );
		threads.execute( eventService );
		CoalesceBlobs coalesceBlobs = factory.coalesceBlobs(
				anything,
				gameStateResponses,
				startGameResponses );
		threads.execute( coalesceBlobs );
		stdOut = factory.showsEvents( messageForStdOut, atcEvents, anything );
		threads.execute( stdOut );
		knowsMap = factory.knowsMap( gameStartEvents, haoEvents );
		threads.execute( knowsMap );
		showsMap = factory.showsMap(
				knowsAirplanes,
				knowsMap,
				haoEvents,
				endGameEvents,
				mhcQuitChannel,
				viewPort );
		threads.execute( showsMap );
		BookendsGames bookendsGames = factory.bookendsGames();
		if ( showsMap != bookendsGames )
			threads.execute( bookendsGames );
	}


	public void quit(
	) {
		gameService.quit();
		eventService.quit();
		stdOut.quit();
		knowsMap.quit();
		showsMap.quit();
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
			quit();
			return;
		}
		catch ( InterruptedException ie )
		{
			log.error( ie.toString() );
		}
	}


	private class GameRunner implements Runnable, Quittable
	{

		private boolean quit = false;
		private int millisecondsToSleep = 200;
		private final Queue<HaoEvent> endGameEvents;


		public GameRunner(
				Queue<HaoEvent> endGameEvents
		) {
			if ( endGameEvents == null )
				throw new NullPointerException( "v must not be null" );
			this.endGameEvents = endGameEvents;
		}



		@Override
		public void quit(
		) {
			quit = true;
			HaoStarter.this.quit();
		}


		@Override
		/** Check queue for requests to print */
		public void run(
		) {
			try
			{
				while ( true )
				{
					while ( ! endGameEvents.isEmpty() )
					{
						HaoEvent message = endGameEvents.poll();
						if ( message == HaoEvent.END_REQUESTED )
						{
							quit();
							break;
						}
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


}




































