package com.lyeeedar.Systems

import com.badlogic.gdx.utils.ObjectFloatMap
import com.esotericsoftware.spine.attachments.Attachment
import com.esotericsoftware.spine.attachments.RegionAttachment
import com.lyeeedar.Components.*
import com.lyeeedar.Renderables.SkeletonRenderable
import com.lyeeedar.Util.AssetManager
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

		if (!weapon.equipped)
		{
			val renderable = entity.renderable()?.renderable as? SkeletonRenderable
			if (renderable != null)
			{
				val slot = renderable.skeleton.findSlot("handr")

				val attachment = RegionAttachment("weapon")
				attachment.region = AssetManager.loadTextureRegion("Oryx/uf_split/uf_items/weapon_axe_exotic1.png")
				attachment.width = attachment.region.regionWidth.toFloat()
				attachment.height = attachment.region.regionHeight.toFloat()
				attachment.updateOffset()

				slot.attachment = attachment
			}

			weapon.equipped = true
		}
	}

	override fun onTurnEntity(entity: Entity)
	{

	}
}