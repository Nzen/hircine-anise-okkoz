
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
import ws.nzen.game.sim.hao.game.AtcEventGameStopped;
import ws.nzen.game.sim.hao.game.AtcGameVersion;
import ws.nzen.game.sim.hao.game.HaoEvent;
import ws.nzen.game.sim.hao.game.HaoMessage;
import ws.nzen.game.sim.hao.service.Factory;
import ws.nzen.game.sim.hao.service.HaoConstants;
import ws.nzen.game.sim.hao.uses.any.Quittable;
import ws.nzen.game.sim.hao.uses.atc.KnowsAirplanesRunnably;
import ws.nzen.game.sim.hao.uses.atc.KnowsAtcVersion;
import ws.nzen.game.sim.hao.uses.atc.KnowsMapRunnably;
import ws.nzen.game.sim.hao.uses.atc.KnowsNodes;
import ws.nzen.game.sim.hao.uses.atc.LocatesNodes;
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
	private final KnowsAtcVersion knowsAtcVersion;
	private final KnowsMapRunnably knowsMap;
	private final KnowsNodes knowsNodes;
	private final LocatesNodes locatesNodes;
	private final ManagesGameState gameService;
	private final RequestsEvents eventService;
	private final ShowsEvents stdOut;
	private final ShowsMap showsMap;


	public HaoStarter(
			String host, int atcPort, int viewPort
	) {
		Factory factory = new Factory();
		threads = Executors.newCachedThreadPool();

		startAndQuit = new GameRunner(
				factory.queueHaoEventEndGame(),
				factory.queueHaoMessageStartGame(),
				factory.queueAtcGameVersion(),
				factory.queueGetAtcVersionRequest(),
				factory.queueStartEventStream(),
				factory.queueAtcEventGameStopped(),
				factory.queueViewConnected() );
		threads.execute( startAndQuit );

		knowsAirplanes = factory.knowsAirplanesRunnably();
		threads.execute( knowsAirplanes );

		knowsAtcVersion = factory.knowsAtcVersion( host, atcPort );
		threads.execute( knowsAtcVersion );

		knowsNodes = factory.knowsNodes( host, atcPort );
		threads.execute( knowsNodes );

		gameService = factory.managesGameState( host, atcPort );
		threads.execute( gameService );

		eventService = factory.requestsEvents( host, atcPort );
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

		locatesNodes = factory.locatesNodes();
		threads.execute( locatesNodes );

		BookendsGames bookendsGames = factory.bookendsGames(
				knowsAirplanes,
				knowsMap,
				viewPort );
		if ( showsMap != bookendsGames )
			threads.execute( bookendsGames );
	}


	public void quit(
	) {
		knowsAirplanes.quit();
		knowsAtcVersion.quit();
		eventService.quit();
		gameService.quit();
		knowsMap.quit();
		knowsNodes.quit();
		locatesNodes.quit();
		showsMap.quit();
		stdOut.quit();
		threads.shutdownNow();
	}


	public void start(
	) {
		int millisecondsToSleep = 1_000;
		try
		{
			Thread.sleep( millisecondsToSleep );
			startAndQuit.init();
			Thread.sleep( millisecondsToSleep );
			gameService.requestGameState(); // FIX remove and tell runner to do so
		}
		catch ( InterruptedException ie )
		{
			log.error( ie.toString() );
		}
	}


	private class GameRunner implements Runnable, Quittable
	{

		private boolean atcListening = false;
		private boolean quit = false;
		private boolean viewListening = false;
		private int millisecondsToSleep = HaoConstants.queueDelayMilliseconds;
		private final Queue<HaoEvent> endGameEvents;
		private final Queue<HaoMessage> haoGameStartRequests;
		private final Queue<AtcGameVersion> atcGameVersions;
		private final Queue<HaoMessage> checkVersionRequests;
		private final Queue<HaoMessage> eventStreamRequests;
		private final Queue<AtcEventGameStopped> atcEventGameStopped;
		private final Queue<HaoMessage> viewConnected;;


		public GameRunner(
				Queue<HaoEvent> endGameEvents,
				Queue<HaoMessage> haoGameStartRequests,
				Queue<AtcGameVersion> atcGameVersions,
				Queue<HaoMessage> checkVersionRequests,
				Queue<HaoMessage> eventStreamRequests,
				Queue<AtcEventGameStopped> atcEventGameStopped,
				Queue<HaoMessage> viewConnected
		) {
			if ( endGameEvents == null )
				throw new NullPointerException( "endGameEvents must not be null" );
			if ( haoGameStartRequests == null )
				throw new NullPointerException( "haoGameStartRequests must not be null" );
			if ( atcGameVersions == null )
				throw new NullPointerException( "queueAtcGameVersions must not be null" );
			if ( checkVersionRequests == null )
				throw new NullPointerException( "checkVersionRequests must not be null" );
			if ( eventStreamRequests == null )
				throw new NullPointerException( "eventStreamRequests must not be null" );
			if ( atcEventGameStopped == null )
				throw new NullPointerException( "atcEventGameStopped must not be null" );
			if ( viewConnected == null )
				throw new NullPointerException( "viewConnected must not be null" );
			this.endGameEvents = endGameEvents;
			this.haoGameStartRequests = haoGameStartRequests;
			this.atcGameVersions = atcGameVersions;
			this.checkVersionRequests = checkVersionRequests;
			this.eventStreamRequests = eventStreamRequests;
			this.atcEventGameStopped = atcEventGameStopped;
			this.viewConnected = viewConnected;
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

					while ( ! atcGameVersions.isEmpty() )
					{
						AtcGameVersion atcVersion = atcGameVersions.poll();
						if ( ! compatible( atcVersion ) )
						{
							log.error( "incompatible with "+ atcVersion );
							quit();
							break;
						}
						else
							listen();
					}

					while ( ! atcEventGameStopped.isEmpty() ) // IMPROVE just wait for person
					{
						AtcEventGameStopped atcMessage = atcEventGameStopped.poll();
						log.info( "Quitting because person can't control, but "+ atcMessage );
						quit();
					}

					while ( ! viewConnected.isEmpty() )
					{
						HaoMessage message = viewConnected.poll();
						if ( message != HaoMessage.VIEW_CONNECTED )
							log.info( "ignoring unhandled message type: "+ message );
						else
							hearViewListening();
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


		void hearViewListening(
		) {
			if ( viewListening )
				return; // ¶ avoid restarting the game if the client view refreshes
			viewListening = true;
			maybeStartGame();
		}


		void init(
		) {
			checkVersionRequests.offer( HaoMessage.CHECK_COMPATABILITY );
		}


		void listen(
		) {
			eventStreamRequests.offer( HaoMessage.START_EVENT_STREAM );
			atcListening = true;
			maybeStartGame();
		}


		void maybeStartGame(
		) {
			if ( atcListening && viewListening )
				start();
			else
				log.info( "closer to starting game" );
		}


		void start(
		) {
			haoGameStartRequests.offer( HaoMessage.START_GAME );
			// IMPROVE only if game is not running, else this will kill the running game
		}


		private boolean compatible(
				AtcGameVersion atcVersion
		) {
			return atcVersion.getMajorVersion() == 0
					&& atcVersion.getMinorVersion() == 3
					&& atcVersion.getPatchVersion() > 1
					&& atcVersion.getPrereleaseVersion().isEmpty();
		}

	}


}




































