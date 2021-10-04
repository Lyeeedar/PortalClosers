package com.lyeeedar.AI.BehaviourTree.Actions

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectMap
import com.lyeeedar.AI.BehaviourTree.BehaviourTreeState
import com.lyeeedar.AI.BehaviourTree.EvaluationState
import com.lyeeedar.AI.BehaviourTree.Nodes.AbstractBehaviourNode
import com.lyeeedar.AI.Tasks.TaskMove
import com.lyeeedar.AI.Tasks.TaskUseAbility
import com.lyeeedar.AI.Tasks.TaskWait
import com.lyeeedar.Components.*
import com.lyeeedar.Direction
import com.lyeeedar.Game.Tile
import com.lyeeedar.SpaceSlot
import com.lyeeedar.Systems.renderSystem
import com.lyeeedar.UI.RenderSystemWidget
import com.lyeeedar.Util.Controls
import com.lyeeedar.Util.DataClass
import com.lyeeedar.Util.Statics
import com.lyeeedar.Util.XmlData

@DataClass(category = "Action")
class ProcessInputBehaviourAction : AbstractBehaviourAction()
{
	//region non-data
	val potentialTargets = Array<Tile>()
	//endregion

	override fun evaluate(state: BehaviourTreeState): EvaluationState
	{
		val entity = state.entity.get() ?: return EvaluationState.FAILED
		val pos = entity.position() ?: return EvaluationState.FAILED
		val task = entity.task() ?: return EvaluationState.FAILED
		if (task.tasks.size > 0) return EvaluationState.FAILED

		val ability = entity.ability()
		if (ability != null)
		{
			for (ab in ability.abilities)
			{
				if (ab.isSelected && ab.cooldown == 0 && ab.remainingUsages != 0)
				{
					if (RenderSystemWidget.instance!!.isSelected)
					{
						val tile = state.world.grid.tryGet(RenderSystemWidget.instance!!.selectedPoint, null)

						if (tile != null && ab.getValidTargets(entity, state.world).contains(tile))
						{
							if (tile.dist(pos.position) !in ab.data.range.x..ab.data.range.y) continue

							task.tasks.add(TaskUseAbility.obtain().set(tile, ab))

							ab.isSelected = false

							return EvaluationState.RUNNING
						}
					}

					return EvaluationState.FAILED
				}
			}
		}

		var moveDir: Direction? = null
		if (RenderSystemWidget.instance!!.isSelected)
		{
			val tile = RenderSystemWidget.instance!!.selectedPoint

			if (tile == pos.position)
			{
				if (Gdx.input.justTouched())
				{
					moveDir = Direction.CENTER
				}
			}
			else
			{
				moveDir = Direction.getCardinalDirection(tile, pos.position)
			}
		}
		else
		{
			if (Statics.controls.isKeyDown(Controls.Keys.LEFT))
			{
				moveDir = Direction.WEST
			}
			else if (Statics.controls.isKeyDown(Controls.Keys.RIGHT))
			{
				moveDir = Direction.EAST
			}
			else if (Statics.controls.isKeyDown(Controls.Keys.UP))
			{
				moveDir = Direction.NORTH
			}
			else if (Statics.controls.isKeyDown(Controls.Keys.DOWN))
			{
				moveDir = Direction.SOUTH
			}
			else if (Statics.controls.isKeyDownAndNotConsumed(Controls.Keys.WAIT))
			{
				Statics.controls.consumeKeyPress(Controls.Keys.WAIT)
				moveDir = Direction.CENTER
			}
			else
			{
				return EvaluationState.FAILED
			}
		}

		if (moveDir != null)
		{
			if (moveDir == Direction.CENTER)
			{
				task.tasks.add(TaskWait.obtain())
			}
			else
			{
				val targetTile = state.world.grid.tryGet(pos.position, moveDir, null)
				if (targetTile != null && (pos.isValidTile(targetTile, entity) || targetTile.contents[pos.slot]?.get()?.isAllies(entity) == true))
				{
					task.tasks.add(TaskMove.obtain().set(moveDir))
				}
				else
				{
					return EvaluationState.FAILED
				}
			}
		}

		return EvaluationState.COMPLETED
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
	}
	override val classID: String = "ProcessInput"
	override fun resolve(nodes: ObjectMap<String, AbstractBehaviourNode>)
	{
		super.resolve(nodes)
	}
	//endregion
}