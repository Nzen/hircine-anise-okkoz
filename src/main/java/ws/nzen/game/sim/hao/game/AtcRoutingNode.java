
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.game;


/**  */
public class AtcRoutingNode
{

	final int latitude;
	final int longitude;
	final boolean restricted;


	/** @param longitude
	 * /** @param latitude
	 * /** @param restricted */
	public AtcRoutingNode(
			int latitude,
			int longitude,
			boolean restricted
	) {
		super();
		/*
		if ( longitude < 0 )
			throw new IllegalArgumentException( "longitude must not be less than zero" );
		if ( latitude < 0 )
			throw new IllegalArgumentException( "latitude must not be less than zero" );
		*/
		this.longitude = longitude;
		this.latitude = latitude;
		this.restricted = restricted;
	}


	public int getLatitude(
	) {
		return latitude;
	}


	public int getLongitude(
	) {
		return longitude;
	}


	public boolean isRestricted(
	) {
		return restricted;
	}


	@Override
	public String toString(
	) {
		return "MapNode [lat-" + latitude + " long-"
				+ longitude + (restricted ? "impassible" : "") + "]";
	}

}
