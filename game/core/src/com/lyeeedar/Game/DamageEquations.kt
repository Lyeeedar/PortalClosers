package com.lyeeedar.Game

import com.lyeeedar.Components.*
import com.lyeeedar.Renderables.Animation.BlinkAnimation
import com.lyeeedar.Renderables.Animation.ColourChangeAnimation
import com.lyeeedar.Renderables.SkeletonRenderable
import com.lyeeedar.Systems.*
import com.lyeeedar.Util.BloodSplatter
import com.lyeeedar.Util.Colour
import com.lyeeedar.Util.Random
import com.lyeeedar.Util.randomWeighted
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

		// FinalDamage = AttackDam * ArmourMitigation * DR * elementalBonus
		fun calculateFinalDamage(defenderStats: StatisticsComponent, damage: AttackDamage)
		{
			val armourMitigation = calculateArmourMitigation(defenderStats, damage)
			val dr = defenderStats.getStat(Statistic.DR)
			val elementalBonus = damage.type.getDamageAgainst(defenderStats.elementalType)

			damage.damage = damage.damage * armourMitigation * (1f - dr) * elementalBonus
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

			// try blocking
			if (checkAegis(rng, defenderStats))
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

			defenderStats.damage(damage)
			defender.ai()?.activate(defender)

			if (defenderStats.hp <= 0)
			{
				val activeAbility = defender.actionSequence()
				activeAbility?.actionSequence?.cancel(activeAbility.actionSequenceState)
			}

			attackerStats.attackDamageDealt += damage.damage
			if (attackerStats.summoner != null)
			{
				attackerStats.summoner!!.statistics()!!.attackDamageDealt += damage.damage
			}

			if (Random.random(Random.sharedRandom) < 0.5f)
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
				attackerStats.healingDone += stolenLife

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

			if (defender.renderable()?.renderable is SkeletonRenderable)
			{
				(defender.renderable()?.renderable as SkeletonRenderable).layerAnimation("hit")
			}
			defender.renderable()?.renderable?.animation = BlinkAnimation.obtain().set(Colour.WHITE, Colour.RED, 0.2f)
		}
	}
}

class AttackDamage(var damage: Float, var type: Elements)