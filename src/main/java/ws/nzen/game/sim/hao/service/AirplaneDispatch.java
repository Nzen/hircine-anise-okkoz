
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.service;


import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ws.nzen.game.sim.hao.game.*;
import ws.nzen.game.sim.hao.uses.atc.KnowsAirplanesRunnably;
import ws.nzen.game.sim.hao.uses.atc.KnowsMap;


/**

*/
public class AirplaneDispatch implements KnowsAirplanesRunnably
{

	private static final Logger log = LoggerFactory
			.getLogger( AirplaneDispatch.class );

	private boolean quit = false;
	private int millisecondsToSleep = HaoConstants.queueDelayMilliseconds;
	private final AirplaneCache airplaneCache;
	private final Queue<AtcEventAirplaneDetected> atcEventsAirplaneDetected;
	private final Queue<AtcEventFlightPlanUpdated> aeFlightChanged;
	private final Queue<AtcFlightPlanRequest> haoFlightPlanRequests;
	private final Queue<AtcFlightPlanResponse> haoFlightPlanResponses;
	private final Queue<HaoEvent> repaintEvents;
	private final Queue<HaoEventAirplaneMoved> queuePlanePositionAndNode;


	public AirplaneDispatch(
			AirplaneCache airplaneCache,
			Queue<AtcEventAirplaneDetected> atcEventsAirplaneDetected,
			Queue<AtcEventFlightPlanUpdated> aeFlightChanged,
			Queue<AtcFlightPlanRequest> haoFlightPlanRequests,
			Queue<AtcFlightPlanResponse> haoFlightPlanResponses,
			Queue<HaoEvent> repaintEvents,
			Queue<HaoEventAirplaneMoved> queuePlanePositionAndNode
	) {
		if ( airplaneCache == null )
			throw new NullPointerException( "airplaneCache must not be null" );
		else if ( atcEventsAirplaneDetected == null )
			throw new NullPointerException( "atcEventsAirplaneDetected must not be null" );
		else if ( aeFlightChanged == null )
			throw new NullPointerException( "aeFlightChanged must not be null" );
		else if ( haoFlightPlanRequests == null )
			throw new NullPointerException( "haoFlightPlanRequests must not be null" );
		else if ( haoFlightPlanResponses == null )
			throw new NullPointerException( "haoFlightPlanResponses must not be null" );
		if ( repaintEvents == null )
			throw new NullPointerException( "repaintEvents must not be null" );
		else if ( queuePlanePositionAndNode == null )
			throw new NullPointerException( "queuePlanePositionAndNode must not be null" );
		this.atcEventsAirplaneDetected = atcEventsAirplaneDetected;
		this.airplaneCache = airplaneCache;
		this.aeFlightChanged = aeFlightChanged;
		this.haoFlightPlanRequests = haoFlightPlanRequests;
		this.haoFlightPlanResponses = haoFlightPlanResponses;
		this.repaintEvents = repaintEvents;
		this.queuePlanePositionAndNode = queuePlanePositionAndNode;
	}


	public Collection<AtcAirplane> findAll(
	) {
		return airplaneCache.findAll();
	}


	public Optional<AtcAirplane> findById(
			String airplaneId
	) {
		return airplaneCache.findById( airplaneId );
	}


	@Override
	public void quit(
	) {
		quit = true;
	}


