package com.lyeeedar.ActionSequence.Actions

import com.badlogic.gdx.utils.ObjectFloatMap
import com.badlogic.gdx.utils.ObjectSet
import com.exp4j.Helpers.CompiledExpression
import com.lyeeedar.ActionSequence.ActionSequenceState
import com.lyeeedar.Components.*
import com.lyeeedar.Game.Statistic
import com.lyeeedar.Systems.EventData
import com.lyeeedar.Systems.EventSystem
import com.lyeeedar.Systems.EventType
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
				if (entity.isEnemies(state.source))
				{
					val sourceStats = state.source.stats()!!

					map.clear()
					sourceStats.write(map, "self")
					targetstats.write(map, "target")

					var damModifier = damage.evaluate(map)
					//damModifier += damModifier * sourceStats.getStat(Statistic.ABILITYPOWER)

					var attackDam = sourceStats.getAttackDam(rng, damModifier, bonusCritChance, bonusCritDamage)

					if (targetstats.checkAegis(rng))
					{
						if (EventSystem.isEventRegistered(EventType.BLOCK, entity))
						{
							val eventData = EventData.obtain().set(EventType.BLOCK, entity, state.source, mapOf(Pair("damage", attackDam.first)))
							eventSystem.addEvent(eventData)
						}

						attackDam = Pair(0f, attackDam.second)
						targetstats.blockedDamage = true

						targetstats.messagesToShow.add(MessageData.obtain().set("Blocked!", Colour.CYAN, 0.4f))
					}

					val finalDam = targetstats.dealDamage(attackDam.first, attackDam.second)

					sourceStats.abilityDamageDealt += finalDam

					if (sourceStats.summoner != null)
					{
						sourceStats.summoner!!.stats()!!.abilityDamageDealt += finalDam
					}

					BloodSplatter.splatter(state.source.pos()!!.position, entity.pos()!!.position, 1f, state.world)
					targetstats.lastHitSource = state.source.pos()!!.position

					val lifeSteal = sourceStats.getStat(Statistic.LIFESTEAL) + bonusLifesteal
					val stolenLife = finalDam * lifeSteal
					if (stolenLife > 0f)
					{
						sourceStats.heal(stolenLife)
						sourceStats.healing += stolenLife

						if (EventSystem.isEventRegistered(EventType.HEALED, state.source))
						{
							val healEventData = EventData.obtain().set(EventType.HEALED, state.source, state.source, mapOf(Pair("damage", stolenLife)))
							eventSystem.addEvent(healEventData)
						}
					}
					else if (stolenLife < 0f)
					{
						sourceStats.dealDamage(stolenLife, false)
					}

					// do damage events

					// crit
					if (attackDam.second)
					{
						if (EventSystem.isEventRegistered(EventType.CRIT, state.source))
						{
							val dealEventData = EventData.obtain().set(EventType.CRIT, state.source, entity, mapOf(
								Pair("damage", finalDam),
								Pair("dist", state.source.pos()!!.position.dist(entity.pos()!!.position).toFloat())))
							eventSystem.addEvent(dealEventData)
						}
					}

					// deal damage
					if (EventSystem.isEventRegistered(EventType.DEALDAMAGE, state.source))
					{
						val dealEventData = EventData.obtain().set(EventType.DEALDAMAGE, state.source, entity, mapOf(
							Pair("damage", finalDam),
							Pair("dist", state.source.pos()!!.position.dist(entity.pos()!!.position).toFloat())))
						eventSystem.addEvent(dealEventData)
					}

					// take damage
					if (EventSystem.isEventRegistered(EventType.TAKEDAMAGE, entity))
					{
						val takeEventData = EventData.obtain().set(EventType.TAKEDAMAGE, entity, state.source, mapOf(
							Pair("damage", finalDam),
							Pair("dist", state.source.pos()!!.position.dist(entity.pos()!!.position).toFloat())))
						eventSystem.addEvent(takeEventData)
					}
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