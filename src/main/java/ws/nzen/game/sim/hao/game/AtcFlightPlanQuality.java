package ws.nzen.game.sim.hao.game;


public enum AtcFlightPlanQuality
{
	ACCEPTED( -2 ),
    INVALID_START( 4 ),
    INVALID_STEP( 2 ),
    NODE_OUTSIDE_MAP( 1 ),
    RESTRICTED_NODE( 0 ),
    SHARP_TURN( 3 ),
	UNSPECIFIED( 0 ),
    ;

	private int flightPlanErrorOrdinal;


	public static AtcFlightPlanQuality fromUpdateFlightPlanErrorOrdinal(
			int errorEnumOrdinal
	) {
		for ( AtcFlightPlanQuality candidate : values() )
			if ( errorEnumOrdinal == candidate.flightPlanErrorOrdinal )
				return candidate;
		return UNSPECIFIED;
	}


	AtcFlightPlanQuality(
			int flightPlanErrorOrdinal
	) {
		this.flightPlanErrorOrdinal = flightPlanErrorOrdinal;
	}

}


















