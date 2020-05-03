package com.lyeeedar.Systems

import com.lyeeedar.Components.*
import com.lyeeedar.Renderables.Animation.AlphaAnimation
import com.lyeeedar.Renderables.Animation.ExpandAnimation
import com.lyeeedar.Renderables.Sprite.Sprite
import com.lyeeedar.Util.round

class BloodSystem(world: World<*>) : AbstractEntitySystem(world, world.getEntitiesFor().all(ComponentType.Blood).get())
{
	override fun updateEntity(entity: Entity, deltaTime: Float)
	{

	}

	override fun onTurnEntity(entity: Entity)
	{
		val blood = entity.blood()!!
		blood.remainingTurns--

		if (blood.remainingTurns <= 0)
		{
			entity.markForDeletion(0f, "faded")
		}
		else
		{
			val renderable = entity.renderable()!!
			val pos = entity.position()!!

			val truePos = pos.position.toVec().add(pos.offset)
			val tile = world.grid.tryGet(truePos.x.round(), truePos.y.round(), null)
			if (tile == null)
			{
				entity.markForDeletion(0f, "off map")
			}
			else
			{
				if (tile.contents.any { it?.get()?.water() != null })
				{
					blood.remainingTurns -= 25
					if (blood.remainingTurns < 0) blood.remainingTurns = 0
				}

				if (world.player!!.position()!!.position.dist(tile) < 20)
				{
					val sprite = renderable.renderable as Sprite

					val oldCol = sprite.colour.copy()

					val oldA = oldCol.a
					oldCol.a = 1f

					val rawAlpha = blood.remainingTurns.toFloat() / blood.totalTurns.toFloat()
					val newA = rawAlpha * blood.originalA
					sprite.colour.a = newA

					sprite.animation = AlphaAnimation.obtain().set(0.1f, oldA, newA, oldCol)

					val prevScale = sprite.baseScale[0]
					sprite.baseScale[0] = blood.originalScale * (1f + (1f - rawAlpha) * 1.5f)
					sprite.baseScale[1] = sprite.baseScale[0]

					sprite.animation = ExpandAnimation.obtain().set(0.1f, prevScale / sprite.baseScale[0], 1f)
				}
			}
		}
	}

}