package com.lyeeedar.Systems

import com.lyeeedar.Components.ComponentType
import com.lyeeedar.Components.ability
import com.lyeeedar.Components.actionSequence
import com.lyeeedar.Game.Ability.AbilityData

class AbilitySystem(world: World<*>) : AbstractSystem(world)
{
	private val abilities = world.getEntitiesFor().all(ComponentType.Ability).get()

	override fun doUpdate(deltaTime: Float)
	{
		for (entity in abilities.entities)
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
	}
}