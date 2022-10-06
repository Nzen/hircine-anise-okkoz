
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.adapt.atc;


import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import atc.v1.Event.StreamRequest;
import atc.v1.Event.StreamResponse;

import ws.nzen.game.sim.hao.game.*;
import ws.nzen.game.sim.hao.uses.any.Quittable;
import ws.nzen.game.sim.hao.uses.atc.RequestsEvents;


/**

*/
public class EventServiceAdapter implements RequestsEvents, Runnable, Quittable
{

	private static final Logger log = LoggerFactory
			.getLogger( EventServiceAdapter.class );

	private boolean quit = false;
	private final EventMapper eventMapper;
	private final EventServiceEndpoint eventService;
	private int millisecondsToSleep = 200;
	private final Queue<AtcEvent> atcEvents;
	private final Queue<AtcEventAirplaneDetected> atcEventsAirplaneDetected;
	private final Queue<AtcEventGameStarted> gameStartEvents;
	private final Queue<StreamRequest> requests;
	private final Queue<StreamResponse> responses;
	private final Queue<HaoMessage> startStreamRequests;
	private final Queue<AtcEventGameStopped> atcEndedGame;
	private final Queue<AtcEventFlightPlanUpdated> aeFlightChanged;
	private final Thread runsEventService;


	public EventServiceAdapter(
			EventServiceEndpoint eventStream,
			EventMapper mapper,
			Queue<StreamRequest> forRequests,
			Queue<StreamResponse> forResponses,
			Queue<AtcEvent> atcEvents,
			Queue<AtcEventGameStarted> gameStartEvents,
			Queue<AtcEventAirplaneDetected> atcEventsAirplaneDetected,
			Queue<HaoMessage> startStreamRequests,
			Queue<AtcEventGameStopped> atcEndedGame,
			Queue<AtcEventFlightPlanUpdated> aeFlightChanged
	) {
		if ( forRequests == null )
			throw new NullPointerException( "forRequests must not be null" );
		else if ( eventStream == null )
			throw new NullPointerException( "endpoint must not be null" );
		else if ( atcEvents == null )
			throw new NullPointerException( "atcEvents must not be null" );
		else if ( gameStartEvents == null )
			throw new NullPointerException( "gameStartEvents must not be null" );
		else if ( mapper == null )
			throw new NullPointerException( "mapper must not be null" );
		else if ( atcEventsAirplaneDetected == null )
			throw new NullPointerException( "atcEventsAirplaneDetected must not be null" );
		else if ( startStreamRequests == null )
			throw new NullPointerException( "startStreamRequests must not be null" );
		else if ( atcEndedGame == null )
			throw new NullPointerException( "atcEndedGame must not be null" );
		else if ( aeFlightChanged == null )
			throw new NullPointerException( "aeFlightChanged must not be null" );
		eventService = eventStream;
		requests = forRequests;
		responses = forResponses;
		this.atcEvents = atcEvents;
		this.gameStartEvents = gameStartEvents;
		this.atcEventsAirplaneDetected = atcEventsAirplaneDetected;
		this.startStreamRequests = startStreamRequests;
		this.atcEndedGame = atcEndedGame;
		this.aeFlightChanged = aeFlightChanged;
		eventMapper = mapper;
		runsEventService = new Thread( eventService );
		runsEventService.start();
	}


	@Override
	public void quit(
	) {
		quit = true;
		runsEventService.interrupt();
	}


	@Override
	/** Check for requests, copy these to outgoing queue. */
	public void run(
	) {
		try
		{
			while ( true )
			{
				while ( ! responses.isEmpty() )
				{
					StreamResponse response = responses.poll();
					if ( response == null )
						break;
					AtcEvent event = eventMapper.asAtcEvent( response );
					if ( event.getType() == AtcEventType.GAME_STARTED )
						gameStartEvents.offer( (AtcEventGameStarted)event );
					else if ( event.getType() == AtcEventType.AIRPLANE_DETECTED )
						atcEventsAirplaneDetected.offer( (AtcEventAirplaneDetected)event );
					else if ( event.getType() == AtcEventType.GAME_STOPPED )
						atcEndedGame.offer( (AtcEventGameStopped)event );
					else if ( event.getType() == AtcEventType.FLIGHT_PLAN_UPDATED )
						aeFlightChanged.offer( (AtcEventFlightPlanUpdated)event );
					else
						atcEvents.offer( event );
				}

				while ( ! startStreamRequests.isEmpty() )
				{
					HaoMessage message = startStreamRequests.poll();
					if ( message != HaoMessage.START_EVENT_STREAM )
						log.info( "ignoring unhandled message type: "+ message );
					else
						requests.offer( StreamRequest.newBuilder().build() );
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

















































