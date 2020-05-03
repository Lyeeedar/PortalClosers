package com.lyeeedar.Components

import com.badlogic.gdx.utils.ObjectFloatMap
import com.badlogic.gdx.utils.ObjectMap
import com.lyeeedar.Game.Statistic
import com.lyeeedar.Systems.*
import com.lyeeedar.Util.Colour
import com.lyeeedar.Util.max
import com.lyeeedar.Util.set
import ktx.collections.set

class HateData(val entityReference: EntityReference)
{
	var value: Float = 0f
}

class HateComponent : AbstractComponent()
{
	override val type: ComponentType = ComponentType.Hate

	val hateMap = ObjectMap<String, HateData>()
	var lastAgroedTarget: EntityReference? = null

	fun getData(entity: Entity): HateData
	{
		val key = "${entity.hashCode()}${entity.usageID}"
		var data = hateMap[key]
		if (data == null)
		{
			data = HateData(entity.getRef())
			hateMap[key] = data
		}

		return data
	}

	fun addRawHate(entity: Entity, amount: Float)
	{
		getData(entity).value += amount
	}

	fun addProximityHate(entity: Entity, dist: Int)
	{
		getData(entity).value += (10 - dist)
	}

	fun addDamageHate(attacker: Entity, defender: Entity, amount: Float)
	{
		val defenderHP = defender.statistics()!!.getStat(Statistic.MAX_HP)
		val fraction = amount / defenderHP

		getData(attacker).value += fraction*20
	}

	fun degradeHate()
	{
		val itr = hateMap.iterator()
		while (itr.hasNext)
		{
			val pair = itr.next()
			if (!pair.value.entityReference.isValid())
			{
				itr.remove()
			}
			else
			{
				pair.value.value -= max(1f, pair.value.value / 4f)

				if (pair.value.value <= 0f)
				{
					itr.remove()
				}
			}
		}
	}

	fun getAgroedTarget(defender: Entity, world: World<*>): EntityReference?
	{
		val defenderPos = defender.position()!!.position

		var bestTarget: EntityReference? = null
		var bestHate = -Float.MAX_VALUE

		for (data in hateMap.values())
		{
			val target = data.entityReference.get() ?: continue

			val targetPos = target.position() ?: continue

			val dist = defenderPos.dist(targetPos.position)
			val falloff = (10f - dist) / 10f

			val scaledHate = data.value * falloff
			if (scaledHate > bestHate)
			{
				bestHate = scaledHate
				bestTarget = data.entityReference
			}
		}

		if (bestTarget != null && lastAgroedTarget?.get() != bestTarget.get())
		{
			defender.statistics()!!.addMessage("!", Colour.RED, 1f)

			if (EventSystem.isEventRegistered(EventType.AGRO_CHANGED, defender))
			{
				val eventSystem = world.eventSystem()!!
				eventSystem.addEvent(EventType.AGRO_CHANGED, defender.getRef(), bestTarget)
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