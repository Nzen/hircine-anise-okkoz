package ws.nzen.game.sim.hao.game;


public class AtcFlightPlanRequest
{

	private final String airplaneId;
	private final AtcFlightPlan flightPlan;


	public AtcFlightPlanRequest(
			AtcFlightPlan flightPlan,
			String airplaneId
	) {
		if ( flightPlan == null )
			throw new NullPointerException( "flightPlan must not be null" );
		else if ( airplaneId == null || airplaneId.isEmpty() )
			throw new NullPointerException( "airplaneId must not be null" );
		this.flightPlan = flightPlan;
		this.airplaneId = airplaneId;
		
	}


	public String getAirplaneId(
	) {
		return airplaneId;
	}


	public AtcFlightPlan getFlightPlan(
	) {
		return flightPlan;
	}

}


















