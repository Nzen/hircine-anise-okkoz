
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.game;

import java.util.Objects;

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


	@Override
	public int hashCode()
	{
		return Objects.hash( latitude, longitude );
	}


	@Override
	public boolean equals( Object obj )
	{
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( getClass() != obj.getClass() )
			return false;
		AtcRoutingNode other = (AtcRoutingNode) obj;
		return latitude == other.latitude && longitude == other.longitude;
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
		return "MapNode ["+ longitude +", "+ latitude
				+ (restricted ? " impassible" : "") + "]";
	}

}
