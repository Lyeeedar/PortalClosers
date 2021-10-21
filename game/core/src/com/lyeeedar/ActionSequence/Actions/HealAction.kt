package com.lyeeedar.ActionSequence.Actions

import com.badlogic.gdx.utils.ObjectFloatMap
import com.badlogic.gdx.utils.ObjectSet
import com.lyeeedar.ActionSequence.ActionSequenceState
import com.lyeeedar.Components.Entity
import com.lyeeedar.Components.isAllies
import com.lyeeedar.Components.statistics
import com.lyeeedar.Components.variables
import com.lyeeedar.Game.Statistic
import com.lyeeedar.SpaceSlot
import com.lyeeedar.Systems.EventSystem
import com.lyeeedar.Systems.EventType
import com.lyeeedar.Systems.eventSystem
import com.lyeeedar.Util.CompiledExpression
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
					source.variables()?.write(map, "source")
					entity.variables()?.write(map, "target")
					state.writeVariables(map)

					val healing = amount.evaluate(map, state.rng)
					targetstats.heal(healing)

					sourceStats.healingDone += healing
					if (sourceStats.summoner != null)
					{
						sourceStats.summoner!!.statistics()!!.healingDone += healing
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
		amount = CompiledExpression(xmlData.get("Amount", "1")!!)
	}
	override val classID: String = "Heal"
	//endregion
}