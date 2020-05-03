package com.lyeeedar.ActionSequence.Actions

import com.badlogic.gdx.utils.ObjectFloatMap
import com.badlogic.gdx.utils.ObjectSet
import com.exp4j.Helpers.CompiledExpression
import com.lyeeedar.ActionSequence.ActionSequenceState
import com.lyeeedar.Components.Entity
import com.lyeeedar.Components.EntityReference
import com.lyeeedar.Components.isAllies
import com.lyeeedar.Components.statistics
import com.lyeeedar.Game.Statistic
import com.lyeeedar.SpaceSlot
import com.lyeeedar.Systems.EventSystem
import com.lyeeedar.Systems.EventType
import com.lyeeedar.Systems.eventSystem
import com.lyeeedar.Util.XmlData

class HealAction : AbstractOneShotActionSequenceAction()
{
	lateinit var amount: CompiledExpression

	//region non-data
	val hitEntities = ObjectSet<Entity>()
	val map = ObjectFloatMap<String>()
	//endregion

	override fun enter(state: ActionSequenceState)
	{
		val source = state.source.get()!!
		val eventSystem = state.world.eventSystem() ?: return

		hitEntities.clear()
		for (point in state.targets)
		{
			val tile = state.world.grid.tryGet(point, null) ?: continue
			for (slot in SpaceSlot.EntityValues)
			{
				val entity = tile.contents[slot]?.get() ?: continue

				if (hitEntities.contains(entity)) continue
				hitEntities.add(entity)

				val targetstats = entity.statistics() ?: continue
				if (entity.isAllies(source))
				{
					val sourceStats = source.statistics()!!

					map.clear()
					sourceStats.write(map, "source")
					targetstats.write(map, "target")
					state.writeVariables(map)

					var healing = amount.evaluate(map)
					healing += healing * sourceStats.getStat(Statistic.ABILITY_POWER)

					targetstats.heal(healing)

					sourceStats.healing += healing
					if (sourceStats.summoner != null)
					{
						sourceStats.summoner!!.statistics()!!.healing += healing
					}

					if (EventSystem.isEventRegistered(EventType.HEALED, entity))
					{
						eventSystem.addEvent(EventType.HEALED, entity.getRef(), source.getRef(), mapOf(Pair("amount", healing)))
					}
				}
			}
		}
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