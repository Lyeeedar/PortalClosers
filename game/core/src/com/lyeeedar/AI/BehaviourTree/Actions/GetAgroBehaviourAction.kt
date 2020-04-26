package com.lyeeedar.AI.BehaviourTree.Actions

import com.badlogic.gdx.utils.ObjectMap
import com.lyeeedar.AI.BehaviourTree.BehaviourTreeState
import com.lyeeedar.AI.BehaviourTree.EvaluationState
import com.lyeeedar.AI.BehaviourTree.Nodes.AbstractBehaviourNode
import com.lyeeedar.Components.ComponentType
import com.lyeeedar.Components.HateComponent
import com.lyeeedar.Util.XmlData

class GetAgroBehaviourAction : AbstractBehaviourAction()
{
	lateinit var key: String

	override fun evaluate(state: BehaviourTreeState): EvaluationState
	{
		val hate = state.entity.addOrGet(ComponentType.Hate) as HateComponent
		val target = hate.getAgroedTarget(state.entity, state.world)

		if (target == null)
		{
			state.removeData(key, 0)
			return EvaluationState.FAILED
		}
		else
		{
			state.setData(key, 0, target)
			return EvaluationState.COMPLETED
		}
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		key = xmlData.get("Key")
	}
	override val classID: String = "GetAgro"
	override fun resolve(nodes: ObjectMap<String, AbstractBehaviourNode>)
	{
		super.resolve(nodes)
	}
	//endregion
}