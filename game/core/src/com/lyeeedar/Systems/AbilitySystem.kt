package com.lyeeedar.Systems

import com.lyeeedar.Components.ComponentType
import com.lyeeedar.Components.ability
import com.lyeeedar.Components.actionSequence
import com.lyeeedar.Game.Ability.AbilityData

class AbilitySystem(world: World<*>) : AbstractSystem(world)
{
	val abilities = world.getEntitiesFor().all(ComponentType.Ability).get()
	val activeAbilities = world.getEntitiesFor().all(ComponentType.ActiveAbility).get()

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
			entity.ability()!!.triggerCooldown(AbilityData.CooldownType.TURN)
		}
	}
}