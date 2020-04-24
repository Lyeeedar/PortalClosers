package com.lyeeedar.Game

import com.lyeeedar.ActionSequence.ActionSequence
import com.lyeeedar.ActionSequence.ActionSequenceState
import com.lyeeedar.Components.Entity
import com.lyeeedar.Components.actionSequence
import com.lyeeedar.Components.stats
import com.lyeeedar.Components.transientActionSequenceArchetype
import com.lyeeedar.Systems.World
import com.lyeeedar.Util.Localisation
import ktx.collections.set
import squidpony.squidmath.LightRNG

enum class DamageType
{
	NONE,
	FIRE, // applies damage dot
	ICE, // freezes target in place for a number of turns
	LIGHTNING, // chance to chain damage to a nearby target
	VORPAL, // pierces armour
	POISON, // applies a dot that isnt mitigated by armour
	BLEED, // does damage on each move
	ACID; // reduces armour

	val niceName: String
		get()
		{
			return when(this)
			{
				NONE -> ""
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

	fun applyStatus(rng: LightRNG, attacker: Entity, defender: Entity, attackDamage: Float, world: World)
	{
		when (this)
		{
			NONE -> {}
			FIRE -> applyFire(attacker, defender, attackDamage, world)
			ICE -> applyIce(attacker, defender, attackDamage, world)
			LIGHTNING -> applyLightning(attacker, defender, attackDamage, world)
			VORPAL -> {}
			POISON -> applyPoison(attacker, defender, attackDamage, world)
			BLEED -> applyBleed(attacker, defender, attackDamage, world)
			ACID -> applyAcid(attacker, defender, attackDamage, world)
		}
	}

	private fun applyFire(attacker: Entity, defender: Entity, attackDamage: Float, world: World)
	{
		applyActionSequence("DamageTypes/Fire", attacker, defender, attackDamage, world)
	}

	private fun applyIce(attacker: Entity, defender: Entity, attackDamage: Float, world: World)
	{
		applyActionSequence("DamageTypes/Ice", attacker, defender, attackDamage, world)
	}

	private fun applyPoison(attacker: Entity, defender: Entity, attackDamage: Float, world: World)
	{
		applyActionSequence("DamageTypes/Poison", attacker, defender, attackDamage, world)
	}

	private fun applyLightning(attacker: Entity, defender: Entity, attackDamage: Float, world: World)
	{
		applyActionSequence("DamageTypes/Lightning", attacker, defender, attackDamage, world)
	}

	private fun applyAcid(attacker: Entity, defender: Entity, attackDamage: Float, world: World)
	{
		val buff = Buff.load("DamageTypes/Acid")
		defender.stats()!!.buffs.add(buff)
	}

	private fun applyBleed(attacker: Entity, defender: Entity, attackDamage: Float, world: World)
	{
		val buff = Buff.load("DamageTypes/Bleed")
		defender.stats()!!.buffs.add(buff)
	}

	private fun applyActionSequence(sequencePath: String, attacker: Entity, defender: Entity, attackDamage: Float, world: World)
	{
		val actionSequence = ActionSequence.load(sequencePath)
		val actionSequenceState = ActionSequenceState.obtain()
		actionSequenceState.set(attacker, world)
		actionSequenceState.data["damage"] = attackDamage

		actionSequenceState.lockedEntityTargets.add(defender)

		val containerEntity = transientActionSequenceArchetype.build()
		containerEntity.actionSequence()!!.set(actionSequence, actionSequenceState)

		world.addEntity(containerEntity)
	}
}