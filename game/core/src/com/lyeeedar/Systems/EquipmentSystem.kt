package com.lyeeedar.Systems

import com.badlogic.gdx.utils.ObjectFloatMap
import com.esotericsoftware.spine.attachments.RegionAttachment
import com.esotericsoftware.spine.attachments.SkeletonAttachment
import com.lyeeedar.Components.*
import com.lyeeedar.Renderables.SkeletonRenderable
import com.lyeeedar.Renderables.Sprite.Sprite
import com.lyeeedar.Util.set

class EquipmentSystem(world: World<*>) : AbstractEntitySystem(world, world.getEntitiesFor().any(ComponentType.Equipment).get())
{
	override fun updateEntity(entity: Entity, deltaTime: Float)
	{
		val equip = entity.equipment()!!

		val variables = ObjectFloatMap<String>()
		entity.variables()?.write(variables)

		equip.updateAvailability(variables, world.rng)

		if (!equip.equipped)
		{
			equip.equip(entity)
		}
	}

	override fun onTurnEntity(entity: Entity)
	{

	}
}