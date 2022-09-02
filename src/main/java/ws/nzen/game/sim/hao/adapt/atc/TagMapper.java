
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.adapt.atc;


import atc.v1.TagOuterClass.Tag;
import ws.nzen.game.sim.hao.game.AtcTeamTag;


/**

*/
public class TagMapper
{

	public AtcTeamTag asHaoTag(
			Tag nativeTag
	) {
		switch ( nativeTag )
		{
			case TAG_BLUE :
				return AtcTeamTag.BLUE;
			case TAG_RED :
				return AtcTeamTag.RED;
			case TAG_UNSPECIFIED :
				return AtcTeamTag.UNSPECIFIED;
			default :
				return AtcTeamTag.UNKNOWN;
		}
	}

}
