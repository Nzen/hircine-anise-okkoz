
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.adapt.cli;


import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**

*/
public class StdOutEndpoint implements Runnable
{

	public static final String QUIT = "QUIT";
	private static final Logger log = LoggerFactory
			.getLogger( StdOutEndpoint.class );
	private int millisecondsToSleep = 200;
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


	/** Check queue for requests to print */
	@Override
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
			}
		}
		catch ( InterruptedException ie )
		{
			log.error( ie.toString() );
		}
	}

}
