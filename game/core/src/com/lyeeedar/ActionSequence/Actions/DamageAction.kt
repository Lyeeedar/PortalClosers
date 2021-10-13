package com.lyeeedar.ActionSequence.Actions

import com.badlogic.gdx.utils.ObjectFloatMap
import com.badlogic.gdx.utils.ObjectSet
import com.lyeeedar.ActionSequence.ActionSequenceState
import com.lyeeedar.Components.Entity
import com.lyeeedar.Components.isEnemies
import com.lyeeedar.Components.statistics
import com.lyeeedar.Game.*
import com.lyeeedar.SpaceSlot
import com.lyeeedar.Util.CompiledExpression
import com.lyeeedar.Util.DataCompiledExpression
import com.lyeeedar.Util.DataValue
import com.lyeeedar.Util.XmlData
import java.util.*

class DamageAction : AbstractOneShotActionSequenceAction()
{
	@DataCompiledExpression(default = "source.damage")
	lateinit var damage: CompiledExpression

	@DataValue(visibleIf = "UseAttackDamageType == false")
	var type: Elements = Elements.NONE

	var useAttackDamageType: Boolean = true

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
					damage += damage * sourceStats.getStat(Statistic.POWER)

					val attackDam = DamageEquations.getAttackDam(state.rng, damage)

					val attackType = type
					val attackObj = AttackDamage(attackDam, attackType)

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
		type = Elements.valueOf(xmlData.get("Type", Elements.NONE.toString())!!.toUpperCase(Locale.ENGLISH))
		useAttackDamageType = xmlData.getBoolean("UseAttackDamageType", true)
	}
	override val classID: String = "Damage"
	//endregion
}