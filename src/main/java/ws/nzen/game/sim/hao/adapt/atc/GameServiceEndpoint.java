
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.adapt.atc;


import atc.v1.Game.GetGameStateRequest;
import atc.v1.Game.GetGameStateResponse;
import atc.v1.Game.StartGameRequest;
import atc.v1.Game.StartGameResponse;
import atc.v1.GameServiceGrpc;
import atc.v1.GameServiceGrpc.GameServiceStub;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Queue;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**

*/
public class GameServiceEndpoint implements Runnable
{

	private static final Logger log = LoggerFactory
			.getLogger( GameServiceEndpoint.class );

	private Channel channel;
	private GameServiceStub gameService;
	private int millisecondsToSleep = 200;
	private final Queue<GetGameStateRequest> gameStateRequests;
	private final Queue<GetGameStateResponse> gameStateResponses;
	private final Queue<StartGameRequest> startGameRequests;
	private final Queue<StartGameResponse> startGameResponses;


	public GameServiceEndpoint(
			String host,
			int port,
			Queue<GetGameStateRequest> forGameStateRequests,
			Queue<GetGameStateResponse> forGameStateResponses,
			Queue<StartGameRequest> forStartGameRequests,
			Queue<StartGameResponse> forStartGameResponses
	) {
		this( ManagedChannelBuilder.forAddress(host, port).usePlaintext(),
				forGameStateRequests,
				forGameStateResponses,
				forStartGameRequests,
				forStartGameResponses );
	}


	public GameServiceEndpoint(
			ManagedChannelBuilder<?> channelBuilder,
			Queue<GetGameStateRequest> forGameStateRequests,
			Queue<GetGameStateResponse> forGameStateResponses,
			Queue<StartGameRequest> forStartGameRequests,
			Queue<StartGameResponse> forStartGameResponses
	) {
		if ( channelBuilder == null )
			throw new NullPointerException( "grpc channel builder must not be null" );
		else if ( forGameStateRequests == null )
			throw new NullPointerException( "game state msg in must not be null" );
		else if ( forGameStateResponses == null )
			throw new NullPointerException( "game state msg out must not be null" );
		else if ( forStartGameRequests == null )
			throw new NullPointerException( "game start msg in must not be null" );
		else if ( forStartGameResponses == null )
			throw new NullPointerException( "game start msg out must not be null" );
		channel = channelBuilder.build();
		gameService = GameServiceGrpc.newStub( channel );
		gameStateRequests = forGameStateRequests;
		gameStateResponses = forGameStateResponses;
		startGameRequests = forStartGameRequests;
		startGameResponses = forStartGameResponses;
	}


	public void requestGameState(
	) {
		requestGameState( GetGameStateRequest.newBuilder().build() );
	}


	public void requestGameState(
			GetGameStateRequest request
	) {
		final int timesToTry = 1;
		final CountDownLatch finishLatch = new CountDownLatch( timesToTry );
		StreamObserver<GetGameStateResponse> callback = new StreamObserver<GetGameStateResponse>()
		{
			@Override
			public void onCompleted(
			) {
				finishLatch.countDown();
			}

			@Override
			public void onError(
					Throwable problem
			) {
				log.error( problem.toString() );
				onCompleted();
			}

			@Override
			public void onNext(
					GetGameStateResponse event
			) {
				gameStateResponses.offer( event );
			}
		};

		gameService.getGameState( request, callback );
	}


	public void requestStartGame(
	) {
		requestStartGame( StartGameRequest.newBuilder().build() );
	}


	public void requestStartGame(
			StartGameRequest request
	) {
		final int timesToTry = 1;
		final CountDownLatch finishLatch = new CountDownLatch( timesToTry );
		StreamObserver<StartGameResponse> callback = new StreamObserver<StartGameResponse>()
		{
			@Override
			public void onCompleted(
			) {
				finishLatch.countDown();
			}

			@Override
			public void onError(
					Throwable problem
			) {
				log.error( problem.toString() );
				onCompleted();
			}

			@Override
			public void onNext(
					StartGameResponse event
			) {
				startGameResponses.offer( event );
			}
		};

		gameService.startGame( request, callback );
	}


	/** Check for requests, send a null request to quit */
	@Override
	public void run(
	) {
		try
		{
			while ( true )
			{
				if ( ! gameStateRequests.isEmpty() )
				{
					GetGameStateRequest gameStateRequest = gameStateRequests.poll();
					requestGameState( gameStateRequest );
				}

				if ( ! startGameRequests.isEmpty() )
				{
					StartGameRequest gameStartRequest = startGameRequests.poll();
					requestStartGame( gameStartRequest );
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
































