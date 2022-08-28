
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.app.service;

import java.util.Queue;

import atc.v1.Game.GetGameStateRequest;
import atc.v1.Game.GetGameStateResponse;
import atc.v1.Game.StartGameRequest;
import atc.v1.Game.StartGameResponse;
import ws.nzen.game.sim.hao.adapt.atc.GameServiceAdapter;
import ws.nzen.game.sim.hao.adapt.atc.GameServiceEndpoint;
import ws.nzen.game.sim.hao.adapt.cli.StdOutAdapter;
import ws.nzen.game.sim.hao.adapt.cli.StdOutEndpoint;
import ws.nzen.game.sim.hao.uses.atc.ManagesGameState;

/**

*/
public class Factory
{


	public static GameServiceAdapter gameServiceAdapter(
			GameServiceEndpoint endpoint,
			Queue<GetGameStateRequest> forGameStateRequests,
			Queue<StartGameRequest> forStartGameRequests
	) {
		return new GameServiceAdapter(
				endpoint,
				forGameStateRequests,
				forStartGameRequests );
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
				forStartGameResponses );
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
				forStartGameResponses );
		return new GameServiceAdapter(
				endpoint,
				forGameStateRequests,
				forStartGameRequests );
	}


	public static StdOutAdapter stdOutAdapter(
			Queue<String> messageIngress
	) {
		return new StdOutAdapter( messageIngress );
	}


	public static StdOutEndpoint stdOutEndpoint(
			Queue<String> messageIngress
	) {
		return new StdOutEndpoint( messageIngress );
	}


}






























