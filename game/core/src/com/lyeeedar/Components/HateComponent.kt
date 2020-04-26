package com.lyeeedar.Components

import com.badlogic.gdx.utils.ObjectFloatMap
import com.lyeeedar.Game.Statistic
import com.lyeeedar.Systems.*
import com.lyeeedar.Util.Colour
import com.lyeeedar.Util.max
import com.lyeeedar.Util.set

class HateComponent : NonDataComponent()
{
	override val type: ComponentType = ComponentType.Hate

	val hateMap = ObjectFloatMap<Entity>()
	var lastAgroedTarget: Entity? = null

	fun addRawHate(entity: Entity, amount: Float)
	{
		hateMap[entity] = hateMap[entity, 0f] + amount
	}

	fun addProximityHate(entity: Entity, dist: Int)
	{
		hateMap[entity] = hateMap[entity, 0f] + (10 - dist)
	}

	fun addDamageHate(attacker: Entity, defender: Entity, amount: Float)
	{
		val defenderHP = defender.statistics()!!.getStat(Statistic.MAX_HP)
		val fraction = amount / defenderHP

		hateMap[attacker] = hateMap[attacker, 0f] + fraction*20
	}

	fun clearHate(entity: Entity)
	{
		hateMap[entity] = 0f
	}

	fun degradeHate()
	{
		val itr = hateMap.iterator()
		while (itr.hasNext)
		{
			val pair = itr.next()
			pair.value -= max(1f, pair.value / 4f)

			if (pair.value <= 0f)
			{
				itr.remove()
			}
		}
	}

	fun getAgroedTarget(defender: Entity, world: World): Entity?
	{
		val defenderPos = defender.position()!!.position

		var bestTarget: Entity? = null
		var bestHate = -Float.MAX_VALUE

		for (target in hateMap)
		{
			val targetPos = target.key.position() ?: continue

			val dist = defenderPos.taxiDist(targetPos.position)
			val falloff = (10f - dist) / 10f

			val scaledHate = target.value * falloff
			if (scaledHate > bestHate)
			{
				bestHate = scaledHate
				bestTarget = target.key
			}
		}

		if (bestTarget != null && lastAgroedTarget != bestTarget)
		{
			defender.statistics()!!.addMessage("!", Colour.RED, 1f)

			if (EventSystem.isEventRegistered(EventType.AGRO_CHANGED, defender))
			{
				val eventSystem = world.eventSystem()!!
				eventSystem.addEvent(EventType.AGRO_CHANGED, defender, bestTarget)
			}
		}

		lastAgroedTarget = bestTarget
		return bestTarget
	}

	override fun reset()
	{
		lastAgroedTarget = null
	}
}