	@Override
	/** Check queue for requests to print */
	public void run(
	) {
		try
		{
			while ( true )
			{
				while ( ! atcEventsAirplaneDetected.isEmpty() )
				{
					AtcEventAirplaneDetected airplaneEvent = atcEventsAirplaneDetected.poll();
					AtcAirplane airplane = airplaneEvent.getAirplane();
					airplaneCache.save( airplane );
					makeNewFlightPlan( airplane.getAtcId() );
				}

				while ( ! aeFlightChanged.isEmpty() )
				{
					AtcEventFlightPlanUpdated airplaneEvent = aeFlightChanged.poll();
					airplaneCache.updateFlightPlan(
							airplaneEvent.getAirplaneId(),
							airplaneEvent.getFlightPlan() );
				}

				while ( ! haoFlightPlanResponses.isEmpty() )
				{
					AtcFlightPlanResponse flightPlanResponse = haoFlightPlanResponses.poll();
					Optional<AtcAirplane> maybeAirplane = airplaneCache.findById(
							flightPlanResponse.getAirplaneId() );
					if ( ! maybeAirplane.isPresent() )
						continue;
					AtcAirplane airplane = maybeAirplane.get();

					if ( flightPlanResponse.getFlightPlanStatus() == AtcFlightPlanQuality.ACCEPTED ) {
						airplane.approveProvisionalFlightPlan();
					}
					else
						log.error( "unable to plan "+ airplane.getAtcIdAsSingleCharacter()
								+" "+ airplane +" because "+ flightPlanResponse.getFlightPlanStatus() );
								// FIX handle with a different request, somehow
				}

				while ( ! queuePlanePositionAndNode.isEmpty() )
				{
					HaoEventAirplaneMoved movedEvent = queuePlanePositionAndNode.poll();
					Optional<AtcAirplane> maybeAirplane = airplaneCache.findById(
							movedEvent.getAirplaneId() );
					if ( ! maybeAirplane.isPresent() )
						continue;
					AtcAirplane airplane = maybeAirplane.get();

					AtcNodePoint airplaneCurrentPosition = movedEvent.getCurrentPositionAndNode();
					airplane.setLocation( airplaneCurrentPosition.getPoint() );

					AtcRoutingNode previouslyClosestNode = airplane.getClosestRoutingNode();
					AtcRoutingNode currentlyClosestNode = airplaneCurrentPosition.getRoutingNode();
					if ( previouslyClosestNode.equals( currentlyClosestNode ) )
						continue; // ¶ moved within the same node's zone

					airplane.setClosestRoutingNode( currentlyClosestNode );
					repaintEvents.offer( HaoEvent.AIRPLANE_NODE_CHANGED );

					List<AtcRoutingNode> approvedFlightPlan = airplane
							.getApprovedFlightPlan().getRoute();
					if ( currentlyClosestNode.equals( approvedFlightPlan.get(
							approvedFlightPlan.size() -1 ) ) )
						makeNewFlightPlan( airplane );
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


	private void makeNewFlightPlan(
			AtcAirplane airplane
	) {
		// FIX generate a valid flight plan
		AtcFlightPlan approvedFlightPlan = airplane.getApprovedFlightPlan();
		List<AtcRoutingNode> approvedRoute = approvedFlightPlan.getRoute();
		List<AtcRoutingNode> fakeRoute = new LinkedList<>();
		// ASK does a flight plan need to start with the last node ?
		AtcRoutingNode finalNode = approvedRoute.get( approvedRoute.size() -1 );
		fakeRoute.add( finalNode );
		AtcMapPoint currentPosition = airplane.getLocation();
		final int alpha = 0, bravo = alpha +1, charlie = bravo +1, delta = charlie +1;
		int arrived;
		if ( currentPosition.getXx() > 350 )
			arrived = alpha;
		else if ( currentPosition.getXx() < -350 )
			arrived = bravo;
		else if ( currentPosition.getYy() > 350 )
			arrived = delta;
		else // if ( currentPosition.getYy() < -350 )
			arrived = charlie;
		for ( int index = 1; index < 6; index += 1 ) {
			AtcRoutingNode nextNode;
			if ( arrived == alpha )
				nextNode = new AtcRoutingNode(
						finalNode.getLatitude(), finalNode.getLongitude() - index, false );
			else if ( arrived == charlie )
				nextNode = new AtcRoutingNode(
						finalNode.getLatitude() + index, finalNode.getLongitude(), false );
			else if ( arrived == delta )
				nextNode = new AtcRoutingNode(
						finalNode.getLatitude() - index, finalNode.getLongitude(), false );
			else // if ( arrived == atBottom )
				nextNode = new AtcRoutingNode(
						finalNode.getLatitude(), finalNode.getLongitude() + index, false );
			fakeRoute.add( nextNode );
		}
		AtcFlightPlan proposedFlightPlan = new AtcFlightPlan( fakeRoute );
		airplane.setProposedFlightPlan( proposedFlightPlan );
		haoFlightPlanRequests.offer( new AtcFlightPlanRequest(
				proposedFlightPlan, airplane.getAtcId() ) );
	}


	private void makeNewFlightPlan(
			String airplaneId
	) {
		Optional<AtcAirplane> maybeAirplane = airplaneCache.findById( airplaneId );
		if ( ! maybeAirplane.isPresent() )
			return;
		makeNewFlightPlan( maybeAirplane.get() );
	}


	@Override
	public boolean updateAirplaneNodes(
			KnowsMap knowsPointsOfNode
	) {
		return airplaneCache.updateAirplaneNodes( knowsPointsOfNode );
	}



}


















