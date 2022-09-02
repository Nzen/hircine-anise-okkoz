/** &copy; beyondRelations, LLC */

package ws.nzen.game.sim.hao.game;


import java.util.ArrayList;
import java.util.List;


/**  */
public class AtcFlightPlan
{

	private final List<AtcRoutingNode> route;


	/** @param route
	 */
	public AtcFlightPlan(
			List<AtcRoutingNode> route
	) {
		super();
		if ( route == null )
			route = new ArrayList<>();
		this.route = route;
	}


	public List<AtcRoutingNode> getRoute(
	) {
		return route;
	}


	@Override
	public String toString(
	) {
		if ( route.isEmpty() )
			return "FlightPlan-empty";
		else if ( route.size() == 1 )
			return "FlightPlan just " + route.get( 0 );
		else
			return "FlightPlan start " + route.get( 0 )
					+ " end " + route.get( route.size() - 1 );
	}

}
