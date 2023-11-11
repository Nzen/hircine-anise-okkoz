
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.adapt.atc;


import atc.v1.MapOuterClass.Point;
import ws.nzen.game.sim.hao.game.AtcMapPoint;


/**  */
public class PointMapper
{

	public java.awt.Point asAwtPoint(
			AtcMapPoint mapPoint
	) {
		return new java.awt.Point( mapPoint.getXx(), mapPoint.getYy() );
	}


	public AtcMapPoint asHaoPoint(
			Point nativePoint
	) {
		return new AtcMapPoint( nativePoint.getX(), nativePoint.getY() );
	}

}
