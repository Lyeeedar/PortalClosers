package com.lyeeedar.ActionSequence.Actions

import com.badlogic.gdx.utils.ObjectFloatMap
import com.badlogic.gdx.utils.ObjectSet
import com.exp4j.Helpers.CompiledExpression
import com.lyeeedar.ActionSequence.ActionSequenceState
import com.lyeeedar.Components.Entity
import com.lyeeedar.Components.hate
import com.lyeeedar.Components.isEnemies
import com.lyeeedar.Components.stats
import com.lyeeedar.SpaceSlot
import com.lyeeedar.Util.XmlData

class GenerateHateAction : AbstractOneShotActionSequenceAction()
{
	lateinit var amount: CompiledExpression

	//region non-data
	val hitEntities = ObjectSet<Entity>()
	val map = ObjectFloatMap<String>()
	//endregion

	override fun enter(state: ActionSequenceState): ActionState
	{
		hitEntities.clear()
		for (target in state.targets)
		{
			val tile = state.world.grid.tryGet(target, null) ?: continue
			for (slot in SpaceSlot.EntityValues)
			{
				val entity = tile.contents[slot] ?: continue

				if (hitEntities.contains(entity)) continue
				hitEntities.add(entity)

				val hate = entity.hate() ?: continue
				val targetstats = entity.stats() ?: continue
				if (entity.isEnemies(state.source))
				{
					val sourceStats = state.source.stats()!!

					map.clear()
					sourceStats.write(map, "self")
					targetstats.write(map, "target")
					state.writeVariables(map)

					val hateAmount = amount.evaluate(map)
					hate.addRawHate(state.source, hateAmount)
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
	override val classID: String = "GenerateHate"
	//endregion
}