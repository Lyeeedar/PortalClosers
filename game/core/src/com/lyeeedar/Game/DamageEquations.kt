package com.lyeeedar.Game

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
				return AttackDamage(mult, DamageType.NONE, true)
			}

			return AttackDamage(1f, DamageType.NONE, false)
		}

		// AttackDam = BaseAtk * 20%Modifier * CritMult
		fun getAttackDam(rng: LightRNG, attackerStats: StatisticsComponent, baseAttack: AttackDamage, extraCritChance: Float = 0f, extraCritDam: Float = 0f): AttackDamage
		{
			val modifier = rng.randomWeighted() * 0.2f // 20% range, weighted to 0
			val attack = baseAttack.damage * (1f + modifier)

			val critMult = getCritMultiplier(rng, attackerStats, extraCritChance, extraCritDam)

			return AttackDamage(attack * critMult.damage, baseAttack.type, critMult.wasCrit)
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
		fun calculateFinalDamage(rng: LightRNG, attackerStats: StatisticsComponent, defenderStats: StatisticsComponent, damage: AttackDamage, bonusStatusChance: Float): Float
		{
			if (damage.type == DamageType.PURE)
			{
				return damage.damage
			}

			var armourMitigation = calculateArmourMitigation(defenderStats, damage)
			if (damage.type == DamageType.VORPAL && shouldApplyStatus(rng, attackerStats, bonusStatusChance))
			{
				armourMitigation = 1f
			}

			val levelSuppression = calculateLevelSuppression(attackerStats, defenderStats)
			val dr = defenderStats.getStat(Statistic.DR)

			return damage.damage * armourMitigation * (1f - dr) * levelSuppression
		}

		fun shouldApplyStatus(rng: LightRNG, attackerStats: StatisticsComponent, bonusStatusChance: Float): Boolean
		{
			val statusChance = attackerStats.getStat(Statistic.STATUS_CHANCE) + bonusStatusChance

			return rng.nextFloat() < statusChance
		}

		fun checkAegis(rng: LightRNG, defenderStats: StatisticsComponent): Boolean
		{
			val aegisChance = defenderStats.getStat(Statistic.AEGIS)
			if (aegisChance > 0f && rng.nextFloat() < aegisChance)
			{
				return true
			}

			return false
		}

		fun doAttack(rng: LightRNG, attacker: Entity, defender: Entity, damage: AttackDamage, world: World, bonusStatusChance: Float = 0f)
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
			val finalDam = calculateFinalDamage(rng, attackerStats, defenderStats, damage, bonusStatusChance)
			defenderStats.damage(finalDam, damage.wasCrit)
			defender.hate()?.addDamageHate(attacker, defender, finalDam)

			// add final dam to stuff
			if (shouldApplyStatus(rng, attackerStats, bonusStatusChance))
			{
				damage.type.applyStatus(rng, attacker, defender, finalDam, world)
			}

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

class AttackDamage(val damage: Float, var type: DamageType, val wasCrit: Boolean = false)
{

}