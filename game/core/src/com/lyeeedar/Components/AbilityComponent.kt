package com.lyeeedar.Components

import com.lyeeedar.Game.Ability.Ability
import com.lyeeedar.Game.Ability.AbilityData
import com.lyeeedar.Game.Ability.AbilityOrb
import com.lyeeedar.Util.DataFileReference
import com.lyeeedar.Util.XmlData

class AbilityComponent : DataComponent()
{
	override val type: ComponentType = ComponentType.Ability

	var ability1Orb: AbilityOrb? = null
	var ability1: Ability? = null

	var ability2Orb: AbilityOrb? = null
	var ability2: Ability? = null

	var ability3Orb: AbilityOrb? = null
	var ability3: Ability? = null

	var ability4Orb: AbilityOrb? = null
	var ability4: Ability? = null

	var ultimateOrb: AbilityOrb? = null
	var ultimate: Ability? = null

	val abilities: Sequence<Ability>
		get() = sequence {
			if (ultimate != null) yield(ultimate!!)
			if (ability1 != null) yield(ability1!!)
			if (ability2 != null) yield(ability2!!)
			if (ability3 != null) yield(ability3!!)
			if (ability4 != null) yield(ability4!!)
		}

	override fun reset()
	{
		ability1 = null
		ability1Orb = null
		ability2 = null
		ability2Orb = null
		ability3 = null
		ability3Orb = null
		ability4 = null
		ability4Orb = null
		ultimate = null
		ultimateOrb = null
	}

	fun createAbilities(tier: Int)
	{
		ability1 = ability1Orb?.getAbility(tier)?.get()
		ability2 = ability2Orb?.getAbility(tier)?.get()
		ability3 = ability3Orb?.getAbility(tier)?.get()
		ability4 = ability4Orb?.getAbility(tier)?.get()
		ultimate = ultimateOrb?.getAbility(tier)?.get()
	}

	override fun initialiseFrom(data: AbstractComponentData)
	{
		val data = data as AbilityComponentData
		ability1Orb = AbilityOrb.tryLoad(data.ability1)
		ability2Orb = AbilityOrb.tryLoad(data.ability2)
		ability3Orb = AbilityOrb.tryLoad(data.ability3)
		ability4Orb = AbilityOrb.tryLoad(data.ability4)
		ultimateOrb = AbilityOrb.tryLoad(data.ultimate)
	}
}

class AbilityComponentData : AbstractComponentData()
{
	@DataFileReference(resourceType = "AbilityOrb")
	var ability1: String? = null

	@DataFileReference(resourceType = "AbilityOrb")
	var ability2: String? = null

	@DataFileReference(resourceType = "AbilityOrb")
	var ability3: String? = null

	@DataFileReference(resourceType = "AbilityOrb")
	var ability4: String? = null

	@DataFileReference(resourceType = "AbilityOrb")
	var ultimate: String? = null

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		ability1 = xmlData.get("Ability1", null)
		ability2 = xmlData.get("Ability2", null)
		ability3 = xmlData.get("Ability3", null)
		ability4 = xmlData.get("Ability4", null)
		ultimate = xmlData.get("Ultimate", null)
	}
	override val classID: String = "Ability"
	//endregion
}