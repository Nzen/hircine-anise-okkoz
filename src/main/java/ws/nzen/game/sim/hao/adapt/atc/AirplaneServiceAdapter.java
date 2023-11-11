package ws.nzen.game.sim.hao.adapt.atc;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ws.nzen.game.sim.hao.game.AtcFlightPlanQuality;
import ws.nzen.game.sim.hao.game.AtcFlightPlanRequest;
import ws.nzen.game.sim.hao.game.AtcFlightPlanResponse;
import ws.nzen.game.sim.hao.service.HaoConstants;
import ws.nzen.game.sim.hao.uses.any.Quittable;
import ws.nzen.game.sim.hao.uses.atc.VetsFlightPlans;

import atc.v1.AirplaneOuterClass.UpdateFlightPlanError;
import atc.v1.AirplaneOuterClass.UpdateFlightPlanError.ValidationError;
import atc.v1.AirplaneOuterClass.UpdateFlightPlanRequest;
import atc.v1.AirplaneOuterClass.UpdateFlightPlanResponse;


public class AirplaneServiceAdapter implements VetsFlightPlans
{

	private static final Logger log = LoggerFactory.getLogger( AirplaneServiceAdapter.class );

	private AirplaneServiceEndpoint airplaneServiceEndpoint;
	private boolean quit = false;
	private int millisecondsToSleep = HaoConstants.queueDelayMilliseconds;
	private final NodeMapper nodeMapper;
	private final Queue<UpdateFlightPlanRequest> atcFlightPlanRequests;
	private final Queue<UpdateFlightPlanResponse> atcFlightPlanResponses;
	private final Queue<AtcFlightPlanRequest> haoFlightPlanRequests;
	private final Queue<AtcFlightPlanResponse> haoFlightPlanResponses;
	private final Queue<String> airplaneIdsRequested;
	private final Thread runsAirplaneServiceEndpoint;


	public AirplaneServiceAdapter(
			AirplaneServiceEndpoint airplaneServiceEndpoint,
			NodeMapper nodeMapper,
			Queue<UpdateFlightPlanRequest> atcFlightPlanRequests,
			Queue<UpdateFlightPlanResponse> atcFlightPlanResponses,
			Queue<AtcFlightPlanRequest> haoFlightPlanRequests,
			Queue<AtcFlightPlanResponse> haoFlightPlanResponses
	) {
		if ( airplaneServiceEndpoint == null )
			throw new NullPointerException( "airplaneServiceEndpoint must not be null" );
		else if ( nodeMapper == null )
			throw new NullPointerException( "nodeMapper must not be null" );
		else if ( atcFlightPlanRequests == null )
			throw new NullPointerException( "atcFlightPlanRequests must not be null" );
		else if ( atcFlightPlanResponses == null )
			throw new NullPointerException( "atcFlightPlanResponses must not be null" );
		else if ( haoFlightPlanRequests == null )
			throw new NullPointerException( "haoFlightPlanRequests must not be null" );
		else if ( haoFlightPlanResponses == null )
			throw new NullPointerException( "haoFlightPlanResponses must not be null" );

		this.airplaneServiceEndpoint = airplaneServiceEndpoint;
		this.nodeMapper = nodeMapper;
		this.atcFlightPlanRequests = atcFlightPlanRequests;
		this.atcFlightPlanResponses = atcFlightPlanResponses;
		this.haoFlightPlanRequests = haoFlightPlanRequests;
		this.haoFlightPlanResponses = haoFlightPlanResponses;
		airplaneIdsRequested = new LinkedList<>();

		runsAirplaneServiceEndpoint = new Thread( airplaneServiceEndpoint );
		runsAirplaneServiceEndpoint.start();
	}


	@Override
	public void quit(
	) {
		quit = true;
	}


	@Override
	/** Check for requests, copy these to outgoing queue. */
	public void run(
	) {
		try
		{
			while ( true )
			{
				while ( ! haoFlightPlanRequests.isEmpty() )
				{
					AtcFlightPlanRequest asHaoRequest = haoFlightPlanRequests.poll();
					UpdateFlightPlanRequest asAtcRequest = asAtcRequest( asHaoRequest );
					atcFlightPlanRequests.offer( asAtcRequest );
				}

				while ( ! atcFlightPlanResponses.isEmpty() )
				{
					UpdateFlightPlanResponse asAtcResponse = atcFlightPlanResponses.poll();
					AtcFlightPlanResponse asHaoResponse = asHaoResponse( asAtcResponse );
					haoFlightPlanResponses.offer( asHaoResponse );
				}

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


	private UpdateFlightPlanRequest asAtcRequest(
			AtcFlightPlanRequest haoRequest
	) {
		airplaneIdsRequested.offer( haoRequest.getAirplaneId() );
		UpdateFlightPlanRequest asAtcRequest = UpdateFlightPlanRequest.newBuilder()
				.setId( haoRequest.getAirplaneId() )
				.addAllFlightPlan( nodeMapper.asAtcFlightPlan( haoRequest.getFlightPlan() ) )
				.build();
		return asAtcRequest;
	}


	private AtcFlightPlanResponse asHaoResponse(
			UpdateFlightPlanResponse asAtcResponse
	) {
		String airplaneId = airplaneIdsRequested.poll();
		if ( asAtcResponse.hasSuccess() )
			return new AtcFlightPlanResponse( AtcFlightPlanQuality.ACCEPTED, airplaneId );

		UpdateFlightPlanError complaint = asAtcResponse.getError();
		Collection<ValidationError> complaintEnumOrdinals = complaint.getErrorsList();
		if ( complaintEnumOrdinals.isEmpty() )
			return new AtcFlightPlanResponse( AtcFlightPlanQuality.UNSPECIFIED, airplaneId );
		else {
			if ( complaintEnumOrdinals.size() > 1 ) {
				StringBuilder errorMessage = new StringBuilder( "Reject flight plan for " );
				errorMessage.append( airplaneId ).append( " because " );
				for ( ValidationError error : complaintEnumOrdinals )
					errorMessage.append( AtcFlightPlanQuality
							.fromUpdateFlightPlanErrorOrdinal( error.getNumber() ) )
						.append( " " );
				log.error( errorMessage.toString() );
			}
			return new AtcFlightPlanResponse(
					AtcFlightPlanQuality.fromUpdateFlightPlanErrorOrdinal(
							complaintEnumOrdinals.iterator().next().getNumber() ),
					airplaneId ); // ¶ just one complaint is plenty
		}
	}

}


















