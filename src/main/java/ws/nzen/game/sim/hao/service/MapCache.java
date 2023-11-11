
/* © authors of Hircine Anise Okkoz, who release this file
 * under the terms of the Apache license v2.0 */

package ws.nzen.game.sim.hao.service;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

import ws.nzen.game.sim.hao.adapt.atc.PointMapper;
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
	private PointMapper pointMapper;
	/*
* horizontal node location distance
* vertical node location distance
* 'minimal' map location
* 'maximal' map location
	 */


	public MapCache(
			PointMapper pointMapper,
			Queue<HaoEvent> repaintEvents
	) {
		if ( repaintEvents == null )
			throw new NullPointerException( "repaintEvents must not be null" );
		if ( pointMapper == null )
			throw new NullPointerException( "pointMapper must not be null" );
		this.repaintEvents = repaintEvents;
		this.pointMapper = pointMapper;
	}


	public AtcRoutingNode closestNode(
			AtcMapPoint position
	) {
		/*
		consider just estimating based on distance each time, rather than storing rectangles
		 */
		Point pointOnMap = pointMapper.asAwtPoint( position );
		for ( AtcRoutingNode node : nodeZones.keySet() ) {
			Rectangle area = nodeZones.get( node );
			if ( area.contains( pointOnMap ) )
				return node;
		}

		return new AtcRoutingNode( Integer.MIN_VALUE, Integer.MIN_VALUE, true );
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
			if ( zone.contains( pointwiseLocation.getXx(), pointwiseLocation.getYy() ) )
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
		// estimate distances here by asking for [-8, 11], [  ]

		/*
		Rectangle mostNegative = new Rectangle();
		AtcRoutingNode negativeNode = nodeZones.keySet().iterator().next();
		Rectangle leastNegative = new Rectangle();
		AtcRoutingNode positiveNode = negativeNode;
		for ( AtcRoutingNode node : nodeZones.keySet() ) {
			Rectangle zone = nodeZones.get( node );
			if ( zone.getMinX() < mostNegative.getMinX() || zone.getMinY() < mostNegative.getMinY() ) {
				mostNegative = zone;
				negativeNode = node;
			}

			if ( zone.getMaxX() > mostNegative.getMaxX() || zone.getMaxY() > mostNegative.getMaxY() ) {
				leastNegative = zone;
				positiveNode = node;
			}
		}
		System.out.println( " negative  "+ negativeNode +" area "+ mostNegative );
		System.out.println( " positive  "+ positiveNode +" area "+ leastNegative );
		negativeNode = new AtcRoutingNode( -8, -11, false );
		mostNegative = nodeZones.get( negativeNode );
		System.out.println( " negative  "+ negativeNode +" area "+ mostNegative );
		positiveNode = new AtcRoutingNode( 8, 11, false );
		leastNegative = nodeZones.get( negativeNode );
		System.out.println( " positive  "+ positiveNode +" area "+ leastNegative );
		*/
	}


}


















