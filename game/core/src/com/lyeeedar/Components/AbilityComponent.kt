package com.lyeeedar.Components

import com.badlogic.gdx.utils.Array
import com.lyeeedar.Game.Ability.Ability
import com.lyeeedar.Game.Ability.AbilityData
import com.lyeeedar.Game.Ability.AbilityTemplate
import com.lyeeedar.Util.XmlData

class AbilityComponent : DataComponent()
{
	override val type: ComponentType = ComponentType.Ability

	val abilities = Array<Ability>()

	override fun reset()
	{
		abilities.clear()
	}

	override fun initialiseFrom(data: AbstractComponentData)
	{
		val data = data as AbilityComponentData
		for (ab in data.abilities)
		{
			abilities.add(ab.getAbility(1))
		}
	}
}

class AbilityComponentData : AbstractComponentData()
{
	val abilities: Array<AbilityTemplate> = Array<AbilityTemplate>()

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		val abilitiesEl = xmlData.getChildByName("Abilities")
		if (abilitiesEl != null)
		{
			for (el in abilitiesEl.children)
			{
				val objabilities: AbilityTemplate
				val objabilitiesEl = el
				objabilities = AbilityTemplate()
				objabilities.load(objabilitiesEl)
				abilities.add(objabilities)
			}
		}
	}
	override val classID: String = "Ability"
	//endregion
}

fun Entity.abilities(): Sequence<Ability> = sequence<Ability> {
	val equip = equipment()
	if (equip != null)
	{
		for (ability in equip.availableAbilities)
		{
			yield(ability)
		}
	}

	val ability = ability()
	if (ability != null)
	{
		for (ab in ability.abilities)
		{
			yield(ab)
		}
	}
}