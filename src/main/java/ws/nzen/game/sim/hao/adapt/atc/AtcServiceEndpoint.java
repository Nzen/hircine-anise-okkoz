
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.adapt.atc;


import atc.v1.Atc.GetVersionRequest;
import atc.v1.Atc.GetVersionResponse;
import atc.v1.AtcServiceGrpc;
import atc.v1.AtcServiceGrpc.AtcServiceStub;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import ws.nzen.game.sim.hao.service.HaoConstants;
import ws.nzen.game.sim.hao.uses.any.Quittable;

import java.util.Queue;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**

*/
public class AtcServiceEndpoint implements Runnable, Quittable
{

	private static final Logger log = LoggerFactory
			.getLogger( AtcServiceEndpoint.class );

	private boolean quit = false;
	private Channel channel;
	private AtcServiceStub atcService;
	private int millisecondsToSleep = HaoConstants.queueDelayMilliseconds;
	private final Queue<GetVersionRequest> versionRequests;
	private final Queue<GetVersionResponse> versionResponses;


	public AtcServiceEndpoint(
			String host,
			int port,
			Queue<GetVersionRequest> versionRequests,
			Queue<GetVersionResponse> versionResponses
	) {
		this(
				ManagedChannelBuilder.forAddress( host, port ).usePlaintext(),
				versionRequests,
				versionResponses
		);
	}


	public AtcServiceEndpoint(
			ManagedChannelBuilder<?> channelBuilder,
			Queue<GetVersionRequest> versionRequests,
			Queue<GetVersionResponse> versionResponses
	) {
		if ( channelBuilder == null )
			throw new NullPointerException( "grpc channel builder must not be null" );
		else if ( versionRequests == null )
			throw new NullPointerException( "version in must not be null" );
		else if ( versionResponses == null )
			throw new NullPointerException( "version msg out must not be null" );
		channel = channelBuilder.build();
		atcService = AtcServiceGrpc.newStub( channel );
		this.versionRequests = versionRequests;
		this.versionResponses = versionResponses;
	}


	@Override
	public void quit(
	) {
		quit = true;
	}


	public void requestVersion(
	) {
		requestVersion( GetVersionRequest.newBuilder().build() );
	}


	public void requestVersion(
			GetVersionRequest request
	) {
		final int timesToTry = 1;
		final CountDownLatch finishLatch = new CountDownLatch( timesToTry );
		StreamObserver<GetVersionResponse> callback = new StreamObserver<GetVersionResponse>()
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
					GetVersionResponse event
			) {
				versionResponses.offer( event );
			}
		};

		atcService.getVersion( request, callback );
	}


	@Override
	/** Check for requests, send a null request to quit */
	public void run(
	) {
		try
		{
			while ( true )
			{
				while ( ! versionRequests.isEmpty() )
				{
					GetVersionRequest versionRequest = versionRequests.poll();
					if ( versionRequest == null )
						break;
					requestVersion( versionRequest );
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
