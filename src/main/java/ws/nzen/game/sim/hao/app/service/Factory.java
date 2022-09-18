
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.app.service;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import atc.v1.Event.*;
import atc.v1.Game.*;
import ws.nzen.game.sim.hao.adapt.atc.*;
import ws.nzen.game.sim.hao.adapt.cli.*;
import ws.nzen.game.sim.hao.adapt.mhc.*;
import ws.nzen.game.sim.hao.game.*;
import ws.nzen.game.sim.hao.uses.atc.*;
import ws.nzen.game.sim.hao.uses.view.*;


/**

*/
public class Factory
{

	private Map<Class<?>, Object> instances = new HashMap<>();


	public CoalesceBlobs coalesceBlobs(
			Queue<Object> outwardQueue,
			Queue<GetGameStateResponse> gameStateResponses,
			Queue<StartGameResponse> startGameResponses
	) {
		Class<?> stdOutAdapterClass = CoalesceBlobs.class;
		if ( instances.containsKey( stdOutAdapterClass ) )
			return (CoalesceBlobs)instances.get( stdOutAdapterClass );
		instances.put(
				stdOutAdapterClass,
				new CoalesceBlobs(
						outwardQueue,
						gameStateResponses,
						startGameResponses ) );
		return coalesceBlobs(
				outwardQueue,
				gameStateResponses,
				startGameResponses );
	}


	public KnowsAirplanesRunnably knowsAirplanesRunnably(
			Queue<HaoEvent> repaintEvents,
			Queue<AtcEventAirplaneDetected> atcEventsAirplaneDetected
	) {
		return airplaneDispatch(
				airplaneCache( repaintEvents ),
				atcEventsAirplaneDetected );
	}


	public KnowsMapRunnably knowsMap(
			Queue<AtcEventGameStarted> events,
			Queue<HaoEvent> repaintEvents
	) {
		return mapDispatch( events, repaintEvents );
	}


	public ManagesGameState managesGameState(
			String host,
			int port,
			Queue<GetGameStateRequest> forGameStateRequests,
			Queue<GetGameStateResponse> forGameStateResponses,
			Queue<StartGameRequest> forStartGameRequests,
			Queue<StartGameResponse> forStartGameResponses
	) {
		return gameServiceAdapter(
				gameServiceEndpoint(
						host,
						port,
						forGameStateRequests,
						forGameStateResponses,
						forStartGameRequests,
						forStartGameResponses ),
				forGameStateRequests,
				forStartGameRequests
		);
	}


	public RequestsEvents requestsEvents(
			String host,
			int port,
			Queue<StreamRequest> forRequests,
			Queue<StreamResponse> forResponses,
			Queue<AtcEvent> atcEvents,
			Queue<AtcEventGameStarted> gameStartEvents,
			Queue<AtcEventAirplaneDetected> atcEventsAirplaneDetected
	) {
		return eventServiceAdapter(
				eventServiceEndpoint(
						host,
						port,
						forRequests,
						forResponses ),
				eventMapper(),
				forRequests,
				forResponses,
				atcEvents,
				gameStartEvents,
				atcEventsAirplaneDetected
		);
	}


	public ShowsEvents showsEvents(
			Queue<String> messageIngress,
			Queue<AtcEvent> atcEvents,
			Queue<Object> blobIngress
	) {
		return stdOutAdapter(
				stdOutEndpoint( messageIngress ),
				messageIngress,
				atcEvents,
				blobIngress );
	}


	public ShowsMap showsMap(
			KnowsAirplanes knowsAirplanes,
			KnowsMap knowsMap,
			Queue<HaoEvent> repaintEvents,
			int portNumber
	) {
		return canvasAdapter(
				boardMapper(),
				canvasEndpoint( portNumber ),
				knowsAirplanes,
				knowsMap,
				repaintEvents );
	}


	private AirplaneCache airplaneCache(
			Queue<HaoEvent> repaintEvents
	) {
		Class<?> airplaneCacheClass = AirplaneCache.class;
		if ( instances.containsKey( airplaneCacheClass ) )
			return (AirplaneCache)instances.get( airplaneCacheClass );
		instances.put( airplaneCacheClass, new AirplaneCache( repaintEvents ) );
		return airplaneCache( repaintEvents );
	}


	private AirplaneDispatch airplaneDispatch(
			AirplaneCache airplaneCache,
			Queue<AtcEventAirplaneDetected> atcEventsAirplaneDetected
	) {
		Class<?> airplaneDispatchClass = AirplaneDispatch.class;
		if ( instances.containsKey( airplaneDispatchClass ) )
			return (AirplaneDispatch)instances.get( airplaneDispatchClass );
		instances.put(
				airplaneDispatchClass,
				new AirplaneDispatch( airplaneCache, atcEventsAirplaneDetected ) );
		return airplaneDispatch( airplaneCache, atcEventsAirplaneDetected );
	}


