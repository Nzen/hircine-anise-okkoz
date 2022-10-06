
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.game;


/**

*/
public class AtcAirplane
{

	private AtcFlightPlan flightPlan;
	private AtcMapPoint location;
	private AtcRoutingNode closestRoutingNode; // FIX have boardMapper ask the KnowsMap for node
	private final AtcTeamTag team;
	private final String atcId;


	/** @param flightPlan
	 * @param location
	 * @param closestRoutingNode
	 * @param team
	 * @param atcId
	 */
	public AtcAirplane(
			String atcId,
			AtcFlightPlan flightPlan,
			AtcMapPoint location,
			AtcRoutingNode closestRoutingNode,
			AtcTeamTag team
	) {
		super();
		if ( atcId == null )
			throw new NullPointerException( "atcId must not be null" );
		else if ( flightPlan == null )
			throw new NullPointerException( "flightPlan must not be null" );
		else if ( location == null )
			throw new NullPointerException( "location must not be null" );
		/*
		else if ( closestRoutingNode == null )
			throw new NullPointerException( "closestRoutingNode must not be null" );
		*/
		else if ( team == null )
			throw new NullPointerException( "team must not be null" );
		if ( atcId.isEmpty() )
			throw new IllegalArgumentException( "atcId must have text" );

		this.flightPlan = flightPlan;
		this.location = location;
		this.closestRoutingNode = closestRoutingNode;
		this.team = team;
		this.atcId = atcId;
	}


	public AtcFlightPlan getFlightPlan(
	) {
		return flightPlan;
	}


	public void setFlightPlan(
			AtcFlightPlan flightPlan
	) {
		this.flightPlan = flightPlan;
	}


	public AtcMapPoint getLocation(
	) {
		return location;
	}


	public void setLocation(
			AtcMapPoint location
	) {
		this.location = location;
	}


	public AtcRoutingNode getClosestRoutingNode(
	) {
		return closestRoutingNode;
	}


	public void setClosestRoutingNode(
			AtcRoutingNode closestRoutingNode
	) {
		this.closestRoutingNode = closestRoutingNode;
	}


	public AtcTeamTag getTeam(
	) {
		return team;
	}


	public String getAtcId(
	) {
		return atcId;
	}


	@Override
	public String toString(
	) {
		return "Airplane [id=" + atcId + " " + team.name() + " " + location + "]";
	}

}
