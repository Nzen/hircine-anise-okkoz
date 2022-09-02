
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.game;


/**

*/
public class AtcAirport
{

	private final AtcRoutingNode entranceNode;
	private final AtcTeamTag team;


	/** @param entranceNode
	 * @param team
	 */
	public AtcAirport(
			AtcRoutingNode entranceNode, AtcTeamTag team
	) {
		super();
		this.entranceNode = entranceNode;
		this.team = team;
	}


	public AtcRoutingNode getEntranceNode(
	) {
		return entranceNode;
	}


	public AtcTeamTag getTeam(
	) {
		return team;
	}


	@Override
	public String toString(
	) {
		return "Airport [" + team.name() + " " + entranceNode + "]";
	}

}
