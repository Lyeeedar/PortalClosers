package com.lyeeedar.Components

import com.lyeeedar.Game.Ability
import com.lyeeedar.Game.AbilityData
import com.lyeeedar.Util.XmlData

class AbilityComponent : DataComponent()
{
	override val type: ComponentType = ComponentType.Ability

	var ability1: Ability? = null
	var ability2: Ability? = null
	var ability3: Ability? = null
	var ability4: Ability? = null

	var ultimate: Ability? = null

	val abilities: Sequence<Ability>
		get() = sequence {
			if (ability1 != null) yield(ability1!!)
			if (ability2 != null) yield(ability2!!)
			if (ability3 != null) yield(ability3!!)
			if (ability4 != null) yield(ability4!!)
			if (ultimate != null) yield(ultimate!!)
		}

	fun triggerCooldown(type: AbilityData.CooldownType)
	{
		for (ability in abilities)
		{
			if (ability.data.cooldownType == type)
			{
				ability.remainingCooldown -= 1
			}
		}
	}

	override fun reset()
	{
		ability1 = null
		ability2 = null
		ability3 = null
		ability4 = null
		ultimate = null
	}

	override fun initialiseFrom(data: AbstractComponentData)
	{
		val data = data as AbilityComponentData
		ability1 = data.ability1?.get()
		ability2 = data.ability2?.get()
		ability3 = data.ability3?.get()
		ability4 = data.ability4?.get()
		ultimate = data.ultimate?.get()
	}
}

class AbilityComponentData : AbstractComponentData()
{
	var ability1: AbilityData? = null
	var ability2: AbilityData? = null
	var ability3: AbilityData? = null
	var ability4: AbilityData? = null

	var ultimate: AbilityData? = null

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		val ability1El = xmlData.getChildByName("Ability1")
		if (ability1El != null)
		{
			ability1 = AbilityData()
			ability1!!.load(ability1El)
		}
		val ability2El = xmlData.getChildByName("Ability2")
		if (ability2El != null)
		{
			ability2 = AbilityData()
			ability2!!.load(ability2El)
		}
		val ability3El = xmlData.getChildByName("Ability3")
		if (ability3El != null)
		{
			ability3 = AbilityData()
			ability3!!.load(ability3El)
		}
		val ability4El = xmlData.getChildByName("Ability4")
		if (ability4El != null)
		{
			ability4 = AbilityData()
			ability4!!.load(ability4El)
		}
		val ultimateEl = xmlData.getChildByName("Ultimate")
		if (ultimateEl != null)
		{
			ultimate = AbilityData()
			ultimate!!.load(ultimateEl)
		}
	}
	override val classID: String = "Ability"
	//endregion
}