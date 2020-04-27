package com.lyeeedar.ActionSequence.Actions

import com.badlogic.gdx.utils.ObjectFloatMap
import com.badlogic.gdx.utils.ObjectSet
import com.exp4j.Helpers.CompiledExpression
import com.lyeeedar.ActionSequence.ActionSequenceState
import com.lyeeedar.Components.*
import com.lyeeedar.Game.AttackDamage
import com.lyeeedar.Game.DamageEquations
import com.lyeeedar.Game.DamageType
import com.lyeeedar.Game.Statistic
import com.lyeeedar.SpaceSlot
import com.lyeeedar.Util.*
import com.lyeeedar.Util.Random
import com.lyeeedar.Util.XmlData
import java.util.*

class DamageAction : AbstractOneShotActionSequenceAction()
{
	lateinit var damage: CompiledExpression
	lateinit var type: DamageType
	var bonusCritChance: Float = 0f
	var bonusCritDamage: Float = 0f
	var bonusStatusChance: Float = 0f

	//region non-data
	val hitEntities = ObjectSet<Entity>()
	val map = ObjectFloatMap<String>()
	//endregion

	override fun enter(state: ActionSequenceState): ActionState
	{
		val source = state.source.get()!!

		val rng = Random.obtainTS(state.seed++)

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
					sourceStats.write(map, "self")
					targetstats.write(map, "target")
					state.writeVariables(map)

					var damage = damage.evaluate(map)
					damage += damage * sourceStats.getStat(Statistic.ABILITY_POWER)

					val attackDam = DamageEquations.getAttackDam(rng, source.statistics()!!, AttackDamage(damage, type), bonusCritChance, bonusCritDamage)
					DamageEquations.doAttack(rng, source, entity, attackDam, state.world, bonusStatusChance)
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
		type = DamageType.valueOf(xmlData.get("Type").toUpperCase(Locale.ENGLISH))
		bonusCritChance = xmlData.getFloat("BonusCritChance", 0f)
		bonusCritDamage = xmlData.getFloat("BonusCritDamage", 0f)
		bonusStatusChance = xmlData.getFloat("BonusStatusChance", 0f)
	}
	override val classID: String = "Damage"
	//endregion
}