
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.adapt.atc;


import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import atc.v1.Atc;
import atc.v1.Atc.GetVersionRequest;
import atc.v1.Atc.GetVersionResponse;

import ws.nzen.game.sim.hao.game.*;
import ws.nzen.game.sim.hao.uses.any.Quittable;
import ws.nzen.game.sim.hao.uses.atc.KnowsAtcVersion;
import ws.nzen.game.sim.hao.uses.atc.RequestsEvents;


/**

*/
public class AtcServiceAdapter implements KnowsAtcVersion
{

	private static final Logger log = LoggerFactory
			.getLogger( AtcServiceAdapter.class );

	private boolean quit = false;
	private final GameVersionMapper gameVersionMapper;
	private final AtcServiceEndpoint gameVersionService;
	private int millisecondsToSleep = 200;
	private final Queue<AtcGameVersion> gameVersions;
	private final Queue<GetVersionRequest> gameVersionRequests;
	private final Queue<GetVersionResponse> gameVersionResponses;
	private final Queue<HaoMessage> checkVersionRequests;
	private final Thread runsGameVersionService;


	public AtcServiceAdapter(
			AtcServiceEndpoint gameVersionStream,
			GameVersionMapper mapper,
			Queue<GetVersionRequest> gameVersionRequests,
			Queue<GetVersionResponse> gameVersionResponses,
			Queue<AtcGameVersion> gameVersions,
			Queue<HaoMessage> checkVersionRequests
	) {
		if ( gameVersionStream == null )
			throw new NullPointerException( "endpoint must not be null" );
		else if ( gameVersionRequests == null )
			throw new NullPointerException( "gameVersionRequests must not be null" );
		else if ( gameVersionResponses == null )
			throw new NullPointerException( "gameVersionResponses must not be null" );
		else if ( mapper == null )
			throw new NullPointerException( "mapper must not be null" );
		else if ( gameVersions == null )
			throw new NullPointerException( "gameVersions must not be null" );
		else if ( checkVersionRequests == null )
			throw new NullPointerException( "checkVersionRequests must not be null" );
		gameVersionService = gameVersionStream;
		this.gameVersionRequests = gameVersionRequests;
		this.gameVersionResponses = gameVersionResponses;
		this.gameVersions = gameVersions;
		this.checkVersionRequests = checkVersionRequests;
		gameVersionMapper = mapper;
		runsGameVersionService = new Thread( gameVersionService );
		runsGameVersionService.start();
	}


	@Override
	public void quit(
	) {
		quit = true;
		runsGameVersionService.interrupt();
	}


	@Override
	/** Check for requests, copy these to outgoing queue. */
	public void run(
	) {
		try
		{
			while ( true )
			{
				while ( ! gameVersionResponses.isEmpty() )
				{
					GetVersionResponse response = gameVersionResponses.poll();
					AtcGameVersion version = gameVersionMapper
							.asHaoVersion( response.getVersion() );
					gameVersions.offer( version );
				}

				while ( ! checkVersionRequests.isEmpty() )
				{
					HaoMessage message = checkVersionRequests.poll();
					if ( message != HaoMessage.CHECK_COMPATABILITY )
						log.info( "ignoring unhandled message type: "+ message );
					else
						gameVersionRequests.offer(
								GetVersionRequest.newBuilder().build() );
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

















