	private AirplaneMapper airplaneMapper(
	) {
		Class<?> airplaneMapperClass = AirplaneMapper.class;
		if ( instances.containsKey( airplaneMapperClass ) )
			return (AirplaneMapper)instances.get( airplaneMapperClass );
		instances.put(
				airplaneMapperClass,
				new AirplaneMapper(
						nodeMapper(),
						pointMapper(),
						tagMapper() ) );
		return airplaneMapper();
	}


	private AirportMapper airportMapper(
	) {
		Class<?> airportMapperClass = AirportMapper.class;
		if ( instances.containsKey( airportMapperClass ) )
			return (AirportMapper)instances.get( airportMapperClass );
		instances.put(
				airportMapperClass,
				new AirportMapper( nodeMapper(), tagMapper() ) );
		return airportMapper();
	}


	private BoardMapper boardMapper(
	) {
		Class<?> boardMapperClass = BoardMapper.class;
		if ( instances.containsKey( boardMapperClass ) )
			return (BoardMapper)instances.get( boardMapperClass );
		instances.put(
				boardMapperClass,
				new BoardMapper() );
		return boardMapper();
	}


	private CanvasAdapter canvasAdapter(
			BoardMapper boardMapper,
			CanvasEndpoint canvasEndpoint,
			KnowsAirplanes knowsAirplanes,
			KnowsMap knowsMap,
			Queue<HaoEvent> repaintEvents
	) {
		Class<?> canvasAdapterClass = CanvasAdapter.class;
		if ( instances.containsKey( canvasAdapterClass ) )
			return (CanvasAdapter)instances.get( canvasAdapterClass );
		instances.put(
				canvasAdapterClass,
				new CanvasAdapter(
						boardMapper,
						canvasEndpoint,
						knowsAirplanes,
						knowsMap,
						repaintEvents ) );
		return canvasAdapter(
				boardMapper,
				canvasEndpoint,
				knowsAirplanes,
				knowsMap,
				repaintEvents );
	}


	private CanvasEndpoint canvasEndpoint(
			int portNumber
	) {
		Class<?> canvasEndpointClass = CanvasEndpoint.class;
		if ( instances.containsKey( canvasEndpointClass ) )
			return (CanvasEndpoint)instances.get( canvasEndpointClass );
		instances.put(
				canvasEndpointClass,
				new CanvasEndpoint( portNumber ) );
		return canvasEndpoint( portNumber );
	}


	private EventServiceAdapter eventServiceAdapter(
			EventServiceEndpoint atcEventStream,
			EventMapper mapper,
			Queue<StreamRequest> forRequests,
			Queue<StreamResponse> forResponses,
			Queue<AtcEvent> atcEvents,
			Queue<AtcEventGameStarted> gameStartEvents,
			Queue<AtcEventAirplaneDetected> atcEventsAirplaneDetected
	) {
		Class<?> eventServiceAdapterClass = EventServiceAdapter.class;
		if ( instances.containsKey( eventServiceAdapterClass ) )
			return (EventServiceAdapter)instances.get( eventServiceAdapterClass );
		instances.put(
				eventServiceAdapterClass,
				new EventServiceAdapter(
						atcEventStream,
						mapper,
						forRequests,
						forResponses,
						atcEvents,
						gameStartEvents,
						atcEventsAirplaneDetected ) );
		return eventServiceAdapter(
				atcEventStream,
				mapper,
				forRequests,
				forResponses,
				atcEvents,
				gameStartEvents,
				atcEventsAirplaneDetected );
	}


	private EventServiceEndpoint eventServiceEndpoint(
			String host,
			int port,
			Queue<StreamRequest> forRequests,
			Queue<StreamResponse> forResponses
	) {
		Class<?> eventServiceEndpointClass = EventServiceEndpoint.class;
		if ( instances.containsKey( eventServiceEndpointClass ) )
			return (EventServiceEndpoint)instances.get( eventServiceEndpointClass );
		instances.put(
				eventServiceEndpointClass,
				new EventServiceEndpoint(
						host,
						port,
						forRequests,
						forResponses ) );
		return eventServiceEndpoint(
				host,
				port,
				forRequests,
				forResponses );
	}


	private EventMapper eventMapper(
	) {
		Class<?> eventMapperClass = EventMapper.class;
		if ( instances.containsKey( eventMapperClass ) )
			return (EventMapper)instances.get( eventMapperClass );
		instances.put(
				eventMapperClass,
				new EventMapper(
						airplaneMapper(),
						mapMapper(),
						nodeMapper(),
						pointMapper() ) );
		return eventMapper();
	}


	private GameServiceAdapter gameServiceAdapter(
			GameServiceEndpoint endpoint,
			Queue<GetGameStateRequest> forGameStateRequests,
			Queue<StartGameRequest> forStartGameRequests
	) {
		Class<?> gameServiceAdapterClass = GameServiceAdapter.class;
		if ( instances.containsKey( gameServiceAdapterClass ) )
			return (GameServiceAdapter)instances.get( gameServiceAdapterClass );
		instances.put(
				gameServiceAdapterClass,
				new GameServiceAdapter(
						endpoint,
						forGameStateRequests,
						forStartGameRequests ) );
		return gameServiceAdapter(
				endpoint,
				forGameStateRequests,
				forStartGameRequests );
	}


