package com.lyeeedar.Systems

import com.lyeeedar.Components.ComponentType
import com.lyeeedar.Components.Entity
import com.lyeeedar.Components.statistics
import com.lyeeedar.Game.Statistic

class StatisticsSystem(world: World<*>) : AbstractEntitySystem(world, world.getEntitiesFor().all(ComponentType.Statistics).get())
{
	override fun updateEntity(entity: Entity, deltaTime: Float)
	{

	}

	override fun onTurnEntity(entity: Entity)
	{
		val stats = entity.statistics()!!

		val maxHP = stats.getStat(Statistic.MAX_HP)
		val regen = stats.getStat(Statistic.REGENERATION)

		if (regen > 0f)
		{
			stats.regenerate(maxHP * regen)
		}
		else if (regen < 0f)
		{
			stats.damage(maxHP * regen, false)
		}
	}

}