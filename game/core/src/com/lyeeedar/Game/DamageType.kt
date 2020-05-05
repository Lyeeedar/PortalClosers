package com.lyeeedar.Game

import com.lyeeedar.ActionSequence.ActionSequence
import com.lyeeedar.ActionSequence.ActionSequenceState
import com.lyeeedar.Components.*
import com.lyeeedar.Systems.World
import com.lyeeedar.Util.Colour
import com.lyeeedar.Util.Localisation
import ktx.collections.set
import squidpony.squidmath.LightRNG

enum class DamageType constructor(val colour: Colour)
{
	NONE(Colour.RED), // change to deal +40% damage
	PURE(Colour.RED), // completely ignore all mitigation
	FIRE(Colour.ORANGE), // applies damage dot
	ICE(Colour.CYAN), // freezes target in place for a number of turns
	LIGHTNING(Colour.YELLOW), // chance to chain damage to a nearby target
	VORPAL(Colour.BLACK), // pierces armour
	POISON(Colour.PURPLE), // applies a dot that isnt mitigated by armour
	BLEED(Colour.RED), // does damage on each move
	ACID(Colour.GREEN); // reduces armour

	val niceName: String
		get()
		{
			return when(this)
			{
				NONE -> Localisation.getText("damagetype.none", "UI")
				PURE -> Localisation.getText("damagetype.pure", "UI")
				FIRE -> Localisation.getText("damagetype.fire", "UI")
				ICE -> Localisation.getText("damagetype.ice", "UI")
				LIGHTNING -> Localisation.getText("damagetype.lightning", "UI")
				VORPAL -> Localisation.getText("damagetype.vorpal", "UI")
				POISON -> Localisation.getText("damagetype.poison", "UI")
				BLEED -> Localisation.getText("damagetype.bleed", "UI")
				ACID -> Localisation.getText("damagetype.acid", "UI")
			}
		}

	val tooltip: String
		get()
		{
			return when(this)
			{
				NONE -> Localisation.getText("damagetype.none.description", "UI")
				PURE -> Localisation.getText("damagetype.pure.description", "UI")
				FIRE -> Localisation.getText("damagetype.fire.description", "UI")
				ICE -> Localisation.getText("damagetype.ice.description", "UI")
				LIGHTNING -> Localisation.getText("damagetype.lightning.description", "UI")
				VORPAL -> Localisation.getText("damagetype.vorpal.description", "UI")
				POISON -> Localisation.getText("damagetype.poison.description", "UI")
				BLEED -> Localisation.getText("damagetype.bleed.description", "UI")
				ACID -> Localisation.getText("damagetype.acid.description", "UI")
			}
		}

	companion object
	{
		val Values = values()
	}

	fun applyCriticalEffect(attacker: Entity, defender: Entity, attackDamage: AttackDamage, world: World<*>, rng: LightRNG)
	{
		when (this)
		{
			NONE -> { attackDamage.damage *= 1.3f }
			PURE -> {}
			FIRE -> applyFire(attacker, defender, attackDamage.damage, world, rng)
			ICE -> applyIce(attacker, defender, attackDamage.damage, world, rng)
			LIGHTNING -> applyLightning(attacker, defender, attackDamage.damage, world, rng)
			VORPAL -> {}
			POISON -> applyPoison(attacker, defender, attackDamage.damage, world, rng)
			BLEED -> applyBleed(attacker, defender, attackDamage.damage, world, rng)
			ACID -> applyAcid(attacker, defender, attackDamage.damage, world, rng)
		}
	}

	private fun applyFire(attacker: Entity, defender: Entity, attackDamage: Float, world: World<*>, rng: LightRNG)
	{
		applyActionSequence("DamageTypes/Fire", attacker, defender, attackDamage, world, rng)
	}

	private fun applyIce(attacker: Entity, defender: Entity, attackDamage: Float, world: World<*>, rng: LightRNG)
	{
		applyActionSequence("DamageTypes/Ice", attacker, defender, attackDamage, world, rng)
	}

	private fun applyPoison(attacker: Entity, defender: Entity, attackDamage: Float, world: World<*>, rng: LightRNG)
	{
		applyActionSequence("DamageTypes/Poison", attacker, defender, attackDamage, world, rng)
	}

	private fun applyLightning(attacker: Entity, defender: Entity, attackDamage: Float, world: World<*>, rng: LightRNG)
	{
		applyActionSequence("DamageTypes/Lightning", attacker, defender, attackDamage, world, rng)
	}

	private fun applyAcid(attacker: Entity, defender: Entity, attackDamage: Float, world: World<*>, rng: LightRNG)
	{
		val buff = Buff.load("DamageTypes/Acid")
		buff.source = attacker.getRef()
		defender.statistics()!!.buffs.add(buff)
	}

	private fun applyBleed(attacker: Entity, defender: Entity, attackDamage: Float, world: World<*>, rng: LightRNG)
	{
		val buff = Buff.load("DamageTypes/Bleed")
		buff.source = attacker.getRef()
		defender.statistics()!!.buffs.add(buff)
	}

	private fun applyActionSequence(sequencePath: String, attacker: Entity, defender: Entity, attackDamage: Float, world: World<*>, rng: LightRNG)
	{
		val actionSequence = ActionSequence.load(sequencePath)

		val containerEntity = transientActionSequenceArchetype.build()
		containerEntity.actionSequence()!!.set(actionSequence)

		val actionSequenceState = containerEntity.actionSequence()!!.actionSequenceState
		actionSequenceState.lockedEntityTargets.add(defender.getRef())
		actionSequenceState.set(attacker.getRef(), world, rng.nextLong())
		actionSequenceState.data["damage"] = attackDamage

		world.addEntity(containerEntity)
	}
}