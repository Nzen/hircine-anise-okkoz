
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.uses.atc;


import java.util.Collection;
import java.util.Optional;

import ws.nzen.game.sim.hao.game.AtcAirplane;


/**

*/
public interface KnowsAirplanes
{

	public Collection<AtcAirplane> findAll(
	);


	public Optional<AtcAirplane> findById(
			String airplaneId
	);

}
