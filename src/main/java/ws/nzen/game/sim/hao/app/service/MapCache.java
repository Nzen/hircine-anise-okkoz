
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.app.service;

import java.util.Collection;
import java.util.LinkedList;

import ws.nzen.game.sim.hao.game.AtcAirport;
import ws.nzen.game.sim.hao.game.AtcMap;
import ws.nzen.game.sim.hao.game.AtcRoutingNode;
import ws.nzen.game.sim.hao.game.AtcTeamTag;
import ws.nzen.game.sim.hao.uses.atc.KnowsMap;
import ws.nzen.game.sim.hao.uses.atc.SavesMap;

/**

*/
public class MapCache implements SavesMap, KnowsMap
{

	private AtcMap map = null;


	public Collection<AtcRoutingNode> getAirportNodesOf(
			AtcTeamTag team
	) {
		Collection<AtcRoutingNode> entrances = new LinkedList<>();
		if ( map == null )
			return entrances;
		for ( AtcAirport airport : map.getAirports() )
		{
			if ( airport.getTeam() == team )
				entrances.add( airport.getEntranceNode() );
		}
		return entrances;
	}


	public void save(
			AtcMap map
	) {
		if ( map == null )
			throw new NullPointerException( "map must not be null" );
		this.map = map;
	}


}


