	private GameServiceEndpoint gameServiceEndpoint(
			String host,
			int port,
			Queue<GetGameStateRequest> forGameStateRequests,
			Queue<GetGameStateResponse> forGameStateResponses,
			Queue<StartGameRequest> forStartGameRequests,
			Queue<StartGameResponse> forStartGameResponses
	) {
		Class<?> gameServiceEndpointClass = GameServiceEndpoint.class;
		if ( instances.containsKey( gameServiceEndpointClass ) )
			return (GameServiceEndpoint)instances.get( gameServiceEndpointClass );
		instances.put(
				gameServiceEndpointClass,
				new GameServiceEndpoint(
						host,
						port,
						forGameStateRequests,
						forGameStateResponses,
						forStartGameRequests,
						forStartGameResponses ) );
		return gameServiceEndpoint(
				host,
				port,
				forGameStateRequests,
				forGameStateResponses,
				forStartGameRequests,
				forStartGameResponses );
	}


	private MapCache mapCache(
			Queue<HaoEvent> repaintEvents
	) {
		Class<?> mapCacheClass = MapCache.class;
		if ( instances.containsKey( mapCacheClass ) )
			return (MapCache)instances.get( mapCacheClass );
		instances.put(
				mapCacheClass,
				new MapCache( repaintEvents ) );
		return mapCache( repaintEvents );
	}


	private MapDispatch mapDispatch(
			Queue<AtcEventGameStarted> events,
			Queue<HaoEvent> repaintEvents
	) {
		return mapDispatch( mapCache( repaintEvents ), events );
	}


	private MapDispatch mapDispatch(
			MapCache mapCache,
			Queue<AtcEventGameStarted> events
	) {
		Class<?> mapDispatchClass = MapDispatch.class;
		if ( instances.containsKey( mapDispatchClass ) )
			return (MapDispatch)instances.get( mapDispatchClass );
		instances.put(
				mapDispatchClass,
				new MapDispatch( mapCache, events ) );
		return mapDispatch( mapCache, events );
	}


	private MapMapper mapMapper(
	) {
		Class<?> mapMapperClass = MapMapper.class;
		if ( instances.containsKey( mapMapperClass ) )
			return (MapMapper)instances.get( mapMapperClass );
		instances.put(
				mapMapperClass,
				new MapMapper( airportMapper(), nodeMapper() ) );
		return mapMapper();
	}


	private NodeMapper nodeMapper(
	) {
		Class<?> nodeMapperClass = NodeMapper.class;
		if ( instances.containsKey( nodeMapperClass ) )
			return (NodeMapper)instances.get( nodeMapperClass );
		instances.put(
				nodeMapperClass,
				new NodeMapper() );
		return nodeMapper();
	}


	private PointMapper pointMapper(
	) {
		Class<?> pointMapperClass = PointMapper.class;
		if ( instances.containsKey( pointMapperClass ) )
			return (PointMapper)instances.get( pointMapperClass );
		instances.put(
				pointMapperClass,
				new PointMapper() );
		return pointMapper();
	}


	private StdOutAdapter stdOutAdapter(
			StdOutEndpoint stdOutEndpoint,
			Queue<String> messageIngress,
			Queue<AtcEvent> atcEvents,
			Queue<Object> blobIngress
	) {
		Class<?> stdOutAdapterClass = StdOutAdapter.class;
		if ( instances.containsKey( stdOutAdapterClass ) )
			return (StdOutAdapter)instances.get( stdOutAdapterClass );
		instances.put(
				stdOutAdapterClass,
				new StdOutAdapter(
						stdOutEndpoint,
						messageIngress,
						atcEvents,
						blobIngress ) );
		return stdOutAdapter(
				stdOutEndpoint,
				messageIngress,
				atcEvents,
				blobIngress );
	}


	private StdOutEndpoint stdOutEndpoint(
			Queue<String> messageIngress
	) {
		Class<?> stdOutEndpointClass = StdOutEndpoint.class;
		if ( instances.containsKey( stdOutEndpointClass ) )
			return (StdOutEndpoint)instances.get( stdOutEndpointClass );
		instances.put(
				stdOutEndpointClass,
				new StdOutEndpoint( messageIngress ) );
		return stdOutEndpoint( messageIngress );
	}


	private TagMapper tagMapper(
	) {
		Class<?> tagMapperClass = TagMapper.class;
		if ( instances.containsKey( tagMapperClass ) )
			return (TagMapper)instances.get( tagMapperClass );
		instances.put(
				tagMapperClass,
				new TagMapper() );
		return tagMapper();
	}

}
