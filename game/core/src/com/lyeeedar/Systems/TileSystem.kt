package com.lyeeedar.Systems

import com.lyeeedar.Components.position
import com.lyeeedar.SpaceSlot

class TileSystem(world: World<*>) : AbstractSystem(world)
{
	override fun doUpdate(deltaTime: Float)
	{
		for (x in 0 until world.grid.width)
		{
			for (y in 0 until world.grid.height)
			{
				val tile = world.grid[x, y]

				if (tile.queuedActions.size > 0)
				{
					val itr = tile.queuedActions.iterator()
					while (itr.hasNext())
					{
						val action = itr.next()
						action.delay -= deltaTime
						if (action.delay <= 0f)
						{
							itr.remove()
							action.function.invoke()
						}
					}
				}
			}
		}
	}

	override fun onTurn()
	{
		for (x in 0 until world.grid.width)
		{
			for (y in 0 until world.grid.height)
			{
				val tile = world.grid[x, y]

				for (slot in SpaceSlot.EntityValues)
				{
					val entity = tile.contents[slot] ?: continue
					val pos = entity.position()
					if (pos != null)
					{
						if (pos.lastPos == pos.position)
						{
							pos.turnsOnTile++
						}
						else
						{
							pos.turnsOnTile = 0
							pos.lastPos = pos.position
						}
					}
				}
			}
		}
	}
}