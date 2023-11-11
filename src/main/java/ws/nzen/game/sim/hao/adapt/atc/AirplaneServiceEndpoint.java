/** &copy; beyondRelations, LLC */
package ws.nzen.game.sim.hao.adapt.atc;

import java.util.Queue;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ws.nzen.game.sim.hao.service.HaoConstants;
import ws.nzen.game.sim.hao.uses.any.Quittable;

import atc.v1.AirplaneOuterClass.UpdateFlightPlanRequest;
import atc.v1.AirplaneOuterClass.UpdateFlightPlanResponse;
import atc.v1.AirplaneServiceGrpc;
import atc.v1.AirplaneServiceGrpc.AirplaneServiceStub;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;


/** Directly, asynchronously requests flight plan changes. */
public class AirplaneServiceEndpoint implements Runnable, Quittable
{

	private static final Logger log = LoggerFactory
			.getLogger( AirplaneServiceEndpoint.class );

	private boolean quit = false;
	private Channel channel;
	private AirplaneServiceStub airplaneService;
	private int millisecondsToSleep = HaoConstants.queueDelayMilliseconds;
	private final Queue<UpdateFlightPlanRequest> flightPlanRequests;
	private final Queue<UpdateFlightPlanResponse> flightPlanResponses;


	public AirplaneServiceEndpoint(
			String host,
			int port,
			Queue<UpdateFlightPlanRequest> flightPlanRequests,
			Queue<UpdateFlightPlanResponse> flightPlanResponses
	) {
		this(
				ManagedChannelBuilder.forAddress( host, port ).usePlaintext(),
				flightPlanRequests,
				flightPlanResponses
		);
	}


	public AirplaneServiceEndpoint(
			ManagedChannelBuilder<?> channelBuilder,
			Queue<UpdateFlightPlanRequest> flightPlanRequests,
			Queue<UpdateFlightPlanResponse> flightPlanResponses
	) {
		if ( channelBuilder == null )
			throw new NullPointerException( "grpc channel builder must not be null" );
		else if ( flightPlanRequests == null )
			throw new NullPointerException( "flightPlanRequests must not be null" );
		else if ( flightPlanResponses == null )
			throw new NullPointerException( "flightPlanResponses must not be null" );
		channel = channelBuilder.build();
		airplaneService = AirplaneServiceGrpc.newStub( channel );
		this.flightPlanRequests = flightPlanRequests;
		this.flightPlanResponses = flightPlanResponses;
	}

	
	/** @see ws.nzen.game.sim.hao.uses.any.Quittable#quit()  */
	@Override
	public void quit(
	) {
		quit = true;

	}


	public void requestFlightPlan(
			UpdateFlightPlanRequest flightPlanRequest
	) {
		final int timesToTry = 1;
		final CountDownLatch finishLatch = new CountDownLatch( timesToTry );
		StreamObserver<UpdateFlightPlanResponse> callback
				= new StreamObserver<UpdateFlightPlanResponse>()
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
					UpdateFlightPlanResponse event
			) {
				flightPlanResponses.offer( event );
			}
		};

		airplaneService.updateFlightPlan( flightPlanRequest, callback );
	}


	/** @see java.lang.Runnable#run()  */
	@Override
	public void run(
	) {
		try
		{
			while ( true )
			{
				while ( ! flightPlanRequests.isEmpty() )
					requestFlightPlan( flightPlanRequests.poll() );

				Thread.sleep( millisecondsToSleep );
				if ( quit )
					return;
			}
		}
		catch ( InterruptedException ie )
		{
			return;
		}

	}

}


















