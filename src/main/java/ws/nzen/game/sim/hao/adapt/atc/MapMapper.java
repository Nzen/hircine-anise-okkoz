
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.adapt.atc;


import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import atc.v1.MapOuterClass.Airport;
import atc.v1.MapOuterClass.Map;
import atc.v1.MapOuterClass.Node;
import ws.nzen.game.sim.hao.game.AtcAirport;
import ws.nzen.game.sim.hao.game.AtcMap;
import ws.nzen.game.sim.hao.game.AtcRoutingNode;


/**  */
public class MapMapper
{

	private final AirportMapper airportMapper;
	private final NodeMapper nodeMapper;


	/** @param airportMapper
	 * /** @param nodeMapper */
	public MapMapper(
			AirportMapper airportMapper, NodeMapper nodeMapper
	) {
		super();
		this.airportMapper = airportMapper;
		this.nodeMapper = nodeMapper;
	}


	public AtcMap asHaoMap(
			Map nativeMap
	) {
		Collection<AtcAirport> airports = new LinkedList<>();
		Collection<AtcRoutingNode> nodes = new ArrayList<>( nativeMap.getRoutingGridCount() );
		for ( Airport airport : nativeMap.getAirportsList() )
			airports.add( airportMapper.asHaoAirport( airport ) );
		for ( Node atcNode : nativeMap.getRoutingGridList() )
			nodes.add( nodeMapper.asHaoRoutingNode( atcNode ) );
		return new AtcMap(
				airports,
				nodes,
				nativeMap.getWidth(),
				nativeMap.getHeight()
		);
	}

}
