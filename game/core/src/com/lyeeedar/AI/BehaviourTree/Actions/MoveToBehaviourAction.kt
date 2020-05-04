package com.lyeeedar.AI.BehaviourTree.Actions

import com.badlogic.gdx.utils.ObjectMap
import com.lyeeedar.AI.BehaviourTree.BehaviourTreeState
import com.lyeeedar.AI.BehaviourTree.EvaluationState
import com.lyeeedar.AI.BehaviourTree.Nodes.AbstractBehaviourNode
import com.lyeeedar.AI.Tasks.TaskMove
import com.lyeeedar.Components.isOnTile
import com.lyeeedar.Components.isValidTile
import com.lyeeedar.Components.position
import com.lyeeedar.Components.task
import com.lyeeedar.Direction
import com.lyeeedar.Game.Tile
import com.lyeeedar.Pathfinding.PathfindCache
import com.lyeeedar.Pathfinding.Pathfinder
import com.lyeeedar.Systems.AbstractTile
import com.lyeeedar.Util.Array2D
import com.lyeeedar.Util.DataClass
import com.lyeeedar.Util.Point
import com.lyeeedar.Util.XmlData

@DataClass(category = "Action")
class MoveToBehaviourAction : AbstractBehaviourAction()
{
	val cacheKey = "moveCache"

	var dst: Int = 0
	var towards: Boolean = true
	lateinit var key: String

	override fun evaluate(state: BehaviourTreeState): EvaluationState
	{
		val entity = state.entity.get() ?: return EvaluationState.FAILED
		val target = state.getData<Point>(key, 0)
		val posData = entity.position()
		val position = posData?.position
		val taskData = entity.task()

		// doesnt have all the needed data, fail
		if ( target == null || posData == null || position == null || taskData == null )
		{
			return EvaluationState.FAILED
		}

		// if we arrived at our target, succeed
		if (
			(towards && (position.dist(target) <= dst || posData.isOnTile(target)))
			||
			(!towards && position.dist(target) >= dst) )
		{
			return EvaluationState.COMPLETED
		}

		// if going away, just go directly away
		if (!towards)
		{
			val dir = Direction.getCardinalDirection(position, target)

			val nextTile = state.world.grid.tryGet(position, dir, null )

			// if next step is impassable then fail
			if (nextTile == null || !posData.isValidTile(nextTile, entity))
			{
				return EvaluationState.FAILED
			}

			taskData.tasks.add(TaskMove.obtain().set(dir))

			return EvaluationState.RUNNING
		}
		// path towards
		else
		{
			var cache = state.getData<PathfindCache<AbstractTile>>(cacheKey, dataGuid, null)
			if (cache == null)
			{
				cache = PathfindCache()
				state.setData(cacheKey, dataGuid, cache)
			}

			val path = cache.getPath(state.world.grid as Array2D<AbstractTile>, position, target, posData.size, entity, posData.slot)
			if (path == null)
			{
				return EvaluationState.FAILED
			}

			// if couldnt find a valid path, fail
			if ( path.size < 2 )
			{
				return EvaluationState.FAILED
			}

			val nextTile = state.world.grid.tryGet(path[1], null)

			// if next step is impassable then fail
			if (nextTile == null || !posData.isValidTile(nextTile, entity))
			{
				cache.invalidatePath()
				return EvaluationState.FAILED
			}

			val dir = Direction.getCardinalDirection(path[1], path[0])

			if (dir == Direction.CENTER )
			{
				return EvaluationState.COMPLETED
			}

			taskData.tasks.add(TaskMove.obtain().set(dir))

			return EvaluationState.RUNNING
		}
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		dst = xmlData.getInt("Dst", 0)
		towards = xmlData.getBoolean("Towards", true)
		key = xmlData.get("Key")
	}
	override val classID: String = "MoveTo"
	override fun resolve(nodes: ObjectMap<String, AbstractBehaviourNode>)
	{
		super.resolve(nodes)
	}
	//endregion
}