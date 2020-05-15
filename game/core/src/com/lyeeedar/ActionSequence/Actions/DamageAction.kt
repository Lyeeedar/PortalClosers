package com.lyeeedar.ActionSequence.Actions

import com.badlogic.gdx.utils.ObjectFloatMap
import com.badlogic.gdx.utils.ObjectSet
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
	@DataCompiledExpression(default = "source.damage")
	lateinit var damage: CompiledExpression

	@DataValue(visibleIf = "UseAttackDamageType == false")
	var type: DamageType = DamageType.NONE

	var alwaysCrit: Boolean = false
	var useAttackDamageType: Boolean = false

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

					var damage = damage.evaluate(map, state.rng)
					damage += damage * sourceStats.getStat(Statistic.ABILITY_POWER)

					val attackDam = DamageEquations.getAttackDam(state.rng, damage)

					val attackType = if (useAttackDamageType) sourceStats.attackDefinition.type else type
					val attackObj = AttackDamage(attackDam, attackType)
					attackObj.wasCrit = alwaysCrit

					DamageEquations.doAttack(state.rng, source, entity, attackObj, state.world)
				}
			}
		}
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		damage = CompiledExpression(xmlData.get("Damage", "source.damage")!!)
		type = DamageType.valueOf(xmlData.get("Type", DamageType.NONE.toString())!!.toUpperCase(Locale.ENGLISH))
		alwaysCrit = xmlData.getBoolean("AlwaysCrit", false)
		useAttackDamageType = xmlData.getBoolean("UseAttackDamageType", false)
	}
	override val classID: String = "Damage"
	//endregion
}