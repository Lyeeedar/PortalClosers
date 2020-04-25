package com.lyeeedar.Systems

import com.lyeeedar.Components.*
import com.lyeeedar.SpaceSlot

class HateSystem(world: World) : AbstractEntitySystem(world, world.getEntitiesFor().all(ComponentType.Hate).get())
{
	override fun updateEntity(entity: Entity, deltaTime: Float)
	{

	}

	override fun onTurnEntity(entity: Entity)
	{
		val hate = entity.hate()!!
		hate.degradeHate()

		val pos = entity.pos()
		if (pos != null)
		{
			for (x in -10 until 10)
			{
				for (y in -10 until 10)
				{
					val tile = world.grid.tryGet(pos.position, x, y, null) ?: continue
					val dist = tile.taxiDist(pos.position)

					for (slot in SpaceSlot.EntityValues)
					{
						val other = tile.contents[slot] ?: continue
						if (other.isEnemies(entity))
						{
							hate.addProximityHate(other, dist)
						}
					}
				}
			}
		}
	}
}