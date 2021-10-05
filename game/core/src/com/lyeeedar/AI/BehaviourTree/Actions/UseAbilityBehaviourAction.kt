package com.lyeeedar.AI.BehaviourTree.Actions

import com.badlogic.gdx.utils.ObjectMap
import com.lyeeedar.AI.BehaviourTree.BehaviourTreeState
import com.lyeeedar.AI.BehaviourTree.EvaluationState
import com.lyeeedar.AI.BehaviourTree.Nodes.AbstractBehaviourNode
import com.lyeeedar.AI.Tasks.TaskUseAbility
import com.lyeeedar.Components.ability
import com.lyeeedar.Components.position
import com.lyeeedar.Components.task
import com.lyeeedar.Game.Ability.AbilityData
import com.lyeeedar.Game.Tile
import com.lyeeedar.Util.Point
import com.lyeeedar.Util.XmlData

class UseAbilityBehaviourAction : AbstractBehaviourAction()
{
	lateinit var key: String

	override fun evaluate(state: BehaviourTreeState): EvaluationState
	{
		val storedTarget = state.getData<Point>(key, 0)
		val entity = state.entity.get() ?: return EvaluationState.FAILED
		val pos = entity.position() ?: return EvaluationState.FAILED
		val abilityHolder = entity.ability() ?: return EvaluationState.FAILED
		val task = entity.task() ?: return EvaluationState.FAILED

		for (ability in abilityHolder.abilities)
		{
			if (ability.cooldown == 0 && ability.remainingUsages != 0)
			{
				val tile = ability.getValidTile(entity, state.world, state.rng, storedTarget, null) ?: continue

				task.tasks.add(TaskUseAbility.obtain().set(tile, ability))

				return EvaluationState.RUNNING
			}
		}

		return EvaluationState.FAILED
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		key = xmlData.get("Key")
	}
	override val classID: String = "UseAbility"
	override fun resolve(nodes: ObjectMap<String, AbstractBehaviourNode>)
	{
		super.resolve(nodes)
	}
	//endregion
}