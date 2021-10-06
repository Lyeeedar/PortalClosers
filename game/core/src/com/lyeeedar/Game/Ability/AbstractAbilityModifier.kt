package com.lyeeedar.Game.Ability

import com.badlogic.gdx.utils.Array
import com.lyeeedar.Util.DataNumericRange
import com.lyeeedar.Util.DataValue
import com.lyeeedar.Util.XmlData
import com.lyeeedar.Util.XmlDataClass

abstract class AbstractAbilityModifier<T: AbstractAbilityModifierKeyframe> : XmlDataClass()
{
	abstract val keyframes: Array<T>
	protected abstract fun applyTo(ability: AbilityData, prev: T, next: T, alpha: Float)

	fun apply(ability: AbilityData, tier: Float)
	{
		// find keyframes
		var prev = keyframes[0]
		var next = keyframes[0]
		for (i in 1 until keyframes.size)
		{
			prev = next
			next = keyframes[i]

			if (next.tier >= tier)
			{
				break
			}
		}

		// calculate lerp alpha
		val alpha = (tier - prev.tier) / (next.tier - prev.tier)

		// apply
		applyTo(ability, prev, next, alpha)
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
	}
	abstract val classID: String
	//endregion
}

abstract class AbstractAbilityModifierKeyframe : XmlDataClass()
{
	@DataValue(dataName = "Time")
	@DataNumericRange(min = 0f)
	var tier: Float = 0f

	//region generated
	override fun load(xmlData: XmlData)
	{
		tier = xmlData.getFloat("Time", 0f)
	}
	abstract val classID: String
	//endregion
}