package com.lyeeedar.Game

import com.lyeeedar.Components.*
import com.lyeeedar.Systems.*
import com.lyeeedar.Util.*
import squidpony.squidmath.LightRNG

class DamageEquations
{
	companion object
	{
		// AttackDam = BaseAtk * 20%Modifier
		fun getAttackDam(rng: LightRNG, baseAttack: Float): Float
		{
			val modifier = rng.randomWeighted() * 0.2f // 20% range, weighted to 0
			val attack = baseAttack * (1f + modifier)

			return attack
		}

		// ArmourMitigation = Damage / (Damage + Armour)
		fun calculateArmourMitigation(defenderStats: StatisticsComponent, damage: AttackDamage): Float
		{
			val armour = defenderStats.getStat(Statistic.ARMOUR)

			val mitigation = damage.damage / (damage.damage + armour)

			return mitigation
		}

		// FinalDamage = AttackDam * ArmourMitigation * DR * LevelSuppression
		fun calculateFinalDamage(defenderStats: StatisticsComponent, damage: AttackDamage)
		{
			if (damage.type == DamageType.PURE)
			{
				return
			}

			var armourMitigation = calculateArmourMitigation(defenderStats, damage)
			if (damage.type == DamageType.VORPAL && damage.wasCrit)
			{
				armourMitigation = 1f
			}

			val dr = defenderStats.getStat(Statistic.DR)

			damage.damage = damage.damage * armourMitigation * (1f - dr)
		}

		fun wasCrit(rng: LightRNG, attackerStats: StatisticsComponent): Boolean
		{
			val critChance = attackerStats.getStat(Statistic.CRIT_CHANCE)
			return rng.nextFloat() < critChance
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

		fun doAttack(rng: LightRNG, attacker: Entity, defender: Entity, damage: AttackDamage, world: World<*>)
		{
			val defenderPos = defender.position()!!.position
			val defenderStats = defender.statistics()!!
			val attackerPos = attacker.position()!!.position
			val attackerStats = attacker.statistics()!!

			val eventSystem = world.eventSystem()!!

			if (wasCrit(rng, attackerStats))
			{
				damage.wasCrit = true
			}

			// try blocking, only if not pure
			if (damage.type != DamageType.PURE && checkAegis(rng, defenderStats))
			{
				if (EventSystem.isEventRegistered(EventType.BLOCK, defender))
				{
					eventSystem.addEvent(EventType.BLOCK, defender.getRef(), attacker.getRef(), mapOf(Pair("damage", damage.damage)))
				}

				defenderStats.blockedDamage = true

				defenderStats.addMessage("Blocked!", Colour.CYAN, 0.4f)

				return
			}

			// apply modified dam
			calculateFinalDamage(defenderStats, damage)
			// add final dam to stuff
			if (damage.wasCrit)
			{
				damage.type.applyCriticalEffect(attacker, defender, damage, world, rng)
			}

			defenderStats.damage(damage)
			defender.hate()?.addDamageHate(attacker, defender, damage.damage)


			attackerStats.attackDamageDealt += damage.damage
			if (attackerStats.summoner != null)
			{
				attackerStats.summoner!!.statistics()!!.attackDamageDealt += damage.damage
			}

			if (damage.wasCrit || Random.random(Random.sharedRandom) < 0.5f)
			{
				if (world.bloodSystem() != null)
				{
					BloodSplatter.splatter(attackerPos, defenderPos, 1f, world, defenderStats.bloodColour)
				}
			}
			defenderStats.lastHitSource = attackerPos

			val lifeSteal = attackerStats.getStat(Statistic.LIFESTEAL)
			val stolenLife = damage.damage * lifeSteal
			if (stolenLife > 0f)
			{
				attackerStats.heal(stolenLife)
				attackerStats.healing += stolenLife

				if (EventSystem.isEventRegistered(EventType.HEALED, attacker))
				{
					eventSystem.addEvent(EventType.HEALED, attacker.getRef(), defender.getRef(), mapOf(Pair("amount", stolenLife)))
				}
			}
			else if (stolenLife < 0f)
			{
				attackerStats.damage(stolenLife)
			}

			// do damage events

			// crit
			if (damage.wasCrit)
			{
				if (EventSystem.isEventRegistered(EventType.CRIT, attacker))
				{
					eventSystem.addEvent(EventType.CRIT, attacker.getRef(), defender.getRef(), mapOf(
						Pair("damage", damage.damage),
						Pair("dist", attackerPos.dist(defenderPos).toFloat())))
				}
			}

			// deal damage
			if (EventSystem.isEventRegistered(EventType.DEAL_DAMAGE, attacker))
			{
				eventSystem.addEvent(EventType.DEAL_DAMAGE, attacker.getRef(), defender.getRef(), mapOf(
					Pair("damage", damage.damage),
					Pair("dist", attackerPos.dist(defenderPos).toFloat())))
			}

			// take damage
			if (EventSystem.isEventRegistered(EventType.TAKE_DAMAGE, defender))
			{
				eventSystem.addEvent(EventType.TAKE_DAMAGE, defender.getRef(), attacker.getRef(), mapOf(
					Pair("damage", damage.damage),
					Pair("dist", attackerPos.dist(defenderPos).toFloat())))
			}
		}
	}
}

class AttackDamage(var damage: Float, var type: DamageType)
{
	var wasCrit: Boolean = false
}