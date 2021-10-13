package com.lyeeedar.Systems

import com.badlogic.gdx.utils.ObjectFloatMap
import com.lyeeedar.Components.ComponentType
import com.lyeeedar.Components.Entity
import com.lyeeedar.Components.statistics
import com.lyeeedar.Components.weapon
import com.lyeeedar.Util.set

class WeaponSystem(world: World<*>) : AbstractEntitySystem(world, world.getEntitiesFor().any(ComponentType.Weapon).get())
{
	override fun updateEntity(entity: Entity, deltaTime: Float)
	{
		val weapon = entity.weapon()!!

		val variables = ObjectFloatMap<String>()
		entity.statistics()?.write(variables)
		variables["resources"] = weapon.resources.toFloat()

		for (move in weapon.weapon.moves)
		{
			move.updateAvailability(variables, world.rng)
		}
	}

	override fun onTurnEntity(entity: Entity)
	{

	}
}