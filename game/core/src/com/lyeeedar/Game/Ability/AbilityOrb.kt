package com.lyeeedar.Game.Ability

import com.badlogic.gdx.utils.Array
import com.lyeeedar.Game.AbilityModifier
import com.lyeeedar.Util.*
import com.lyeeedar.Util.XmlData
import com.lyeeedar.Util.XmlDataClassLoader

@DataFile(colour = "255,179,0", icon = "Sprites/Icons/Firebolt.png")
class AbilityOrb : XmlDataClass()
{
	@DataXml(actualClass = "AbilityData")
	lateinit var abilityTemplate: XmlData

	val modifiers: Array<AbstractAbilityModifier<*>> = Array<AbstractAbilityModifier<*>>()

	fun getAbilities(tier: Int): Array<AbilityData>
	{
		val output = Array<AbilityData>()
		//getAbilities(tier, 1, tier1, output)
		return output
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