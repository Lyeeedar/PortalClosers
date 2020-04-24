package com.lyeeedar.ActionSequence.Actions

import com.badlogic.gdx.utils.ObjectFloatMap
import com.badlogic.gdx.utils.ObjectSet
import com.exp4j.Helpers.CompiledExpression
import com.lyeeedar.ActionSequence.ActionSequenceState
import com.lyeeedar.Components.*
import com.lyeeedar.Game.DamageEquations
import com.lyeeedar.Game.Statistic
import com.lyeeedar.Systems.EventData
import com.lyeeedar.Systems.EventSystem
import com.lyeeedar.Systems.EventType
import com.lyeeedar.Systems.eventSystem
import com.lyeeedar.Util.*
import com.lyeeedar.Util.XmlData

class DamageAction : AbstractOneShotActionSequenceAction()
{
	lateinit var damage: CompiledExpression
	var bonusLifesteal: Float = 0f
	var bonusCritChance: Float = 0f
	var bonusCritDamage: Float = 0f

	//region non-data
	val hitEntities = ObjectSet<Entity>()
	val map = ObjectFloatMap<String>()
	//endregion

	override fun enter(state: ActionSequenceState): ActionState
	{
		val rng = Random.obtainTS(state.seed++)

		hitEntities.clear()
		for (point in state.targets)
		{
			val tile = state.world.grid.tryGet(point, null) ?: continue
			for (entity in tile.contents)
			{
				if (hitEntities.contains(entity)) continue
				hitEntities.add(entity)

				val targetstats = entity.stats() ?: continue
				if (entity.isEnemies(state.source))
				{
					val sourceStats = state.source.stats()!!

					map.clear()
					sourceStats.write(map, "self")
					targetstats.write(map, "target")

					var damModifier = damage.evaluate(map)
					damModifier += damModifier * sourceStats.getStat(Statistic.ABILITY_POWER)

					val attackDam = DamageEquations.getAttackDam(rng, state.source.stats()!!, damModifier, bonusCritChance, bonusCritDamage)
					DamageEquations.doAttack(rng, state.source, entity, attackDam, state.world)
				}
			}
		}

		rng.freeTS()

		return ActionState.Completed
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		damage = CompiledExpression(xmlData.get("Damage"))
		bonusLifesteal = xmlData.getFloat("BonusLifesteal", 0f)
		bonusCritChance = xmlData.getFloat("BonusCritChance", 0f)
		bonusCritDamage = xmlData.getFloat("BonusCritDamage", 0f)
	}
	override val classID: String = "Damage"
	//endregion
}