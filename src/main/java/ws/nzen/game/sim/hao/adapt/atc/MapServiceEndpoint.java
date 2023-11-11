
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.adapt.atc;


import atc.v1.MapOuterClass.Node;
import atc.v1.MapOuterClass.NodeToPointRequest;
import atc.v1.MapOuterClass.NodeToPointResponse;
import atc.v1.MapServiceGrpc;
import atc.v1.MapServiceGrpc.MapServiceStub;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Queue;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ws.nzen.game.sim.hao.service.HaoConstants;
import ws.nzen.game.sim.hao.uses.any.Quittable;


/**

*/
public class MapServiceEndpoint implements Runnable, Quittable
{

	private static final Logger log = LoggerFactory
			.getLogger( MapServiceEndpoint.class );

	private boolean quit = false;
	private Channel channel;
	private MapServiceStub mapService;
	private int millisecondsToSleep = HaoConstants.queueDelayMilliseconds;
	private final Queue<NodeToPointRequest> nodeToPointRequests;
	private final Queue<NodeToPointResponse> nodeToPointResponses;


	public MapServiceEndpoint(
			String host,
			int port,
			Queue<NodeToPointRequest> nodeToPointRequests,
			Queue<NodeToPointResponse> nodeToPointResponses
	) {
		this(
				ManagedChannelBuilder.forAddress( host, port ).usePlaintext(),
				nodeToPointRequests,
				nodeToPointResponses
		);
	}


	public MapServiceEndpoint(
			ManagedChannelBuilder<?> channelBuilder,
			Queue<NodeToPointRequest> nodeToPointRequests,
			Queue<NodeToPointResponse> nodeToPointResponses
	) {
		if ( channelBuilder == null )
			throw new NullPointerException( "grpc channel builder must not be null" );
		else if ( nodeToPointRequests == null )
			throw new NullPointerException( "nodeToPointRequests must not be null" );
		else if ( nodeToPointResponses == null )
			throw new NullPointerException( "nodeToPointResponses must not be null" );
		channel = channelBuilder.build();
		mapService = MapServiceGrpc.newStub( channel );
		this.nodeToPointRequests = nodeToPointRequests;
		this.nodeToPointResponses = nodeToPointResponses;
	}


	@Override
	public void quit(
	) {
		quit = true;
	}


	public void requestNodePoint(
			NodeToPointRequest request
	) {
		final int timesToTry = 1;
		final CountDownLatch finishLatch = new CountDownLatch( timesToTry );
		StreamObserver<NodeToPointResponse> callback = new StreamObserver<NodeToPointResponse>()
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
					NodeToPointResponse event
			) {
				nodeToPointResponses.offer( event );
			}
		};

		mapService.nodeToPoint( request, callback );
	}


	@Override
	/** Check for requests, send a null request to quit */
	public void run(
	) {
		try
		{
			while ( true )
			{
				while ( ! nodeToPointRequests.isEmpty() )
					requestNodePoint( nodeToPointRequests.poll() );

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


	void requestNodePoint(
			Node nodeToGetPointOf
	) {
		requestNodePoint( NodeToPointRequest.newBuilder()
				.setNode( nodeToGetPointOf ).build() );
	}

}


















