package com.lyeeedar.Game

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectFloatMap
import com.lyeeedar.Game.Ability.Ability
import com.lyeeedar.Game.Ability.AbilityTemplate
import com.lyeeedar.Renderables.Sprite.Sprite
import com.lyeeedar.Util.*
import com.lyeeedar.Util.AssetManager
import com.lyeeedar.Util.XmlData
import squidpony.squidmath.LightRNG

@DataFile(colour = "204,186,114", icon = "Sprites/Oryx/uf_split/uf_items/gem_amethyst.png")
class ElementalCore : XmlDataClass()
{
	@DataNeedsLocalisation(file = "ElementalCore")
	lateinit var name: String

	@DataNeedsLocalisation(file = "ElementalCore")
	lateinit var description: String

	@DataLayeredSprite
	lateinit var icon: Sprite

	val abilities: Array<CoreAbility> = Array()

	fun updateAvailability(variables: ObjectFloatMap<String>, rng: LightRNG)
	{
		for (ability in abilities)
		{
			ability.getAsAbility().available = ability.availableCondition.evaluate(variables, rng) > 0f
		}
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		name = xmlData.get("Name")
		description = xmlData.get("Description")
		icon = AssetManager.loadLayeredSprite(xmlData.getChildByName("Icon")!!)
		val abilitiesEl = xmlData.getChildByName("Abilities")
		if (abilitiesEl != null)
		{
			for (el in abilitiesEl.children)
			{
				val objabilities: CoreAbility
				val objabilitiesEl = el
				objabilities = CoreAbility()
				objabilities.load(objabilitiesEl)
				abilities.add(objabilities)
			}
		}
	}
	//endregion
}

class CoreAbility : XmlDataClass()
{
	@DataCompiledExpression(default = "1")
	lateinit var availableCondition: CompiledExpression

	lateinit var ability: AbilityTemplate

	@Transient
	var actualAbility: Ability? = null

	fun getAsAbility(): Ability
	{
		if (actualAbility == null)
		{
			actualAbility = ability.getAbility(1)
		}
		return actualAbility!!
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		availableCondition = CompiledExpression(xmlData.get("AvailableCondition", "1")!!)
		val abilityEl = xmlData.getChildByName("Ability")!!
		ability = AbilityTemplate()
		ability.load(abilityEl)
	}
	//endregion
}