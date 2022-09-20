
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.adapt.mhc;


import java.awt.Point;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ws.nzen.game.adventure.mhc.HandlesInput;
import ws.nzen.game.adventure.mhc.Keymap;
import ws.nzen.game.adventure.mhc.message.Cell;
import ws.nzen.game.adventure.mhc.message.ClientJsonMessage;
import ws.nzen.game.adventure.mhc.message.Quit;
import ws.nzen.game.adventure.mhc.message.Request;
import ws.nzen.game.adventure.mhc.message.Type;
import ws.nzen.game.sim.hao.game.MhcBoard;


/**

*/
public class CanvasEndpoint extends WebSocketServer
{

	private static final Point maxScreenDim = new Point( 10, 10 );
	private static final Point maxMapDim = new Point( 30, 30 );

	private final Logger log = LoggerFactory.getLogger( CanvasEndpoint.class );
	private MhcBoard board = null;
	private Map<InetSocketAddress, WebSocket> listeners = new HashMap<>();
	private final Queue<Quit> mhcQuitOutput;


	public CanvasEndpoint(
			int portNumber,
			Queue<Quit> mhcQuitOutput
	) {
		super( new InetSocketAddress( portNumber ) );
		if ( mhcQuitOutput == null )
			throw new NullPointerException( "mhcQuitOutput must not be null" );
		this.mhcQuitOutput = mhcQuitOutput;
	}


	@Override
	public void onClose(
			WebSocket conn, int code, String reason, boolean remote
	) {
		log.info(
				"finished with " + conn
						.getRemoteSocketAddress().getAddress().getHostAddress()
		);
		listeners.remove( conn.getLocalSocketAddress() );
	}


	@Override
	public void onError(
			WebSocket conn, Exception ex
	) {
		String connId = (conn != null)
				? conn.getRemoteSocketAddress().getAddress().toString()
				: "null socket";
		log.info( "failed with " + connId + "\n" + ex );
	}


	@Override
	public void onMessage(
			WebSocket conn, String jsonMsg
	) {
		log.info( "received " + jsonMsg + " " + conn.hashCode() );
		Keymap mhcKeymap = new Keymap();
		String quitCharacter = mhcKeymap.getQuitKey();
		HandlesInput mhcKeyToRequest = new HandlesInput();
		JSONObject mapped = new JSONObject( jsonMsg );
		if ( mapped.has( ClientJsonMessage.FLAG_TYPE ) )
		{
			if ( mapped.getString( ClientJsonMessage.FLAG_TYPE )
					.equals( ClientJsonMessage.TYPE_KEY )
					// IMPROVE store the keypress state, rather than only use keypress
					&& mapped.getBoolean( ClientJsonMessage.KEY_F_PRESSING ) )
			{
				try
				{
					Request move = HandlesInput.fromKey(
							mapped.getString( ClientJsonMessage.KEY_F_KCODE ),
							mhcKeymap );
					if ( move.type() == Type.QUIT )
					{
						log.info( "told to quit" );
						conn.close();
						mhcQuitOutput.offer( new Quit() ); // IMPROVE make this a string queue or something
					}
				}
				catch ( RuntimeException re )
				{
					// IMPROVE ignoring invalid keys
				}
			}
		}

		/* JSONObject mapped = new JSONObject( jsonMsg );
		 * if ( mapped.has( ClientJsonMessage.FLAG_TYPE ) )
		 * {
		 * if ( mapped.getString( ClientJsonMessage.FLAG_TYPE )
		 * .equals( ClientJsonMessage.TYPE_KEY )
		 * // IMPROVE store the keypress state, rather than only use keypress
		 * && mapped.getBoolean( ClientJsonMessage.KEY_F_PRESSING ) )
		 * {
		 * Request move = HandlesInput.fromKey(
		 * mapped.getString( ClientJsonMessage.KEY_F_KCODE ),
		 * inputMap );
		 * if ( move.type() == Type.MOVE && mode == GameState.AVATAR_TURN )
		 * {
		 * boolean did = playerAvatar.move(
		 * (Move)move, board, entities, true );
		 * if ( did )
		 * {
		 * board.recalcFieldOfView( playerAvatar, fovRadius );
		 * mode = GameState.WORLD_TURN;
		 * conn.send( boardViewMessage().toString() );
		 * }
		 * }
		 * else if ( move.type() == Type.QUIT )
		 * {
		 * conn.close();
		 * outChannel.info( "told to quit" );
		 * System.exit( 0 );
		 * }
		 * }
		 * else if ( mapped.getString( ClientJsonMessage.FLAG_TYPE )
		 * .equals( ClientJsonMessage.TYPE_KEYMAP ) )
		 * {
		 * inputMap.adopt( translateKeymap(
		 * mapped.getJSONObject( ClientJsonMessage.KEYMAP_F_KEYMAP ) ) );
		 * }
		 * } */
	}


	@Override
	public void onOpen(
			WebSocket conn, ClientHandshake handshake
	) {
		log.info(
				"began with " + conn.getRemoteSocketAddress()
						+ " id-" + conn.hashCode()
		);
		conn.send( boardViewMessage().toString() );
		listeners.put( conn.getLocalSocketAddress(), conn );
	}


	@Override
	public void onStart(
	) {
		setConnectionLostTimeout( 100 );
	}


	public void updateView(
			MhcBoard map
	) {
		if ( listeners.isEmpty() )
			return;
		String boardViewMessage = boardViewMessage( map ).toString();
		for ( WebSocket conn : listeners.values() )
			conn.send( boardViewMessage );
	}


	private JSONObject boardViewMessage(
	) {
		return boardViewMessage( board );
	}


	private JSONObject boardViewMessage(
			MhcBoard map
	) {
		JSONObject wholeMessage = new JSONObject();
		if ( map == null )
			return wholeMessage;
		JSONArray layers = new JSONArray();
		for ( List<Cell> layer : map.boardForClient() )
		{
			JSONArray currLayer = new JSONArray();
			for ( Cell cell : layer )
			{
				JSONObject jsCell = new JSONObject();
				jsCell.put( "symbol", cell.getSymbol() );
				jsCell.put( "color", cell.getColor() );
				jsCell.put( "xxPos", cell.getXxPos() );
				jsCell.put( "yyPos", cell.getYyPos() );
				currLayer.put( jsCell );
			}
			layers.put( currLayer );
		}
		wholeMessage.put( ClientJsonMessage.FLAG_TYPE, ClientJsonMessage.TYPE_CANVAS );
		wholeMessage.put( ClientJsonMessage.CANVAS_F_VALUE, layers );
		return wholeMessage;
	}

}
