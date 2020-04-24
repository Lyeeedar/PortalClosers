package com.lyeeedar.Game

import com.badlogic.gdx.math.MathUtils
import com.lyeeedar.Components.*
import com.lyeeedar.Systems.*
import com.lyeeedar.Util.*
import squidpony.squidmath.LightRNG

class DamageEquations
{
	companion object
	{
		// AverageCritMult = 1f + (CritChance * CritDamage)
		fun getCritMultiplier(rng: LightRNG, attackerStats: StatisticsComponent, extraCritChance: Float = 0f, extraCritDam: Float = 0f): AttackDamage
		{
			val critChance = attackerStats.getStat(Statistic.CRIT_CHANCE) + extraCritChance
			if (rng.nextFloat() <= critChance)
			{
				val mult = 1f + attackerStats.getStat(Statistic.CRIT_DAMAGE) + extraCritDam
				return AttackDamage(mult, true)
			}

			return AttackDamage(1f, false)
		}

		// AttackDam = BaseAtk * 20%Modifier * CritMult
		fun getAttackDam(rng: LightRNG, attackerStats: StatisticsComponent, multiplier: Float, extraCritChance: Float = 0f, extraCritDam: Float = 0f): AttackDamage
		{
			// TODO: Included weapon dam in this. Maybe 'BaseAttack = WeaponAttack+(StatAtk*LevelMult)'
			val baseAttack = attackerStats.getStat(Statistic.ATK_POWER)

			val modifier = rng.randomWeighted() * 0.2f // 20% range, weighted to 0
			val attack = baseAttack * (1f + modifier)

			val critMult = getCritMultiplier(rng, attackerStats, extraCritChance, extraCritDam)

			return AttackDamage(attack * critMult.damage * multiplier, critMult.wasCrit)
		}

		// ArmourMitigation = Damage / (Damage + Armour)
		fun calculateArmourMitigation(defenderStats: StatisticsComponent, damage: AttackDamage): Float
		{
			val armour = defenderStats.getStat(Statistic.ARMOUR)

			val mitigation = damage.damage / (damage.damage + armour)

			return mitigation
		}

		// LevelSuppression =  EasingFactor / (EasingFactor + (DefenderLvl - AttackerLvl))
		fun calculateLevelSuppression(attackerStats: StatisticsComponent, defenderStats: StatisticsComponent): Float
		{
			val easingFactor = 20f
			val attackerLvl = attackerStats.level
			val defenderLvl = defenderStats.level
			val levelDiff = defenderLvl - attackerLvl

			if (levelDiff < -easingFactor)
			{
				return 1f
			}

			val rawSuppresion = easingFactor / (easingFactor + levelDiff)

			return min(1f, rawSuppresion)
		}

		// FinalDamage = AttackDam * ArmourMitigation * DR * LevelSuppression
		fun calculateFinalDamage(attackerStats: StatisticsComponent, defenderStats: StatisticsComponent, damage: AttackDamage): Float
		{
			val armourMitigation = calculateArmourMitigation(defenderStats, damage)
			val levelSuppression = calculateLevelSuppression(attackerStats, defenderStats)
			val dr = defenderStats.getStat(Statistic.DR)

			return damage.damage * armourMitigation * (1f - dr) * levelSuppression
		}

		fun checkAegis(rng: LightRNG, stats: StatisticsComponent): Boolean
		{
			val aegisChance = stats.getStat(Statistic.AEGIS)
			if (aegisChance > 0f && rng.nextFloat() < aegisChance)
			{
				return true
			}

			return false
		}

		fun doAttack(rng: LightRNG, attacker: Entity, defender: Entity, damage: AttackDamage, world: World)
		{
			val defenderPos = defender.pos()!!.position
			val defenderStats = defender.stats()!!
			val attackerPos = attacker.pos()!!.position
			val attackerStats = attacker.stats()!!

			val eventSystem = world.eventSystem()!!

			// try blocking
			if (checkAegis(rng, defenderStats))
			{
				if (EventSystem.isEventRegistered(EventType.BLOCK, defender))
				{
					val eventData = EventData.obtain().set(EventType.BLOCK, defender, attacker, mapOf(Pair("damage", damage.damage)))
					eventSystem.addEvent(eventData)
				}

				defenderStats.blockedDamage = true

				defenderStats.addMessage("Blocked!", Colour.CYAN, 0.4f)

				return
			}

			// apply modified dam
			val finalDam = calculateFinalDamage(attackerStats, defenderStats, damage)
			defenderStats.damage(finalDam, damage.wasCrit)

			// add final dam to stuff
			attackerStats.attackDamageDealt += finalDam
			if (attackerStats.summoner != null)
			{
				attackerStats.summoner!!.stats()!!.attackDamageDealt += finalDam
			}

			if (damage.wasCrit || Random.random(Random.sharedRandom) < 0.5f)
			{
				BloodSplatter.splatter(attackerPos, defenderPos, 1f, world)
			}
			defenderStats.lastHitSource = attackerPos

			val lifeSteal = attackerStats.getStat(Statistic.LIFESTEAL)
			val stolenLife = finalDam * lifeSteal
			if (stolenLife > 0f)
			{
				attackerStats.heal(stolenLife)
				attackerStats.healing += stolenLife

				if (EventSystem.isEventRegistered(EventType.HEALED, attacker))
				{
					val healEventData = EventData.obtain().set(EventType.HEALED, attacker, defender, mapOf(Pair("amount", stolenLife)))
					eventSystem.addEvent(healEventData)
				}
			}
			else if (stolenLife < 0f)
			{
				attackerStats.damage(stolenLife, false)
			}

			// do damage events

			// crit
			if (damage.wasCrit)
			{
				if (EventSystem.isEventRegistered(EventType.CRIT, attacker))
				{
					val dealEventData = EventData.obtain().set(EventType.CRIT, attacker, defender, mapOf(
						Pair("damage", finalDam),
						Pair("dist", attackerPos.taxiDist(defenderPos).toFloat())))
					eventSystem.addEvent(dealEventData)
				}
			}

			// deal damage
			if (EventSystem.isEventRegistered(EventType.DEAL_DAMAGE, attacker))
			{
				val dealEventData = EventData.obtain().set(EventType.DEAL_DAMAGE, attacker, defender, mapOf(
					Pair("damage", finalDam),
					Pair("dist", attackerPos.taxiDist(defenderPos).toFloat())))
				eventSystem.addEvent(dealEventData)
			}

			// take damage
			if (EventSystem.isEventRegistered(EventType.TAKE_DAMAGE, defender))
			{
				val takeEventData = EventData.obtain().set(EventType.TAKE_DAMAGE, defender, attacker, mapOf(
					Pair("damage", finalDam),
					Pair("dist", attackerPos.taxiDist(defenderPos).toFloat())))
				eventSystem.addEvent(takeEventData)
			}
		}
	}
}