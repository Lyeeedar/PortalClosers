package com.lyeeedar.Game

import com.lyeeedar.Components.Entity
import com.lyeeedar.Components.isAllies
import com.lyeeedar.Components.position
import com.lyeeedar.SpaceSlot
import com.lyeeedar.Systems.AbstractTile

class Tile(x: Int, y: Int) : AbstractTile(x, y)
{
	override fun getPassable(travelType: SpaceSlot, self: Any?): Boolean
	{
		if (travelType == SpaceSlot.LIGHT)
		{
			return wall == null
		}

		if (wall != null) return false
		if (contents.get(SpaceSlot.WALL) != null) { return false; }

		val obj = contents.get(travelType)
		if (obj != null && obj != self)
		{
			if (self is Entity)
			{
				if (self.isAllies(obj) && obj.position()!!.turnsOnTile < 3)
				{
					return true
				}
			}

			return false
		}

		return true
	}

	override fun getInfluence(travelType: SpaceSlot, self: Any?): Int
	{
		return 0
	}
}