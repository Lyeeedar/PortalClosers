package com.lyeeedar.ActionSequence.Actions

import com.lyeeedar.ActionSequence.ActionSequenceState
import com.lyeeedar.Components.weapon
import com.lyeeedar.Util.XmlData

class ConsumeResourcesAction : AbstractOneShotActionSequenceAction()
{
	var count: Int = 1

	override fun enter(state: ActionSequenceState)
	{
		val weapon = state.source.get()?.weapon() ?: return
		weapon.consumeResource(count)
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		count = xmlData.getInt("Count", 1)
	}
	override val classID: String = "ConsumeResources"
	//endregion
}