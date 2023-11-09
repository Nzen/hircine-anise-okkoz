package ws.nzen.game.sim.hao.game;


public class AtcFlightPlanResponse
{

	private final String airplaneId;
	private final AtcFlightPlanQuality flightPlanStatus;


	public AtcFlightPlanResponse(
			AtcFlightPlanQuality flightPlanStatus,
			String airplaneId
	) {
		if ( flightPlanStatus == null )
			throw new NullPointerException( "flightPlanStatus must not be null" );
		else if ( airplaneId == null || airplaneId.isEmpty() )
			throw new NullPointerException( "airplaneId must not be null" );
		this.flightPlanStatus = flightPlanStatus;
		this.airplaneId = airplaneId;
		
	}


	public String getAirplaneId(
	) {
		return airplaneId;
	}


	public AtcFlightPlanQuality getFlightPlanStatus(
	) {
		return flightPlanStatus;
	}

}


















