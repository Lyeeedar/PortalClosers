package com.lyeeedar.AI.BehaviourTree.Actions

import com.badlogic.gdx.utils.ObjectMap
import com.lyeeedar.AI.BehaviourTree.BehaviourTreeState
import com.lyeeedar.AI.BehaviourTree.EvaluationState
import com.lyeeedar.AI.BehaviourTree.Nodes.AbstractBehaviourNode
import com.lyeeedar.Components.ComponentType
import com.lyeeedar.Components.HateComponent
import com.lyeeedar.Components.pack
import com.lyeeedar.Util.DataClass
import com.lyeeedar.Util.XmlData

@DataClass(category = "Entity")
class GetAgroBehaviourAction : AbstractBehaviourAction()
{
	lateinit var key: String

	override fun evaluate(state: BehaviourTreeState): EvaluationState
	{
		val entity = state.entity.get() ?: return EvaluationState.FAILED
		val hate = entity.addOrGet(ComponentType.Hate) as HateComponent
		var target = hate.getAgroedTarget(entity, state.world)

		if (target == null)
		{
			val pack = entity.pack()
			if (pack != null)
			{
				target = pack.getPackAgro()
			}
		}

		if (target == null || !target.isValid())
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