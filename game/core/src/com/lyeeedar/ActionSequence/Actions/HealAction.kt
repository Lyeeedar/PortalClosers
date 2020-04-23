package com.lyeeedar.ActionSequence.Actions

import com.badlogic.gdx.utils.ObjectFloatMap
import com.badlogic.gdx.utils.ObjectSet
import com.exp4j.Helpers.CompiledExpression
import com.lyeeedar.ActionSequence.ActionSequenceState
import com.lyeeedar.Components.Entity
import com.lyeeedar.Components.isAllies
import com.lyeeedar.Components.stats
import com.lyeeedar.Game.Statistic
import com.lyeeedar.Systems.EventData
import com.lyeeedar.Systems.EventSystem
import com.lyeeedar.Systems.EventType
import com.lyeeedar.Util.XmlData

class HealAction : AbstractOneShotActionSequenceAction()
{
	lateinit var amount: CompiledExpression

	//region non-data
	val hitEntities = ObjectSet<Entity>()
	val map = ObjectFloatMap<String>()
	//endregion

	override fun enter(state: ActionSequenceState): ActionState
	{
		val eventSystem = state.world.systems.filterIsInstance <EventSystem>().firstOrNull() ?: return ActionState.Completed

		hitEntities.clear()
		for (point in state.targets)
		{
			val tile = state.world.grid.tryGet(point, null) ?: continue
			for (entity in tile.contents)
			{
				if (hitEntities.contains(entity)) continue
				hitEntities.add(entity)

				val targetstats = entity.stats() ?: continue
				if (entity.isAllies(state.source))
				{
					val sourceStats = state.source.stats()!!

					map.clear()
					sourceStats.write(map, "self")
					targetstats.write(map, "target")

					var healModifier = amount.evaluate(map)
					//healModifier += healModifier * sourceStats.getStat(Statistic.ABILITYPOWER)

					val power = sourceStats.getStat(Statistic.POWER)
					val healing = power * healModifier
					targetstats.heal(healing)

					state.source.stats()!!.healing += healing

					if (state.source.stats()!!.summoner != null)
					{
						state.source.stats()!!.summoner!!.stats()!!.healing += healing
					}

					if (EventSystem.isEventRegistered(EventType.HEALED, entity))
					{
						val healEventData = EventData.obtain().set(EventType.HEALED, entity, state.source, mapOf(Pair("amount", healing)))
						eventSystem.addEvent(healEventData)
					}
				}
			}
		}

		return ActionState.Completed
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		amount = CompiledExpression(xmlData.get("Amount"))
	}
	override val classID: String = "Heal"
	//endregion
}