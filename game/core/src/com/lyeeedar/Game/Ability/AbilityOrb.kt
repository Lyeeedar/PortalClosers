package com.lyeeedar.Game.Ability

import com.badlogic.gdx.utils.Array
import com.lyeeedar.Util.*
import com.lyeeedar.Util.XmlData
import com.lyeeedar.Util.XmlDataClassLoader

@DataFile(colour = "255,179,0", icon = "Sprites/Icons/Firebolt.png")
class AbilityOrb : XmlDataClass()
{
	@DataXml(actualClass = "AbilityData")
	lateinit var abilityTemplate: XmlData

	val modifiers: Array<AbstractAbilityModifier<*>> = Array<AbstractAbilityModifier<*>>()

	fun getAbility(tier: Int): AbilityData
	{
		val data = AbilityData()
		data.load(abilityTemplate)

		for (modifier in modifiers)
		{
			modifier.apply(data, tier.toFloat())
		}

		data.nameTransforms.add { "$it ${tier.toRomanNumerals()}" }

		return data
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
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