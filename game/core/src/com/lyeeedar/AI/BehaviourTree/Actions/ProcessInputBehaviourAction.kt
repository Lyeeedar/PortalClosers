package com.lyeeedar.AI.BehaviourTree.Actions

import com.badlogic.gdx.Gdx
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
import com.lyeeedar.Game.Ability.Ability
import com.lyeeedar.Game.Ability.AbilityData
import com.lyeeedar.Game.Tile
import com.lyeeedar.SpaceSlot
import com.lyeeedar.UI.AbilityWidget
import com.lyeeedar.UI.AbstractAbilityWidget.Companion.selectedAbility
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

		for (ab in entity.abilities())
		{
			if (ab.isSelected && ab.cooldown == 0 && ab.remainingUsages != 0)
			{
				if (RenderSystemWidget.instance!!.isSelected)
				{
					val tile = state.world.grid.tryGet(RenderSystemWidget.instance!!.selectedPoint, null)

					if (tile != null && ab.getValidTargets(entity, state.world, null).contains(tile))
					{
						if (ab.data.targetType != AbilityData.TargetType.SELF && tile.dist(pos.position) !in ab.data.range.x..ab.data.range.y) continue

						task.tasks.add(TaskUseAbility.obtain().set(tile, ab))

						ab.isSelected = false
						selectedAbility = null

						return EvaluationState.RUNNING
					}
				}

				return EvaluationState.FAILED
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
				if (Statics.supportsDiagonals)
				{
					moveDir = Direction.getDirection(pos.position, tile)
				}
				else
				{
					moveDir = Direction.getCardinalDirection(tile, pos.position)
				}
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
				val attack = entity.weapon()!!.weapon.moves[0].getAsAbility()
				potentialTargets.clear()

				if (attack.data.range.y == 1)
				{
					val tile = state.world.grid.tryGet(pos.position, moveDir, null) as? Tile ?: return EvaluationState.FAILED
					potentialTargets.add(tile)
				}
				else
				{
					for (point in Direction.buildCone(moveDir, pos.position, attack.data.range.y))
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
							val dist = pos.position.dist(tile)
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
					task.tasks.add(TaskUseAbility.obtain().set(enemyTile, attack))
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