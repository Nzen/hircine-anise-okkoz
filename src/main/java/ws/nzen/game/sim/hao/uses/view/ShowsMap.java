
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.uses.view;

import ws.nzen.game.sim.hao.uses.any.Quittable;

/**

*/
public interface ShowsMap extends Runnable, Quittable
{

	public void updateMap(
			boolean requireNodeChangeForRepaint
	);

}


















