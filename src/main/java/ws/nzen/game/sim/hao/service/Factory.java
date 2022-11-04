
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.service;


import java.awt.Rectangle;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import atc.v1.Atc.GetVersionRequest;
import atc.v1.Atc.GetVersionResponse;
import atc.v1.Event.*;
import atc.v1.Game.*;
import atc.v1.MapOuterClass.NodeToPointRequest;
import atc.v1.MapOuterClass.NodeToPointResponse;

import ws.nzen.game.adventure.mhc.message.*;
import ws.nzen.game.sim.hao.adapt.atc.*;
import ws.nzen.game.sim.hao.adapt.cli.*;
import ws.nzen.game.sim.hao.adapt.mhc.*;
import ws.nzen.game.sim.hao.game.*;
import ws.nzen.game.sim.hao.uses.atc.*;
import ws.nzen.game.sim.hao.uses.view.*;


/**
Creates (singletons) of anything.
*/
@SuppressWarnings( value="unchecked" )
public class Factory
{

	private final Map<Class<?>, Object> instances = new HashMap<>();
	private final Map<String, Queue<?>> queues = new HashMap<>();


	public BookendsGames bookendsGames(
			KnowsAirplanes knowsAirplanes,
			KnowsMap knowsMap,
			int portNumber
	) {
		return canvasAdapter(
				boardMapper(),
				canvasEndpoint( portNumber, queueCanvasQuit(), queueCanvasConnected() ),
				knowsAirplanes,
				knowsMap,
				queueRepaintHaoEvent(),
				queueHaoEventEndGame(),
				queueHaoMessageStartGame(),
				queueCanvasStart(),
				queueCanvasQuit(),
				queueViewConnected(),
				queueCanvasConnected() );
	}


	public KnowsAirplanesRunnably knowsAirplanesRunnably(
	) {
		return airplaneDispatch(
				airplaneCache( queueRepaintHaoEvent() ),
				queueAtcEventAirplaneDetected(),
				queueAtcEventFlightChanged() );
	}


	public KnowsAtcVersion knowsAtcVersion(
			String host,
			int port
	) {
		return atcServiceAdapter(
				atcServiceEndpoint(
						host,
						port,
						queueGetVersionRequest(),
						queueGetVersionResponse() ),
				gameVersionMapper(),
				queueGetVersionRequest(),
				queueGetVersionResponse(),
				queueAtcGameVersion(),
				queueGetAtcVersionRequest() );
	}


	public KnowsNodes knowsNodes(
			String host,
			int port
	) {
		return mapServiceAdapter(
				mapMapper(),
				mapServiceEndpoint(
						host,
						port,
						queueNodeToPointRequests(),
						queueNodeToPointResponses() ),
				nodeMapper(),
				pointMapper(),
				queueNodeToPointRequests(),
				queueNodeToPointResponses(),
				queueNodeLocationRequests(),
				queueNodeLocationResponses() );
	}


	public KnowsMapRunnably knowsMap(
	) {
		return mapDispatch(
				queueAtcEventGameStarted(),
				queueRepaintHaoEvent(),
				queueNodesToZone(),
				queueZonedNodes() );
	}


	public LocatesNodes locatesNodes(
	) {
		return zonesMap(
				queueNodesToZone(),
				queueZonedNodes(),
				queueNodeLocationRequests(),
				queueNodeLocationResponses() );
	}


	public ManagesGameState managesGameState(
			String host,
			int port
	) {
		return gameServiceAdapter(
				gameServiceEndpoint(
						host,
						port,
						queueGetGameStateRequest(),
						queueGetGameStateResponse(),
						queueStartGameRequest(),
						queueStartGameResponse() ),
				queueGetGameStateRequest(),
				queueGetGameStateResponse(),
				queueStartGameRequest(),
				queueStartGameResponse(),
				queueHaoMessageStartGame()
		);
	}


	public Queue<AtcEventAirplaneDetected> queueAtcEventAirplaneDetected(
	) {
		final String name = "queueAtcEventAirplaneDetected";
		if ( ! queues.containsKey( name ) )
			queues.put( name, new ConcurrentLinkedQueue<AtcEventAirplaneDetected>() );
		return (Queue<AtcEventAirplaneDetected>)queues.get( name );
	}


