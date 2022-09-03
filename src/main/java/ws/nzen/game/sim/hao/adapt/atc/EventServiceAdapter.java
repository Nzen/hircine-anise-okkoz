
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.adapt.atc;


import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import atc.v1.Event.StreamRequest;
import atc.v1.Event.StreamResponse;
import ws.nzen.game.sim.hao.game.*;
import ws.nzen.game.sim.hao.uses.atc.RequestsEvents;


/**

*/
public class EventServiceAdapter implements RequestsEvents, Runnable
{

	private static final Logger log = LoggerFactory
			.getLogger( EventServiceAdapter.class );

	private final EventMapper eventMapper;
	private final EventServiceEndpoint eventService;
	private int millisecondsToSleep = 200;
	private final Queue<AtcEvent> events;
	private final Queue<AtcEventGameStarted> gameStartEvents;
	private final Queue<StreamRequest> requests;
	private final Queue<StreamResponse> responses;
	private final Thread runsEventService;


	public EventServiceAdapter(
			EventServiceEndpoint eventStream,
			EventMapper mapper,
			Queue<StreamRequest> forRequests,
			Queue<StreamResponse> forResponses,
			Queue<AtcEvent> atcEvents,
			Queue<AtcEventGameStarted> gameStartEvents
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
		eventService = eventStream;
		requests = forRequests;
		responses = forResponses;
		events = atcEvents;
		this.gameStartEvents = gameStartEvents;
		eventMapper = mapper;
		runsEventService = new Thread( eventService );
		runsEventService.start();
	}


	@Override
	public void requestMoreEvents(
	) {
		requests.offer( StreamRequest.newBuilder().build() );
	}


	@Override
	public void quit(
	) {
		runsEventService.interrupt();
	}


	/** Check for requests, send a null request to quit */
	@Override
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
					AtcEvent event = eventMapper.asHaoEvent( response );
					if ( event.getType() == AtcEventType.GAME_STARTED )
						gameStartEvents.offer( (AtcEventGameStarted)event );
					else
						events.offer( event );
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

















































