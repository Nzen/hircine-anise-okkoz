
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao;


import atc.v1.Event.StreamRequest;
import atc.v1.Event.StreamResponse;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ws.nzen.game.sim.hao.game.AtcEvent;
import ws.nzen.game.sim.hao.game.AtcEventAirplaneDetected;
import ws.nzen.game.sim.hao.game.AtcEventGameStarted;
import ws.nzen.game.sim.hao.game.HaoEvent;
import ws.nzen.game.sim.hao.game.HaoMessage;
import ws.nzen.game.sim.hao.service.Factory;
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
	private final GameRunner startAndQuit;
	private final KnowsAirplanesRunnably knowsAirplanes;
	private final KnowsMapRunnably knowsMap;
	private final ManagesGameState gameService;
	private final RequestsEvents eventService;
	private final ShowsEvents stdOut;
	private final ShowsMap showsMap;


	public HaoStarter(
			String host, int atcPort, int viewPort
	) {
		Factory factory = new Factory();
		threads = Executors.newCachedThreadPool();
		startAndQuit = new GameRunner( factory.queueHaoEventEndGame(), factory.queueHaoMessageStartGame() );
		threads.execute( startAndQuit );
		knowsAirplanes = factory.knowsAirplanesRunnably();
		threads.execute( knowsAirplanes );
		gameService = factory.managesGameState(
				host,
				atcPort );
		threads.execute( gameService );
		eventService = factory.requestsEvents(
				host,
				atcPort );
		threads.execute( eventService );
		stdOut = factory.showsEvents();
		threads.execute( stdOut );
		knowsMap = factory.knowsMap();
		threads.execute( knowsMap );
		showsMap = factory.showsMap(
				knowsAirplanes,
				knowsMap,
				viewPort );
		threads.execute( showsMap );
		BookendsGames bookendsGames = factory.bookendsGames(
				knowsAirplanes,
				knowsMap,
				viewPort );
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
			startAndQuit.start();
			gameService.requestGameState();
			Thread.sleep( millisecondsToSleep * 30 );
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
		private final Queue<HaoMessage> haoGameStartRequests;


		public GameRunner(
				Queue<HaoEvent> endGameEvents,
				Queue<HaoMessage> haoGameStartRequests
		) {
			if ( endGameEvents == null )
				throw new NullPointerException( "endGameEvents must not be null" );
			if ( haoGameStartRequests == null )
				throw new NullPointerException( "haoGameStartRequests must not be null" );
			this.endGameEvents = endGameEvents;
			this.haoGameStartRequests = haoGameStartRequests;
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


		void init(
		) {
			// eventService.requestMoreEvents(); // use a queue instead
		}


		void start(
		) {
			haoGameStartRequests.offer( HaoMessage.START_GAME );
		}

	}


}




