	public Queue<AtcEventFlightPlanUpdated> queueAtcEventFlightChanged(
	) {
		final String name = "queueAtcEventFlightChanged";
		if ( ! queues.containsKey( name ) )
			queues.put( name, new ConcurrentLinkedQueue<AtcEventFlightPlanUpdated>() );
		return (Queue<AtcEventFlightPlanUpdated>)queues.get( name );
	}


	public Queue<AtcEventGameStarted> queueAtcEventGameStarted(
	) {
		final String name = "queueAtcEventGameStarted";
		if ( ! queues.containsKey( name ) )
			queues.put( name, new ConcurrentLinkedQueue<AtcEventGameStarted>() );
		return (Queue<AtcEventGameStarted>)queues.get( name );
	}


	public Queue<AtcEventGameStopped> queueAtcEventGameStopped(
	) {
		final String name = "queueAtcEventGameStopped";
		if ( ! queues.containsKey( name ) )
			queues.put( name, new ConcurrentLinkedQueue<AtcEventGameStopped>() );
		return (Queue<AtcEventGameStopped>)queues.get( name );
	}


	public Queue<AtcGameVersion> queueAtcGameVersion(
	) {
		final String name = "queueAtcGameVersion";
		if ( ! queues.containsKey( name ) )
			queues.put( name, new ConcurrentLinkedQueue<AtcGameVersion>() );
		return (Queue<AtcGameVersion>)queues.get( name );
	}


	public Queue<Move> queueCanvasConnected(
	) {
		final String name = "queueCanvasConnected";
		if ( ! queues.containsKey( name ) )
			queues.put( name, new ConcurrentLinkedQueue<Move>() );
		return (Queue<Move>)queues.get( name );
	}


	public Queue<Quit> queueCanvasQuit(
	) {
		final String nameCanvasQuit = "queueCanvasQuit";
		if ( ! queues.containsKey( nameCanvasQuit ) )
			queues.put( nameCanvasQuit, new ConcurrentLinkedQueue<Quit>() );
		return (Queue<Quit>)queues.get( nameCanvasQuit );
	}


	public Queue<MhcMessage> queueCanvasStart(
	) {
		final String nameCanvasStart = "queueCanvasStart";
		if ( ! queues.containsKey( nameCanvasStart ) )
			queues.put( nameCanvasStart, new ConcurrentLinkedQueue<MhcMessage>() );
		return (Queue<MhcMessage>)queues.get( nameCanvasStart );
	}


	public Queue<HaoMessage> queueGetAtcVersionRequest(
	) {
		final String name = "queueGetAtcVersionRequest";
		if ( ! queues.containsKey( name ) )
			queues.put( name, new ConcurrentLinkedQueue<HaoMessage>() );
		return (Queue<HaoMessage>)queues.get( name );
	}


	public Queue<GetGameStateRequest> queueGetGameStateRequest(
	) {
		final String nameGetGameStateRequest = "queueGetGameStateRequest";
		if ( ! queues.containsKey( nameGetGameStateRequest ) )
			queues.put( nameGetGameStateRequest, new ConcurrentLinkedQueue<GetGameStateRequest>() );
		return (Queue<GetGameStateRequest>)queues.get( nameGetGameStateRequest );
	}


	public Queue<GetGameStateResponse> queueGetGameStateResponse(
	) {
		final String nameGetGameStateResponse = "queueGetGameStateResponse";
		if ( ! queues.containsKey( nameGetGameStateResponse ) )
			queues.put( nameGetGameStateResponse, new ConcurrentLinkedQueue<GetGameStateResponse>() );
		return (Queue<GetGameStateResponse>)queues.get( nameGetGameStateResponse );
	}


	public Queue<GetVersionRequest> queueGetVersionRequest(
	) {
		final String name = "queueGetVersionRequest";
		if ( ! queues.containsKey( name ) )
			queues.put( name, new ConcurrentLinkedQueue<GetVersionRequest>() );
		return (Queue<GetVersionRequest>)queues.get( name );
	}


	public Queue<GetVersionResponse> queueGetVersionResponse(
	) {
		final String name = "queueGetVersionResponse";
		if ( ! queues.containsKey( name ) )
			queues.put( name, new ConcurrentLinkedQueue<GetVersionResponse>() );
		return (Queue<GetVersionResponse>)queues.get( name );
	}


	public Queue<HaoEvent> queueHaoEventEndGame(
	) {
		final String name = "queueHaoEventEndGame";
		if ( ! queues.containsKey( name ) )
			queues.put( name, new ConcurrentLinkedQueue<HaoEvent>() );
		return (Queue<HaoEvent>)queues.get( name );
	}


