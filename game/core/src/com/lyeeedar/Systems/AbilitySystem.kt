package com.lyeeedar.Systems

import com.lyeeedar.Components.*

class AbilitySystem(world: World<*>) : AbstractSystem(world)
{
	private val abilities = world.getEntitiesFor().all(ComponentType.Ability).get()
	private val activeAbilities = world.getEntitiesFor().all(ComponentType.ActiveAbility).get()
	private val combos = world.getEntitiesFor().all(ComponentType.Combo).get()
	private val equipments = world.getEntitiesFor().all(ComponentType.Equipment).get()

	override fun doUpdate(deltaTime: Float)
	{
		for (entity in activeAbilities.entities)
		{
			if (entity.actionSequence() == null)
			{
				entity.removeComponent(ComponentType.ActiveAbility)
			}
		}
	}

	override fun onTurn()
	{
		for (entity in abilities.entities)
		{
			for (ability in entity.ability()!!.abilities)
			{
				if (ability.cooldown > 0)
				{
					ability.cooldown--
				}
			}
		}
		for (entity in combos.entities)
		{
			val combo = entity.combo() ?: continue
			for (step in combo.combo.nodeMap.values())
			{
				val ability = step.getAsAbility()
				if (ability.cooldown > 0)
				{
					ability.cooldown--
				}
			}
		}
		for (entity in equipments.entities)
		{
			val equip = entity.equipment() ?: continue
			for (ability in equip.allAbilities)
			{
				if (ability.cooldown > 0)
				{
					ability.cooldown--
				}
			}
		}
	}
}