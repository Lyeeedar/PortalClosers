package com.lyeeedar.Game.Equipment

import com.badlogic.gdx.utils.ObjectFloatMap
import com.lyeeedar.Game.Ability.Ability
import com.lyeeedar.Game.Ability.AbilityTemplate
import com.lyeeedar.Renderables.Sprite.Sprite
import com.lyeeedar.Util.*
import com.lyeeedar.Util.AssetManager
import com.lyeeedar.Util.XmlData
import com.lyeeedar.Util.XmlDataClassLoader
import squidpony.squidmath.LightRNG

@DataFile(colour = "104,226,114", icon = "Sprites/Oryx/uf_split/uf_items/potion_green.png")
@DataClass(name = "Sigil")
class Sigil : UtilityItem()
{

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
	}
	override val classID: String = ""
	//endregion
}

abstract class UtilityItem : XmlDataClass()
{
	@DataNeedsLocalisation(file = "UtilityItem")
	lateinit var name: String

	@DataNeedsLocalisation(file = "UtilityItem")
	lateinit var description: String

	@DataLayeredSprite
	lateinit var icon: Sprite

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

	fun updateAvailability(variables: ObjectFloatMap<String>, rng: LightRNG)
	{
		getAsAbility().available = availableCondition.evaluate(variables, rng) > 0f
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		name = xmlData.get("Name")
		description = xmlData.get("Description")
		icon = AssetManager.loadLayeredSprite(xmlData.getChildByName("Icon")!!)
		availableCondition = CompiledExpression(xmlData.get("AvailableCondition", "1")!!)
		val abilityEl = xmlData.getChildByName("Ability")!!
		ability = AbilityTemplate()
		ability.load(abilityEl)
	}
	abstract val classID: String
	//endregion
}