	public Queue<HaoMessage> queueHaoMessageStartGame(
	) {
		final String name = "queueHaoMessageStartGame";
		if ( ! queues.containsKey( name ) )
			queues.put( name, new ConcurrentLinkedQueue<HaoMessage>() );
		return (Queue<HaoMessage>)queues.get( name );
	}


	public Queue<AtcEvent> queueOtherAtcEvent(
	) {
		final String name = "queueOtherAtcEvent";
		if ( ! queues.containsKey( name ) )
			queues.put( name, new ConcurrentLinkedQueue<AtcEvent>() );
		return (Queue<AtcEvent>)queues.get( name );
	}


	public Queue<Map<AtcRoutingNode, Rectangle>> queueZonedNodes(
	) {
		final String name = "queueZonedNodes";
		if ( ! queues.containsKey( name ) )
			queues.put( name, new ConcurrentLinkedQueue<Map<AtcRoutingNode, Rectangle>>() );
		return (Queue<Map<AtcRoutingNode, Rectangle>>)queues.get( name );
	}


	public Queue<AtcRoutingNode> queueNodeLocationRequests(
	) {
		final String name = "queueNodeLocationRequests";
		if ( ! queues.containsKey( name ) )
			queues.put( name, new ConcurrentLinkedQueue<Collection<AtcRoutingNode>>() );
		return (Queue<AtcRoutingNode>)queues.get( name );
	}


	public Queue<Collection<AtcRoutingNode>> queueNodesToZone(
	) {
		final String name = "queueNodesToZone";
		if ( ! queues.containsKey( name ) )
			queues.put( name, new ConcurrentLinkedQueue<Collection<AtcRoutingNode>>() );
		return (Queue<Collection<AtcRoutingNode>>)queues.get( name );
	}


	public Queue<AtcNodePoint> queueNodeLocationResponses(
	) {
		final String name = "queueNodeLocationResponses";
		if ( ! queues.containsKey( name ) )
			queues.put( name, new ConcurrentLinkedQueue<AtcNodePoint>() );
		return (Queue<AtcNodePoint>)queues.get( name );
	}


	public Queue<NodeToPointRequest> queueNodeToPointRequests(
	) {
		final String name = "queueNodeToPointRequest";
		if ( ! queues.containsKey( name ) )
			queues.put( name, new ConcurrentLinkedQueue<NodeToPointRequest>() );
		return (Queue<NodeToPointRequest>)queues.get( name );
	}


	public Queue<NodeToPointResponse> queueNodeToPointResponses(
	) {
		final String name = "queueNodeToPointResponse";
		if ( ! queues.containsKey( name ) )
			queues.put( name, new ConcurrentLinkedQueue<NodeToPointResponse>() );
		return (Queue<NodeToPointResponse>)queues.get( name );
	}


	public Queue<HaoEvent> queueRepaintHaoEvent(
	) {
		final String name = "queueRepaintHaoEvent";
		if ( ! queues.containsKey( name ) )
			queues.put( name, new ConcurrentLinkedQueue<HaoEvent>() );
		return (Queue<HaoEvent>)queues.get( name );
	}


	public Queue<StartGameRequest> queueStartGameRequest(
	) {
		final String nameStartGameRequest = "queueStartGameRequest";
		if ( ! queues.containsKey( nameStartGameRequest ) )
			queues.put( nameStartGameRequest, new ConcurrentLinkedQueue<StartGameRequest>() );
		return (Queue<StartGameRequest>)queues.get( nameStartGameRequest );
	}


	public Queue<StartGameResponse> queueStartGameResponse(
	) {
		final String nameStartGameResponse = "queueStartGameResponse";
		if ( ! queues.containsKey( nameStartGameResponse ) )
			queues.put( nameStartGameResponse, new ConcurrentLinkedQueue<StartGameResponse>() );
		return (Queue<StartGameResponse>)queues.get( nameStartGameResponse );
	}


	public Queue<StreamRequest> queueStreamRequest(
	) {
		final String name = "queueStreamRequest";
		if ( ! queues.containsKey( name ) )
			queues.put( name, new ConcurrentLinkedQueue<StreamRequest>() );
		return (Queue<StreamRequest>)queues.get( name );
	}


