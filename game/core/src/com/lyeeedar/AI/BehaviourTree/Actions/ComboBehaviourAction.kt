package com.lyeeedar.AI.BehaviourTree.Actions

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectMap
import com.lyeeedar.AI.BehaviourTree.BehaviourTreeState
import com.lyeeedar.AI.BehaviourTree.EvaluationState
import com.lyeeedar.AI.BehaviourTree.Nodes.AbstractBehaviourNode
import com.lyeeedar.AI.Tasks.TaskCombo
import com.lyeeedar.AI.Tasks.TaskUseAbility
import com.lyeeedar.Components.ability
import com.lyeeedar.Components.combo
import com.lyeeedar.Components.position
import com.lyeeedar.Components.task
import com.lyeeedar.Direction
import com.lyeeedar.Game.Combo.AbstractComboStep
import com.lyeeedar.Game.Tile
import com.lyeeedar.Util.Point
import com.lyeeedar.Util.XmlData

class ComboBehaviourAction : AbstractBehaviourAction()
{
	lateinit var key: String

	override fun evaluate(state: BehaviourTreeState): EvaluationState
	{
		val storedTarget = state.getData<Point>(key, 0)
		val entity = state.entity.get() ?: return EvaluationState.FAILED
		val pos = entity.position() ?: return EvaluationState.FAILED
		val comboHolder = entity.combo() ?: return EvaluationState.FAILED
		val task = entity.task() ?: return EvaluationState.FAILED

		if (comboHolder.currentStep != null)
		{
			if (comboHolder.currentStep!!.next.size == 0)
			{
				comboHolder.currentStep = null
				comboHolder.lastTarget = null
				comboHolder.fixedDirection = null
			}
			else
			{
				// execute next step
				for (next in comboHolder.currentStep!!.next)
				{
					val step = next.next ?: continue
					val ability = step.getAsAbility()

					if (ability.cooldown > 0) continue
					if (ability.remainingUsages == 0) continue
					if (step.chance.evaluate(state.getVariables(), state.rng) == 0f) continue

					var tile = ability.getValidTile(entity, state.world, state.rng, storedTarget, comboHolder.fixedDirection)

					if (tile == null && !comboHolder.currentStep!!.canStop)
					{
						tile = state.world.grid.tryGet(comboHolder.lastTarget!!, null) as? Tile
					}

					if (tile == null) continue

					task.tasks.add(TaskCombo.obtain().set(tile, step))

					return EvaluationState.RUNNING
				}
			}
		}

		// Try to start the combo
		for (root in comboHolder.combo.roots)
		{
			val step = root.next ?: continue
			val ability = step.getAsAbility()

			if (ability.cooldown > 0) continue
			if (ability.remainingUsages == 0) continue
			if (step.chance.evaluate(state.getVariables(), state.rng) == 0f) continue

			val tile = ability.getValidTile(entity, state.world, state.rng, storedTarget, null) ?: continue
			comboHolder.currentStep = step
			comboHolder.lastTarget = tile

			task.tasks.add(TaskCombo.obtain().set(tile, step))

			return EvaluationState.RUNNING
		}

		return EvaluationState.FAILED
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		key = xmlData.get("Key")
	}
	override val classID: String = "Combo"
	override fun resolve(nodes: ObjectMap<String, AbstractBehaviourNode>)
	{
		super.resolve(nodes)
	}
	//endregion
}