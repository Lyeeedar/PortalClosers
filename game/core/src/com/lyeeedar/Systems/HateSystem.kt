package com.lyeeedar.Systems

import com.lyeeedar.Components.*
import com.lyeeedar.SpaceSlot

class HateSystem(world: World<*>) : AbstractEntitySystem(world, world.getEntitiesFor().all(ComponentType.Hate).get())
{
	override fun updateEntity(entity: Entity, deltaTime: Float)
	{

	}

	override fun onTurnEntity(entity: Entity)
	{
		val hate = entity.hate()!!
		hate.degradeHate()

		val pos = entity.position()
		if (pos != null)
		{
			val vision = entity.addOrGet(ComponentType.Vision) as VisionComponent
			val points = vision.getVision(pos.position.x, pos.position.y)

			for (point in points)
			{
				val tile = world.grid.tryGet(point, null) ?: continue
				val dist = tile.dist(pos.position)

				for (slot in SpaceSlot.EntityValues)
				{
					val other = tile.contents[slot]?.get() ?: continue
					if (other.isEnemies(entity))
					{
						hate.addProximityHate(other, dist)
					}
				}
			}
		}
	}
}