package com.lyeeedar.Game.Ability

import com.badlogic.gdx.utils.Array
import com.lyeeedar.Util.DataTimeline
import com.lyeeedar.Util.XmlData
import com.lyeeedar.Util.lerp

class DamageAbilityModifier : AbstractAbilityModifier<DamageKeyframeData>()
{
	@DataTimeline
	override val keyframes: Array<DamageKeyframeData> = Array<DamageKeyframeData>()

	override fun applyTo(ability: AbilityData, prev: DamageKeyframeData, next: DamageKeyframeData, alpha: Float)
	{
		val multiplier = prev.multiplier.lerp(next.multiplier, alpha)

	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		val keyframesEl = xmlData.getChildByName("Keyframes")
		if (keyframesEl != null)
		{
			for (el in keyframesEl.children)
			{
				val objkeyframes: DamageKeyframeData
				val objkeyframesEl = el
				objkeyframes = DamageKeyframeData()
				objkeyframes.load(objkeyframesEl)
				keyframes.add(objkeyframes)
			}
		}
	}
	override val classID: String = "Damage"
	//endregion
}

class DamageKeyframeData : AbstractAbilityModifierKeyframe()
{
	var multiplier: Float = 1f

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		multiplier = xmlData.getFloat("Multiplier", 1f)
	}
	override val classID: String = "DamageKeyframe"
	//endregion
}