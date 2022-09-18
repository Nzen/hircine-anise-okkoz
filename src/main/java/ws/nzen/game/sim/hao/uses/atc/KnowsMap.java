
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.uses.atc;


import java.util.Collection;
import java.util.Optional;

import ws.nzen.game.sim.hao.game.AtcMap;
import ws.nzen.game.sim.hao.game.AtcRoutingNode;
import ws.nzen.game.sim.hao.game.AtcTeamTag;


/**

*/
public interface KnowsMap
{

	public Collection<AtcRoutingNode> getAirportNodesOf(
			AtcTeamTag team
	);


	public Optional<AtcMap> getMap(
	);

}