	public Queue<StreamResponse> queueStreamResponse(
	) {
		final String name = "queueStreamResponse";
		if ( ! queues.containsKey( name ) )
			queues.put( name, new ConcurrentLinkedQueue<StreamResponse>() );
		return (Queue<StreamResponse>)queues.get( name );
	}


	public Queue<String> queueString(
	) {
		final String name = "queueString";
		if ( ! queues.containsKey( name ) )
			queues.put( name, new ConcurrentLinkedQueue<String>() );
		return (Queue<String>)queues.get( name );
	}


	public Queue<HaoMessage> queueStartEventStream(
	) {
		final String name = "queueStartEventStream";
		if ( ! queues.containsKey( name ) )
			queues.put( name, new ConcurrentLinkedQueue<HaoMessage>() );
		return (Queue<HaoMessage>)queues.get( name );
	}


	public Queue<HaoMessage> queueViewConnected(
	) {
		final String name = "queueViewConnected";
		if ( ! queues.containsKey( name ) )
			queues.put( name, new ConcurrentLinkedQueue<HaoMessage>() );
		return (Queue<HaoMessage>)queues.get( name );
	}
/*


	public Queue<> queue(
	) {
		final String name = "queue";
		if ( ! queues.containsKey( name ) )
			queues.put( name, new ConcurrentLinkedQueue<>() );
		return (Queue<>)queues.get( name );
	}
*/


	public RequestsEvents requestsEvents(
			String host,
			int port
	) {
		return eventServiceAdapter(
				eventServiceEndpoint(
						host,
						port,
						queueStreamRequest(),
						queueStreamResponse() ),
				eventMapper(),
				queueStreamRequest(),
				queueStreamResponse(),
				queueOtherAtcEvent(),
				queueAtcEventGameStarted(),
				queueAtcEventAirplaneDetected(),
				queueStartEventStream(),
				queueAtcEventGameStopped(),
				queueAtcEventFlightChanged() );
	}


	public ShowsEvents showsEvents(
	) {
		return stdOutAdapter(
				stdOutEndpoint( queueString() ),
				queueString(),
				queueOtherAtcEvent() );
	}


