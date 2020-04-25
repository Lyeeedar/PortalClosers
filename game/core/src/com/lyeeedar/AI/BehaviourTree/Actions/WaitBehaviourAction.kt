package com.lyeeedar.AI.BehaviourTree.Actions

import com.badlogic.gdx.utils.ObjectMap
import com.exp4j.Helpers.CompiledExpression
import com.lyeeedar.AI.BehaviourTree.BehaviourTreeState
import com.lyeeedar.AI.BehaviourTree.EvaluationState
import com.lyeeedar.AI.BehaviourTree.Nodes.AbstractBehaviourNode
import com.lyeeedar.AI.Tasks.TaskWait
import com.lyeeedar.Components.task
import com.lyeeedar.Util.XmlData
import com.lyeeedar.Util.round

class WaitBehaviourAction : AbstractBehaviourAction()
{
	lateinit var count: CompiledExpression

	override fun evaluate(state: BehaviourTreeState): EvaluationState
	{
		val task = state.entity.task() ?: return EvaluationState.FAILED

		val num = count.evaluate(state.getVariables()).round()

		for (i in 0 until num)
		{
			task.tasks.add(TaskWait.obtain())
		}

		return EvaluationState.COMPLETED
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		count = CompiledExpression(xmlData.get("Count"))
	}
	override val classID: String = "Wait"
	override fun resolve(nodes: ObjectMap<String, AbstractBehaviourNode>)
	{
		super.resolve(nodes)
	}
	//endregion
}