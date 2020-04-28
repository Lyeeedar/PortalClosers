package com.lyeeedar.AI.BehaviourTree.Actions

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectMap
import com.lyeeedar.AI.BehaviourTree.BehaviourTreeState
import com.lyeeedar.AI.BehaviourTree.EvaluationState
import com.lyeeedar.AI.BehaviourTree.Nodes.AbstractBehaviourNode
import com.lyeeedar.AI.Tasks.TaskAttack
import com.lyeeedar.AI.Tasks.TaskMove
import com.lyeeedar.AI.Tasks.TaskWait
import com.lyeeedar.Components.isEnemies
import com.lyeeedar.Components.position
import com.lyeeedar.Components.statistics
import com.lyeeedar.Components.task
import com.lyeeedar.Direction
import com.lyeeedar.Game.Tile
import com.lyeeedar.SpaceSlot
import com.lyeeedar.Systems.renderSystem
import com.lyeeedar.UI.RenderSystemWidget
import com.lyeeedar.Util.Controls
import com.lyeeedar.Util.Statics
import com.lyeeedar.Util.XmlData


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

		var moveDir: Direction? = null
		if (Gdx.input.isTouched(0) && !Gdx.input.isTouched(1) && !Gdx.input.isTouched(2) && !Gdx.input.isTouched(3) && !Gdx.input.isTouched(4))
		{
			val tile = RenderSystemWidget.instance.selectedPoint ?: return EvaluationState.FAILED

			if (tile == pos.position)
			{
				if (Gdx.input.justTouched())
				{
					moveDir = Direction.CENTER
				}
			}
			else
			{
				moveDir = Direction.getDirection(pos.position, tile)
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
				val attack = entity.statistics()!!.attackDefinition
				potentialTargets.clear()

				if (attack.range <= 1)
				{
					val tile = state.world.grid.tryGet(pos.position, moveDir, null) as? Tile ?: return EvaluationState.FAILED
					potentialTargets.add(tile)
				}
				else
				{
					for (point in Direction.buildCone(moveDir, pos.position, attack.range))
					{
						val tile = state.world.grid.tryGet(point, null) as? Tile ?: continue
						if (tile.skipRender || tile.skipRenderEntities) continue
						potentialTargets.add(tile)
					}
				}

				var enemyTile: Tile? = null
				var enemyTileDist: Int = Int.MAX_VALUE
				outer@for (tile in potentialTargets)
				{
					for (slot in SpaceSlot.EntityValues)
					{
						val other = tile.contents[slot]?.get() ?: continue
						if (other.isEnemies(entity))
						{
							val dist = pos.position.taxiDist(tile)
							if (dist < enemyTileDist)
							{
								enemyTile = tile
								enemyTileDist = dist
							}

							break
						}
					}
				}

				if (enemyTile != null)
				{
					val attack = entity.statistics()!!.attackDefinition
					task.tasks.add(TaskAttack.obtain().set(enemyTile, attack))
				}
				else
				{
					task.tasks.add(TaskMove.obtain().set(moveDir))
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