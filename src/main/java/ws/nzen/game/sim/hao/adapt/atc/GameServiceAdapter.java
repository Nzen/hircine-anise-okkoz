
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.adapt.atc;


import atc.v1.Game.GetGameStateRequest;
import atc.v1.Game.StartGameRequest;

import java.util.Queue;

import ws.nzen.game.sim.hao.uses.atc.ManagesGameState;


/**

*/
public class GameServiceAdapter implements ManagesGameState
{

	private final GameServiceEndpoint gameService;
	private final Queue<GetGameStateRequest> gameStateRequests;
	private final Queue<StartGameRequest> startGameRequests;
	private final Thread runsGameService;


	public GameServiceAdapter(
			GameServiceEndpoint endpoint,
			Queue<GetGameStateRequest> forGameStateRequests,
			Queue<StartGameRequest> forStartGameRequests
	) {
		if ( endpoint == null )
			throw new NullPointerException( "endpoint must not be null" );
		else if ( forGameStateRequests == null )
			throw new NullPointerException( "game state msg in must not be null" );
		else if ( forStartGameRequests == null )
			throw new NullPointerException( "game start msg in must not be null" );
		gameService = endpoint;
		gameStateRequests = forGameStateRequests;
		startGameRequests = forStartGameRequests;
		runsGameService = new Thread( endpoint );
		runsGameService.start();
	}


	@Override
	public void quit(
	) {
		runsGameService.interrupt();
	}


	@Override
	public void requestGameState(
	) {
		gameStateRequests.offer( GetGameStateRequest.newBuilder().build() );
	}


	@Override
	public void startGame(
	) {
		startGameRequests.offer( StartGameRequest.newBuilder().build() );
	}


}


















