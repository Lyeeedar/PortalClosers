package com.lyeeedar.Game.Ability

import com.badlogic.gdx.utils.Array
import com.lyeeedar.ActionSequence.Actions.AttachParticleAction
import com.lyeeedar.ActionSequence.Actions.ReplaceSourceRenderableAction
import com.lyeeedar.ActionSequence.Actions.SpawnOneShotParticleAction
import com.lyeeedar.ActionSequence.Actions.SpawnTrackedParticleAction
import com.lyeeedar.Util.AssetManager
import com.lyeeedar.Util.Colour
import com.lyeeedar.Util.DataTimeline
import com.lyeeedar.Util.XmlData

class ColourAbilityModifier : AbstractAbilityModifier<ColourKeyframeData>()
{
	@DataTimeline
	override val keyframes: Array<ColourKeyframeData> = Array()

	//region non-data
	val tempCol = Colour()
	//endregion
	override fun applyTo(ability: AbilityData, prev: ColourKeyframeData, next: ColourKeyframeData, alpha: Float)
	{
		val colour = tempCol.set(prev.colour).lerp(next.colour, alpha)

		for (action in ability.actionSequence.rawActions)
		{
			if (action is SpawnOneShotParticleAction)
			{
				action.particle.colour = action.particle.colour.copy().mul(colour)
			}
			else if (action is SpawnTrackedParticleAction)
			{
				action.particle.colour = action.particle.colour.copy().mul(colour)
			}
			else if (action is ReplaceSourceRenderableAction)
			{
				action.renderable.colour = action.renderable.colour.copy().mul(colour)
			}
			else if (action is AttachParticleAction)
			{
				action.particle.colour = action.particle.colour.copy().mul(colour)
			}
		}

		tempCol.lerp(Colour.WHITE, 0.7f)
		ability.icon.colour = ability.icon.colour.copy().mul(tempCol)
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
				val objkeyframes: ColourKeyframeData
				val objkeyframesEl = el
				objkeyframes = ColourKeyframeData()
				objkeyframes.load(objkeyframesEl)
				keyframes.add(objkeyframes)
			}
		}
	}
	override val classID: String = "Colour"
	//endregion
}

class ColourKeyframeData : AbstractAbilityModifierKeyframe()
{
	lateinit var colour: Colour

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		colour = AssetManager.loadColour(xmlData.getChildByName("Colour")!!)
	}
	override val classID: String = "ColourKeyframe"
	//endregion
}