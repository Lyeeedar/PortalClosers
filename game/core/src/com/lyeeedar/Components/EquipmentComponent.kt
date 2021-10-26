package com.lyeeedar.Components

import com.badlogic.gdx.utils.ObjectFloatMap
import com.esotericsoftware.spine.Slot
import com.esotericsoftware.spine.attachments.SkeletonAttachment
import com.lyeeedar.Game.Ability.Ability
import com.lyeeedar.Game.ElementalCore
import com.lyeeedar.Game.Equipment.Sigil
import com.lyeeedar.Game.Weapon
import com.lyeeedar.Renderables.SkeletonRenderable
import com.lyeeedar.Util.DataFileReference
import com.lyeeedar.Util.XmlData
import com.lyeeedar.Util.getXml
import squidpony.squidmath.LightRNG

class EquipmentComponent : DataComponent()
{
	override val type: ComponentType = ComponentType.Equipment

	lateinit var weapon: Weapon
	lateinit var core: ElementalCore
	lateinit var sigil: Sigil

	var resources: Int = 0

	var equipped = false

	val allAbilities = sequence<Ability> {
		for (move in weapon.moves)
		{
			for (variant in move.variants)
			{
				yield(variant.getAsAbility())
			}
		}
		for (ability in core.abilities)
		{
			yield(ability.getAsAbility())
		}
		yield(sigil.getAsAbility())
	}

	val availableAbilities = sequence<Ability> {
		for (move in weapon.moves)
		{
			yield(move.getAsAbility())
		}
		for (ability in core.abilities)
		{
			yield(ability.getAsAbility())
		}
		yield(sigil.getAsAbility())
	}

	fun updateAvailability(variables: ObjectFloatMap<String>, rng: LightRNG)
	{
		weapon.updateAvailability(variables, rng)
		core.updateAvailability(variables, rng)
		sigil.updateAvailability(variables, rng)
	}

	fun equip(entity: Entity)
	{
		val renderable = entity.renderable()?.renderable as? SkeletonRenderable
		if (renderable != null)
		{
			val left = weapon.leftHand
			if (left != null)
			{
				val slot = renderable.skeleton.findSlot("gripl")
				attachToSlot(slot, renderable, left)
			}

			val right = weapon.rightHand
			if (right != null)
			{
				val slot = renderable.skeleton.findSlot("gripr")
				attachToSlot(slot, renderable, right)
			}
		}

		val stats = entity.statistics()
		if (stats != null)
		{
			stats.elementalType = core.element
		}

		equipped = true
	}

	private fun attachToSlot(slot: Slot, skeletonRenderable: SkeletonRenderable, renderable: SkeletonRenderable)
	{
		val child = renderable.copy() as SkeletonRenderable
		val attachment = SkeletonAttachment("weapon")
		attachment.skeleton = child.skeleton
		skeletonRenderable.attachedSkeletons.add(child)

		slot.attachment = attachment
	}

	override fun initialiseFrom(data: AbstractComponentData)
	{
		val data: EquipmentComponentData = data as EquipmentComponentData
		weapon = Weapon()
		weapon.load(getXml(data.weapon))

		resources = weapon.defaultResources

		core = ElementalCore()
		core.load(getXml(data.elementalCore))

		sigil = Sigil()
		sigil.load(getXml(data.sigil))
	}

	override fun reset()
	{
		resources = 0
		equipped = false
	}

	fun buildResource(count: Int)
	{
		resources += count
		if (resources > weapon.maxResources)
		{
			resources = weapon.maxResources
		}
	}

	fun consumeResource(count: Int)
	{
		resources -= count
		if (resources < 0)
		{
			resources = 0
		}
	}
}

class EquipmentComponentData : AbstractComponentData()
{
	@DataFileReference(resourceType = "Weapon")
	lateinit var weapon: String

	@DataFileReference(resourceType = "ElementalCore")
	lateinit var elementalCore: String

	@DataFileReference(resourceType = "Sigil")
	lateinit var sigil: String

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		weapon = xmlData.get("Weapon")
		elementalCore = xmlData.get("ElementalCore")
		sigil = xmlData.get("Sigil")
	}
	override val classID: String = "Equipment"
	//endregion
}