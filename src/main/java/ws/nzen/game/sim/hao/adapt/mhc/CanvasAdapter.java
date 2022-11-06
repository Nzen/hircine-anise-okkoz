
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.adapt.mhc;


import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ws.nzen.game.adventure.mhc.message.Move;
import ws.nzen.game.adventure.mhc.message.Quit;
import ws.nzen.game.sim.hao.game.*;
import ws.nzen.game.sim.hao.service.HaoConstants;
import ws.nzen.game.sim.hao.uses.atc.KnowsAirplanes;
import ws.nzen.game.sim.hao.uses.atc.KnowsMap;
import ws.nzen.game.sim.hao.uses.view.BookendsGames;
import ws.nzen.game.sim.hao.uses.view.ShowsMap;


/**

*/
public class CanvasAdapter implements BookendsGames, ShowsMap
{

	private static final Logger log = LoggerFactory.getLogger( CanvasAdapter.class );
	private boolean quit = false;
	private final BoardMapper boardMapper;
	private final CanvasEndpoint canvas;
	private final KnowsAirplanes knowsAirplanes;
	private final KnowsMap haoMap;
	private int millisecondsToSleep = HaoConstants.queueDelayMilliseconds;
	private final Queue<HaoEvent> repaintInput;
	private final Queue<HaoEvent> endGameOutward;
	private final Queue<HaoMessage> haoGameStartRequests;
	private final Queue<MhcMessage> mhcGameStartRequests;
	private final Queue<Quit> mhcQuitInput;
	private final Queue<HaoMessage> viewConnected;
	private final Queue<Move> mhcViewConnected;


	public CanvasAdapter(
			BoardMapper boardMapper,
			CanvasEndpoint canvas,
			KnowsAirplanes knowsAirplanes,
			KnowsMap haoMap,
			Queue<HaoEvent> repaintInput,
			Queue<HaoEvent> endGameOutward,
			Queue<HaoMessage> haoGameStartRequests,
			Queue<MhcMessage> mhcGameStartRequests,
			Queue<Quit> mhcQuitInput,
			Queue<HaoMessage> viewConnected,
			Queue<Move> mhcViewConnected
	) {
		if ( boardMapper == null )
			throw new NullPointerException( "boardMapper must not be null" );
		else if ( canvas == null )
			throw new NullPointerException( "canvas must not be null" );
		else if ( knowsAirplanes == null )
			throw new NullPointerException( "knowsAirplanes must not be null" );
		else if ( haoMap == null )
			throw new NullPointerException( "haoMap must not be null" );
		else if ( repaintInput == null )
			throw new NullPointerException( "repaintInput must not be null" );
		else if ( endGameOutward == null )
			throw new NullPointerException( "endGameOutward must not be null" );
		else if ( mhcGameStartRequests == null )
			throw new NullPointerException( "mhcGameStartRequests must not be null" );
		else if ( mhcQuitInput == null )
			throw new NullPointerException( "mhcQuitInput must not be null" );
		else if ( viewConnected == null )
			throw new NullPointerException( "viewConnected must not be null" );
		else if ( mhcViewConnected == null )
			throw new NullPointerException( "mhcViewConnected must not be null" );
		this.boardMapper = boardMapper;
		this.canvas = canvas;
		this.knowsAirplanes = knowsAirplanes;
		this.haoMap = haoMap;
		this.repaintInput = repaintInput;
		this.endGameOutward = endGameOutward;
		this.haoGameStartRequests = haoGameStartRequests;
		this.mhcGameStartRequests = mhcGameStartRequests;
		this.mhcQuitInput = mhcQuitInput;
		this.viewConnected = viewConnected;
		this.mhcViewConnected = mhcViewConnected;
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
			mainLoop :
			while ( true )
			{
				while ( ! mhcQuitInput.isEmpty() )
				{
					Quit message = mhcQuitInput.poll();
					log.info( message.toString() );
					endGameOutward.offer( HaoEvent.END_REQUESTED );
					quit();
					break mainLoop;
				}

				while ( ! repaintInput.isEmpty() )
				{
					HaoEvent message = repaintInput.poll();
					if ( message == null )
						break;
					updateMap( message == HaoEvent.AIRPLANE_NODE_CHANGED );
				}

				while ( ! mhcQuitInput.isEmpty() )
				{
					Quit message = mhcQuitInput.poll();
					if ( message == null )
						break;
					endGameOutward.offer( HaoEvent.END_REQUESTED );
				}

				while ( ! mhcViewConnected.isEmpty() )
				{
					Move message = mhcViewConnected.poll();
					if ( message == null )
						break;
					viewConnected.offer( HaoMessage.VIEW_CONNECTED );
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
			boolean requireNodeChangeForRepaint
	) {
		Optional<AtcMap> map = haoMap.getMap();
		if ( ! map.isPresent() )
			return;
		boolean nodeChanged = knowsAirplanes.updateAirplaneNodes( haoMap );
		Collection<AtcAirplane> airplanes = knowsAirplanes.findAll();
		if ( requireNodeChangeForRepaint && ! nodeChanged )
			return;
		MhcBoard mhcBoard = boardMapper.asMhcBoard( map.get(), airplanes );
		canvas.updateView( mhcBoard );
	}


}






























