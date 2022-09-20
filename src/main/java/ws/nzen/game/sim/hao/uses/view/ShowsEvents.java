
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.uses.view;

import ws.nzen.game.sim.hao.uses.any.Quittable;

/**

*/
public interface ShowsEvents extends Runnable, Quittable
{

	public void showMessage(
			Object something
	);


	public void showMessage(
			String message
	);

}
