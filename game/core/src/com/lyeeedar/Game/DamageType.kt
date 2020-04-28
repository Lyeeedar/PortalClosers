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
	NONE(Colour.WHITE),
	PURE(Colour.WHITE), // completely ignore all mitigation
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
				NONE -> ""
				PURE -> ""
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
				NONE -> ""
				PURE -> ""
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

	fun applyStatus(rng: LightRNG, attacker: Entity, defender: Entity, attackDamage: Float, world: World<*>)
	{
		when (this)
		{
			NONE -> {}
			PURE -> {}
			FIRE -> applyFire(attacker, defender, attackDamage, world)
			ICE -> applyIce(attacker, defender, attackDamage, world)
			LIGHTNING -> applyLightning(attacker, defender, attackDamage, world)
			VORPAL -> {}
			POISON -> applyPoison(attacker, defender, attackDamage, world)
			BLEED -> applyBleed(attacker, defender, attackDamage, world)
			ACID -> applyAcid(attacker, defender, attackDamage, world)
		}
	}

	private fun applyFire(attacker: Entity, defender: Entity, attackDamage: Float, world: World<*>)
	{
		applyActionSequence("DamageTypes/Fire", attacker, defender, attackDamage, world)
	}

	private fun applyIce(attacker: Entity, defender: Entity, attackDamage: Float, world: World<*>)
	{
		applyActionSequence("DamageTypes/Ice", attacker, defender, attackDamage, world)
	}

	private fun applyPoison(attacker: Entity, defender: Entity, attackDamage: Float, world: World<*>)
	{
		applyActionSequence("DamageTypes/Poison", attacker, defender, attackDamage, world)
	}

	private fun applyLightning(attacker: Entity, defender: Entity, attackDamage: Float, world: World<*>)
	{
		applyActionSequence("DamageTypes/Lightning", attacker, defender, attackDamage, world)
	}

	private fun applyAcid(attacker: Entity, defender: Entity, attackDamage: Float, world: World<*>)
	{
		val buff = Buff.load("DamageTypes/Acid")
		buff.source = EntityReference(attacker)
		defender.statistics()!!.buffs.add(buff)
	}

	private fun applyBleed(attacker: Entity, defender: Entity, attackDamage: Float, world: World<*>)
	{
		val buff = Buff.load("DamageTypes/Bleed")
		buff.source = EntityReference(attacker)
		defender.statistics()!!.buffs.add(buff)
	}

	private fun applyActionSequence(sequencePath: String, attacker: Entity, defender: Entity, attackDamage: Float, world: World<*>)
	{
		val actionSequence = ActionSequence.load(sequencePath)

		val containerEntity = transientActionSequenceArchetype.build()
		containerEntity.actionSequence()!!.set(actionSequence)

		val actionSequenceState = containerEntity.actionSequence()!!.actionSequenceState
		actionSequenceState.lockedEntityTargets.add(EntityReference(defender))
		actionSequenceState.set(EntityReference(attacker), world)
		actionSequenceState.data["damage"] = attackDamage

		world.addEntity(containerEntity)
	}
}