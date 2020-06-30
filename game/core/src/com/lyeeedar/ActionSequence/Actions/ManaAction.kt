package com.lyeeedar.ActionSequence.Actions

import com.badlogic.gdx.utils.ObjectFloatMap
import com.badlogic.gdx.utils.ObjectSet
import com.lyeeedar.ActionSequence.ActionSequenceState
import com.lyeeedar.Components.Entity
import com.lyeeedar.Components.ability
import com.lyeeedar.Components.isEnemies
import com.lyeeedar.Components.statistics
import com.lyeeedar.Game.AttackDamage
import com.lyeeedar.Game.DamageEquations
import com.lyeeedar.Game.Statistic
import com.lyeeedar.SpaceSlot
import com.lyeeedar.Util.CompiledExpression
import com.lyeeedar.Util.DataCompiledExpression
import com.lyeeedar.Util.XmlData

class ManaAction : AbstractOneShotActionSequenceAction()
{
	@DataCompiledExpression(default = "source.damage")
	lateinit var amount: CompiledExpression

	//region non-data
	val hitEntities = ObjectSet<Entity>()
	val map = ObjectFloatMap<String>()
	//endregion

	override fun enter(state: ActionSequenceState)
	{
		val source = state.source.get()!!

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
				if (entity.isEnemies(source))
				{
					val sourceStats = source.statistics()!!

					map.clear()
					sourceStats.write(map, "source")
					targetstats.write(map, "target")
					state.writeVariables(map)

					var amount = amount.evaluate(map, state.rng)
					amount += amount * sourceStats.getStat(Statistic.ABILITY_POWER)

					entity.ability()?.gainMana(amount.toInt())
				}
			}
		}
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		amount = CompiledExpression(xmlData.get("Amount", "source.damage")!!)
	}
	override val classID: String = "Mana"
	//endregion
}