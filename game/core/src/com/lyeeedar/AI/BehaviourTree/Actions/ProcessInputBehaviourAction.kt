package com.lyeeedar.AI.BehaviourTree.Actions

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.ObjectMap
import com.lyeeedar.AI.BehaviourTree.BehaviourTreeState
import com.lyeeedar.AI.BehaviourTree.EvaluationState
import com.lyeeedar.AI.BehaviourTree.Nodes.AbstractBehaviourNode
import com.lyeeedar.AI.Tasks.TaskMove
import com.lyeeedar.AI.Tasks.TaskWait
import com.lyeeedar.Components.position
import com.lyeeedar.Components.task
import com.lyeeedar.Direction
import com.lyeeedar.Systems.renderSystem
import com.lyeeedar.Util.Statics
import com.lyeeedar.Util.XmlData


class ProcessInputBehaviourAction : AbstractBehaviourAction()
{
	override fun evaluate(state: BehaviourTreeState): EvaluationState
	{
		val renderSystem = state.world.renderSystem() ?: return EvaluationState.FAILED
		val pos = state.entity.position() ?: return EvaluationState.FAILED
		val task = state.entity.task() ?: return EvaluationState.FAILED

		if (Gdx.input.isTouched(0) && !Gdx.input.isTouched(1) && !Gdx.input.isTouched(2) && !Gdx.input.isTouched(3) && !Gdx.input.isTouched(4))
		{
			val touchX = Gdx.input.x
			val touchY = Gdx.input.y

			val mousePos: Vector3 = Statics.stage.camera.unproject(Vector3(touchX.toFloat(), touchY.toFloat(), 0f))

			val mousePosX = mousePos.x.toInt()
			val mousePosY = mousePos.y.toInt()

			val tile = renderSystem.getClickTile(mousePosX, mousePosY) ?: return EvaluationState.FAILED

			if (tile == pos.position)
			{
				if (Gdx.input.justTouched())
				{
					task.tasks.add(TaskWait.obtain())
				}
			}
			else
			{
				val dir = Direction.getCardinalDirection(pos.position, tile)
				task.tasks.add(TaskMove.obtain().set(dir))
			}
		}
		else
		{
			return EvaluationState.FAILED
		}

		return EvaluationState.COMPLETED
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		afterLoad()
	}
	override val classID: String = "ProcessInput"
	override fun resolve(nodes: ObjectMap<String, AbstractBehaviourNode>)
	{
		super.resolve(nodes)
	}
	//endregion
}