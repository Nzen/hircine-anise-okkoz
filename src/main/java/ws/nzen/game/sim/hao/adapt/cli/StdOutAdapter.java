
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.adapt.cli;


import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ws.nzen.game.sim.hao.game.AtcEvent;
import ws.nzen.game.sim.hao.uses.any.Quittable;
import ws.nzen.game.sim.hao.uses.view.ShowsEvents;


/**
Adapts between other subsystems and StdOutEndpoint.
Mostly does so by consuming the Object stream, at this time.
*/
public class StdOutAdapter implements ShowsEvents, Runnable, Quittable
{

	private static final Logger log = LoggerFactory
			.getLogger( StdOutAdapter.class );

	private boolean quit = false;
	private int millisecondsToSleep = 200;
	private final Queue<String> messages;
	private final Queue<AtcEvent> atcEvents;
	private final Queue<Object> blobs;
	private final StdOutEndpoint stdout;
	private final Thread runsStdout;


	public StdOutAdapter(
			StdOutEndpoint systemOutPrintln,
			Queue<String> messageEgress,
			Queue<AtcEvent> atcEvents,
			Queue<Object> objects
	) {
		if ( messageEgress == null )
			throw new NullPointerException( "messageEgress must not be null" );
		else if ( systemOutPrintln == null )
			throw new NullPointerException( "endpoint must not be null" );
		else if ( atcEvents == null )
			throw new NullPointerException( "atcEvents must not be null" );
		else if ( objects == null )
			throw new NullPointerException( "objects must not be null" );
		messages = messageEgress;
		stdout = systemOutPrintln;
		this.atcEvents = atcEvents;
		blobs = objects;
		runsStdout = new Thread( stdout );
		runsStdout.start();
	}


	@Override
	public void quit(
	) {
		quit = true;
		runsStdout.interrupt();
	}


	@Override
	/** Check queue for requests to show */
	public void run(
	) {
		try
		{
			while ( true )
			{
				while ( ! atcEvents.isEmpty() )
				{
					AtcEvent blob = atcEvents.poll();
					if ( blob == null )
						break;
else if ( blob instanceof ws.nzen.game.sim.hao.game.AtcEventAirplaneMoved )
	continue; // ¶ ignore deluge of airplane movement events
					showMessage( blob );
				}
				while ( ! blobs.isEmpty() )
				{
					Object blob = blobs.poll();
					if ( blob == null )
						break;
					showMessage( blob );
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


	public void showMessage(
			Object something
	) {
		if ( something == null )
			return;
		else
			messages.offer( something.toString() );
	}


	public void showMessage(
			String message
	) {
		messages.offer( message );
	}

}
