package com.lyeeedar.ActionSequence.Actions

import com.lyeeedar.ActionSequence.ActionSequenceState
import com.lyeeedar.Components.ComponentType
import com.lyeeedar.Components.EntityPool
import com.lyeeedar.Components.actionSequence
import com.lyeeedar.Util.DataClass
import com.lyeeedar.Util.XmlData

@DataClass(category = "Meta")
class DetachAction : AbstractOneShotActionSequenceAction()
{
	override fun enter(state: ActionSequenceState)
	{
		val source = state.source.get() ?: return

		val sequenceHolder = source.actionSequence()
		if (sequenceHolder?.actionSequenceState != state) return
		source.removeComponent(ComponentType.ActionSequence)

		val activeAbility = source.removeComponent(ComponentType.ActiveAbility)

		val entity = EntityPool.obtain()
		entity.addComponent(sequenceHolder)
		entity.addComponent(ComponentType.Transient)

		if (activeAbility != null)
		{
			entity.addComponent(activeAbility)
		}

		state.world.addEntity(entity)
		state.detached = true
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
	}
	override val classID: String = "Detach"
	//endregion
}