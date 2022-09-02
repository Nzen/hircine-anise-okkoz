
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.game;


import java.util.Collection;


/**  */
public class AtcMap
{

	private final Collection<AtcAirport> airports;
	private final Collection<AtcRoutingNode> nodes;
	private final int width;
	private final int height;


	/** @param airports
	 * /** @param nodes
	 * /** @param width
	 * /** @param height */
	public AtcMap(
			Collection<AtcAirport> airports,
			Collection<AtcRoutingNode> nodes,
			int width,
			int height
	) {
		super();
		this.airports = airports;
		this.nodes = nodes;
		this.width = width;
		this.height = height;
	}


	public Collection<AtcAirport> getAirports(
	) {
		return airports;
	}


	public Collection<AtcRoutingNode> getNodes(
	) {
		return nodes;
	}


	public int getWidth(
	) {
		return width;
	}


	public int getHeight(
	) {
		return height;
	}


	@Override
	public String toString(
	) {
		return "AtcMap [w" + width + " h" + height + " airports " + airports.size() + "]";
	}

}
