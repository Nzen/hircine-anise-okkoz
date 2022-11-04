
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.adapt.atc;


import atc.v1.Event.StreamRequest;
import atc.v1.Event.StreamResponse;
import atc.v1.EventServiceGrpc;
import atc.v1.EventServiceGrpc.EventServiceStub;

import java.util.Queue;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import ws.nzen.game.sim.hao.service.HaoConstants;
import ws.nzen.game.sim.hao.uses.any.Quittable;


/**

*/
public class EventServiceEndpoint implements Runnable, Quittable
{

	private static final Logger log = LoggerFactory
			.getLogger( EventServiceEndpoint.class );
	private boolean quit = false;
	private Channel channel;
	private EventServiceStub eventService;
	private int millisecondsToSleep = HaoConstants.queueDelayMilliseconds;
	private final Queue<StreamRequest> requests;
	private final Queue<StreamResponse> responses;


	public EventServiceEndpoint(
			String host,
			int port,
			Queue<StreamRequest> forRequests,
			Queue<StreamResponse> forResponses
	) {
		this(
				ManagedChannelBuilder.forAddress( host, port ).usePlaintext(),
				forRequests,
				forResponses
		);
	}


	public EventServiceEndpoint(
			ManagedChannelBuilder<?> channelBuilder,
			Queue<StreamRequest> forRequests,
			Queue<StreamResponse> forResponses
	) {
		if ( forRequests == null )
			throw new NullPointerException( "forRequests must not be null" );
		else if ( forResponses == null )
			throw new NullPointerException( "forResponses must not be null" );
		channel = channelBuilder.build();
		eventService = EventServiceGrpc.newStub( channel );
		requests = forRequests;
		responses = forResponses;
	}


	@Override
	public void quit(
	) {
		quit = true;
	}


	public void requestMoreEvents(
	) {
		requestMoreEvents( StreamRequest.newBuilder().build() );
	}


	public void requestMoreEvents(
			StreamRequest request
	) {
		final int timesToTry = 1;
		final CountDownLatch finishLatch = new CountDownLatch( timesToTry );
		StreamObserver<StreamResponse> firehose = new StreamObserver<StreamResponse>()
		{
			@Override
			public void onCompleted(
			) {
				System.out.println( "ese.rme-done" );
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
					StreamResponse event
			) {
				responses.offer( event );
			}
		};

		eventService.stream( request, firehose );
	}


	@Override
	/** Check for event requests, send a null request to quit */
	public void run(
	) {
		try
		{
			while ( true )
			{
				while ( ! requests.isEmpty() )
				{
					StreamRequest request = requests.poll();
					if ( request == null )
						break;
					requestMoreEvents( request );
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

}
