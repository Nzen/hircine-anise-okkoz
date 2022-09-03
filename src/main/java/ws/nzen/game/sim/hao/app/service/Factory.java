
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.app.service;


import java.util.Queue;

import atc.v1.Event.*;
import atc.v1.Game.*;
import ws.nzen.game.sim.hao.adapt.atc.*;
import ws.nzen.game.sim.hao.adapt.cli.*;
import ws.nzen.game.sim.hao.game.*;
import ws.nzen.game.sim.hao.uses.atc.*;
import ws.nzen.game.sim.hao.uses.view.*;


/**

*/
public class Factory
{

	public static AirplaneMapper airplaneMapper(
	) {
		return new AirplaneMapper(
				nodeMapper(),
				pointMapper(),
				tagMapper()
		);
	}


	public static AirportMapper airportMapper(
	) {
		return new AirportMapper( nodeMapper(), tagMapper() );
	}


	public static EventServiceAdapter eventServiceAdapter(
			EventServiceEndpoint eventStream,
			EventMapper mapper,
			Queue<StreamRequest> forRequests,
			Queue<StreamResponse> forResponses,
			Queue<AtcEvent> atcEvents,
			Queue<AtcEventGameStarted> gameStartEvents
	) {
		return new EventServiceAdapter(
				eventStream,
				mapper,
				forRequests,
				forResponses,
				atcEvents,
				gameStartEvents
		);
	}


	public static EventServiceEndpoint eventServiceEndpoint(
			String host,
			int port,
			Queue<StreamRequest> forRequests,
			Queue<StreamResponse> forResponses
	) {
		return new EventServiceEndpoint(
				host,
				port,
				forRequests,
				forResponses
		);
	}


	public static EventMapper eventMapper(
	) {
		return new EventMapper(
				airplaneMapper(),
				mapMapper(),
				nodeMapper(),
				pointMapper()
		);
	}


	public static GameServiceAdapter gameServiceAdapter(
			GameServiceEndpoint endpoint,
			Queue<GetGameStateRequest> forGameStateRequests,
			Queue<StartGameRequest> forStartGameRequests
	) {
		return new GameServiceAdapter(
				endpoint,
				forGameStateRequests,
				forStartGameRequests
		);
	}


	public static GameServiceEndpoint gameServiceEndpoint(
			String host,
			int port,
			Queue<GetGameStateRequest> forGameStateRequests,
			Queue<GetGameStateResponse> forGameStateResponses,
			Queue<StartGameRequest> forStartGameRequests,
			Queue<StartGameResponse> forStartGameResponses
	) {
		return new GameServiceEndpoint(
				host,
				port,
				forGameStateRequests,
				forGameStateResponses,
				forStartGameRequests,
				forStartGameResponses
		);
	}


	public static ManagesGameState managesGameState(
			String host,
			int port,
			Queue<GetGameStateRequest> forGameStateRequests,
			Queue<GetGameStateResponse> forGameStateResponses,
			Queue<StartGameRequest> forStartGameRequests,
			Queue<StartGameResponse> forStartGameResponses
	) {
		GameServiceEndpoint endpoint = gameServiceEndpoint(
				host,
				port,
				forGameStateRequests,
				forGameStateResponses,
				forStartGameRequests,
				forStartGameResponses
		);
		return new GameServiceAdapter(
				endpoint,
				forGameStateRequests,
				forStartGameRequests
		);
	}


	public static MapDispatch mapDispatch(
			Queue<AtcEventGameStarted> events
	) {
		return new MapDispatch( new MapCache(), events );
	}


	public static MapDispatch mapDispatch(
			MapCache mapCache,
			Queue<AtcEventGameStarted> events
	) {
		return new MapDispatch( mapCache, events );
	}


	public static MapMapper mapMapper(
	) {
		return new MapMapper( airportMapper(), nodeMapper() );
	}


	public static NodeMapper nodeMapper(
	) {
		return new NodeMapper();
	}


	public static PointMapper pointMapper(
	) {
		return new PointMapper();
	}


	public static RequestsEvents requestsEvents(
			String host,
			int port,
			Queue<StreamRequest> forRequests,
			Queue<StreamResponse> forResponses,
			Queue<AtcEvent> atcEvents,
			Queue<AtcEventGameStarted> gameStartEvents
	) {
		EventServiceEndpoint endpoint = eventServiceEndpoint(
				host,
				port,
				forRequests,
				forResponses
		);
		return eventServiceAdapter(
				endpoint,
				eventMapper(),
				forRequests,
				forResponses,
				atcEvents,
				gameStartEvents
		);
	}


	public static ShowsEvents showsEvents(
			Queue<String> messageIngress,
			Queue<? extends Object> blobIngress
	) {
		return stdOutAdapter(
				stdOutEndpoint( messageIngress ),
				messageIngress,
				blobIngress );
	}


	public static StdOutAdapter stdOutAdapter(
			StdOutEndpoint stdOutEndpoint,
			Queue<String> messageIngress,
			Queue<? extends Object> blobIngress
	) {
		return new StdOutAdapter( stdOutEndpoint, messageIngress, blobIngress );
	}


	public static StdOutEndpoint stdOutEndpoint(
			Queue<String> messageIngress
	) {
		return new StdOutEndpoint( messageIngress );
	}


	public static TagMapper tagMapper(
	) {
		return new TagMapper();
	}

}
