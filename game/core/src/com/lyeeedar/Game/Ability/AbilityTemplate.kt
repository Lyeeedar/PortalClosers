package com.lyeeedar.Game.Ability

import com.badlogic.gdx.utils.Array
import com.lyeeedar.Renderables.Sprite.Sprite
import com.lyeeedar.Util.*
import com.lyeeedar.Util.AssetManager
import com.lyeeedar.Util.XmlData

class AbilityTemplate : XmlDataClass()
{
	@DataNeedsLocalisation(file = "Ability")
	lateinit var name: String

	@DataNeedsLocalisation(file = "Ability")
	lateinit var description: String

	@DataLayeredSprite
	lateinit var icon: Sprite

	@DataXml(actualClass = "AbilityData")
	lateinit var abilityTemplate: XmlData

	val modifiers: Array<AbstractAbilityModifier<*>> = Array<AbstractAbilityModifier<*>>()

	fun getAbility(tier: Int): Ability
	{
		val data = AbilityData()
		data.load(abilityTemplate)
		data.actionSequence = data.actionSequence.loadDuplicate()

		val name = Localisation.getText(this.name, "Ability")
		val description = Localisation.getText(this.description, "Ability")

		val ability = Ability(data)
		ability.name = "$name ${tier.toRomanNumerals()}"
		ability.description = description
		ability.icon = icon.copy()

		for (modifier in modifiers)
		{
			modifier.apply(ability, tier.toFloat())
		}

		return ability
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		name = xmlData.get("Name")
		description = xmlData.get("Description")
		icon = AssetManager.loadLayeredSprite(xmlData.getChildByName("Icon")!!)
		abilityTemplate = xmlData.getChildByName("AbilityTemplate")!!
		val modifiersEl = xmlData.getChildByName("Modifiers")
		if (modifiersEl != null)
		{
			for (el in modifiersEl.children)
			{
				val objmodifiers: AbstractAbilityModifier<*>
				val objmodifiersEl = el
				objmodifiers = XmlDataClassLoader.loadAbstractAbilityModifier(objmodifiersEl.get("classID", objmodifiersEl.name)!!)
				objmodifiers.load(objmodifiersEl)
				modifiers.add(objmodifiers)
			}
		}
	}
	//endregion
}