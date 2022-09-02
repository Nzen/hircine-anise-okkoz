
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.game;


/**

*/
public class AtcMapPoint
{

	private final int xx;
	private final int yy;


	public AtcMapPoint(
			int xx, int yy
	) {
		super();
		/*
		if ( xx < 0 )
			throw new IllegalArgumentException( "xx must not be less than zero" );
		if ( yy < 0 )
			throw new IllegalArgumentException( "yy must not be less than zero" );
		*/
		this.xx = xx;
		this.yy = yy;
	}


	public int getXx(
	) {
		return xx;
	}


	public int getYy(
	) {
		return yy;
	}


	@Override
	public String toString(
	) {
		return "MapPoint [xx=" + xx + ", yy=" + yy + "]";
	}

}
