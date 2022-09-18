
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.adapt.mhc;


import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ws.nzen.game.sim.hao.game.*;
import ws.nzen.game.sim.hao.uses.any.Quittable;
import ws.nzen.game.sim.hao.uses.atc.KnowsAirplanes;
import ws.nzen.game.sim.hao.uses.atc.KnowsMap;
import ws.nzen.game.sim.hao.uses.view.ShowsMap;


/**

*/
public class CanvasAdapter implements ShowsMap, Quittable
{

	private static final Logger log = LoggerFactory.getLogger( CanvasAdapter.class );
	private boolean quit = false;
	private final BoardMapper boardMapper;
	private final CanvasEndpoint canvas;
	private final KnowsAirplanes knowsAirplanes;
	private final KnowsMap haoMap;
	private int millisecondsToSleep = 200;
	private final Queue<HaoEvent> repaintEvents;


	public CanvasAdapter(
			BoardMapper boardMapper,
			CanvasEndpoint canvas,
			KnowsAirplanes knowsAirplanes,
			KnowsMap haoMap,
			Queue<HaoEvent> repaintEvents
	) {
		if ( boardMapper == null )
			throw new NullPointerException( "boardMapper must not be null" );
		else if ( canvas == null )
			throw new NullPointerException( "canvas must not be null" );
		else if ( knowsAirplanes == null )
			throw new NullPointerException( "knowsAirplanes must not be null" );
		else if ( haoMap == null )
			throw new NullPointerException( "haoMap must not be null" );
		else if ( repaintEvents == null )
			throw new NullPointerException( "repaintEvents must not be null" );
		this.boardMapper = boardMapper;
		this.canvas = canvas;
		this.knowsAirplanes = knowsAirplanes;
		this.haoMap = haoMap;
		this.repaintEvents = repaintEvents;
		canvas.start();
	}


	@Override
	public void quit(
	) {
		quit = true;
		try
		{
			canvas.stop();
		}
		catch ( IOException | InterruptedException e )
		{
			e.printStackTrace();
		}
	}


	@Override
	/** Check queue for requests to print */
	public void run(
	) {
		try
		{
			while ( true )
			{
				while ( ! repaintEvents.isEmpty() )
				{
					HaoEvent message = repaintEvents.poll();
					if ( message == null )
						break;
					updateMap();
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


	public void updateMap(
	) {
		Optional<AtcMap> map = haoMap.getMap();
		Collection<AtcAirplane> airplanes = knowsAirplanes.findAll();
		if ( ! map.isPresent() )
			return;
		MhcBoard mhcBoard = boardMapper.asMhcBoard( map.get(), airplanes );
		canvas.updateView( mhcBoard );
	}


}
