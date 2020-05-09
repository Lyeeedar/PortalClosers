package com.lyeeedar.Game.Ability

import com.badlogic.gdx.utils.Array
import com.lyeeedar.ActionSequence.Actions.RepeatAction
import com.lyeeedar.Util.DataTimeline
import com.lyeeedar.Util.XmlData
import com.lyeeedar.Util.lerp
import com.lyeeedar.Util.round

class RepeatAbilityModifier : AbstractAbilityModifier<RepeatKeyframeData>()
{
	@DataTimeline
	override val keyframes: Array<RepeatKeyframeData> = Array()

	override fun applyTo(ability: AbilityData, prev: RepeatKeyframeData, next: RepeatKeyframeData, alpha: Float)
	{
		val countChange = prev.countChange.toFloat().lerp(next.countChange.toFloat(), alpha).round()
		var actualCount = 0

		for (action in ability.actionSequence.rawActions)
		{
			if (action is RepeatAction)
			{
				actualCount = action.count + countChange
				action.count = actualCount
			}
		}

		ability.description = ability.description.replace("{Repeat}", actualCount.toString())
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
				val objkeyframes: RepeatKeyframeData
				val objkeyframesEl = el
				objkeyframes = RepeatKeyframeData()
				objkeyframes.load(objkeyframesEl)
				keyframes.add(objkeyframes)
			}
		}
	}
	override val classID: String = "Repeat"
	//endregion
}

class RepeatKeyframeData : AbstractAbilityModifierKeyframe()
{
	var countChange: Int = 0

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		countChange = xmlData.getInt("CountChange", 0)
	}
	override val classID: String = "RepeatKeyframe"
	//endregion
}