	public ShowsMap showsMap(
			KnowsAirplanes knowsAirplanes,
			KnowsMap knowsMap,
			int portNumber
	) {
		return canvasAdapter(
				boardMapper(),
				canvasEndpoint( portNumber, queueCanvasQuit(), queueCanvasConnected() ),
				knowsAirplanes,
				knowsMap,
				queueRepaintHaoEvent(),
				queueHaoEventEndGame(),
				queueHaoMessageStartGame(),
				queueCanvasStart(),
				queueCanvasQuit(),
				queueViewConnected(),
				queueCanvasConnected() );
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
			Queue<AtcEventAirplaneDetected> atcEventsAirplaneDetected,
			Queue<AtcEventFlightPlanUpdated> aeFlightChanged
	) {
		Class<?> airplaneDispatchClass = AirplaneDispatch.class;
		if ( instances.containsKey( airplaneDispatchClass ) )
			return (AirplaneDispatch)instances.get( airplaneDispatchClass );
		instances.put(
				airplaneDispatchClass,
				new AirplaneDispatch(
						airplaneCache,
						atcEventsAirplaneDetected,
						aeFlightChanged ) );
		return airplaneDispatch(
				airplaneCache,
				atcEventsAirplaneDetected,
				aeFlightChanged );
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


	private AtcServiceAdapter atcServiceAdapter(
			AtcServiceEndpoint gameVersionStream,
			GameVersionMapper mapper,
			Queue<GetVersionRequest> gameVersionRequests,
			Queue<GetVersionResponse> gameVersionResponses,
			Queue<AtcGameVersion> gameVersions,
			Queue<HaoMessage> checkVersionRequests
	) {
		Class<?> atcServiceAdapterClass = AtcServiceAdapter.class;
		if ( instances.containsKey( atcServiceAdapterClass ) )
			return (AtcServiceAdapter)instances.get( atcServiceAdapterClass );
		instances.put(
				atcServiceAdapterClass,
				new AtcServiceAdapter(
						gameVersionStream,
						mapper,
						gameVersionRequests,
						gameVersionResponses,
						gameVersions,
						checkVersionRequests ) );
		return atcServiceAdapter(
				gameVersionStream,
				mapper,
				gameVersionRequests,
				gameVersionResponses,
				gameVersions,
				checkVersionRequests );
	}


	private AtcServiceEndpoint atcServiceEndpoint(
			String host,
			int port,
			Queue<GetVersionRequest> versionRequests,
			Queue<GetVersionResponse> versionResponses
	) {
		Class<?> atcServiceEndpointClass = AtcServiceEndpoint.class;
		if ( instances.containsKey( atcServiceEndpointClass ) )
			return (AtcServiceEndpoint)instances.get( atcServiceEndpointClass );
		instances.put(
				atcServiceEndpointClass,
				new AtcServiceEndpoint(
						host,
						port,
						versionRequests,
						versionResponses ) );
		return atcServiceEndpoint(
				host,
				port,
				versionRequests,
				versionResponses );
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
			Queue<HaoEvent> repaintInput,
			Queue<HaoEvent> endGameOutward,
			Queue<HaoMessage> startGameOutward,
			Queue<MhcMessage> startGameMhc,
			Queue<Quit> mhcQuitInput,
			Queue<HaoMessage> viewConnected,
			Queue<Move> mhcViewConnected
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
						repaintInput,
						endGameOutward,
						startGameOutward,
						startGameMhc,
						mhcQuitInput,
						viewConnected,
						mhcViewConnected ) );
		return canvasAdapter(
				boardMapper,
				canvasEndpoint,
				knowsAirplanes,
				knowsMap,
				repaintInput,
				endGameOutward,
				startGameOutward,
				startGameMhc,
				mhcQuitInput,
				viewConnected,
				mhcViewConnected );
	}


	private CanvasEndpoint canvasEndpoint(
			int portNumber,
			Queue<Quit> mhcQuitOutput,
			Queue<Move> mhcViewConnected
	) {
		Class<?> canvasEndpointClass = CanvasEndpoint.class;
		if ( instances.containsKey( canvasEndpointClass ) )
			return (CanvasEndpoint)instances.get( canvasEndpointClass );
		instances.put(
				canvasEndpointClass,
				new CanvasEndpoint( portNumber, mhcQuitOutput, mhcViewConnected ) );
		return canvasEndpoint( portNumber, mhcQuitOutput, mhcViewConnected );
	}


	private EventServiceAdapter eventServiceAdapter(
			EventServiceEndpoint atcEventStream,
			EventMapper mapper,
			Queue<StreamRequest> forRequests,
			Queue<StreamResponse> forResponses,
			Queue<AtcEvent> atcEvents,
			Queue<AtcEventGameStarted> gameStartEvents,
			Queue<AtcEventAirplaneDetected> atcEventsAirplaneDetected,
			Queue<HaoMessage> checkVersionRequests,
			Queue<AtcEventGameStopped> atcEndedGame,
			Queue<AtcEventFlightPlanUpdated> aeFlightChanged
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
						atcEventsAirplaneDetected,
						checkVersionRequests,
						atcEndedGame,
						aeFlightChanged ) );
		return eventServiceAdapter(
				atcEventStream,
				mapper,
				forRequests,
				forResponses,
				atcEvents,
				gameStartEvents,
				atcEventsAirplaneDetected,
				checkVersionRequests,
				atcEndedGame,
				aeFlightChanged );
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
			Queue<GetGameStateResponse> forGameStateResponses,
			Queue<StartGameRequest> forStartGameRequests,
			Queue<StartGameResponse> forStartGameResponses,
			Queue<HaoMessage> haoGameStartRequests
	) {
		Class<?> gameServiceAdapterClass = GameServiceAdapter.class;
		if ( instances.containsKey( gameServiceAdapterClass ) )
			return (GameServiceAdapter)instances.get( gameServiceAdapterClass );
		instances.put(
				gameServiceAdapterClass,
				new GameServiceAdapter(
						endpoint,
						forGameStateRequests,
						forGameStateResponses,
						forStartGameRequests,
						forStartGameResponses,
						haoGameStartRequests ) );
		return gameServiceAdapter(
				endpoint,
				forGameStateRequests,
				forGameStateResponses,
				forStartGameRequests,
				forStartGameResponses,
				haoGameStartRequests );
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


	private GameVersionMapper gameVersionMapper(
	) {
		Class<?> gameVersionMapperClass = GameVersionMapper.class;
		if ( instances.containsKey( gameVersionMapperClass ) )
			return (GameVersionMapper)instances.get( gameVersionMapperClass );
		instances.put(
				gameVersionMapperClass,
				new GameVersionMapper() );
		return gameVersionMapper();
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
			Queue<HaoEvent> repaintEvents,
			Queue<Collection<AtcRoutingNode>> nodesToZone,
			Queue<Map<AtcRoutingNode, Rectangle>> nodeZones
	) {
		return mapDispatch(
				mapCache( repaintEvents ),
				events,
				nodesToZone,
				nodeZones );
	}


	private MapDispatch mapDispatch(
			MapCache mapCache,
			Queue<AtcEventGameStarted> events,
			Queue<Collection<AtcRoutingNode>> nodesToZone,
			Queue<Map<AtcRoutingNode, Rectangle>> nodeZones
	) {
		Class<?> mapDispatchClass = MapDispatch.class;
		if ( instances.containsKey( mapDispatchClass ) )
			return (MapDispatch)instances.get( mapDispatchClass );
		instances.put(
				mapDispatchClass,
				new MapDispatch(
						mapCache,
						events,
						nodesToZone,
						nodeZones ) );
		return mapDispatch(
				mapCache,
				events,
				nodesToZone,
				nodeZones );
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


	private MapServiceAdapter mapServiceAdapter(
			MapMapper mapMapper,
			MapServiceEndpoint mapServiceEndpoint,
			NodeMapper nodeMapper,
			PointMapper pointMapper,
			Queue<NodeToPointRequest> nodeToPointRequests,
			Queue<NodeToPointResponse> nodeToPointResponses,
			Queue<AtcRoutingNode> nodeLocationRequests,
			Queue<AtcNodePoint> nodeLocationResponses
	) {
		Class<?> adapterClass = MapServiceAdapter.class;
		if ( instances.containsKey( adapterClass ) )
			return (MapServiceAdapter)instances.get( adapterClass );
		instances.put(
				adapterClass,
				new MapServiceAdapter(
						mapMapper,
						mapServiceEndpoint,
						nodeMapper,
						pointMapper,
						nodeToPointRequests,
						nodeToPointResponses,
						nodeLocationRequests,
						nodeLocationResponses ) );
		return mapServiceAdapter(
				mapMapper,
				mapServiceEndpoint,
				nodeMapper,
				pointMapper,
				nodeToPointRequests,
				nodeToPointResponses,
				nodeLocationRequests,
				nodeLocationResponses );
	}


	private MapServiceEndpoint mapServiceEndpoint(
			String host,
			int port,
			Queue<NodeToPointRequest> nodeToPointRequests,
			Queue<NodeToPointResponse> nodeToPointResponses
	) {
		Class<?> endpointClass = MapServiceEndpoint.class;
		if ( instances.containsKey( endpointClass ) )
			return (MapServiceEndpoint)instances.get( endpointClass );
		instances.put(
				endpointClass,
				new MapServiceEndpoint(
						host,
						port,
						nodeToPointRequests,
						nodeToPointResponses ) );
		return mapServiceEndpoint(
				host,
				port,
				nodeToPointRequests,
				nodeToPointResponses );
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
			Queue<AtcEvent> atcEvents
	) {
		Class<?> stdOutAdapterClass = StdOutAdapter.class;
		if ( instances.containsKey( stdOutAdapterClass ) )
			return (StdOutAdapter)instances.get( stdOutAdapterClass );
		instances.put(
				stdOutAdapterClass,
				new StdOutAdapter(
						stdOutEndpoint,
						messageIngress,
						atcEvents ) );
		return stdOutAdapter(
				stdOutEndpoint,
				messageIngress,
				atcEvents );
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


	private ZonesMap zonesMap(
			Queue<Collection<AtcRoutingNode>> nodesToZone,
			Queue<Map<AtcRoutingNode, Rectangle>> nodeZones,
			Queue<AtcRoutingNode> nodeToPointRequests,
			Queue<AtcNodePoint> nodeLocationResponses
	) {
		Class<?> theClass = ZonesMap.class;
		if ( instances.containsKey( theClass ) )
			return (ZonesMap)instances.get( theClass );
		instances.put(
				theClass,
				new ZonesMap(
						nodesToZone,
						nodeZones,
						nodeToPointRequests,
						nodeLocationResponses ) );
		return zonesMap(
				nodesToZone,
				nodeZones,
				nodeToPointRequests,
				nodeLocationResponses );
	}

}












