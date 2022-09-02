
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.adapt.atc;


import atc.v1.MapOuterClass.Airport;

import ws.nzen.game.sim.hao.game.AtcAirport;


/**

*/
public class AirportMapper
{

	private final NodeMapper nodeMapper;
	private final TagMapper tagMapper;


	/** @param nodeMapper
	 * /** @param tagMapper */
	public AirportMapper(
			NodeMapper nodeMapper, TagMapper tagMapper
	) {
		super();
		if ( nodeMapper == null )
			throw new NullPointerException( "forRequests must not be null" );
		else if ( tagMapper == null )
			throw new NullPointerException( "forResponses must not be null" );
		this.nodeMapper = nodeMapper;
		this.tagMapper = tagMapper;
	}


	public AtcAirport asHaoAirport(
			Airport nativeMap
	) {
		return new AtcAirport(
				nodeMapper.asHaoRoutingNode( nativeMap.getNode() ),
				tagMapper.asHaoTag( nativeMap.getTag() )
		);
	}

}
