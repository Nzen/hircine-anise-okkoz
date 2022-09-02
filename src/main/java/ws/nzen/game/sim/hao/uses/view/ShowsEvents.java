
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.uses.view;


/**

*/
public interface ShowsEvents extends Runnable
{

	public void quit(
	);


	public void showMessage(
			Object something
	);


	public void showMessage(
			String message
	);

}
