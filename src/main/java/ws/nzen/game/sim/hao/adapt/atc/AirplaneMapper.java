
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.adapt.atc;


import atc.v1.AirplaneOuterClass.Airplane;
import ws.nzen.game.sim.hao.game.AtcAirplane;
import ws.nzen.game.sim.hao.game.AtcFlightPlan;
import ws.nzen.game.sim.hao.game.AtcMapPoint;
import ws.nzen.game.sim.hao.game.AtcTeamTag;


/**  */
public class AirplaneMapper
{

	private final NodeMapper nodeMapper;
	private final PointMapper pointMapper;
	private final TagMapper tagMapper;


	public AirplaneMapper(
			NodeMapper nodeMapper,
			PointMapper pointMapper,
			TagMapper tagMapper
	) {
		super();
		if ( nodeMapper == null )
			throw new NullPointerException( "nodeMapper must not be null" );
		else if ( pointMapper == null )
			throw new NullPointerException( "pointMapper must not be null" );
		else if ( tagMapper == null )
			throw new NullPointerException( "tagMapper must not be null" );
		this.nodeMapper = nodeMapper;
		this.pointMapper = pointMapper;
		this.tagMapper = tagMapper;
	}


	public AtcAirplane asHaoAirplane(
			Airplane nativeAirplane
	) {
		AtcMapPoint location = pointMapper.asHaoPoint( nativeAirplane.getPoint() );
		AtcFlightPlan flightPlan = nodeMapper.asHaoFlightPlan( nativeAirplane.getFlightPlanList() );
		AtcTeamTag team = tagMapper.asHaoTag( nativeAirplane.getTag() );
		return new AtcAirplane(
				nativeAirplane.getId(),
				flightPlan,
				location,
				flightPlan.getRoute().get( 0 ),
				team
		);
	}

}
