
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.adapt.atc;


import atc.v1.Atc.Version;
import ws.nzen.game.sim.hao.game.AtcGameVersion;


/**

*/
public class GameVersionMapper
{

	public AtcGameVersion asHaoVersion(
			Version grpcStyle
	) {
		return new AtcGameVersion(
				(int)grpcStyle.getMajor(),
				(int)grpcStyle.getMinor(),
				(int)grpcStyle.getPatch(),
				grpcStyle.getPre() );
	}

}
