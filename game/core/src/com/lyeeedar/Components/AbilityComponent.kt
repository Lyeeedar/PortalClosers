package com.lyeeedar.Components

import com.badlogic.gdx.utils.Array
import com.lyeeedar.Game.Ability.Ability
import com.lyeeedar.Game.Ability.AbilityData
import com.lyeeedar.Game.Ability.AbilityOrb
import com.lyeeedar.Util.DataFileReference
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
			abilities.add(Ability(ab))
		}
	}
}

class AbilityComponentData : AbstractComponentData()
{
	val abilities: Array<AbilityData> = Array<AbilityData>()

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		val abilitiesEl = xmlData.getChildByName("Abilities")
		if (abilitiesEl != null)
		{
			for (el in abilitiesEl.children)
			{
				val objabilities: AbilityData
				val objabilitiesEl = el
				objabilities = AbilityData()
				objabilities.load(objabilitiesEl)
				abilities.add(objabilities)
			}
		}
	}
	override val classID: String = "Ability"
	//endregion
}