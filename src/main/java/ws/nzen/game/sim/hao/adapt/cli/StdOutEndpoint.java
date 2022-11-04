
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.adapt.cli;


import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ws.nzen.game.sim.hao.service.HaoConstants;
import ws.nzen.game.sim.hao.uses.any.Quittable;



/**

*/
public class StdOutEndpoint implements Runnable, Quittable
{

	public static final String QUIT = "QUIT";
	private static final Logger log = LoggerFactory
			.getLogger( StdOutEndpoint.class );
	private boolean quit = false;
	private int millisecondsToSleep = HaoConstants.queueDelayMilliseconds;
	private final Queue<String> messagesToLog;


	public StdOutEndpoint(
			Queue<String> messageEgress
	) {
		if ( messageEgress == null )
			throw new NullPointerException( "msg in must not be null" );
		messagesToLog = messageEgress;

	}


	public Queue<String> getRequestsFromOutside(
	) {
		return messagesToLog;
	}


	@Override
	public void quit(
	) {
		quit = true;
	}


	@Override
	/** Check queue for requests to print */
	public void run(
	) {
		try
		{
			while ( true )
			{
				while ( ! messagesToLog.isEmpty() )
				{
					String said = messagesToLog.poll();
					if ( QUIT.equals( said ) )
						return;
					else if ( said == null )
						break;
					else
						log.info( said );
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
