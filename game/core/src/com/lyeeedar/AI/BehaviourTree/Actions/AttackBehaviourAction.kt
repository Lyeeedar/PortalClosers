package com.lyeeedar.AI.BehaviourTree.Actions

import com.badlogic.gdx.utils.ObjectMap
import com.lyeeedar.AI.BehaviourTree.BehaviourTreeState
import com.lyeeedar.AI.BehaviourTree.EvaluationState
import com.lyeeedar.AI.BehaviourTree.Nodes.AbstractBehaviourNode
import com.lyeeedar.AI.Tasks.TaskAttack
import com.lyeeedar.Components.position
import com.lyeeedar.Components.statistics
import com.lyeeedar.Components.task
import com.lyeeedar.Game.Tile
import com.lyeeedar.Util.DataClass
import com.lyeeedar.Util.Point
import com.lyeeedar.Util.XmlData

@DataClass(category = "Action")
class AttackBehaviourAction : AbstractBehaviourAction()
{
	lateinit var key: String

	override fun evaluate(state: BehaviourTreeState): EvaluationState
	{
		val target = state.getData<Point>(key, 0)
		val posData = state.entity.get()?.position()
		val taskData = state.entity.get()?.task()
		val stats = state.entity.get()?.statistics()

		// doesnt have all the needed data, fail
		if ( target == null || posData == null || taskData == null || stats == null )
		{
			return EvaluationState.FAILED
		}

		val tile = posData.position as? Tile ?: return EvaluationState.FAILED
		val targetTile = state.world.grid.tryGet(target, null) ?: return EvaluationState.FAILED

		if (target.dist(tile) > stats.attackDefinition.range)
		{
			return EvaluationState.FAILED
		}

		taskData.tasks.add(TaskAttack.obtain().set(targetTile, stats.attackDefinition))

		return EvaluationState.RUNNING
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		key = xmlData.get("Key")
	}
	override val classID: String = "Attack"
	override fun resolve(nodes: ObjectMap<String, AbstractBehaviourNode>)
	{
		super.resolve(nodes)
	}
	//endregion
}