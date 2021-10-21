package com.lyeeedar.Systems

import com.badlogic.gdx.utils.ObjectFloatMap
import com.esotericsoftware.spine.attachments.RegionAttachment
import com.esotericsoftware.spine.attachments.SkeletonAttachment
import com.lyeeedar.Components.*
import com.lyeeedar.Renderables.SkeletonRenderable
import com.lyeeedar.Renderables.Sprite.Sprite
import com.lyeeedar.Util.set

class WeaponSystem(world: World<*>) : AbstractEntitySystem(world, world.getEntitiesFor().any(ComponentType.Weapon).get())
{
	override fun updateEntity(entity: Entity, deltaTime: Float)
	{
		val weapon = entity.weapon()!!

		val variables = ObjectFloatMap<String>()
		entity.variables()?.write(variables)

		for (move in weapon.weapon.moves)
		{
			move.updateAvailability(variables, world.rng)
		}

		if (!weapon.equipped)
		{
			val renderable = entity.renderable()?.renderable as? SkeletonRenderable
			if (renderable != null)
			{
				val slot = renderable.skeleton.findSlot("handr")

				val child = weapon.weapon.renderable.copy()
				if (child is SkeletonRenderable)
				{
					val attachment = SkeletonAttachment("weapon")
					attachment.skeleton = child.skeleton
					renderable.attachedSkeletons.add(child)

					slot.attachment = attachment
				}
				else
				{
					val attachment = RegionAttachment("weapon")
					attachment.region = (renderable as Sprite).currentTexture
					attachment.width = attachment.region.regionWidth.toFloat()
					attachment.height = attachment.region.regionHeight.toFloat()
					attachment.updateOffset()

					slot.attachment = attachment
				}
			}

			weapon.equipped = true
		}
	}

	override fun onTurnEntity(entity: Entity)
	{

	}
}