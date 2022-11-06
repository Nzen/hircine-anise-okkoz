
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.service;

import java.awt.Rectangle;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

import ws.nzen.game.sim.hao.game.AtcAirport;
import ws.nzen.game.sim.hao.game.AtcMap;
import ws.nzen.game.sim.hao.game.AtcMapPoint;
import ws.nzen.game.sim.hao.game.AtcRoutingNode;
import ws.nzen.game.sim.hao.game.AtcTeamTag;
import ws.nzen.game.sim.hao.game.HaoEvent;
import ws.nzen.game.sim.hao.uses.atc.KnowsMap;
import ws.nzen.game.sim.hao.uses.atc.SavesMap;

/**

*/
public class MapCache implements SavesMap, KnowsMap
{

	private AtcMap map = null;
	private final Queue<HaoEvent> repaintEvents;
	private Map<AtcRoutingNode, Rectangle> nodeZones = new HashMap<>(); 


	public MapCache(
			Queue<HaoEvent> repaintEvents
	) {
		if ( repaintEvents == null )
			throw new NullPointerException( "repaintEvents must not be null" );
		this.repaintEvents = repaintEvents;
	}


	public Collection<AtcRoutingNode> getAirportNodesOf(
			AtcTeamTag team
	) {
		Collection<AtcRoutingNode> entrances = new LinkedList<>();
		if ( map == null )
			return entrances;
		for ( AtcAirport airport : map.getAirports() )
		{
			if ( airport.getTeam() == team )
				entrances.add( airport.getEntranceNode() );
		}
		return entrances;
	}


	public Optional<AtcMap> getMap(
	) {
		if ( map == null )
			return Optional.empty();
		else
			return Optional.of( map );
	}


	@Override
	public Optional<AtcRoutingNode> geNodeOf(
			AtcMapPoint pointwiseLocation
	) {
		for ( AtcRoutingNode node : nodeZones.keySet() )
		{
			Rectangle zone = nodeZones.get( node );
			if ( zone.contains(
					pointwiseLocation.getXx(), pointwiseLocation.getYy() ) )
				return Optional.of( node );
		}
		return Optional.empty();
	}


	public void save(
			AtcMap map
	) {
		if ( map == null )
			throw new NullPointerException( "map must not be null" );
		this.map = map;
		repaintEvents.offer( HaoEvent.MAP_CHANGED );
	}


	public void save(
			Map<AtcRoutingNode, Rectangle> zones
	) {
		nodeZones.putAll( zones );
	}


}


















