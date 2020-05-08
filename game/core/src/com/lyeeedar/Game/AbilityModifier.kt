package com.lyeeedar.Game

import com.badlogic.gdx.utils.Array
import com.lyeeedar.ActionSequence.Actions.*
import com.lyeeedar.Renderables.Sprite.Sprite
import com.lyeeedar.Util.*
import com.lyeeedar.Util.AssetManager
import com.lyeeedar.Util.XmlData
import com.lyeeedar.Util.XmlDataClassLoader

@DataGraphNode
class AbilityModifier : XmlDataClass()
{
	@DataNeedsLocalisation(file = "Ability")
	lateinit var name: String

	@DataNeedsLocalisation(file = "Ability")
	lateinit var description: String

	var icon: Sprite? = null

	var cooldownModifier: Int = 0

	var rangeModifier: Point = Point(0, 0)

	val modifierActions: Array<AbstractAbilityModifierAction> = Array()

	@DataGraphReference(elementIsChild = true)
	val nextTier: Array<AbilityModifier> = Array()

	//region generated
	override fun load(xmlData: XmlData)
	{
		name = xmlData.get("Name")
		description = xmlData.get("Description")
		icon = AssetManager.tryLoadSprite(xmlData.getChildByName("Icon"))
		cooldownModifier = xmlData.getInt("CooldownModifier", 0)
		val rangeModifierRaw = xmlData.get("RangeModifier", "0, 0")!!.split(',')
		rangeModifier = Point(rangeModifierRaw[0].trim().toInt(), rangeModifierRaw[1].trim().toInt())
		val modifierActionsEl = xmlData.getChildByName("ModifierActions")
		if (modifierActionsEl != null)
		{
			for (el in modifierActionsEl.children)
			{
				val objmodifierActions: AbstractAbilityModifierAction
				val objmodifierActionsEl = el
				objmodifierActions = XmlDataClassLoader.loadAbstractAbilityModifierAction(objmodifierActionsEl.get("classID", objmodifierActionsEl.name)!!)
				objmodifierActions.load(objmodifierActionsEl)
				modifierActions.add(objmodifierActions)
			}
		}
		val nextTierEl = xmlData.getChildByName("NextTier")
		if (nextTierEl != null)
		{
			for (el in nextTierEl.children)
			{
				val objnextTier: AbilityModifier
				val objnextTierEl = el
				objnextTier = AbilityModifier()
				objnextTier.load(objnextTierEl)
				nextTier.add(objnextTier)
			}
		}
	}
	//endregion
}

abstract class AbstractAbilityModifierAction : XmlDataClass()
{
	abstract fun applyTo(action: AbstractActionSequenceAction)

	//region generated
	override fun load(xmlData: XmlData)
	{
	}
	abstract val classID: String
	//endregion
}

class ColourModifierAction : AbstractAbilityModifierAction()
{
	var colourShift: Colour = Colour.WHITE

	override fun applyTo(action: AbstractActionSequenceAction)
	{
		if (action is SpawnOneShotParticleAction)
		{
			action.particle.colour = action.particle.colour.copy().mul(colourShift)
		}
		else if (action is SpawnTrackedParticleAction)
		{
			action.particle.colour = action.particle.colour.copy().mul(colourShift)
		}
		else if (action is ReplaceSourceRenderableAction)
		{
			action.renderable.colour = action.renderable.colour.copy().mul(colourShift)
		}
		else if (action is AttachParticleAction)
		{
			action.particle.colour = action.particle.colour.copy().mul(colourShift)
		}
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		colourShift = AssetManager.tryLoadColour(xmlData.getChildByName("ColourShift"))!!
	}
	override val classID: String = "ColourModifier"
	//endregion
}

class RepeatModifierAction : AbstractAbilityModifierAction()
{
	var countModifier: Int = 0

	override fun applyTo(action: AbstractActionSequenceAction)
	{
		if (action is RepeatAction)
		{
			action.count += countModifier
		}
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		countModifier = xmlData.getInt("CountModifier", 0)
	}
	override val classID: String = "RepeatModifier"
	//endregion
}

class DamageModifierAction : AbstractAbilityModifierAction()
{
	var damageModifier: String = "source.damage"
	var replace: Boolean = false

	override fun applyTo(action: AbstractActionSequenceAction)
	{
		if (action is DamageAction)
		{
			if (replace)
			{
				action.damage = CompiledExpression(damageModifier)
			}
			else
			{
				val newEqn = "(${action.damage.expression})$damageModifier"
				action.damage = CompiledExpression(newEqn)
			}
		}
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		damageModifier = xmlData.get("DamageModifier", "source.damage")!!
		replace = xmlData.getBoolean("Replace", false)
	}
	override val classID: String = "DamageModifier"
	//endregion
}

class HealModifierAction : AbstractAbilityModifierAction()
{
	var healModifier: String = "source.damage"
	var replace: Boolean = false

	override fun applyTo(action: AbstractActionSequenceAction)
	{
		if (action is HealAction)
		{
			if (replace)
			{
				action.amount = CompiledExpression(healModifier)
			}
			else
			{
				val newEqn = "(${action.amount.expression})$healModifier"
				action.amount = CompiledExpression(newEqn)
			}
		}
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		healModifier = xmlData.get("HealModifier", "source.damage")!!
		replace = xmlData.getBoolean("Replace", false)
	}
	override val classID: String = "HealModifier"
	//endregion
}