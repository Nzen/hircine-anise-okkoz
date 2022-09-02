
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.game;


import java.time.LocalDateTime;


/**

*/
public abstract class AtcEvent
{

	public abstract AtcEventType getType(
	);


	public abstract LocalDateTime getCreatedTime(
	);

}
