package com.lyeeedar.Systems

class TileSystem(world: World) : AbstractSystem(world)
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
}