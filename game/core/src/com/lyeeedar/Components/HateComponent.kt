package com.lyeeedar.Components

import com.badlogic.gdx.utils.ObjectFloatMap
import com.lyeeedar.Game.Statistic
import com.lyeeedar.Util.Colour
import com.lyeeedar.Util.max
import com.lyeeedar.Util.set

inline fun Entity.hate(): HateComponent? = this.components[ComponentType.Hate] as HateComponent?
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
		val defenderHP = defender.stats()!!.getStat(Statistic.MAX_HP)
		val fraction = amount / defenderHP

		hateMap[attacker] = hateMap[attacker, 0f] + fraction*20
	}

	fun clearHate(entity: Entity)
	{
		hateMap[entity] = 0f
	}

	fun degradeHate()
	{
		for (key in hateMap.keys())
		{
			hateMap[key] = max(hateMap[key, 0f] - 1f, 0f)
		}
	}

	fun getAgroedTarget(defender: Entity): Entity?
	{
		val defenderPos = defender.pos()!!.position

		var bestTarget: Entity? = null
		var bestHate = -Float.MAX_VALUE

		for (target in hateMap)
		{
			val targetPos = target.key.pos() ?: continue

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
			defender.stats()!!.addMessage("!", Colour.RED, 1f)
		}

		lastAgroedTarget = bestTarget
		return bestTarget
	}

	override fun reset()
	{
		lastAgroedTarget = null
	}
}