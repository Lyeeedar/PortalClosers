package com.lyeeedar.Game

import com.lyeeedar.Components.Entity
import com.lyeeedar.Pathfinding.IPathfindingTile
import com.lyeeedar.Renderables.Sprite.SpriteWrapper
import com.lyeeedar.SpaceSlot
import com.lyeeedar.Util.FastEnumMap
import com.lyeeedar.Util.Point

class Tile(x: Int, y: Int) : Point(x, y), IPathfindingTile
{
	lateinit var floor: SpriteWrapper
	var wall: SpriteWrapper? = null
	val contents: FastEnumMap<SpaceSlot, Entity> = FastEnumMap(SpaceSlot::class.java)

	override fun getPassable(travelType: SpaceSlot, self: Any?): Boolean
	{
		return true
	}

	override fun getInfluence(travelType: SpaceSlot, self: Any?): Int
	{
		return 0
	}
}